package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agrupa las piezas mezcladas y la solucion conocida de una prueba.
 */
public class DatosRompecabezas {
    private final int dimension;
    private final int valorMaximo;
    private final List<Pieza> piezasMezcladas;
    private final Rompecabezas solucionConocida;

    /**
     * Crea el contenedor de datos generados.
     *
     * @param dimension dimension del rompecabezas
     * @param valorMaximo valor maximo permitido para los bordes
     * @param piezasMezcladas piezas en orden aleatorio
     * @param solucionConocida solucion usada al generar las piezas
     */
    public DatosRompecabezas(int dimension, int valorMaximo, List<Pieza> piezasMezcladas, Rompecabezas solucionConocida) {
        this.dimension = dimension;
        this.valorMaximo = valorMaximo;
        this.piezasMezcladas = Collections.unmodifiableList(new ArrayList<>(piezasMezcladas));
        this.solucionConocida = solucionConocida.copiar();
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
     * Obtiene el valor maximo permitido para los bordes.
     *
     * @return valor maximo de borde
     */
    public int obtenerValorMaximo() {
        return valorMaximo;
    }

    /**
     * Obtiene una copia mutable de las piezas mezcladas.
     *
     * @return lista de piezas mezcladas
     */
    public List<Pieza> obtenerPiezasMezcladas() {
        return new ArrayList<>(piezasMezcladas);
    }

    /**
     * Obtiene una copia de la solucion conocida.
     *
     * @return rompecabezas solucion
     */
    public Rompecabezas obtenerSolucionConocida() {
        return solucionConocida.copiar();
    }
}
