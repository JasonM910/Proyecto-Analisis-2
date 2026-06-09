import controlador.ControladorPruebas;

/**
 * Punto de entrada del proyecto.
 *
 * @since 2026-05-29
 * @version 2026-06-09
 */
public class Main {

    /**
     * Ejecuta todas las pruebas configuradas sin solicitar datos al usuario.
     *
     * @param args argumentos de consola no utilizados
     */
    public static void main(String[] args) {
        ControladorPruebas controladorPruebas = new ControladorPruebas();
        controladorPruebas.ejecutarPruebas();
    }
}
