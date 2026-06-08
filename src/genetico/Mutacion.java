package genetico;

import modelo.Pieza;

import java.util.Random;

/**
 * Aplica mutaciones a los cromosomas.
 */
public class Mutacion {
    private final Random random;
    private final FitnessCalculator fitnessCalculator;

    public Mutacion() {
        this.random = new Random();
        this.fitnessCalculator = new FitnessCalculator();
    }

    /**
     * Aplica mutacion a un cromosoma. La mutacion se conserva solo si mejora o iguala el fitness.
     *
     * @param cromosoma cromosoma original
     * @param permitirRotacion indica si se permite rotar piezas
     * @param metricas metricas del algoritmo genetico
     * @return cromosoma mutado si mejora o iguala, si no retorna el original
     */
    public Cromosoma mutar(Cromosoma cromosoma, boolean permitirRotacion, MetricasGenetico metricas) {
        Cromosoma original = cromosoma.copiar();
        Cromosoma mutado = cromosoma.copiar();

        int cantidadPiezas = mutado.obtenerCantidadPiezas();

        if (cantidadPiezas < 2) {
            return original;
        }

        double fitnessOriginal = fitnessCalculator.calcularFitness(original, metricas);

        int indiceUno = random.nextInt(cantidadPiezas);
        int indiceDos = random.nextInt(cantidadPiezas);

        while (indiceUno == indiceDos) {
            indiceDos = random.nextInt(cantidadPiezas);
            metricas.comparaciones++;
            metricas.asignaciones++;
        }

        mutado.intercambiarPiezas(indiceUno, indiceDos);

        System.out.println("Mutacion aplicada:");
        System.out.println("Individuo original: " + original.describir() + " puntuacion " + fitnessOriginal);
        System.out.println("Intercambio de posiciones: " + indiceUno + " y " + indiceDos);

        metricas.asignaciones += 5;
        metricas.cantidadMutaciones++;

        if (permitirRotacion) {
            aplicarRotacionAleatoria(mutado, cantidadPiezas, metricas);
        }

        double fitnessMutado = fitnessCalculator.calcularFitness(mutado, metricas);

        System.out.println("Mutacion: " + mutado.describir() + " puntuacion " + fitnessMutado);

        metricas.comparaciones++;

        if (fitnessMutado >= fitnessOriginal) {
            System.out.println("Resultado de mutacion: ACEPTADA");
            System.out.println();
            return mutado;
        }

        System.out.println("Resultado de mutacion: DESCARTADA");
        System.out.println();

        return original;
    }

    private void aplicarRotacionAleatoria(Cromosoma cromosoma, int cantidadPiezas, MetricasGenetico metricas) {
        int indiceRotacion = random.nextInt(cantidadPiezas);
        Pieza pieza = cromosoma.obtenerPiezaEnIndice(indiceRotacion);
        int opcion = random.nextInt(3);

        if (opcion == 0) {
            cromosoma.reemplazarPieza(indiceRotacion, pieza.rotar90());
            System.out.println("Rotacion aplicada a posicion " + indiceRotacion + ": 90 grados");
        } else if (opcion == 1) {
            cromosoma.reemplazarPieza(indiceRotacion, pieza.rotar180());
            System.out.println("Rotacion aplicada a posicion " + indiceRotacion + ": 180 grados");
        } else {
            cromosoma.reemplazarPieza(indiceRotacion, pieza.rotar270());
            System.out.println("Rotacion aplicada a posicion " + indiceRotacion + ": 270 grados");
        }

        metricas.asignaciones += 4;
        metricas.comparaciones += 2;
    }
}