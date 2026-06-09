package modelo;

/**
 * Representa los cuatro lados de una pieza cuadrada.
 *
 * @since 2026-05-29
 * @version 2026-06-09
 */
public enum Direccion {
    ARRIBA,
    DERECHA,
    ABAJO,
    IZQUIERDA;

    /**
     * Obtiene el lado opuesto de la direccion actual.
     *
     * @return direccion opuesta
     */
    public Direccion obtenerOpuesta() {
        switch (this) {
            case ARRIBA:
                return ABAJO;
            case DERECHA:
                return IZQUIERDA;
            case ABAJO:
                return ARRIBA;
            case IZQUIERDA:
                return DERECHA;
            default:
                throw new IllegalStateException("Direccion no soportada: " + this);
        }
    }
}
