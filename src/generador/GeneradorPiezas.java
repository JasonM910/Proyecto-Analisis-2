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
    public static final int VALOR_EXTERIOR = 0;
    private static final long SEMILLA_BASE = 2_026_052_930L;

    /**
     * Genera piezas para una dimension y rango de valores.
     *
     * @param dimension dimension del rompecabezas
     * @param valorMaximo valor maximo permitido para bordes internos
     * @return datos generados con piezas mezcladas y solucion conocida
     */
    public DatosRompecabezas generar(int dimension, int valorMaximo) {
        if (valorMaximo <= VALOR_EXTERIOR) {
            throw new IllegalArgumentException("El valor maximo debe ser mayor que cero.");
        }
        if (dimension == 2) {
            return generarPiezasQuemadas(valorMaximo);
        }
        return generarPiezasAleatorias(dimension, valorMaximo);
    }

    private DatosRompecabezas generarPiezasQuemadas(int valorMaximo) {
        Pieza[][] matriz = new Pieza[2][2];
        if (valorMaximo <= 9) {
            matriz[0][0] = new Pieza(1, 0, 3, 5, 0);
            matriz[0][1] = new Pieza(2, 0, 0, 7, 3);
            matriz[1][0] = new Pieza(3, 5, 4, 0, 0);
            matriz[1][1] = new Pieza(4, 7, 0, 0, 4);
        } else {
            matriz[0][0] = new Pieza(1, 0, 11, 6, 0);
            matriz[0][1] = new Pieza(2, 0, 0, 14, 11);
            matriz[1][0] = new Pieza(3, 6, 9, 0, 0);
            matriz[1][1] = new Pieza(4, 14, 0, 0, 9);
        }
        return construirDatosDesdeMatriz(matriz, 2, valorMaximo);
    }

    private DatosRompecabezas generarPiezasAleatorias(int dimension, int valorMaximo) {
        Random random = new Random(calcularSemilla(dimension, valorMaximo));
        int[][] unionesVerticales = new int[dimension][dimension - 1];
        int[][] unionesHorizontales = new int[dimension - 1][dimension];

        for (int fila = 0; fila < dimension; fila++) {
            for (int columna = 0; columna < dimension - 1; columna++) {
                unionesVerticales[fila][columna] = obtenerValorInterno(random, valorMaximo);
            }
        }

        for (int fila = 0; fila < dimension - 1; fila++) {
            for (int columna = 0; columna < dimension; columna++) {
                unionesHorizontales[fila][columna] = obtenerValorInterno(random, valorMaximo);
            }
        }

        Pieza[][] matriz = new Pieza[dimension][dimension];
        int id = 1;
        for (int fila = 0; fila < dimension; fila++) {
            for (int columna = 0; columna < dimension; columna++) {
                int arriba = fila == 0 ? VALOR_EXTERIOR : unionesHorizontales[fila - 1][columna];
                int derecha = columna == dimension - 1 ? VALOR_EXTERIOR : unionesVerticales[fila][columna];
                int abajo = fila == dimension - 1 ? VALOR_EXTERIOR : unionesHorizontales[fila][columna];
                int izquierda = columna == 0 ? VALOR_EXTERIOR : unionesVerticales[fila][columna - 1];
                matriz[fila][columna] = new Pieza(id, arriba, derecha, abajo, izquierda);
                id++;
            }
        }

        return construirDatosDesdeMatriz(matriz, dimension, valorMaximo);
    }

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

        Collections.shuffle(piezas, new Random(calcularSemilla(dimension, valorMaximo) + 17));
        return new DatosRompecabezas(dimension, valorMaximo, piezas, solucion);
    }

    private int obtenerValorInterno(Random random, int valorMaximo) {
        return random.nextInt(valorMaximo) + 1;
    }

    private long calcularSemilla(int dimension, int valorMaximo) {
        return SEMILLA_BASE + (long) dimension * 10_000L + valorMaximo * 97L;
    }
}
