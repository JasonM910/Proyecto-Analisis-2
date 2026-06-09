package fuerzaBruta;

import modelo.Pieza;
import modelo.Rompecabezas;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resuelve un rompecabezas cuadrado usando fuerza bruta con backtracking y poda.
 *
 * @since 2026-06-08
 * @version 2026-06-09
 */
public class FuerzaBrutaSolver {
    private static final long LIMITE_POR_DEFECTO = 250_000L;
    private static final int BYTES_REFERENCIA = 8;
    private static final int BYTES_INT = 4;
    private static final int BYTES_LONG = 8;
    private static final int BYTES_BOOLEAN = 1;
    private static final int BYTES_CHAR = 2;
    private static final int LINEAS_CODIGO_ALGORITMO = 256;

    private Rompecabezas solucion;
    private Rompecabezas mejorParcial;
    private MetricasFuerzaBruta metricas;
    private final boolean permitirRotacion;
    private final long limiteAlternativas;

    /**
     * Crea el solucionador con un limite de alternativas por defecto.
     *
     * @param permitirRotacion indica si se deben probar rotaciones de cada pieza
     */
    public FuerzaBrutaSolver(boolean permitirRotacion) {
        this(permitirRotacion, LIMITE_POR_DEFECTO);
    }

    /**
     * Crea el solucionador con un limite maximo de alternativas evaluadas.
     *
     * @param permitirRotacion indica si se deben probar rotaciones de cada pieza
     * @param limiteAlternativas cantidad maxima de alternativas que se evaluaran
     */
    public FuerzaBrutaSolver(boolean permitirRotacion, long limiteAlternativas) {
        this.permitirRotacion = permitirRotacion;
        this.limiteAlternativas = limiteAlternativas <= 0 ? LIMITE_POR_DEFECTO : limiteAlternativas;
        this.metricas = new MetricasFuerzaBruta();
    }

    /**
     * Intenta resolver el rompecabezas con las piezas recibidas.
     *
     * @param piezas piezas disponibles en orden desordenado
     * @param dimension dimension del rompecabezas
     * @return solucion completa o mejor solucion parcial encontrada
     */
    public Rompecabezas resolver(List<Pieza> piezas, int dimension) {
        metricas = new MetricasFuerzaBruta();
        metricas.limiteAlternativas = limiteAlternativas;
        validarEntrada(piezas, dimension);
        long inicio = System.nanoTime();

        Rompecabezas tablero = new Rompecabezas(dimension);
        mejorParcial = tablero.copiar();
        List<Pieza> disponibles = new ArrayList<>(piezas);
        metricas.asignaciones += 3;

        boolean encontrado = backtracking(tablero, disponibles, 0, dimension);

        long fin = System.nanoTime();
        metricas.tiempoMilisegundos = (fin - inicio) / 1_000_000.0;
        metricas.solucionEncontrada = encontrado;
        metricas.memoriaBytes = calcularMemoriaConsumida(piezas.size(), dimension);
        metricas.lineasCodigoAlgoritmo = LINEAS_CODIGO_ALGORITMO;

        metricas.comparaciones++;
        metricas.lineasEjecutadas = metricas.asignaciones + metricas.comparaciones;
        if (encontrado) {
            return solucion;
        }

        return mejorParcial;
    }

    /**
     * Explora candidatos en orden fila-columna hasta encontrar solucion o llegar al limite.
     */
    private boolean backtracking(Rompecabezas tablero, List<Pieza> disponibles, int posicion, int dimension) {
        metricas.comparaciones++;
        if (metricas.limiteAlcanzado) {
            return false;
        }

        actualizarMejorParcial(posicion, tablero);

        if (compararValores(posicion, dimension * dimension)) {
            solucion = tablero.copiar();
            metricas.asignaciones++;
            return true;
        }

        int fila = posicion / dimension;
        int columna = posicion % dimension;
        metricas.asignaciones += 2;

        int i = 0;
        metricas.asignaciones++;
        while (registrarComparacionIndice(i, disponibles.size())) {
            Pieza piezaOriginal = disponibles.get(i);
            List<Pieza> orientaciones = obtenerOrientaciones(piezaOriginal);
            metricas.asignaciones += 2;

            int indiceOrientacion = 0;
            metricas.asignaciones++;
            while (registrarComparacionIndice(indiceOrientacion, orientaciones.size())) {
                Pieza pieza = orientaciones.get(indiceOrientacion);
                metricas.asignaciones++;
                if (!registrarAlternativa()) {
                    return false;
                }

                metricas.comparaciones++;
                if (esCandidatoValido(tablero, fila, columna, pieza)) {
                    tablero.colocarPieza(fila, columna, pieza);
                    metricas.asignaciones++;

                    Pieza removida = disponibles.remove(i);
                    metricas.asignaciones++;

                    boolean solucionRama = backtracking(
                            tablero,
                            disponibles,
                            posicion + 1,
                            dimension
                    );
                    metricas.asignaciones++;
                    metricas.comparaciones++;
                    if (solucionRama) {
                        return true;
                    }
                    metricas.comparaciones++;
                    if (metricas.limiteAlcanzado) {
                        return false;
                    }

                    disponibles.add(i, removida);
                    tablero.removerPieza(fila, columna);
                    metricas.asignaciones += 2;
                } else {
                    metricas.podasRealizadas++;
                }
                indiceOrientacion++;
                metricas.asignaciones++;
            }
            i++;
            metricas.asignaciones++;
        }

        return false;
    }

    /**
     * Verifica los vecinos superior e izquierdo ya fijados por el recorrido.
     */
    private boolean esCandidatoValido(Rompecabezas tablero, int fila, int columna, Pieza pieza) {
        metricas.comparaciones++;
        if (fila > 0) {
            Pieza superior = tablero.obtenerPieza(fila - 1, columna);
            metricas.asignaciones++;
            metricas.comparaciones++;
            if (superior != null && !compararValores(pieza.obtenerArriba(), superior.obtenerAbajo())) {
                return false;
            }
        }

        metricas.comparaciones++;
        if (columna > 0) {
            Pieza izquierda = tablero.obtenerPieza(fila, columna - 1);
            metricas.asignaciones++;
            metricas.comparaciones++;
            if (izquierda != null && !compararValores(pieza.obtenerIzquierda(), izquierda.obtenerDerecha())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Construye las orientaciones que se probaran para una pieza.
     */
    private List<Pieza> obtenerOrientaciones(Pieza pieza) {
        Map<String, Pieza> orientacionesUnicas = new LinkedHashMap<>();

        orientacionesUnicas.put(pieza.obtenerClaveBordes(), pieza);
        metricas.asignaciones++;

        metricas.comparaciones++;
        if (permitirRotacion) {
            Pieza rotada90 = pieza.rotar90();
            Pieza rotada180 = pieza.rotar180();
            Pieza rotada270 = pieza.rotar270();
            orientacionesUnicas.put(rotada90.obtenerClaveBordes(), rotada90);
            orientacionesUnicas.put(rotada180.obtenerClaveBordes(), rotada180);
            orientacionesUnicas.put(rotada270.obtenerClaveBordes(), rotada270);
            metricas.asignaciones += 6;
        }

        return new ArrayList<>(orientacionesUnicas.values());
    }

    /**
     * Registra una alternativa antes de probarla y controla el limite.
     */
    private boolean registrarAlternativa() {
        metricas.comparaciones++;
        if (metricas.alternativasEvaluadas >= limiteAlternativas) {
            metricas.limiteAlcanzado = true;
            return false;
        }
        metricas.alternativasEvaluadas++;
        return true;
    }

    /**
     * Compara dos bordes y registra la operacion.
     */
    private boolean compararValores(int primero, int segundo) {
        metricas.comparaciones++;
        return primero == segundo;
    }

    /**
     * Conserva una copia del estado parcial mas profundo.
     */
    private void actualizarMejorParcial(int profundidad, Rompecabezas tablero) {
        metricas.comparaciones++;
        if (profundidad > metricas.profundidadMaxima) {
            metricas.profundidadMaxima = profundidad;
            mejorParcial = tablero.copiar();
            metricas.asignaciones++;
        }
    }

    /**
     * Registra la condicion de continuacion de un recorrido indexado.
     */
    private boolean registrarComparacionIndice(int indice, int limite) {
        metricas.comparaciones++;
        return indice < limite;
    }

    /**
     * Calcula manualmente la memoria de variables y estructuras principales.
     */
    private long calcularMemoriaConsumida(int cantidadPiezas, int dimension) {
        long memoriaMetricas = 7L * BYTES_LONG + 2L * BYTES_INT
                + 2L * BYTES_BOOLEAN + BYTES_LONG;
        long memoriaPiezas = (long) cantidadPiezas * 6L * BYTES_INT;
        long celdasTableros = (long) dimension * dimension * BYTES_REFERENCIA * 3L;
        long filasTableros = 3L * dimension * BYTES_REFERENCIA;
        long referenciasDisponibles = (long) cantidadPiezas * BYTES_REFERENCIA
                + 2L * BYTES_INT + BYTES_REFERENCIA;
        long entradasMapa = 4L * (4L * BYTES_REFERENCIA + BYTES_INT);
        long clavesBordes = 4L * (4L * BYTES_INT + 3L * BYTES_CHAR + BYTES_REFERENCIA);
        long memoriaOrientaciones = permitirRotacion
                ? 3L * 6L * BYTES_INT + entradasMapa + clavesBordes
                : BYTES_REFERENCIA + entradasMapa / 4L + clavesBordes / 4L;
        long memoriaPila = (long) (metricas.profundidadMaxima + 1)
                * (5L * BYTES_INT + 4L * BYTES_REFERENCIA + BYTES_BOOLEAN);
        long memoriaSolver = 3L * BYTES_REFERENCIA + BYTES_BOOLEAN + BYTES_LONG;
        return memoriaMetricas + memoriaPiezas + celdasTableros + filasTableros
                + referenciasDisponibles + memoriaOrientaciones
                + memoriaPila + memoriaSolver;
    }

    /**
     * Verifica la cantidad de piezas y la dimension recibidas.
     */
    private void validarEntrada(List<Pieza> piezas, int dimension) {
        metricas.comparaciones += 3;
        if (piezas == null || dimension < 2 || piezas.size() != dimension * dimension) {
            throw new IllegalArgumentException("Se requieren exactamente n^2 piezas y una dimension minima de 2.");
        }
    }

    /**
     * Obtiene las metricas generadas en la ultima ejecucion.
     *
     * @return metricas del algoritmo
     */
    public MetricasFuerzaBruta obtenerMetricas() {
        return metricas;
    }
}
