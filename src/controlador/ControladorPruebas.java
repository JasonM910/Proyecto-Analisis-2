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
                DatosRompecabezas datos = generadorPiezas.generar(dimension, valorMaximo);
                for (boolean permitirRotacion : OPCIONES_ROTACION) {
                    ejecutarPrueba(datos, permitirRotacion);
                }
            }
        }
    }

    /**
     * Ejecuta una variante de fuerza bruta y, para piezas fijas, el genetico.
     */
    private void ejecutarPrueba(DatosRompecabezas datos, boolean permitirRotacion) {
        int dimension = datos.obtenerDimension();
        int valorMaximo = datos.obtenerValorMaximo();
        List<Pieza> piezas = datos.obtenerPiezasMezcladas();
        Rompecabezas solucionConocida = datos.obtenerSolucionConocida();

        imprimirTituloPrueba(dimension, valorMaximo, permitirRotacion);
        System.out.println("Cantidad de piezas generadas: " + piezas.size());
        System.out.println("Orden mezclado de piezas:");
        imprimirPiezasMezcladas(piezas);

        imprimirValidacionSolucionConocida(solucionConocida);

        FuerzaBrutaSolver solver = new FuerzaBrutaSolver(permitirRotacion, calcularLimiteAlternativas(dimension, permitirRotacion));
        Rompecabezas solucionFuerzaBruta = solver.resolver(piezas, dimension);
        MetricasFuerzaBruta metricas = solver.obtenerMetricas();

        System.out.println("Resultado fuerza bruta:");
        System.out.println(metricas.seEncontroSolucion()
                ? "Solucion completa encontrada:"
                : "Mejor solucion parcial encontrada antes del limite:");
        System.out.println(solucionFuerzaBruta.aTextoCompacto());
        System.out.println("Solucion completa: " + (metricas.seEncontroSolucion() ? "SI" : "NO"));
        System.out.println("Limite alcanzado: " + (metricas.seAlcanzoLimite() ? "SI" : "NO"));
        if (metricas.seAlcanzoLimite()) {
            System.out.println("Tipo de resultado: PARCIAL POR CORTE EXPERIMENTAL");
        }
        System.out.println("Limite de alternativas: " + metricas.obtenerLimiteAlternativas());
        System.out.println("Profundidad maxima: " + metricas.obtenerProfundidadMaxima());
        System.out.println("Alternativas evaluadas: " + metricas.obtenerAlternativasEvaluadas());
        System.out.println("Podas realizadas: " + metricas.obtenerPodasRealizadas());
        System.out.println("Comparaciones: " + metricas.obtenerComparaciones());
        System.out.println("Asignaciones: " + metricas.obtenerAsignaciones());
        System.out.println("Lineas ejecutadas: " + metricas.obtenerLineasEjecutadas());
        System.out.println("Lineas de codigo del modulo sin comentarios: "
                + metricas.obtenerLineasCodigoAlgoritmo());
        System.out.printf("Tiempo ms: %.3f%n", metricas.obtenerTiempoMilisegundos());
        System.out.println("Memoria calculada bytes: " + metricas.obtenerMemoriaBytes());
        imprimirValidacionSolucionFuerzaBruta(solucionFuerzaBruta);

        if (!permitirRotacion) {
            ejecutarAlgoritmoGenetico(piezas, dimension);
        }

        System.out.println("============================================================");
        System.out.println();
    }

    /**
     * Imprime la configuracion general de la ejecucion automatica.
     */
    private void imprimirEncabezadoGeneral() {
        System.out.println("============================================================");
        System.out.println("EJECUCION AUTOMATICA DE ROMPECABEZAS CUADRADOS");
        System.out.println("Tamanos: 2x2, 3x3, 4x4, 5x5, 6x6, 10x10, 15x15");
        System.out.println("Rangos: 0..9 y 0..15 | Variante con/sin rotacion");
        System.out.println("Lineas ejecutadas = asignaciones + comparaciones explicitas");
        System.out.println("Memoria calculada con el modelo analitico de variables y estructuras");
        System.out.println("============================================================");
        System.out.println();
    }

    /**
     * Delimita una prueba mediante su dimension, rango y variante.
     */
    private void imprimirTituloPrueba(int dimension, int valorMaximo, boolean permitirRotacion) {
        System.out.println("============================================================");
        System.out.println("PRUEBA " + dimension + "x" + dimension
                + " | Valores 0.." + valorMaximo
                + " | Rotacion permitida: " + (permitirRotacion ? "SI" : "NO"));
        System.out.println("============================================================");
    }

    /**
     * Imprime las piezas en el orden desordenado entregado a los algoritmos.
     */
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

    /**
     * Confirma que la solucion usada para generar las piezas es valida.
     */
    private void imprimirValidacionSolucionConocida(Rompecabezas solucionConocida) {
        boolean bordesInternosValidos = solucionConocida.verificarBordesAdyacentes();
        if (!bordesInternosValidos) {
            throw new IllegalStateException("El generador produjo una solucion conocida invalida.");
        }
        System.out.println("Validacion de solucion generada:");
        System.out.println("Bordes internos compatibles: SI");
    }

    /**
     * Informa si fuerza bruta produjo un tablero completo y compatible.
     */
    private void imprimirValidacionSolucionFuerzaBruta(Rompecabezas solucionFuerzaBruta) {
        boolean solucionValida = solucionFuerzaBruta.estaCompleto()
                && solucionFuerzaBruta.verificarBordesAdyacentes();
        System.out.println("Solucion fuerza bruta valida: " + (solucionValida ? "SI" : "NO"));
    }

    /**
     * Ejecuta e imprime el algoritmo genetico para piezas sin rotacion.
     */
    private void ejecutarAlgoritmoGenetico(List<Pieza> piezas, int dimension) {
        System.out.println();
        System.out.println("Resultado algoritmo genetico con piezas fijas:");

        GeneticSolver solverGenetico = new GeneticSolver();
        Rompecabezas solucionGenetica = solverGenetico.resolver(piezas, dimension);
        MetricasGenetico metricasGenetico = solverGenetico.obtenerMetricas();
        boolean solucionValida = solucionGenetica.estaCompleto()
                && solucionGenetica.verificarBordesAdyacentes();

        System.out.println(metricasGenetico.seEncontroSolucion()
                ? "Solucion completa encontrada:"
                : "Mejor solucion candidata encontrada:");
        System.out.println("========== RESUMEN GENETICO ==========");
        System.out.println(solucionGenetica.aTextoCompacto());
        System.out.println("Solucion completa: " + (metricasGenetico.seEncontroSolucion() ? "SI" : "NO"));
        System.out.println("Solucion genetica valida: " + (solucionValida ? "SI" : "NO"));
        System.out.println("Mejor fitness: " + metricasGenetico.obtenerMejorFitness());
        System.out.println("Peor fitness: " + metricasGenetico.obtenerPeorFitness());
        System.out.println("Promedio fitness: " + metricasGenetico.obtenerPromedioFitness());
        System.out.println("Cantidad de poblacion: " + metricasGenetico.obtenerCantidadPoblacion());
        System.out.println("Hijos por generacion: " + metricasGenetico.obtenerCantidadHijos());
        System.out.println("Generaciones ejecutadas: " + metricasGenetico.obtenerGeneracionesEjecutadas());
        System.out.println("Comparaciones: " + metricasGenetico.obtenerComparaciones());
        System.out.println("Asignaciones: " + metricasGenetico.obtenerAsignaciones());
        System.out.println("Lineas ejecutadas: " + metricasGenetico.obtenerLineasEjecutadas());
        System.out.println("Lineas de codigo del modulo sin comentarios: "
                + metricasGenetico.obtenerLineasCodigoAlgoritmo());
        System.out.printf("Tiempo de computo ms: %.3f%n", metricasGenetico.obtenerTiempoMilisegundos());
        System.out.println("Memoria calculada bytes: " + metricasGenetico.obtenerMemoriaBytes());
        System.out.println("Mutaciones intentadas: " + metricasGenetico.obtenerMutacionesIntentadas());
        System.out.println("Mutaciones aceptadas: " + metricasGenetico.obtenerCantidadMutaciones());
        System.out.println("Mutaciones descartadas: " + metricasGenetico.obtenerMutacionesDescartadas());
        System.out.println("Cruces realizados: " + metricasGenetico.obtenerCantidadCruces());
    }

    /**
     * Define el corte experimental usado para evitar ejecuciones impracticables.
     */
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
