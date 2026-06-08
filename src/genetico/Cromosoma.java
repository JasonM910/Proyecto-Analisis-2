package genetico;

import modelo.Pieza;
import modelo.Rompecabezas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa un cromosoma del algoritmo genetico.
 * Cada cromosoma contiene una posible distribucion de piezas.
 */
public class Cromosoma {
    private final List<Pieza> piezas;
    private final int dimension;
    private double fitness;

    public Cromosoma(List<Pieza> piezas, int dimension) {
        this.piezas = new ArrayList<>(piezas);
        this.dimension = dimension;
        this.fitness = 0.0;
    }

    public List<Pieza> obtenerPiezas() {
        return new ArrayList<>(piezas);
    }

    public int obtenerDimension() {
        return dimension;
    }

    public double obtenerFitness() {
        return fitness;
    }

    public void establecerFitness(double fitness) {
        this.fitness = fitness;
    }

    public Pieza obtenerPiezaEnIndice(int indice) {
        return piezas.get(indice);
    }

    public int obtenerCantidadPiezas() {
        return piezas.size();
    }

    public Rompecabezas convertirARompecabezas() {
        Rompecabezas rompecabezas = new Rompecabezas(dimension);
        int indice = 0;

        for (int fila = 0; fila < dimension; fila++) {
            for (int columna = 0; columna < dimension; columna++) {
                rompecabezas.colocarPieza(fila, columna, piezas.get(indice));
                indice++;
            }
        }

        return rompecabezas;
    }

    public void intercambiarPiezas(int indiceUno, int indiceDos) {
        Collections.swap(piezas, indiceUno, indiceDos);
    }

    public void reemplazarPieza(int indice, Pieza pieza) {
        piezas.set(indice, pieza);
    }

    public String describir() {
        StringBuilder texto = new StringBuilder();

        for (Pieza pieza : piezas) {
            texto.append(pieza.describir()).append(" ");
        }

        return texto.toString();
    }

    public Cromosoma copiar() {
        Cromosoma copia = new Cromosoma(piezas, dimension);
        copia.establecerFitness(fitness);
        return copia;
    }
}