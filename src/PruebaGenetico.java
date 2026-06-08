import generador.GeneradorPiezas;
import genetico.GeneticSolver;
import genetico.MetricasGenetico;
import modelo.DatosRompecabezas;
import modelo.Rompecabezas;
import genetico.ReporteGeneticoCsv;

public class PruebaGenetico {

    public static void main(String[] args) {

        GeneradorPiezas generador = new GeneradorPiezas();

        DatosRompecabezas datos =
                generador.generar(3, 9);

        GeneticSolver solver =
                new GeneticSolver(false);

        Rompecabezas resultado =
                solver.resolver(
                        datos.obtenerPiezasMezcladas(),
                        datos.obtenerDimension()
                );

        MetricasGenetico metricas =
                solver.obtenerMetricas();

        System.out.println("========== RESULTADO ==========");

        System.out.println(
                resultado.aTextoCompacto()
        );

        System.out.println(
                "Mejor fitness: "
                        + metricas.mejorFitness
        );

        System.out.println(
                "Tiempo: "
                        + metricas.tiempoMilisegundos
        );

        System.out.println(
                "Generaciones: "
                        + metricas.generacionesEjecutadas
                
        );
        System.out.println("Comparaciones: " + metricas.comparaciones);
        System.out.println("Asignaciones: " + metricas.asignaciones);
        System.out.println("Lineas ejecutadas: " + metricas.lineasEjecutadas);
        System.out.println("Memoria bytes: " + metricas.memoriaBytes);
        System.out.println("Peor fitness: " + metricas.peorFitness);
        System.out.println("Promedio fitness: " + metricas.promedioFitness);
        System.out.println("Mutaciones: " + metricas.cantidadMutaciones);
        System.out.println("Cruces: " + metricas.cantidadCruces);
        ReporteGeneticoCsv reporte =
        new ReporteGeneticoCsv();

        reporte.guardarResultado(
                datos.obtenerDimension()
                        + "x"
                        + datos.obtenerDimension(),
                false,
                metricas
        );
    }
}