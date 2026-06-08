package genetico;

import modelo.Pieza;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Realiza cruces entre cromosomas usando cruce ordenado.
 */
public class Cruce {
    private final Random random;

    public Cruce() {
        this.random = new Random();
    }

    public Cromosoma[] cruzar(Cromosoma padreUno, Cromosoma padreDos, MetricasGenetico metricas) {
        int cantidadPiezas = padreUno.obtenerCantidadPiezas();

        int puntoInicio = random.nextInt(cantidadPiezas);
        int puntoFin = random.nextInt(cantidadPiezas);

        if (puntoInicio > puntoFin) {
            int temporal = puntoInicio;
            puntoInicio = puntoFin;
            puntoFin = temporal;
        }

        Cromosoma hijoUno = crearHijo(padreUno, padreDos, puntoInicio, puntoFin, metricas);
        Cromosoma hijoDos = crearHijo(padreDos, padreUno, puntoInicio, puntoFin, metricas);

        metricas.cantidadCruces++;
        metricas.asignaciones += 6;

        return new Cromosoma[]{hijoUno, hijoDos};
    }

    private Cromosoma crearHijo(Cromosoma padreBase, Cromosoma padreComplemento, int puntoInicio, int puntoFin, MetricasGenetico metricas) {
        int cantidadPiezas = padreBase.obtenerCantidadPiezas();
        Pieza[] piezasHijo = new Pieza[cantidadPiezas];
        Set<Integer> idsUsados = new HashSet<>();

        metricas.asignaciones += 3;

        for (int indice = puntoInicio; indice <= puntoFin; indice++) {
            Pieza pieza = padreBase.obtenerPiezaEnIndice(indice);
            piezasHijo[indice] = pieza;
            idsUsados.add(pieza.obtenerId());
            metricas.asignaciones += 2;
        }

        int indiceHijo = 0;
        metricas.asignaciones++;

        for (int indice = 0; indice < cantidadPiezas; indice++) {
            Pieza pieza = padreComplemento.obtenerPiezaEnIndice(indice);
            metricas.asignaciones++;
            metricas.comparaciones++;

            if (!idsUsados.contains(pieza.obtenerId())) {
                while (piezasHijo[indiceHijo] != null) {
                    indiceHijo++;
                    metricas.comparaciones++;
                    metricas.asignaciones++;
                }

                piezasHijo[indiceHijo] = pieza;
                idsUsados.add(pieza.obtenerId());
                metricas.asignaciones += 2;
            }
        }

        List<Pieza> listaHijo = new ArrayList<>();

        for (Pieza pieza : piezasHijo) {
            listaHijo.add(pieza);
            metricas.asignaciones++;
        }

        return new Cromosoma(listaHijo, padreBase.obtenerDimension());
    }
}