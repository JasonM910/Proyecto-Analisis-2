package genetico;

import modelo.Pieza;
import modelo.Rompecabezas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Resuelve el rompecabezas mediante un algoritmo genetico sin rotacion de piezas.
 *
 * @since 2026-06-08
 * @version 2026-06-09
 */
public class GeneticSolver {
    private static final int GENERACIONES = 10;
    private static final long SEMILLA_BASE = 2_026_060_800L;
    private static final int LINEAS_CODIGO_ALGORITMO = 879;
    private static final int BYTES_REFERENCIA = 8;
    private static final int BYTES_INT = 4;
    private static final int BYTES_LONG = 8;
    private static final int BYTES_DOUBLE = 8;
    private static final int BYTES_BOOLEAN = 1;

    private final FitnessCalculator fitnessCalculator;
    private final Seleccion seleccion;
    private final Cruce cruce;
    private final Mutacion mutacion;
    private final Random random;

    private MetricasGenetico metricas;
    private List<Cromosoma> poblacion;
    private List<Cromosoma> mejoresGlobales;
    private long tiempoImpresionNanos;

    /**
     * Crea el solucionador genetico. Este algoritmo siempre usa piezas fijas.
     */
    public GeneticSolver() {
        this.random = new Random(SEMILLA_BASE);
        this.fitnessCalculator = new FitnessCalculator();
        this.seleccion = new Seleccion();
        this.cruce = new Cruce(random);
        this.mutacion = new Mutacion(random);
        this.metricas = new MetricasGenetico();
        this.poblacion = new ArrayList<>();
        this.mejoresGlobales = new ArrayList<>();
    }

    /**
     * Ejecuta exactamente diez generaciones sobre las piezas recibidas.
     *
     * @param piezas piezas mezcladas del rompecabezas
     * @param dimension dimension del tablero
     * @return mejor rompecabezas encontrado
     */
    public Rompecabezas resolver(List<Pieza> piezas, int dimension) {
        long inicio = System.nanoTime();
        reiniciarEstado();
        validarEntrada(piezas, dimension);
        random.setSeed(calcularSemilla(piezas, dimension));
        metricas.asignaciones++;

        int cantidadPoblacion = calcularCantidadPoblacion(dimension);
        int cantidadHijos = calcularCantidadHijos(dimension);
        int fitnessMaximo = fitnessCalculator.calcularFitnessMaximo(dimension);

        metricas.cantidadPoblacion = cantidadPoblacion;
        metricas.cantidadHijos = cantidadHijos;
        metricas.lineasCodigoAlgoritmo = LINEAS_CODIGO_ALGORITMO;
        metricas.asignaciones += 6;

        poblacion = generarPoblacionInicial(piezas, dimension, cantidadPoblacion);
        metricas.asignaciones++;
        evaluarPoblacion(poblacion);
        registrarMejores(poblacion);

        int generacion = 1;
        metricas.asignaciones++;
        while (metricas.registrarComparacion(generacion <= GENERACIONES)) {
            imprimirGeneracion(generacion);

            List<Cromosoma> hijos = generarHijos(poblacion, cantidadHijos);
            List<Cromosoma> poblacionTotal = new ArrayList<>(poblacion.size() + hijos.size());
            poblacionTotal.addAll(poblacion);
            poblacionTotal.addAll(hijos);
            metricas.asignaciones += poblacion.size() + hijos.size() + 3L;

            poblacion = seleccion.seleccionarSobrevivientes(
                    poblacionTotal,
                    cantidadPoblacion,
                    metricas
            );

            Cromosoma mejor = obtenerMejorCromosoma();
            metricas.generacionesEjecutadas = generacion;
            metricas.asignaciones += 2;
            if (metricas.registrarComparacion(mejor.obtenerFitness() >= fitnessMaximo)) {
                metricas.solucionEncontrada = true;
                metricas.asignaciones++;
            }
            imprimirMejorGeneracion(mejor);
            generacion++;
            metricas.asignaciones++;
        }

        actualizarEstadisticasFinales();
        Cromosoma mejorFinal = obtenerMejorCromosoma();
        Rompecabezas resultado = mejorFinal.convertirARompecabezas(metricas);
        metricas.asignaciones += 2;
        long fin = System.nanoTime();
        metricas.tiempoMilisegundos = (fin - inicio - tiempoImpresionNanos) / 1_000_000.0;
        metricas.memoriaBytes = calcularMemoriaConsumida(piezas.size(), dimension);
        metricas.lineasEjecutadas = metricas.asignaciones + metricas.comparaciones;

        imprimirMejoresTres();
        return resultado;
    }

    /**
     * Reinicia las metricas, la poblacion y el archivo de mejores resultados.
     */
    private void reiniciarEstado() {
        metricas = new MetricasGenetico();
        poblacion = new ArrayList<>();
        mejoresGlobales = new ArrayList<>();
        tiempoImpresionNanos = 0;
        metricas.asignaciones += 4;
    }

    /**
     * Crea permutaciones iniciales validas y sin piezas repetidas.
     */
    private List<Cromosoma> generarPoblacionInicial(
            List<Pieza> piezas,
            int dimension,
            int cantidadPoblacion
    ) {
        List<Cromosoma> poblacionInicial = new ArrayList<>();
        Set<String> firmas = new HashSet<>();

        while (metricas.registrarComparacion(poblacionInicial.size() < cantidadPoblacion)) {
            List<Pieza> piezasAleatorias = new ArrayList<>(piezas);
            Collections.shuffle(piezasAleatorias, random);
            Cromosoma cromosoma = new Cromosoma(piezasAleatorias, dimension, metricas);
            long intercambiosMezcla = Math.max(0, piezasAleatorias.size() - 1L);
            metricas.comparaciones += intercambiosMezcla + 1L;
            metricas.asignaciones += 3L * intercambiosMezcla + 4L;

            if (metricas.registrarComparacion(firmas.add(cromosoma.obtenerFirma(metricas)))) {
                poblacionInicial.add(cromosoma);
                metricas.asignaciones++;
            }
        }
        return poblacionInicial;
    }

    /**
     * Calcula el fitness de todos los cromosomas recibidos.
     */
    private void evaluarPoblacion(List<Cromosoma> cromosomas) {
        int indice = 0;
        metricas.asignaciones++;
        while (metricas.registrarComparacion(indice < cromosomas.size())) {
            fitnessCalculator.calcularFitness(cromosomas.get(indice), metricas);
            indice++;
            metricas.asignaciones++;
        }
    }

    /**
     * Selecciona padres y produce exactamente el doble de la poblacion.
     */
    private List<Cromosoma> generarHijos(
            List<Cromosoma> poblacionActual,
            int cantidadHijos
    ) {
        List<Cromosoma> hijos = new ArrayList<>();
        List<Cromosoma> padresOrdenados = seleccion.ordenarPorFitness(poblacionActual, metricas);
        Set<String> firmasUsadas = obtenerFirmas(poblacionActual);
        int indicePadre = 0;
        metricas.asignaciones += 4;

        while (metricas.registrarComparacion(hijos.size() < cantidadHijos)) {
            Cromosoma padreUno = padresOrdenados.get(indicePadre % padresOrdenados.size());
            Cromosoma padreDos = padresOrdenados.get((indicePadre + 1) % padresOrdenados.size());
            Cromosoma[] hijosGenerados = cruce.cruzar(padreUno, padreDos, metricas);
            metricas.asignaciones += 3;
            evaluarPoblacionComoArreglo(hijosGenerados);
            imprimirCruce(padreUno, padreDos, hijosGenerados[0], hijosGenerados[1]);

            int indiceHijo = 0;
            metricas.asignaciones++;
            while (metricas.registrarComparacion(indiceHijo < hijosGenerados.length)) {
                if (metricas.registrarComparacion(hijos.size() >= cantidadHijos)) {
                    break;
                }
                Cromosoma hijoGenerado = hijosGenerados[indiceHijo];
                Cromosoma hijoAceptado = resolverDuplicado(
                        hijoGenerado,
                        firmasUsadas
                );
                firmasUsadas.add(hijoAceptado.obtenerFirma(metricas));
                hijos.add(hijoAceptado);
                registrarMejor(hijoAceptado);
                indiceHijo++;
                metricas.asignaciones += 5;
            }

            indicePadre++;
            metricas.asignaciones++;
        }

        return hijos;
    }

    /**
     * Evalua los dos hijos devueltos por un cruce.
     */
    private void evaluarPoblacionComoArreglo(Cromosoma[] cromosomas) {
        int indice = 0;
        metricas.asignaciones++;
        while (metricas.registrarComparacion(indice < cromosomas.length)) {
            fitnessCalculator.calcularFitness(cromosomas[indice], metricas);
            indice++;
            metricas.asignaciones++;
        }
    }

    /**
     * Intenta mejorar mediante mutacion un hijo igual a otro individuo.
     */
    private Cromosoma resolverDuplicado(
            Cromosoma cromosoma,
            Set<String> firmasUsadas
    ) {
        if (metricas.registrarComparacion(
                !firmasUsadas.contains(cromosoma.obtenerFirma(metricas))
        )) {
            return cromosoma;
        }

        ResultadoMutacion resultado = mutacion.mutarDuplicado(cromosoma, firmasUsadas, metricas);
        imprimirMutacion(resultado);
        if (metricas.registrarComparacion(resultado.fueAceptada())) {
            return resultado.obtenerResultado();
        }
        return cromosoma;
    }

    /**
     * Reune las firmas utilizadas en una poblacion.
     */
    private Set<String> obtenerFirmas(List<Cromosoma> cromosomas) {
        Set<String> firmas = new HashSet<>();
        int indice = 0;
        metricas.asignaciones += 2;
        while (metricas.registrarComparacion(indice < cromosomas.size())) {
            firmas.add(cromosomas.get(indice).obtenerFirma(metricas));
            indice++;
            metricas.asignaciones++;
        }
        return firmas;
    }

    /**
     * Obtiene el individuo mas apto de la poblacion actual.
     */
    private Cromosoma obtenerMejorCromosoma() {
        List<Cromosoma> ordenados = seleccion.ordenarPorFitness(poblacion, metricas);
        metricas.asignaciones++;
        if (metricas.registrarComparacion(ordenados.isEmpty())) {
            throw new IllegalStateException("No existe poblacion genetica.");
        }
        return ordenados.get(0);
    }

    /**
     * Obtiene los tres mejores resultados unicos encontrados durante la ejecucion.
     *
     * @return hasta tres cromosomas diferentes ordenados por fitness
     */
    public List<Cromosoma> obtenerMejoresTres() {
        List<Cromosoma> mejores = new ArrayList<>();
        for (Cromosoma cromosoma : mejoresGlobales) {
            mejores.add(cromosoma.copiar());
        }
        return mejores;
    }

    /**
     * Registra varios candidatos en el archivo limitado de mejores resultados.
     */
    private void registrarMejores(List<Cromosoma> cromosomas) {
        int indice = 0;
        metricas.asignaciones++;
        while (metricas.registrarComparacion(indice < cromosomas.size())) {
            registrarMejor(cromosomas.get(indice));
            indice++;
            metricas.asignaciones++;
        }
    }

    /**
     * Conserva un candidato unico si pertenece a los tres mejores globales.
     */
    private void registrarMejor(Cromosoma cromosoma) {
        int indice = 0;
        metricas.asignaciones++;
        while (metricas.registrarComparacion(indice < mejoresGlobales.size())) {
            Cromosoma registrado = mejoresGlobales.get(indice);
            metricas.asignaciones++;
            if (metricas.registrarComparacion(
                    registrado.obtenerFirma(metricas).equals(cromosoma.obtenerFirma(metricas))
            )) {
                return;
            }
            indice++;
            metricas.asignaciones++;
        }

        mejoresGlobales.add(cromosoma.copiar(metricas));
        mejoresGlobales = seleccion.ordenarPorFitness(mejoresGlobales, metricas);
        metricas.asignaciones += 2;
        if (metricas.registrarComparacion(mejoresGlobales.size() > 3)) {
            mejoresGlobales.remove(mejoresGlobales.size() - 1);
            metricas.asignaciones++;
        }
    }

    /**
     * Calcula mejor, peor y promedio de la poblacion final.
     */
    private void actualizarEstadisticasFinales() {
        List<Cromosoma> ordenados = seleccion.ordenarPorFitness(poblacion, metricas);
        double sumaFitness = 0;
        int indice = 0;
        metricas.asignaciones += 3;

        while (metricas.registrarComparacion(indice < ordenados.size())) {
            sumaFitness += ordenados.get(indice).obtenerFitness();
            indice++;
            metricas.asignaciones += 2;
        }

        metricas.mejorFitness = ordenados.get(0).obtenerFitness();
        metricas.peorFitness = ordenados.get(ordenados.size() - 1).obtenerFitness();
        metricas.promedioFitness = sumaFitness / ordenados.size();
        metricas.asignaciones += 3;
    }

    /**
     * Obtiene la poblacion exigida por la tabla del proyecto.
     */
    private int calcularCantidadPoblacion(int dimension) {
        if (metricas.registrarComparacion(dimension == 2)) {
            return 2;
        }
        if (metricas.registrarComparacion(dimension == 3)) {
            return 3;
        }
        if (metricas.registrarComparacion(dimension == 4)) {
            return 4;
        }
        if (metricas.registrarComparacion(dimension == 5)) {
            return 5;
        }
        if (metricas.registrarComparacion(dimension == 6)) {
            return 6;
        }
        if (metricas.registrarComparacion(dimension == 10)) {
            return 10;
        }
        if (metricas.registrarComparacion(dimension == 15)) {
            return 15;
        }
        throw new IllegalArgumentException("Dimension no configurada para el algoritmo genetico.");
    }

    /**
     * Calcula la cantidad de hijos como dos veces la poblacion.
     */
    private int calcularCantidadHijos(int dimension) {
        return calcularCantidadPoblacion(dimension) * 2;
    }

    /**
     * Calcula manualmente la memoria de las estructuras geneticas principales.
     */
    private long calcularMemoriaConsumida(int cantidadPiezas, int dimension) {
        long poblacion = (long) metricas.cantidadPoblacion
                * (cantidadPiezas * BYTES_REFERENCIA + 2L * BYTES_REFERENCIA
                + BYTES_INT + BYTES_DOUBLE);
        long hijos = (long) metricas.cantidadHijos
                * (cantidadPiezas * BYTES_REFERENCIA + 2L * BYTES_REFERENCIA
                + BYTES_INT + BYTES_DOUBLE);
        long listasReemplazo = (long) (metricas.cantidadPoblacion + metricas.cantidadHijos)
                * 2L * BYTES_REFERENCIA;
        long mejores = (long) mejoresGlobales.size()
                * (cantidadPiezas * BYTES_REFERENCIA + 2L * BYTES_REFERENCIA
                + BYTES_INT + BYTES_DOUBLE);
        long piezasBase = (long) cantidadPiezas * 6L * BYTES_INT;
        long firmas = (long) (metricas.cantidadPoblacion + metricas.cantidadHijos)
                * (cantidadPiezas * 2L * BYTES_INT + BYTES_REFERENCIA);
        long auxiliaresCruce = 2L * cantidadPiezas
                * (BYTES_REFERENCIA + BYTES_INT + BYTES_REFERENCIA);
        long auxiliaresMutacion = 2L * cantidadPiezas * BYTES_REFERENCIA
                + 2L * BYTES_INT + 2L * BYTES_DOUBLE + BYTES_BOOLEAN;
        long tableroResultado = (long) dimension * dimension * BYTES_REFERENCIA;
        long mediciones = 4L * BYTES_LONG + 8L * BYTES_INT
                + 4L * BYTES_DOUBLE + BYTES_BOOLEAN;
        long referenciasSolver = 9L * BYTES_REFERENCIA + BYTES_LONG;

        return poblacion + hijos + listasReemplazo + mejores + piezasBase
                + firmas + auxiliaresCruce + auxiliaresMutacion
                + tableroResultado + mediciones + referenciasSolver;
    }

    /**
     * Produce una semilla reproducible a partir del caso generado.
     */
    private long calcularSemilla(List<Pieza> piezas, int dimension) {
        long semilla = SEMILLA_BASE + dimension;
        int indice = 0;
        metricas.asignaciones += 2;
        while (metricas.registrarComparacion(indice < piezas.size())) {
            Pieza pieza = piezas.get(indice);
            semilla = 31L * semilla + pieza.obtenerId();
            semilla = 31L * semilla + pieza.obtenerArriba();
            semilla = 31L * semilla + pieza.obtenerDerecha();
            semilla = 31L * semilla + pieza.obtenerAbajo();
            semilla = 31L * semilla + pieza.obtenerIzquierda();
            indice++;
            metricas.asignaciones += 7;
        }
        return semilla;
    }

    /**
     * Comprueba que el conjunto recibido pueda formar un cromosoma valido.
     */
    private void validarEntrada(List<Pieza> piezas, int dimension) {
        metricas.comparaciones += 2;
        if (piezas == null || piezas.size() != dimension * dimension) {
            throw new IllegalArgumentException("La cantidad de piezas debe ser igual a n^2.");
        }
        new Cromosoma(piezas, dimension, metricas);
        metricas.asignaciones++;
    }

    /**
     * Imprime el numero de la generacion sin incluir ese costo en el tiempo.
     */
    private void imprimirGeneracion(int generacion) {
        long inicio = System.nanoTime();
        System.out.println("Generacion genetica #" + generacion);
        tiempoImpresionNanos += System.nanoTime() - inicio;
    }

    /**
     * Imprime padres, hijos y fitness del cruce.
     */
    private void imprimirCruce(
            Cromosoma padreUno,
            Cromosoma padreDos,
            Cromosoma hijoUno,
            Cromosoma hijoDos
    ) {
        long inicio = System.nanoTime();
        System.out.println("Padre 1: " + padreUno.describir() + " puntuacion " + padreUno.obtenerFitness());
        System.out.println("Padre 2: " + padreDos.describir() + " puntuacion " + padreDos.obtenerFitness());
        System.out.println("Hijo 1: " + hijoUno.describir() + " puntuacion " + hijoUno.obtenerFitness());
        System.out.println("Hijo 2: " + hijoDos.describir() + " puntuacion " + hijoDos.obtenerFitness());
        System.out.println();
        tiempoImpresionNanos += System.nanoTime() - inicio;
    }

    /**
     * Imprime el resultado y las puntuaciones de una mutacion.
     */
    private void imprimirMutacion(ResultadoMutacion resultado) {
        long inicio = System.nanoTime();
        System.out.println("Mutacion por poblacion repetida:");
        System.out.println("Individuo original: " + resultado.obtenerOriginal().describir()
                + " puntuacion " + resultado.obtenerOriginal().obtenerFitness());
        System.out.println("Intercambio de posiciones: "
                + resultado.obtenerIndiceUno() + " y " + resultado.obtenerIndiceDos());
        System.out.println("Mutacion: " + resultado.obtenerResultado().describir()
                + " puntuacion " + resultado.obtenerResultado().obtenerFitness());
        if (resultado.fueAceptada()) {
            System.out.println("Resultado de mutacion: ACEPTADA");
        } else {
            System.out.println("Resultado de mutacion: DESCARTADA");
        }
        System.out.println();
        tiempoImpresionNanos += System.nanoTime() - inicio;
    }

    /**
     * Imprime el mejor fitness alcanzado en una generacion.
     */
    private void imprimirMejorGeneracion(Cromosoma mejor) {
        long inicio = System.nanoTime();
        System.out.println("Mejor fitness de la generacion: " + mejor.obtenerFitness());
        System.out.println();
        tiempoImpresionNanos += System.nanoTime() - inicio;
    }

    /**
     * Imprime los tres mejores cromosomas unicos observados.
     */
    private void imprimirMejoresTres() {
        long inicio = System.nanoTime();
        List<Cromosoma> mejores = obtenerMejoresTres();
        System.out.println("========== MEJORES 3 POBLACIONES ==========");

        for (int indice = 0; indice < mejores.size(); indice++) {
            Cromosoma cromosoma = mejores.get(indice);
            System.out.println("Poblacion #" + (indice + 1));
            System.out.println("Fitness: " + cromosoma.obtenerFitness());
            System.out.println(cromosoma.describir());
            System.out.println();
        }
        tiempoImpresionNanos += System.nanoTime() - inicio;
    }

    /**
     * Obtiene las metricas de la ultima ejecucion.
     *
     * @return metricas geneticas
     */
    public MetricasGenetico obtenerMetricas() {
        return metricas;
    }
}
