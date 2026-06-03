package controlador;

import fuerzaBruta.FuerzaBrutaSolver;
import fuerzaBruta.MetricasFuerzaBruta;
import generador.GeneradorPiezas;
import modelo.DatosRompecabezas;
import modelo.Pieza;
import modelo.Rompecabezas;

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

        FuerzaBrutaSolver solver = new FuerzaBrutaSolver(permitirRotacion);
        Rompecabezas solucionFuerzaBruta = solver.resolver(piezas, dimension);
        MetricasFuerzaBruta metricas = solver.obtenerMetricas();

        imprimirTituloPrueba(dimension, valorMaximo, permitirRotacion);
        System.out.println("Cantidad de piezas generadas: " + piezas.size());
        System.out.println("Orden mezclado de piezas:");
        imprimirPiezasMezcladas(piezas);

        System.out.println();
        System.out.println("Solucion conocida generada:");
        System.out.println(solucionConocida.aTextoCompacto());
        imprimirValidacionSolucionConocida(solucionConocida);

        System.out.println("Solucion encontrada por fuerza bruta:");
        System.out.println("Alternativas evaluadas: " + metricas.alternativasEvaluadas);
        System.out.println("Podas realizadas: " + metricas.podasRealizadas);
        System.out.println("Comparaciones: " + metricas.comparaciones);
        System.out.println("Asignaciones: " + metricas.asignaciones);
        System.out.println("Lineas ejecutadas: " + metricas.lineasEjecutadas);
        System.out.println("Tiempo ms: " + metricas.tiempoMilisegundos);
        imprimirValidacionSolucionFuerzaBruta(solucionFuerzaBruta);
        System.out.println("============================================================");
        System.out.println();
    }

    private void imprimirEncabezadoGeneral() {
        System.out.println("============================================================");
        System.out.println("GENERACION AUTOMATICA DE ROMPECABEZAS CUADRADOS");
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

    private void imprimirValidacionSolucionFuerzaBruta(Rompecabezas solucionFuerzaBruta){
        boolean solucionValida = solucionFuerzaBruta.verificarBordesAdyacentes() && solucionFuerzaBruta.verificarBordesExternos(GeneradorPiezas.VALOR_EXTERIOR) && solucionFuerzaBruta.estaCompleto();
        System.out.println("Solucion fuerza bruta valida: "+ (solucionValida ? "SI" : "NO"));
    }

}
