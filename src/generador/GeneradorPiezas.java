package generador;

import modelo.DatosRompecabezas;
import modelo.Pieza;
import modelo.Rompecabezas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Genera piezas mezcladas garantizando que exista al menos una solucion.
 */
public class GeneradorPiezas {
    private final Random random;

    /**
     * Crea un generador pseudoaleatorio para las pruebas del proyecto.
     */
    public GeneradorPiezas() {
        this.random = new Random();
    }

    /**
     * Genera piezas para una dimension y rango de valores.
     *
     * @param dimension dimension del rompecabezas
     * @param valorMaximo valor maximo permitido para bordes internos
     * @return datos generados con piezas mezcladas y solucion conocida
     */
    public DatosRompecabezas generar(int dimension, int valorMaximo) {
        if (dimension < 2) {
            throw new IllegalArgumentException("La dimension debe ser al menos 2.");
        }
        if (valorMaximo != 9 && valorMaximo != 15) {
            throw new IllegalArgumentException("El rango debe ser 0..9 o 0..15.");
        }
        if (dimension == 2) {
            return generarPiezasQuemadas(valorMaximo);
        }
        return generarPiezasAleatorias(dimension, valorMaximo);
    }

    /**
     * Construye las dos configuraciones manuales solicitadas para 2x2.
     */
    private DatosRompecabezas generarPiezasQuemadas(int valorMaximo) {
        Pieza[][] matriz = new Pieza[2][2];
        if (valorMaximo <= 9) {
            matriz[0][0] = new Pieza(1, 8, 3, 5, 1);
            matriz[0][1] = new Pieza(2, 4, 6, 7, 3);
            matriz[1][0] = new Pieza(3, 5, 4, 2, 9);
            matriz[1][1] = new Pieza(4, 7, 0, 8, 4);
        } else {
            matriz[0][0] = new Pieza(1, 12, 11, 6, 2);
            matriz[0][1] = new Pieza(2, 4, 15, 14, 11);
            matriz[1][0] = new Pieza(3, 6, 9, 1, 13);
            matriz[1][1] = new Pieza(4, 14, 3, 10, 9);
        }
        return construirDatosDesdeMatriz(matriz, 2, valorMaximo);
    }

    /**
     * Genera un tablero solucion y reutiliza cada union en las dos piezas vecinas.
     */
    private DatosRompecabezas generarPiezasAleatorias(int dimension, int valorMaximo) {
        int[][] unionesVerticales = new int[dimension][dimension - 1];
        int[][] unionesHorizontales = new int[dimension - 1][dimension];

        for (int fila = 0; fila < dimension; fila++) {
            for (int columna = 0; columna < dimension - 1; columna++) {
                unionesVerticales[fila][columna] = obtenerValorAleatorio(valorMaximo);
            }
        }

        for (int fila = 0; fila < dimension - 1; fila++) {
            for (int columna = 0; columna < dimension; columna++) {
                unionesHorizontales[fila][columna] = obtenerValorAleatorio(valorMaximo);
            }
        }

        Pieza[][] matriz = new Pieza[dimension][dimension];
        int id = 1;
        for (int fila = 0; fila < dimension; fila++) {
            for (int columna = 0; columna < dimension; columna++) {
                int arriba = fila == 0
                        ? obtenerValorAleatorio(valorMaximo)
                        : unionesHorizontales[fila - 1][columna];
                int derecha = columna == dimension - 1
                        ? obtenerValorAleatorio(valorMaximo)
                        : unionesVerticales[fila][columna];
                int abajo = fila == dimension - 1
                        ? obtenerValorAleatorio(valorMaximo)
                        : unionesHorizontales[fila][columna];
                int izquierda = columna == 0
                        ? obtenerValorAleatorio(valorMaximo)
                        : unionesVerticales[fila][columna - 1];
                matriz[fila][columna] = new Pieza(id, arriba, derecha, abajo, izquierda);
                id++;
            }
        }

        return construirDatosDesdeMatriz(matriz, dimension, valorMaximo);
    }

    /**
     * Convierte una matriz solucion en piezas mezcladas y datos verificables.
     */
    private DatosRompecabezas construirDatosDesdeMatriz(Pieza[][] matriz, int dimension, int valorMaximo) {
        Rompecabezas solucion = new Rompecabezas(dimension);
        List<Pieza> piezas = new ArrayList<>();
        for (int fila = 0; fila < dimension; fila++) {
            for (int columna = 0; columna < dimension; columna++) {
                Pieza pieza = matriz[fila][columna];
                solucion.colocarPieza(fila, columna, pieza);
                piezas.add(pieza);
            }
        }

        Collections.shuffle(piezas, random);
        if (estaEnOrdenDeSolucion(piezas)) {
            Collections.swap(piezas, 0, 1);
        }
        return new DatosRompecabezas(dimension, valorMaximo, piezas, solucion);
    }

    /**
     * Detecta el unico caso en que la mezcla conserva el orden de la solucion.
     */
    private boolean estaEnOrdenDeSolucion(List<Pieza> piezas) {
        for (int indice = 0; indice < piezas.size(); indice++) {
            if (piezas.get(indice).obtenerId() != indice + 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene un valor incluido en el rango 0..valorMaximo.
     */
    private int obtenerValorAleatorio(int valorMaximo) {
        return random.nextInt(valorMaximo + 1);
    }
}
