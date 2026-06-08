package genetico;

import generador.GeneradorPiezas;
import modelo.Pieza;
import modelo.Rompecabezas;

/**
 * Calcula la aptitud de un cromosoma segun la cantidad de bordes que coinciden.
 */
public class FitnessCalculator {

    /**
     * Calcula el fitness de un cromosoma.
     *
     * @param cromosoma cromosoma por evaluar
     * @param metricas objeto de metricas del algoritmo genetico
     * @return puntuacion obtenida
     */
    public double calcularFitness(Cromosoma cromosoma, MetricasGenetico metricas) {
        Rompecabezas tablero = cromosoma.convertirARompecabezas();
        int dimension = cromosoma.obtenerDimension();
        double fitness = 0;

        metricas.asignaciones += 3;

        for (int fila = 0; fila < dimension; fila++) {
            for (int columna = 0; columna < dimension; columna++) {
                Pieza actual = tablero.obtenerPieza(fila, columna);
                metricas.asignaciones++;

                if (columna < dimension - 1) {
                    Pieza derecha = tablero.obtenerPieza(fila, columna + 1);
                    metricas.asignaciones++;
                    metricas.comparaciones++;

                    if (actual.obtenerDerecha() == derecha.obtenerIzquierda()) {
                        fitness++;
                        metricas.asignaciones++;
                    }
                }

                if (fila < dimension - 1) {
                    Pieza abajo = tablero.obtenerPieza(fila + 1, columna);
                    metricas.asignaciones++;
                    metricas.comparaciones++;

                    if (actual.obtenerAbajo() == abajo.obtenerArriba()) {
                        fitness++;
                        metricas.asignaciones++;
                    }
                }

                if (fila == 0) {
                    metricas.comparaciones++;
                    if (actual.obtenerArriba() == GeneradorPiezas.VALOR_EXTERIOR) {
                        fitness++;
                        metricas.asignaciones++;
                    }
                }

                if (fila == dimension - 1) {
                    metricas.comparaciones++;
                    if (actual.obtenerAbajo() == GeneradorPiezas.VALOR_EXTERIOR) {
                        fitness++;
                        metricas.asignaciones++;
                    }
                }

                if (columna == 0) {
                    metricas.comparaciones++;
                    if (actual.obtenerIzquierda() == GeneradorPiezas.VALOR_EXTERIOR) {
                        fitness++;
                        metricas.asignaciones++;
                    }
                }

                if (columna == dimension - 1) {
                    metricas.comparaciones++;
                    if (actual.obtenerDerecha() == GeneradorPiezas.VALOR_EXTERIOR) {
                        fitness++;
                        metricas.asignaciones++;
                    }
                }
            }
        }

        cromosoma.establecerFitness(fitness);
        metricas.asignaciones++;

        return fitness;
    }

    /**
     * Calcula el fitness maximo posible para una dimension.
     *
     * @param dimension dimension del tablero
     * @return cantidad maxima de coincidencias posibles
     */
    public int calcularFitnessMaximo(int dimension) {
        int bordesInternosHorizontales = dimension * (dimension - 1);
        int bordesInternosVerticales = dimension * (dimension - 1);
        int bordesExternos = dimension * 4;

        return bordesInternosHorizontales + bordesInternosVerticales + bordesExternos;
    }
}