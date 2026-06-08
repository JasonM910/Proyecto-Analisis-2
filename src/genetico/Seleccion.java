package genetico;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Selecciona los cromosomas mas aptos de la poblacion.
 */
public class Seleccion {

    /**
     * Ordena la poblacion de mayor a menor fitness.
     *
     * @param poblacion lista de cromosomas
     * @return poblacion ordenada
     */
    public List<Cromosoma> ordenarPorFitness(List<Cromosoma> poblacion) {
        List<Cromosoma> copia = new ArrayList<>(poblacion);

        copia.sort(Comparator.comparingDouble(Cromosoma::obtenerFitness).reversed());

        return copia;
    }

    /**
     * Selecciona los mejores cromosomas como padres.
     *
     * @param poblacion lista de cromosomas evaluados
     * @param cantidadPadres cantidad de padres requeridos
     * @return lista de padres seleccionados
     */
    public List<Cromosoma> seleccionarMejores(List<Cromosoma> poblacion, int cantidadPadres) {
        List<Cromosoma> ordenados = ordenarPorFitness(poblacion);
        List<Cromosoma> seleccionados = new ArrayList<>();

        int limite = Math.min(cantidadPadres, ordenados.size());

        for (int indice = 0; indice < limite; indice++) {
            seleccionados.add(ordenados.get(indice).copiar());
        }

        return seleccionados;
    }

    /**
     * Selecciona los mejores cromosomas para sobrevivir a la siguiente generacion.
     *
     * @param poblacionTotal padres e hijos juntos
     * @param cantidadSobrevivientes cantidad fija de poblacion
     * @return nueva poblacion
     */
    public List<Cromosoma> seleccionarSobrevivientes(List<Cromosoma> poblacionTotal, int cantidadSobrevivientes) {
        List<Cromosoma> ordenados = ordenarPorFitness(poblacionTotal);
        List<Cromosoma> sobrevivientes = new ArrayList<>();

        int limite = Math.min(cantidadSobrevivientes, ordenados.size());

        for (int indice = 0; indice < limite; indice++) {
            sobrevivientes.add(ordenados.get(indice).copiar());
        }

        return sobrevivientes;
    }
}