package genetico;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Genera un archivo CSV con las mediciones del algoritmo genetico.
 */
public class ReporteGeneticoCsv {

    private static final String ARCHIVO = "mediciones_genetico.csv";

    public void guardarResultado(
            String caso,
            boolean rotacion,
            MetricasGenetico metricas
    ) {
        boolean archivoExiste = new java.io.File(ARCHIVO).exists();

        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO, true))) {

            if (!archivoExiste) {
                writer.println("Caso,Rotacion,MejorFitness,PeorFitness,PromedioFitness,TiempoMs,Generaciones,Comparaciones,Asignaciones,LineasEjecutadas,MemoriaBytes,Mutaciones,Cruces");
            }

            writer.println(
                    caso + "," +
                    (rotacion ? "Si" : "No") + "," +
                    metricas.mejorFitness + "," +
                    metricas.peorFitness + "," +
                    metricas.promedioFitness + "," +
                    metricas.tiempoMilisegundos + "," +
                    metricas.generacionesEjecutadas + "," +
                    metricas.comparaciones + "," +
                    metricas.asignaciones + "," +
                    metricas.lineasEjecutadas + "," +
                    metricas.memoriaBytes + "," +
                    metricas.cantidadMutaciones + "," +
                    metricas.cantidadCruces
            );

            System.out.println("Reporte CSV actualizado: " + ARCHIVO);

        } catch (IOException e) {
            System.out.println("Error al guardar el reporte CSV: " + e.getMessage());
        }
    }
}