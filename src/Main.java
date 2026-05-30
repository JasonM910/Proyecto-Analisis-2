import controlador.ControladorPruebas;


/**
 * Punto de entrada del proyecto.
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
