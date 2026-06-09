package genetico;

import modelo.Pieza;

/**
 * Calcula la aptitud de un cromosoma segun la cantidad de bordes que coinciden.
 *
 * @since 2026-06-08
 * @version 2026-06-09
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
        int dimension = cromosoma.obtenerDimension();
        double fitness = 0;
        metricas.asignaciones += 2;

        int fila = 0;
        metricas.asignaciones++;
        while (metricas.registrarComparacion(fila < dimension)) {
            int columna = 0;
            metricas.asignaciones++;
            while (metricas.registrarComparacion(columna < dimension)) {
                Pieza actual = cromosoma.obtenerPiezaEnIndice(fila * dimension + columna);
                metricas.asignaciones++;

                if (metricas.registrarComparacion(columna < dimension - 1)) {
                    Pieza derecha = cromosoma.obtenerPiezaEnIndice(fila * dimension + columna + 1);
                    metricas.asignaciones++;
                    if (metricas.registrarComparacion(
                            actual.obtenerDerecha() == derecha.obtenerIzquierda()
                    )) {
                        fitness++;
                        metricas.asignaciones++;
                    }
                }

                if (metricas.registrarComparacion(fila < dimension - 1)) {
                    Pieza abajo = cromosoma.obtenerPiezaEnIndice((fila + 1) * dimension + columna);
                    metricas.asignaciones++;
                    if (metricas.registrarComparacion(
                            actual.obtenerAbajo() == abajo.obtenerArriba()
                    )) {
                        fitness++;
                        metricas.asignaciones++;
                    }
                }

                columna++;
                metricas.asignaciones++;
            }
            fila++;
            metricas.asignaciones++;
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

        return bordesInternosHorizontales + bordesInternosVerticales;
    }
}
