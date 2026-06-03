package fuerzaBruta;

import generador.GeneradorPiezas;
import modelo.Pieza;
import modelo.Rompecabezas;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resuelve un rompecabezas cuadrado usando fuerza bruta con backtracking y poda.
 */
public class FuerzaBrutaSolver {
    private static final long LIMITE_POR_DEFECTO = 250_000L;
    private static final int BYTES_REFERENCIA = 8;
    private static final int BYTES_INT = 4;
    private static final int BYTES_LONG = 8;
    private static final int BYTES_BOOLEAN = 1;
    private static final int LINEAS_CODIGO_ALGORITMO = 204;

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
        long inicio = System.nanoTime();

        Rompecabezas tablero = new Rompecabezas(dimension);
        mejorParcial = tablero.copiar();
        List<Pieza> disponibles = new ArrayList<>(piezas);
        metricas.asignaciones += 3;

        boolean encontrado = backtracking(tablero, disponibles, 0, dimension);

        long fin = System.nanoTime();
        metricas.tiempoMilisegundos = (fin - inicio) / 1_000_000.0;
        metricas.solucionEncontrada = encontrado;
        metricas.lineasEjecutadas = metricas.asignaciones + metricas.comparaciones;
        metricas.memoriaBytes = estimarMemoriaConsumida(piezas.size(), dimension);
        metricas.lineasCodigoAlgoritmo = LINEAS_CODIGO_ALGORITMO;

        if (encontrado) {
            return solucion;
        }

        return mejorParcial;
    }

    /**
     * Explora candidatos en orden fila-columna hasta encontrar solucion o llegar al limite.
     */
    private boolean backtracking(Rompecabezas tablero, List<Pieza> disponibles, int posicion, int dimension) {
        if (metricas.limiteAlcanzado) {
            return false;
        }

        actualizarMejorParcial(posicion, tablero);

        if (compararValores(posicion, dimension * dimension)) {
            if (tablero.verificarBordesAdyacentes() && tablero.verificarBordesExternos(GeneradorPiezas.VALOR_EXTERIOR)) {
                solucion = tablero.copiar();
                metricas.asignaciones++;
                return true;
            }
            metricas.podasRealizadas++;
            return false;
        }

        int fila = posicion / dimension;
        int columna = posicion % dimension;
        metricas.asignaciones += 2;

        for (int i = 0; i < disponibles.size(); i++) {
            Pieza piezaOriginal = disponibles.get(i);
            List<Pieza> orientaciones = obtenerOrientaciones(piezaOriginal);
            metricas.asignaciones += 2;

            for (Pieza pieza : orientaciones) {
                if (!registrarAlternativa()) {
                    return false;
                }

                if (esCandidatoValido(tablero, fila, columna, pieza, dimension)) {
                    tablero.colocarPieza(fila, columna, pieza);
                    metricas.asignaciones++;

                    Pieza removida = disponibles.remove(i);
                    metricas.asignaciones++;

                    if (backtracking(tablero, disponibles, posicion + 1, dimension)) {
                        return true;
                    }
                    if (metricas.limiteAlcanzado) {
                        return false;
                    }

                    disponibles.add(i, removida);
                    tablero.removerPieza(fila, columna);
                    metricas.asignaciones += 2;
                } else {
                    metricas.podasRealizadas++;
                }
            }
        }

        return false;
    }

    /**
     * Verifica bordes externos e internos ya fijados por el recorrido row-major.
     */
    private boolean esCandidatoValido(Rompecabezas tablero, int fila, int columna, Pieza pieza, int dimension) {
        if (fila == 0 && !compararValores(pieza.obtenerArriba(), GeneradorPiezas.VALOR_EXTERIOR)) {
            return false;
        }
        if (columna == 0 && !compararValores(pieza.obtenerIzquierda(), GeneradorPiezas.VALOR_EXTERIOR)) {
            return false;
        }
        if (fila == dimension - 1 && !compararValores(pieza.obtenerAbajo(), GeneradorPiezas.VALOR_EXTERIOR)) {
            return false;
        }
        if (columna == dimension - 1 && !compararValores(pieza.obtenerDerecha(), GeneradorPiezas.VALOR_EXTERIOR)) {
            return false;
        }

        if (fila > 0) {
            Pieza superior = tablero.obtenerPieza(fila - 1, columna);
            metricas.asignaciones++;
            if (superior != null && !compararValores(pieza.obtenerArriba(), superior.obtenerAbajo())) {
                return false;
            }
        }

        if (columna > 0) {
            Pieza izquierda = tablero.obtenerPieza(fila, columna - 1);
            metricas.asignaciones++;
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

    private boolean registrarAlternativa() {
        if (metricas.alternativasEvaluadas >= limiteAlternativas) {
            metricas.limiteAlcanzado = true;
            return false;
        }
        metricas.alternativasEvaluadas++;
        return true;
    }

    private boolean compararValores(int primero, int segundo) {
        metricas.comparaciones++;
        return primero == segundo;
    }

    private void actualizarMejorParcial(int profundidad, Rompecabezas tablero) {
        if (profundidad > metricas.profundidadMaxima) {
            metricas.profundidadMaxima = profundidad;
            mejorParcial = tablero.copiar();
            metricas.asignaciones++;
        }
    }

    private long estimarMemoriaConsumida(int cantidadPiezas, int dimension) {
        long memoriaMetricas = 8L * BYTES_LONG + 2L * BYTES_BOOLEAN + BYTES_INT;
        long memoriaTablero = (long) dimension * dimension * BYTES_REFERENCIA;
        long memoriaListas = (long) cantidadPiezas * BYTES_REFERENCIA * (permitirRotacion ? 5 : 2);
        long memoriaPiezas = (long) cantidadPiezas * (5L * BYTES_INT + BYTES_REFERENCIA);
        long memoriaSolver = 4L * BYTES_REFERENCIA + BYTES_BOOLEAN + BYTES_LONG;
        return memoriaMetricas + memoriaTablero + memoriaListas + memoriaPiezas + memoriaSolver;
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
