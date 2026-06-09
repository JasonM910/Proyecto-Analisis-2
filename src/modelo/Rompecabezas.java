package modelo;

/**
 * Representa el estado de un rompecabezas cuadrado de dimension n x n.
 */
public class Rompecabezas {
    private final int dimension;
    private final Pieza[][] piezas;

    /**
     * Crea un tablero vacio para la dimension indicada.
     *
     * @param dimension cantidad de filas y columnas
     */
    public Rompecabezas(int dimension) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("La dimension debe ser mayor que cero.");
        }
        this.dimension = dimension;
        this.piezas = new Pieza[dimension][dimension];
    }

    private Rompecabezas(int dimension, Pieza[][] piezas) {
        this.dimension = dimension;
        this.piezas = piezas;
    }

    /**
     * Obtiene la dimension del rompecabezas.
     *
     * @return dimension del tablero
     */
    public int obtenerDimension() {
        return dimension;
    }

    /**
     * Coloca una pieza en la posicion indicada.
     *
     * @param fila indice de fila
     * @param columna indice de columna
     * @param pieza pieza a colocar
     */
    public void colocarPieza(int fila, int columna, Pieza pieza) {
        validarPosicion(fila, columna);
        piezas[fila][columna] = pieza;
    }

    /**
     * Elimina la pieza ubicada en la posicion indicada.
     *
     * @param fila indice de fila
     * @param columna indice de columna
     */
    public void removerPieza(int fila, int columna) {
        validarPosicion(fila, columna);
        piezas[fila][columna] = null;
    }

    /**
     * Obtiene la pieza ubicada en la posicion indicada.
     *
     * @param fila indice de fila
     * @param columna indice de columna
     * @return pieza ubicada o null si la celda esta vacia
     */
    public Pieza obtenerPieza(int fila, int columna) {
        validarPosicion(fila, columna);
        return piezas[fila][columna];
    }

    /**
     * Verifica si una pieza respeta las adyacencias ya colocadas en una posicion.
     *
     * @param fila indice de fila
     * @param columna indice de columna
     * @param pieza pieza candidata
     * @return true si las adyacencias coinciden
     */
    public boolean esCompatibleEnPosicion(int fila, int columna, Pieza pieza) {
        validarPosicion(fila, columna);
        if (fila > 0 && piezas[fila - 1][columna] != null && !pieza.esCompatible(piezas[fila - 1][columna], Direccion.ARRIBA)) {
            return false;
        }
        if (columna > 0 && piezas[fila][columna - 1] != null && !pieza.esCompatible(piezas[fila][columna - 1], Direccion.IZQUIERDA)) {
            return false;
        }
        if (fila < dimension - 1 && piezas[fila + 1][columna] != null && !pieza.esCompatible(piezas[fila + 1][columna], Direccion.ABAJO)) {
            return false;
        }
        return columna >= dimension - 1 || piezas[fila][columna + 1] == null || pieza.esCompatible(piezas[fila][columna + 1], Direccion.DERECHA);
    }

    /**
     * Verifica todas las coincidencias entre bordes adyacentes.
     *
     * @return true si todas las adyacencias coinciden
     */
    public boolean verificarBordesAdyacentes() {
        for (int fila = 0; fila < dimension; fila++) {
            for (int columna = 0; columna < dimension; columna++) {
                Pieza actual = piezas[fila][columna];
                if (actual == null) {
                    return false;
                }
                if (columna < dimension - 1 && !actual.esCompatible(piezas[fila][columna + 1], Direccion.DERECHA)) {
                    return false;
                }
                if (fila < dimension - 1 && !actual.esCompatible(piezas[fila + 1][columna], Direccion.ABAJO)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Indica si todas las celdas tienen una pieza asignada.
     *
     * @return true si el tablero esta lleno
     */
    public boolean estaCompleto() {
        for (int fila = 0; fila < dimension; fila++) {
            for (int columna = 0; columna < dimension; columna++) {
                if (piezas[fila][columna] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Obtiene una copia de la matriz de piezas.
     *
     * @return copia superficial del estado actual
     */
    public Pieza[][] obtenerCopiaEstado() {
        Pieza[][] copia = new Pieza[dimension][dimension];
        for (int fila = 0; fila < dimension; fila++) {
            System.arraycopy(piezas[fila], 0, copia[fila], 0, dimension);
        }
        return copia;
    }

    /**
     * Crea una copia del rompecabezas actual.
     *
     * @return nuevo rompecabezas con el mismo estado
     */
    public Rompecabezas copiar() {
        return new Rompecabezas(dimension, obtenerCopiaEstado());
    }

    /**
     * Genera una vista compacta con identificadores y rotaciones.
     *
     * @return texto del tablero
     */
    public String aTextoCompacto() {
        StringBuilder texto = new StringBuilder();
        for (int fila = 0; fila < dimension; fila++) {
            for (int columna = 0; columna < dimension; columna++) {
                Pieza pieza = piezas[fila][columna];
                if (pieza == null) {
                    texto.append("   --   ");
                } else {
                    texto.append(String.format("P%03d@%03d", pieza.obtenerId(), pieza.obtenerRotacionGrados()));
                }
                if (columna < dimension - 1) {
                    texto.append(" | ");
                }
            }
            texto.append(System.lineSeparator());
        }
        return texto.toString();
    }

    /**
     * Valida que una coordenada pertenezca al tablero.
     */
    private void validarPosicion(int fila, int columna) {
        if (fila < 0 || fila >= dimension || columna < 0 || columna >= dimension) {
            throw new IndexOutOfBoundsException("La posicion esta fuera del rompecabezas.");
        }
    }
}
