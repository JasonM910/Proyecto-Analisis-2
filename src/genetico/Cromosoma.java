package genetico;

import modelo.Pieza;
import modelo.Rompecabezas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representa un cromosoma del algoritmo genetico.
 * Cada cromosoma contiene una posible distribucion de piezas.
 *
 * @since 2026-06-08
 * @version 2026-06-09
 */
public class Cromosoma {
    private final List<Pieza> piezas;
    private final int dimension;
    private double fitness;

    /**
     * Crea un cromosoma valido con exactamente n^2 piezas no repetidas.
     *
     * @param piezas piezas que forman la solucion candidata
     * @param dimension dimension del rompecabezas
     */
    public Cromosoma(List<Pieza> piezas, int dimension) {
        this(piezas, dimension, null);
    }

    /**
     * Crea un cromosoma y registra el trabajo de validacion y copia.
     *
     * @param piezas piezas que forman la solucion candidata
     * @param dimension dimension del rompecabezas
     * @param metricas mediciones de la ejecucion, o null para no registrar
     */
    Cromosoma(List<Pieza> piezas, int dimension, MetricasGenetico metricas) {
        validarPiezas(piezas, dimension, metricas);
        this.piezas = new ArrayList<>(piezas);
        this.dimension = dimension;
        this.fitness = 0.0;
        if (metricas != null) {
            metricas.asignaciones += piezas.size() + 3L;
        }
    }

    /**
     * Obtiene una copia de las piezas del cromosoma.
     *
     * @return piezas en el orden actual
     */
    public List<Pieza> obtenerPiezas() {
        return new ArrayList<>(piezas);
    }

    /**
     * Obtiene la dimension del tablero representado.
     *
     * @return dimension del rompecabezas
     */
    public int obtenerDimension() {
        return dimension;
    }

    /**
     * Obtiene la aptitud calculada.
     *
     * @return fitness del cromosoma
     */
    public double obtenerFitness() {
        return fitness;
    }

    /**
     * Almacena la aptitud calculada.
     *
     * @param fitness nueva aptitud
     */
    public void establecerFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Obtiene la pieza de una posicion lineal.
     *
     * @param indice posicion dentro del cromosoma
     * @return pieza ubicada en el indice
     */
    public Pieza obtenerPiezaEnIndice(int indice) {
        return piezas.get(indice);
    }

    /**
     * Obtiene la cantidad de piezas.
     *
     * @return longitud del cromosoma
     */
    public int obtenerCantidadPiezas() {
        return piezas.size();
    }

    /**
     * Convierte el cromosoma en una matriz de rompecabezas.
     *
     * @return tablero representado por el cromosoma
     */
    public Rompecabezas convertirARompecabezas() {
        return convertirARompecabezas(null);
    }

    /**
     * Convierte el cromosoma en tablero y registra sus ciclos.
     *
     * @param metricas mediciones de la ejecucion, o null para no registrar
     * @return tablero representado por el cromosoma
     */
    Rompecabezas convertirARompecabezas(MetricasGenetico metricas) {
        Rompecabezas rompecabezas = new Rompecabezas(dimension);
        int indice = 0;
        int fila = 0;
        registrarAsignaciones(metricas, 3);

        while (registrarComparacion(fila < dimension, metricas)) {
            int columna = 0;
            registrarAsignaciones(metricas, 1);
            while (registrarComparacion(columna < dimension, metricas)) {
                rompecabezas.colocarPieza(fila, columna, piezas.get(indice));
                indice++;
                columna++;
                registrarAsignaciones(metricas, 3);
            }
            fila++;
            registrarAsignaciones(metricas, 1);
        }

        return rompecabezas;
    }

    /**
     * Intercambia dos genes sin alterar el conjunto de piezas.
     *
     * @param indiceUno primera posicion
     * @param indiceDos segunda posicion
     */
    public void intercambiarPiezas(int indiceUno, int indiceDos) {
        Collections.swap(piezas, indiceUno, indiceDos);
    }

    /**
     * Describe el orden y los bordes de todas las piezas.
     *
     * @return descripcion del cromosoma
     */
    public String describir() {
        StringBuilder texto = new StringBuilder();

        for (Pieza pieza : piezas) {
            texto.append(pieza.describir()).append(" ");
        }

        return texto.toString();
    }

    /**
     * Crea una copia independiente del cromosoma.
     *
     * @return copia con el mismo fitness
     */
    public Cromosoma copiar() {
        return copiar(null);
    }

    /**
     * Crea una copia y registra la validacion y las asignaciones asociadas.
     *
     * @param metricas mediciones de la ejecucion, o null para no registrar
     * @return copia con el mismo fitness
     */
    Cromosoma copiar(MetricasGenetico metricas) {
        Cromosoma copia = new Cromosoma(piezas, dimension, metricas);
        copia.establecerFitness(fitness);
        registrarAsignaciones(metricas, 2);
        return copia;
    }

    /**
     * Construye una firma que identifica el orden y orientacion de las piezas.
     *
     * @return firma unica del cromosoma
     */
    public String obtenerFirma() {
        return obtenerFirma(null);
    }

    /**
     * Construye la firma y registra el recorrido de genes.
     *
     * @param metricas mediciones de la ejecucion, o null para no registrar
     * @return firma unica del cromosoma
     */
    String obtenerFirma(MetricasGenetico metricas) {
        StringBuilder firma = new StringBuilder();
        int indice = 0;
        registrarAsignaciones(metricas, 2);
        while (registrarComparacion(indice < piezas.size(), metricas)) {
            Pieza pieza = piezas.get(indice);
            firma.append(pieza.obtenerId())
                    .append('@')
                    .append(pieza.obtenerRotacionGrados())
                    .append(';');
            indice++;
            registrarAsignaciones(metricas, 6);
        }
        return firma.toString();
    }

    /**
     * Comprueba longitud, valores nulos e identificadores repetidos.
     *
     * @param piezas piezas que se validaran
     * @param dimension dimension esperada
     * @param metricas mediciones de la ejecucion, o null para no registrar
     */
    private void validarPiezas(
            List<Pieza> piezas,
            int dimension,
            MetricasGenetico metricas
    ) {
        boolean listaNula = registrarComparacion(piezas == null, metricas);
        boolean cantidadInvalida = !listaNula
                && registrarComparacion(piezas.size() != dimension * dimension, metricas);
        registrarAsignaciones(metricas, 2);
        if (listaNula || cantidadInvalida) {
            throw new IllegalArgumentException("El cromosoma debe contener exactamente n^2 piezas.");
        }

        Set<Integer> ids = new HashSet<>();
        int indice = 0;
        registrarAsignaciones(metricas, 2);
        while (registrarComparacion(indice < piezas.size(), metricas)) {
            Pieza pieza = piezas.get(indice);
            boolean piezaNula = registrarComparacion(pieza == null, metricas);
            boolean repetida = !piezaNula
                    && registrarComparacion(!ids.add(pieza.obtenerId()), metricas);
            registrarAsignaciones(metricas, 3);
            if (piezaNula || repetida) {
                throw new IllegalArgumentException("El cromosoma contiene piezas nulas o repetidas.");
            }
            indice++;
            registrarAsignaciones(metricas, 1);
        }
    }

    /**
     * Registra una comparacion cuando existen metricas activas.
     *
     * @param resultado valor de la condicion
     * @param metricas mediciones de la ejecucion, o null
     * @return el mismo resultado recibido
     */
    private boolean registrarComparacion(boolean resultado, MetricasGenetico metricas) {
        if (metricas != null) {
            metricas.comparaciones++;
        }
        return resultado;
    }

    /**
     * Suma asignaciones cuando existen metricas activas.
     *
     * @param metricas mediciones de la ejecucion, o null
     * @param cantidad operaciones que se registraran
     */
    private void registrarAsignaciones(MetricasGenetico metricas, long cantidad) {
        if (metricas != null) {
            metricas.asignaciones += cantidad;
        }
    }
}
