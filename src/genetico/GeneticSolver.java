package genetico;

import modelo.Pieza;
import modelo.Rompecabezas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Resuelve el rompecabezas usando algoritmo genetico.
 */
public class GeneticSolver {
    private static final int GENERACIONES = 10;
    private static final int LINEAS_CODIGO_ALGORITMO = 230;
    private static final int BYTES_REFERENCIA = 8;
    private static final int BYTES_INT = 4;
    private static final int BYTES_DOUBLE = 8;
    private static final int BYTES_BOOLEAN = 1;

    private final boolean permitirRotacion;
    private final FitnessCalculator fitnessCalculator;
    private final Seleccion seleccion;
    private final Cruce cruce;
    private final Mutacion mutacion;
    private final Random random;

    private MetricasGenetico metricas;
    private List<Cromosoma> poblacion;

    public GeneticSolver(boolean permitirRotacion) {
        this.permitirRotacion = permitirRotacion;
        this.fitnessCalculator = new FitnessCalculator();
        this.seleccion = new Seleccion();
        this.cruce = new Cruce();
        this.mutacion = new Mutacion();
        this.random = new Random();
        this.metricas = new MetricasGenetico();
        this.poblacion = new ArrayList<>();
    }

    /**
     * Ejecuta el algoritmo genetico sobre las piezas recibidas.
     *
     * @param piezas piezas mezcladas del rompecabezas
     * @param dimension dimension del tablero
     * @return mejor rompecabezas encontrado
     */
    public Rompecabezas resolver(List<Pieza> piezas, int dimension) {
        metricas = new MetricasGenetico();
        long inicio = System.nanoTime();

        int cantidadPoblacion = calcularCantidadPoblacion(dimension);
        int cantidadHijos = calcularCantidadHijos(dimension);
        int fitnessMaximo = fitnessCalculator.calcularFitnessMaximo(dimension);

        metricas.cantidadPoblacion = cantidadPoblacion;
        metricas.cantidadHijos = cantidadHijos;
        metricas.lineasCodigoAlgoritmo = LINEAS_CODIGO_ALGORITMO;
        metricas.asignaciones += 5;

        poblacion = generarPoblacionInicial(piezas, dimension, cantidadPoblacion);
        evaluarPoblacion(poblacion);

        for (int generacion = 1; generacion <= GENERACIONES; generacion++) {
            System.out.println("Generacion genetica #" + generacion);

            List<Cromosoma> hijos = generarHijos(poblacion, cantidadHijos);
            evaluarPoblacion(hijos);

            List<Cromosoma> poblacionTotal = new ArrayList<>();
            poblacionTotal.addAll(poblacion);
            poblacionTotal.addAll(hijos);

            poblacion = seleccion.seleccionarSobrevivientes(poblacionTotal, cantidadPoblacion);
            evaluarPoblacion(poblacion);

            Cromosoma mejor = obtenerMejorCromosoma();
            System.out.println("Mejor fitness de la generacion: " + mejor.obtenerFitness());
            System.out.println();

            metricas.generacionesEjecutadas = generacion;
            metricas.asignaciones += 6;

            if (mejor.obtenerFitness() >= fitnessMaximo) {
                metricas.solucionEncontrada = true;
                break;
            }

            metricas.comparaciones++;
        }

        actualizarEstadisticasFinales();

        imprimirMejoresTres();

        long fin = System.nanoTime();
        metricas.tiempoMilisegundos = (fin - inicio) / 1_000_000.0;
        metricas.lineasEjecutadas = metricas.asignaciones + metricas.comparaciones;
        metricas.memoriaBytes = estimarMemoriaConsumida(piezas.size(), dimension);

        return obtenerMejorCromosoma().convertirARompecabezas();
    }

    private List<Cromosoma> generarPoblacionInicial(List<Pieza> piezas, int dimension, int cantidadPoblacion) {
        List<Cromosoma> poblacionInicial = new ArrayList<>();

        for (int indice = 0; indice < cantidadPoblacion; indice++) {
            List<Pieza> piezasAleatorias = new ArrayList<>(piezas);
            Collections.shuffle(piezasAleatorias, random);

            if (permitirRotacion) {
                aplicarRotacionesAleatorias(piezasAleatorias);
            }

            Cromosoma cromosoma = new Cromosoma(piezasAleatorias, dimension);
            poblacionInicial.add(cromosoma);

            metricas.asignaciones += 4;
        }

        return poblacionInicial;
    }

    private void aplicarRotacionesAleatorias(List<Pieza> piezas) {
        for (int indice = 0; indice < piezas.size(); indice++) {
            Pieza pieza = piezas.get(indice);
            int opcion = random.nextInt(4);

            if (opcion == 1) {
                piezas.set(indice, pieza.rotar90());
            } else if (opcion == 2) {
                piezas.set(indice, pieza.rotar180());
            } else if (opcion == 3) {
                piezas.set(indice, pieza.rotar270());
            }

            metricas.asignaciones += 3;
            metricas.comparaciones += 3;
        }
    }

    private void evaluarPoblacion(List<Cromosoma> cromosomas) {
        for (Cromosoma cromosoma : cromosomas) {
            fitnessCalculator.calcularFitness(cromosoma, metricas);
            metricas.asignaciones++;
        }
    }

    private List<Cromosoma> generarHijos(List<Cromosoma> poblacionActual, int cantidadHijos) {
        List<Cromosoma> hijos = new ArrayList<>();
        List<Cromosoma> padresOrdenados = seleccion.ordenarPorFitness(poblacionActual);

        int indicePadre = 0;

        while (hijos.size() < cantidadHijos) {
            Cromosoma padreUno = padresOrdenados.get(indicePadre % padresOrdenados.size());
            Cromosoma padreDos = padresOrdenados.get((indicePadre + 1) % padresOrdenados.size());

            Cromosoma[] hijosGenerados = cruce.cruzar(padreUno, padreDos, metricas);

            Cromosoma hijoUno = mutacion.mutar(hijosGenerados[0], permitirRotacion, metricas);
            Cromosoma hijoDos = mutacion.mutar(hijosGenerados[1], permitirRotacion, metricas);

            fitnessCalculator.calcularFitness(hijoUno, metricas);
            fitnessCalculator.calcularFitness(hijoDos, metricas);

            imprimirCruce(padreUno, padreDos, hijoUno, hijoDos);

            hijos.add(hijoUno);

            if (hijos.size() < cantidadHijos) {
                hijos.add(hijoDos);
            }

            indicePadre++;
            metricas.asignaciones += 8;
            metricas.comparaciones++;
        }

        return hijos;
    }

    private void imprimirCruce(Cromosoma padreUno, Cromosoma padreDos, Cromosoma hijoUno, Cromosoma hijoDos) {
        System.out.println("Padre 1: " + padreUno.describir() + " puntuacion " + padreUno.obtenerFitness());
        System.out.println("Padre 2: " + padreDos.describir() + " puntuacion " + padreDos.obtenerFitness());
        System.out.println("Hijo 1: " + hijoUno.describir() + " puntuacion " + hijoUno.obtenerFitness());
        System.out.println("Hijo 2: " + hijoDos.describir() + " puntuacion " + hijoDos.obtenerFitness());
        System.out.println();
    }

    private Cromosoma obtenerMejorCromosoma() {
        return poblacion.stream()
                .max(Comparator.comparingDouble(Cromosoma::obtenerFitness))
                .orElseThrow(() -> new IllegalStateException("No existe poblacion genetica."));
    }

    public List<Cromosoma> obtenerMejoresTres() {
        List<Cromosoma> ordenados = seleccion.ordenarPorFitness(poblacion);
        List<Cromosoma> mejores = new ArrayList<>();

        int limite = Math.min(3, ordenados.size());

        for (int indice = 0; indice < limite; indice++) {
            mejores.add(ordenados.get(indice).copiar());
        }

        return mejores;
    }

    private void actualizarEstadisticasFinales() {
        List<Cromosoma> ordenados = seleccion.ordenarPorFitness(poblacion);

        double sumaFitness = 0;

        for (Cromosoma cromosoma : ordenados) {
            sumaFitness += cromosoma.obtenerFitness();
            metricas.asignaciones++;
        }

        metricas.mejorFitness = ordenados.get(0).obtenerFitness();
        metricas.peorFitness = ordenados.get(ordenados.size() - 1).obtenerFitness();
        metricas.promedioFitness = sumaFitness / ordenados.size();
        metricas.asignaciones += 4;
    }

    private int calcularCantidadPoblacion(int dimension) {
        if (dimension == 2) {
            return 2;
        }
        if (dimension == 3) {
            return 3;
        }
        if (dimension == 4) {
            return 4;
        }
        if (dimension == 5) {
            return 5;
        }
        if (dimension == 6) {
            return 6;
        }
        if (dimension == 10) {
            return 10;
        }
        return 15;
    }

    private int calcularCantidadHijos(int dimension) {
        return calcularCantidadPoblacion(dimension) * 2;
    }

    private long estimarMemoriaConsumida(int cantidadPiezas, int dimension) {
        long memoriaCromosomas = (long) metricas.cantidadPoblacion * cantidadPiezas * BYTES_REFERENCIA;
        long memoriaPiezas = (long) cantidadPiezas * (5L * BYTES_INT + BYTES_REFERENCIA);
        long memoriaTablero = (long) dimension * dimension * BYTES_REFERENCIA;
        long memoriaMetricas = 8L * BYTES_INT + 8L * BYTES_DOUBLE + 5L * BYTES_BOOLEAN;
        long memoriaAuxiliar = 10L * BYTES_REFERENCIA;

        return memoriaCromosomas + memoriaPiezas + memoriaTablero + memoriaMetricas + memoriaAuxiliar;
    }

    private void imprimirMejoresTres() {
    List<Cromosoma> mejores = obtenerMejoresTres();

    System.out.println("========== MEJORES 3 POBLACIONES ==========");

    for (int i = 0; i < mejores.size(); i++) {
        Cromosoma cromosoma = mejores.get(i);

        System.out.println("Poblacion #" + (i + 1));
        System.out.println("Fitness: " + cromosoma.obtenerFitness());
        System.out.println(cromosoma.describir());
        System.out.println();
    }
    }

    private boolean esCromosomaValido(Cromosoma cromosoma) {
    List<Integer> ids = new ArrayList<>();

    for (Pieza pieza : cromosoma.obtenerPiezas()) {
        if (ids.contains(pieza.obtenerId())) {
            return false;
        }

        ids.add(pieza.obtenerId());
    }

    return ids.size() == cromosoma.obtenerCantidadPiezas();
}

    public MetricasGenetico obtenerMetricas() {
        return metricas;
    }
}