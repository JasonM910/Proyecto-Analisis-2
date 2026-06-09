package modelo;

import java.util.Objects;

/**
 * Representa una pieza cuadrada con cuatro bordes numerados en sentido horario.
 *
 * @since 2026-05-29
 * @version 2026-06-09
 */
public final class Pieza {
    private final int id;
    private final int arriba;
    private final int derecha;
    private final int abajo;
    private final int izquierda;
    private final int rotacionGrados;

    /**
     * Crea una pieza sin rotacion aplicada.
     *
     * @param id identificador unico de la pieza
     * @param arriba valor del borde superior
     * @param derecha valor del borde derecho
     * @param abajo valor del borde inferior
     * @param izquierda valor del borde izquierdo
     */
    public Pieza(int id, int arriba, int derecha, int abajo, int izquierda) {
        this(id, arriba, derecha, abajo, izquierda, 0);
    }

    private Pieza(int id, int arriba, int derecha, int abajo, int izquierda, int rotacionGrados) {
        this.id = id;
        this.arriba = arriba;
        this.derecha = derecha;
        this.abajo = abajo;
        this.izquierda = izquierda;
        this.rotacionGrados = rotacionGrados;
    }

    /**
     * Obtiene el identificador unico de la pieza.
     *
     * @return identificador unico
     */
    public int obtenerId() {
        return id;
    }

    /**
     * Obtiene el valor del borde superior.
     *
     * @return valor superior
     */
    public int obtenerArriba() {
        return arriba;
    }

    /**
     * Obtiene el valor del borde derecho.
     *
     * @return valor derecho
     */
    public int obtenerDerecha() {
        return derecha;
    }

    /**
     * Obtiene el valor del borde inferior.
     *
     * @return valor inferior
     */
    public int obtenerAbajo() {
        return abajo;
    }

    /**
     * Obtiene el valor del borde izquierdo.
     *
     * @return valor izquierdo
     */
    public int obtenerIzquierda() {
        return izquierda;
    }

    /**
     * Obtiene la rotacion acumulada de la pieza.
     *
     * @return grados de rotacion
     */
    public int obtenerRotacionGrados() {
        return rotacionGrados;
    }

    /**
     * Obtiene el valor de un borde segun la direccion indicada.
     *
     * @param direccion lado que se desea consultar
     * @return valor del borde solicitado
     */
    public int obtenerLado(Direccion direccion) {
        switch (direccion) {
            case ARRIBA:
                return arriba;
            case DERECHA:
                return derecha;
            case ABAJO:
                return abajo;
            case IZQUIERDA:
                return izquierda;
            default:
                throw new IllegalStateException("Direccion no soportada: " + direccion);
        }
    }

    /**
     * Retorna una nueva pieza rotada 90 grados en sentido horario.
     *
     * @return pieza rotada 90 grados
     */
    public Pieza rotar90() {
        return new Pieza(id, izquierda, arriba, derecha, abajo, (rotacionGrados + 90) % 360);
    }

    /**
     * Retorna una nueva pieza rotada 180 grados.
     *
     * @return pieza rotada 180 grados
     */
    public Pieza rotar180() {
        return new Pieza(id, abajo, izquierda, arriba, derecha, (rotacionGrados + 180) % 360);
    }

    /**
     * Retorna una nueva pieza rotada 270 grados en sentido horario.
     *
     * @return pieza rotada 270 grados
     */
    public Pieza rotar270() {
        return new Pieza(id, derecha, abajo, izquierda, arriba, (rotacionGrados + 270) % 360);
    }

    /**
     * Comprueba si el lado indicado coincide con el lado opuesto de otra pieza.
     *
     * @param otra pieza adyacente
     * @param lado lado propio que se desea comparar
     * @return true si los bordes son compatibles
     */
    public boolean esCompatible(Pieza otra, Direccion lado) {
        return obtenerLado(lado) == otra.obtenerLado(lado.obtenerOpuesta());
    }

    /**
     * Construye una clave textual con los cuatro bordes.
     *
     * @return clave de bordes
     */
    public String obtenerClaveBordes() {
        return arriba + ":" + derecha + ":" + abajo + ":" + izquierda;
    }

    /**
     * Describe la pieza con identificador, rotacion y bordes.
     *
     * @return descripcion compacta de la pieza
     */
    public String describir() {
        return "P" + id + "(r" + rotacionGrados + ")[" + arriba + "," + derecha + "," + abajo + "," + izquierda + "]";
    }

    /**
     * Devuelve la descripcion textual de la pieza.
     *
     * @return descripcion compacta
     */
    @Override
    public String toString() {
        return describir();
    }

    /**
     * Compara piezas por su identificador unico.
     *
     * @param objeto objeto por comparar
     * @return true si representa la misma pieza
     */
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Pieza)) {
            return false;
        }
        Pieza pieza = (Pieza) objeto;
        return id == pieza.id;
    }

    /**
     * Calcula el hash a partir del identificador.
     *
     * @return codigo hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
