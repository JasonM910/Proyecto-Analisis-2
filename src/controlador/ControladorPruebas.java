package controlador;

import fuerzaBruta.FuerzaBrutaSolver;
import fuerzaBruta.MetricasFuerzaBruta;
import generador.GeneradorPiezas;
import modelo.DatosRompecabezas;
import modelo.Pieza;
import modelo.Rompecabezas;
import genetico.GeneticSolver;
import genetico.MetricasGenetico;

import java.util.List;
import java.util.Locale;

/**
 * Coordina la generacion y verificacion automatica de rompecabezas.
 */
public class ControladorPruebas {
    private static final int[] DIMENSIONES = {2, 3, 4, 5, 6, 10, 15};
    private static final int[] VALORES_MAXIMOS = {9, 15};
    private static final boolean[] OPCIONES_ROTACION = {false, true};

    private final GeneradorPiezas generadorPiezas;

    /**
     * Crea el controlador con el generador de piezas del proyecto.
     */
    public ControladorPruebas() {
        this.generadorPiezas = new GeneradorPiezas();
    }

    /**
     * Genera todas las pruebas sin interaccion con el usuario.
     */
    public void ejecutarPruebas() {
        Locale.setDefault(Locale.US);
        imprimirEncabezadoGeneral();
        for (int dimension : DIMENSIONES) {
            for (int valorMaximo : VALORES_MAXIMOS) {
                for (boolean permitirRotacion : OPCIONES_ROTACION) {
                    ejecutarPrueba(dimension, valorMaximo, permitirRotacion);
                }
            }
        }
    }

    private void ejecutarPrueba(int dimension, int valorMaximo, boolean permitirRotacion) {
        DatosRompecabezas datos = generadorPiezas.generar(dimension, valorMaximo);
        List<Pieza> piezas = datos.obtenerPiezasMezcladas();
        Rompecabezas solucionConocida = datos.obtenerSolucionConocida();

        imprimirTituloPrueba(dimension, valorMaximo, permitirRotacion);
        System.out.println("Cantidad de piezas generadas: " + piezas.size());
        System.out.println("Orden mezclado de piezas:");
        imprimirPiezasMezcladas(piezas);

        System.out.println();
        System.out.println("Solucion conocida generada:");
        System.out.println(solucionConocida.aTextoCompacto());
        imprimirValidacionSolucionConocida(solucionConocida);

        FuerzaBrutaSolver solver = new FuerzaBrutaSolver(permitirRotacion, calcularLimiteAlternativas(dimension, permitirRotacion));
        Rompecabezas solucionFuerzaBruta = solver.resolver(piezas, dimension);
        MetricasFuerzaBruta metricas = solver.obtenerMetricas();

        System.out.println("Resultado fuerza bruta:");
        System.out.println(metricas.solucionEncontrada ? "Solucion completa encontrada:" : "Solucion parcial encontrada:");
        System.out.println(solucionFuerzaBruta.aTextoCompacto());
        System.out.println("Solucion completa: " + (metricas.solucionEncontrada ? "SI" : "NO"));
        System.out.println("Limite alcanzado: " + (metricas.limiteAlcanzado ? "SI" : "NO"));
        System.out.println("Limite de alternativas: " + metricas.limiteAlternativas);
        System.out.println("Profundidad maxima: " + metricas.profundidadMaxima);
        System.out.println("Alternativas evaluadas: " + metricas.alternativasEvaluadas);
        System.out.println("Podas realizadas: " + metricas.podasRealizadas);
        System.out.println("Comparaciones: " + metricas.comparaciones);
        System.out.println("Asignaciones: " + metricas.asignaciones);
        System.out.println("Lineas ejecutadas: " + metricas.lineasEjecutadas);
        System.out.println("Lineas de codigo del algoritmo: " + metricas.lineasCodigoAlgoritmo);
        System.out.printf("Tiempo ms: %.3f%n", metricas.tiempoMilisegundos);
        System.out.println("Memoria estimada bytes: " + metricas.memoriaBytes);
        imprimirValidacionSolucionFuerzaBruta(solucionFuerzaBruta);
        System.out.println("============================================================");
        System.out.println();
        System.out.println("Resultado algoritmo genetico:");

        GeneticSolver solverGenetico = new GeneticSolver(permitirRotacion);
        Rompecabezas solucionGenetica = solverGenetico.resolver(piezas, dimension);
        MetricasGenetico metricasGenetico = solverGenetico.obtenerMetricas();

        System.out.println(metricasGenetico.solucionEncontrada ? "Solucion completa encontrada:" : "Solucion parcial encontrada:");
        System.out.println();
        System.out.println("========== RESUMEN GENETICO ==========");
        System.out.println(solucionGenetica.aTextoCompacto());
        System.out.println("Solucion completa: " + (metricasGenetico.solucionEncontrada ? "SI" : "NO"));
        System.out.println("Mejor fitness: " + metricasGenetico.mejorFitness);
        System.out.println("Peor fitness: " + metricasGenetico.peorFitness);
        System.out.println("Promedio fitness: " + metricasGenetico.promedioFitness);
        System.out.println("Generaciones ejecutadas: " + metricasGenetico.generacionesEjecutadas);
        System.out.println("Comparaciones: " + metricasGenetico.comparaciones);
        System.out.println("Asignaciones: " + metricasGenetico.asignaciones);
        System.out.println("Lineas ejecutadas: " + metricasGenetico.lineasEjecutadas);
        System.out.println("Lineas de codigo del algoritmo: " + metricasGenetico.lineasCodigoAlgoritmo);
        System.out.printf("Tiempo ms: %.3f%n", metricasGenetico.tiempoMilisegundos);
        System.out.println("Memoria estimada bytes: " + metricasGenetico.memoriaBytes);
        System.out.println("Mutaciones realizadas: " + metricasGenetico.cantidadMutaciones);
        System.out.println("Cruces realizados: " + metricasGenetico.cantidadCruces);
    }

    private void imprimirEncabezadoGeneral() {
        System.out.println("============================================================");
        System.out.println("EJECUCION AUTOMATICA DE ROMPECABEZAS CUADRADOS");
        System.out.println("Tamanos: 2x2, 3x3, 4x4, 5x5, 6x6, 10x10, 15x15");
        System.out.println("Rangos: 0..9 y 0..15 | Variante con/sin rotacion");
        System.out.println("============================================================");
        System.out.println();
    }

    private void imprimirTituloPrueba(int dimension, int valorMaximo, boolean permitirRotacion) {
        System.out.println("============================================================");
        System.out.println("PRUEBA " + dimension + "x" + dimension
                + " | Valores 0.." + valorMaximo
                + " | Rotacion permitida: " + (permitirRotacion ? "SI" : "NO"));
        System.out.println("============================================================");
    }

    private void imprimirPiezasMezcladas(List<Pieza> piezas) {
        int piezasPorLinea = 4;
        for (int indice = 0; indice < piezas.size(); indice++) {
            System.out.print(piezas.get(indice).describir());
            if ((indice + 1) % piezasPorLinea == 0 || indice == piezas.size() - 1) {
                System.out.println();
            } else {
                System.out.print("  ");
            }
        }
    }

    private void imprimirValidacionSolucionConocida(Rompecabezas solucionConocida) {
        boolean bordesInternosValidos = solucionConocida.verificarBordesAdyacentes();
        boolean bordesExternosValidos = solucionConocida.verificarBordesExternos(GeneradorPiezas.VALOR_EXTERIOR);
        System.out.println("Validacion de solucion generada:");
        System.out.println("Bordes internos compatibles: " + (bordesInternosValidos ? "SI" : "NO"));
        System.out.println("Bordes externos con valor 0: " + (bordesExternosValidos ? "SI" : "NO"));
    }

    private void imprimirValidacionSolucionFuerzaBruta(Rompecabezas solucionFuerzaBruta) {
        boolean solucionValida = solucionFuerzaBruta.estaCompleto()
                && solucionFuerzaBruta.verificarBordesAdyacentes()
                && solucionFuerzaBruta.verificarBordesExternos(GeneradorPiezas.VALOR_EXTERIOR);
        System.out.println("Solucion fuerza bruta valida: " + (solucionValida ? "SI" : "NO"));
    }

    private long calcularLimiteAlternativas(int dimension, boolean permitirRotacion) {
        if (dimension <= 3) {
            return permitirRotacion ? 1_000_000L : 250_000L;
        }
        if (dimension <= 5) {
            return permitirRotacion ? 200_000L : 100_000L;
        }
        if (dimension == 6) {
            return permitirRotacion ? 100_000L : 50_000L;
        }
        return permitirRotacion ? 50_000L : 25_000L;
    }
}
