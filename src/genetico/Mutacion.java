package genetico;

import java.util.Random;
import java.util.Set;

/**
 * Resuelve cromosomas duplicados mediante un intercambio aleatorio.
 *
 * @since 2026-06-08
 * @version 2026-06-09
 */
public class Mutacion {
    private final Random random;
    private final FitnessCalculator fitnessCalculator;

    /**
     * Crea el operador con la fuente aleatoria compartida por el solucionador.
     *
     * @param random generador aleatorio
     */
    public Mutacion(Random random) {
        this.random = random;
        this.fitnessCalculator = new FitnessCalculator();
    }

    /**
     * Intenta una mutacion y la acepta solo si es unica y mejora el fitness.
     *
     * @param cromosoma cromosoma duplicado
     * @param firmasUsadas firmas que no pueden repetirse
     * @param metricas mediciones de la ejecucion
     * @return resultado del intento de mutacion
     */
    public ResultadoMutacion mutarDuplicado(
            Cromosoma cromosoma,
            Set<String> firmasUsadas,
            MetricasGenetico metricas
    ) {
        Cromosoma original = cromosoma.copiar(metricas);
        Cromosoma mutado = cromosoma.copiar(metricas);
        double fitnessOriginal = original.obtenerFitness();
        int cantidadPiezas = mutado.obtenerCantidadPiezas();
        int indiceUno = random.nextInt(cantidadPiezas);
        int indiceDos = random.nextInt(cantidadPiezas);
        metricas.mutacionesIntentadas++;
        metricas.asignaciones += 6;

        while (metricas.registrarComparacion(indiceUno == indiceDos)) {
            indiceDos = random.nextInt(cantidadPiezas);
            metricas.asignaciones++;
        }

        mutado.intercambiarPiezas(indiceUno, indiceDos);
        metricas.asignaciones += 3;
        fitnessCalculator.calcularFitness(mutado, metricas);

        boolean mejora = metricas.registrarComparacion(mutado.obtenerFitness() > fitnessOriginal);
        boolean esUnica = metricas.registrarComparacion(
                !firmasUsadas.contains(mutado.obtenerFirma(metricas))
        );
        boolean aceptada = mejora && esUnica;
        metricas.asignaciones += 3;

        if (metricas.registrarComparacion(aceptada)) {
            metricas.cantidadMutaciones++;
            return new ResultadoMutacion(
                    original,
                    mutado,
                    indiceUno,
                    indiceDos,
                    true,
                    metricas
            );
        }

        metricas.mutacionesDescartadas++;
        return new ResultadoMutacion(
                original,
                mutado,
                indiceUno,
                indiceDos,
                false,
                metricas
        );
    }
}
