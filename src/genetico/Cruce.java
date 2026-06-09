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

    /**
     * Crea el operador con la fuente aleatoria compartida por el solucionador.
     *
     * @param random generador aleatorio
     */
    public Cruce(Random random) {
        this.random = random;
    }

    /**
     * Genera dos hijos validos mediante cruce ordenado.
     *
     * @param padreUno primer padre
     * @param padreDos segundo padre
     * @param metricas mediciones de la ejecucion
     * @return dos hijos sin piezas repetidas
     */
    public Cromosoma[] cruzar(Cromosoma padreUno, Cromosoma padreDos, MetricasGenetico metricas) {
        int cantidadPiezas = padreUno.obtenerCantidadPiezas();
        int puntoInicio = random.nextInt(cantidadPiezas);
        int puntoFin = random.nextInt(cantidadPiezas);
        metricas.asignaciones += 3;

        if (metricas.registrarComparacion(puntoInicio > puntoFin)) {
            int temporal = puntoInicio;
            puntoInicio = puntoFin;
            puntoFin = temporal;
            metricas.asignaciones += 3;
        }

        Cromosoma hijoUno = crearHijo(padreUno, padreDos, puntoInicio, puntoFin, metricas);
        Cromosoma hijoDos = crearHijo(padreDos, padreUno, puntoInicio, puntoFin, metricas);

        metricas.cantidadCruces++;
        metricas.asignaciones += 3;

        return new Cromosoma[]{hijoUno, hijoDos};
    }

    /**
     * Conserva un segmento del primer padre y completa con el orden del segundo.
     */
    private Cromosoma crearHijo(Cromosoma padreBase, Cromosoma padreComplemento, int puntoInicio, int puntoFin, MetricasGenetico metricas) {
        int cantidadPiezas = padreBase.obtenerCantidadPiezas();
        Pieza[] piezasHijo = new Pieza[cantidadPiezas];
        Set<Integer> idsUsados = new HashSet<>();
        metricas.asignaciones += 3;

        int indice = puntoInicio;
        metricas.asignaciones++;
        while (metricas.registrarComparacion(indice <= puntoFin)) {
            Pieza pieza = padreBase.obtenerPiezaEnIndice(indice);
            piezasHijo[indice] = pieza;
            idsUsados.add(pieza.obtenerId());
            indice++;
            metricas.asignaciones += 4;
        }

        int indiceHijo = 0;
        indice = 0;
        metricas.asignaciones += 2;
        while (metricas.registrarComparacion(indice < cantidadPiezas)) {
            Pieza pieza = padreComplemento.obtenerPiezaEnIndice(indice);
            metricas.asignaciones++;

            if (metricas.registrarComparacion(!idsUsados.contains(pieza.obtenerId()))) {
                while (metricas.registrarComparacion(piezasHijo[indiceHijo] != null)) {
                    indiceHijo++;
                    metricas.asignaciones++;
                }

                piezasHijo[indiceHijo] = pieza;
                idsUsados.add(pieza.obtenerId());
                metricas.asignaciones += 2;
            }
            indice++;
            metricas.asignaciones++;
        }

        List<Pieza> listaHijo = new ArrayList<>();
        indice = 0;
        metricas.asignaciones += 2;
        while (metricas.registrarComparacion(indice < piezasHijo.length)) {
            listaHijo.add(piezasHijo[indice]);
            indice++;
            metricas.asignaciones += 2;
        }

        return new Cromosoma(listaHijo, padreBase.obtenerDimension(), metricas);
    }
}
