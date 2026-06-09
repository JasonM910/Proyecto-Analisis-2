package genetico;

/**
 * Describe una mutacion intentada sobre un cromosoma duplicado.
 *
 * @since 2026-06-08
 * @version 2026-06-09
 */
public class ResultadoMutacion {
    private final Cromosoma original;
    private final Cromosoma resultado;
    private final int indiceUno;
    private final int indiceDos;
    private final boolean aceptada;

    /**
     * Crea el resultado de una mutacion.
     *
     * @param original cromosoma antes de mutar
     * @param resultado cromosoma resultante
     * @param indiceUno primera posicion intercambiada
     * @param indiceDos segunda posicion intercambiada
     * @param aceptada indica si la mutacion fue conservada
     * @param metricas mediciones de la ejecucion
     */
    public ResultadoMutacion(
            Cromosoma original,
            Cromosoma resultado,
            int indiceUno,
            int indiceDos,
            boolean aceptada,
            MetricasGenetico metricas
    ) {
        this.original = original.copiar(metricas);
        this.resultado = resultado.copiar(metricas);
        this.indiceUno = indiceUno;
        this.indiceDos = indiceDos;
        this.aceptada = aceptada;
        metricas.asignaciones += 5;
    }

    /**
     * Obtiene el cromosoma previo a la mutacion.
     *
     * @return copia del original
     */
    public Cromosoma obtenerOriginal() {
        return original.copiar();
    }

    /**
     * Obtiene el cromosoma producido por la mutacion.
     *
     * @return copia del resultado
     */
    public Cromosoma obtenerResultado() {
        return resultado.copiar();
    }

    /**
     * Obtiene la primera posicion intercambiada.
     *
     * @return primer indice o -1 si se descarto
     */
    public int obtenerIndiceUno() {
        return indiceUno;
    }

    /**
     * Obtiene la segunda posicion intercambiada.
     *
     * @return segundo indice o -1 si se descarto
     */
    public int obtenerIndiceDos() {
        return indiceDos;
    }

    /**
     * Indica si la mutacion mejoro el fitness y fue conservada.
     *
     * @return true cuando la mutacion fue aceptada
     */
    public boolean fueAceptada() {
        return aceptada;
    }
}
