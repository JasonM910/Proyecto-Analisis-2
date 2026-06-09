package genetico;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Selecciona los cromosomas mas aptos de la poblacion.
 *
 * @since 2026-06-08
 * @version 2026-06-09
 */
public class Seleccion {

    /**
     * Ordena la poblacion y registra las comparaciones del ordenamiento.
     *
     * @param poblacion lista de cromosomas
     * @param metricas mediciones de la ejecucion
     * @return poblacion ordenada
     */
    public List<Cromosoma> ordenarPorFitness(List<Cromosoma> poblacion, MetricasGenetico metricas) {
        List<Cromosoma> copia = new ArrayList<>(poblacion);
        if (metricas != null) {
            metricas.asignaciones += poblacion.size() + 1L;
        }

        Comparator<Cromosoma> comparador = new Comparator<Cromosoma>() {
            /**
             * Compara dos cromosomas por fitness descendente.
             *
             * @param primero primer cromosoma
             * @param segundo segundo cromosoma
             * @return resultado de la comparacion
             */
            @Override
            public int compare(Cromosoma primero, Cromosoma segundo) {
                if (metricas != null) {
                    metricas.comparaciones++;
                }
                return Double.compare(segundo.obtenerFitness(), primero.obtenerFitness());
            }
        };
        if (metricas != null) {
            metricas.asignaciones++;
        }
        copia.sort(comparador);

        return copia;
    }

    /**
     * Selecciona los individuos mas aptos y conserva el tamano de la poblacion.
     *
     * @param poblacionTotal padres e hijos juntos
     * @param cantidadSobrevivientes cantidad fija de poblacion
     * @param metricas mediciones de la ejecucion
     * @return nueva poblacion con la cantidad solicitada
     */
    public List<Cromosoma> seleccionarSobrevivientes(
            List<Cromosoma> poblacionTotal,
            int cantidadSobrevivientes,
            MetricasGenetico metricas
    ) {
        List<Cromosoma> ordenados = ordenarPorFitness(poblacionTotal, metricas);
        List<Cromosoma> sobrevivientes = new ArrayList<>();
        int indice = 0;
        if (metricas != null) {
            metricas.asignaciones += 3;
        }

        while (metricas.registrarComparacion(indice < cantidadSobrevivientes)) {
            Cromosoma cromosoma = ordenados.get(indice);
            sobrevivientes.add(cromosoma.copiar(metricas));
            indice++;
            metricas.asignaciones += 3;
        }

        return sobrevivientes;
    }
}
