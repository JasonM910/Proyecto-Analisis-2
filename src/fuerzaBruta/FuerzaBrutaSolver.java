package fuerzaBruta;

import modelo.Pieza;
import modelo.Rompecabezas;

import java.util.ArrayList;
import java.util.List;

public class FuerzaBrutaSolver {

    private Rompecabezas solucion;
    private MetricasFuerzaBruta metricas;
    private boolean permitirRotacion;

    public FuerzaBrutaSolver(boolean permitirRotacion) {
        this.permitirRotacion = permitirRotacion;
        this.metricas = new MetricasFuerzaBruta();
    }

    public Rompecabezas resolver(List<Pieza> piezas, int dimension) {
        long inicio = System.currentTimeMillis();

        Rompecabezas tablero = new Rompecabezas(dimension);
        List<Pieza> disponibles = new ArrayList<>(piezas);

        boolean encontrado = backtracking(tablero, disponibles, 0, dimension);

        long fin = System.currentTimeMillis();
        metricas.tiempoMilisegundos = fin - inicio;

        if (encontrado) {
            return solucion;
        }

        return tablero;
    }

    private boolean backtracking(Rompecabezas tablero, List<Pieza> disponibles, int posicion, int dimension) {
        metricas.lineasEjecutadas++;

        if (posicion == dimension * dimension) {
            metricas.comparaciones++;
            solucion = tablero.copiar();
            return true;
        }

        int fila = posicion / dimension;
        int columna = posicion % dimension;

        for (int i = 0; i < disponibles.size(); i++) {
            Pieza piezaOriginal = disponibles.get(i);
            List<Pieza> orientaciones = obtenerOrientaciones(piezaOriginal);

            for (Pieza pieza : orientaciones) {
                metricas.alternativasEvaluadas++;

                if (tablero.esCompatibleEnPosicion(fila, columna, pieza)) {
                    metricas.comparaciones++;
                    tablero.colocarPieza(fila, columna, pieza);
                    metricas.asignaciones++;

                    Pieza removida = disponibles.remove(i);

                    if (backtracking(tablero, disponibles, posicion + 1, dimension)) {
                        return true;
                    }

                    disponibles.add(i, removida);
                    tablero.removerPieza(fila, columna);
                } else {
                    metricas.podasRealizadas++;
                }
            }
        }

        return false;
    }

    private List<Pieza> obtenerOrientaciones(Pieza pieza) {
        List<Pieza> orientaciones = new ArrayList<>();

        orientaciones.add(pieza);

        if (permitirRotacion) {
            orientaciones.add(pieza.rotar90());
            orientaciones.add(pieza.rotar180());
            orientaciones.add(pieza.rotar270());
        }

        return orientaciones;
    }

    public MetricasFuerzaBruta obtenerMetricas() {
        return metricas;
    }
}