package genetico;

/**
 * Almacena las mediciones empiricas del algoritmo genetico.
 *
 * @since 2026-06-08
 * @version 2026-06-09
 */
public class MetricasGenetico {
    long comparaciones;
    long asignaciones;
    long lineasEjecutadas;
    double tiempoMilisegundos;
    long memoriaBytes;
    int lineasCodigoAlgoritmo;

    int generacionesEjecutadas;
    double mejorFitness;
    double peorFitness;
    double promedioFitness;

    int cantidadPoblacion;
    int cantidadHijos;
    int cantidadMutaciones;
    int mutacionesIntentadas;
    int mutacionesDescartadas;
    int cantidadCruces;

    boolean solucionEncontrada;

    /**
     * Registra una comparacion explicita del algoritmo.
     *
     * @param resultado valor producido por la condicion
     * @return el mismo resultado recibido
     */
    boolean registrarComparacion(boolean resultado) {
        comparaciones++;
        return resultado;
    }

    /** @return comparaciones realizadas */
    public long obtenerComparaciones() {
        return comparaciones;
    }

    /** @return asignaciones realizadas */
    public long obtenerAsignaciones() {
        return asignaciones;
    }

    /** @return suma de asignaciones y comparaciones */
    public long obtenerLineasEjecutadas() {
        return lineasEjecutadas;
    }

    /** @return tiempo de computo en milisegundos */
    public double obtenerTiempoMilisegundos() {
        return tiempoMilisegundos;
    }

    /** @return memoria calculada en bytes */
    public long obtenerMemoriaBytes() {
        return memoriaBytes;
    }

    /** @return lineas de codigo del modulo sin comentarios */
    public int obtenerLineasCodigoAlgoritmo() {
        return lineasCodigoAlgoritmo;
    }

    /** @return generaciones ejecutadas */
    public int obtenerGeneracionesEjecutadas() {
        return generacionesEjecutadas;
    }

    /** @return mejor fitness de la poblacion final */
    public double obtenerMejorFitness() {
        return mejorFitness;
    }

    /** @return peor fitness de la poblacion final */
    public double obtenerPeorFitness() {
        return peorFitness;
    }

    /** @return promedio de fitness de la poblacion final */
    public double obtenerPromedioFitness() {
        return promedioFitness;
    }

    /** @return cantidad de individuos de la poblacion */
    public int obtenerCantidadPoblacion() {
        return cantidadPoblacion;
    }

    /** @return cantidad de hijos producidos por generacion */
    public int obtenerCantidadHijos() {
        return cantidadHijos;
    }

    /** @return mutaciones aceptadas */
    public int obtenerCantidadMutaciones() {
        return cantidadMutaciones;
    }

    /** @return mutaciones intentadas */
    public int obtenerMutacionesIntentadas() {
        return mutacionesIntentadas;
    }

    /** @return mutaciones descartadas */
    public int obtenerMutacionesDescartadas() {
        return mutacionesDescartadas;
    }

    /** @return cruces realizados */
    public int obtenerCantidadCruces() {
        return cantidadCruces;
    }

    /** @return true si se alcanzo el fitness maximo */
    public boolean seEncontroSolucion() {
        return solucionEncontrada;
    }
}
