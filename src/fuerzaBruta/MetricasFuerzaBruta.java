package fuerzaBruta;

/**
 * Almacena las mediciones empiricas reportadas por el algoritmo de fuerza bruta.
 *
 * @since 2026-06-08
 * @version 2026-06-09
 */
public class MetricasFuerzaBruta {
    long comparaciones;
    long asignaciones;
    long lineasEjecutadas;
    long alternativasEvaluadas;
    long podasRealizadas;
    double tiempoMilisegundos;
    long memoriaBytes;
    int lineasCodigoAlgoritmo;
    boolean solucionEncontrada;
    boolean limiteAlcanzado;
    int profundidadMaxima;
    long limiteAlternativas;

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

    /** @return alternativas probadas */
    public long obtenerAlternativasEvaluadas() {
        return alternativasEvaluadas;
    }

    /** @return candidatos descartados por poda */
    public long obtenerPodasRealizadas() {
        return podasRealizadas;
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

    /** @return true si se encontro una solucion completa */
    public boolean seEncontroSolucion() {
        return solucionEncontrada;
    }

    /** @return true si finalizo por el corte experimental */
    public boolean seAlcanzoLimite() {
        return limiteAlcanzado;
    }

    /** @return mayor cantidad de piezas colocadas */
    public int obtenerProfundidadMaxima() {
        return profundidadMaxima;
    }

    /** @return limite configurado de alternativas */
    public long obtenerLimiteAlternativas() {
        return limiteAlternativas;
    }
}
