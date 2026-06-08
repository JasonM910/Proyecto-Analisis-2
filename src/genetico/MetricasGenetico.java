package genetico;

/**
 * Almacena las mediciones empiricas del algoritmo genetico.
 */
public class MetricasGenetico {
    public long comparaciones;
    public long asignaciones;
    public long lineasEjecutadas;
    public double tiempoMilisegundos;
    public long memoriaBytes;
    public int lineasCodigoAlgoritmo;

    public int generacionesEjecutadas;
    public double mejorFitness;
    public double peorFitness;
    public double promedioFitness;

    public int cantidadPoblacion;
    public int cantidadHijos;
    public int cantidadMutaciones;
    public int cantidadCruces;

    public boolean solucionEncontrada;
}