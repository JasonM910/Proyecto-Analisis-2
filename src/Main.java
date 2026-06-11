import controlador.ControladorPruebas;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        PrintStream consola = System.out;
        Path rutaResultados = Paths.get("resultados_consola.txt");

        try (
                OutputStream archivo = Files.newOutputStream(rutaResultados);
                PrintStream salidaDuplicada = new PrintStream(
                        new SalidaDuplicada(consola, archivo),
                        true,
                    StandardCharsets.UTF_8.name()
                )
        ) {
            System.setOut(salidaDuplicada);
            ControladorPruebas controladorPruebas = new ControladorPruebas();
            controladorPruebas.ejecutarPruebas();
            System.out.println("Resultados guardados en: " + rutaResultados.toAbsolutePath());
        } catch (IOException excepcion) {
            consola.println("No se pudo crear el archivo de resultados: " + excepcion.getMessage());
        } finally {
            System.setOut(consola);
        }
    }

    /**
     * Replica la salida en la consola y en el archivo de resultados.
     */
    private static class SalidaDuplicada extends OutputStream {
        private final OutputStream consola;
        private final OutputStream archivo;

        /**
         * Crea una salida conectada a dos destinos.
         *
         * @param consola flujo de la consola
         * @param archivo flujo del archivo de resultados
         */
        SalidaDuplicada(OutputStream consola, OutputStream archivo) {
            this.consola = consola;
            this.archivo = archivo;
        }

        /**
         * Escribe un byte en ambos destinos.
         *
         * @param valor byte por escribir
         * @throws IOException si falla la escritura
         */
        @Override
        public void write(int valor) throws IOException {
            consola.write(valor);
            archivo.write(valor);
        }

        /**
         * Escribe un bloque de bytes en ambos destinos.
         *
         * @param datos arreglo de bytes
         * @param inicio posicion inicial
         * @param cantidad cantidad de bytes
         * @throws IOException si falla la escritura
         */
        @Override
        public void write(byte[] datos, int inicio, int cantidad) throws IOException {
            consola.write(datos, inicio, cantidad);
            archivo.write(datos, inicio, cantidad);
        }

        /**
         * Fuerza la escritura pendiente en ambos destinos.
         *
         * @throws IOException si falla la escritura
         */
        @Override
        public void flush() throws IOException {
            consola.flush();
            archivo.flush();
        }

        /**
         * Cierra solamente el archivo para mantener disponible la consola.
         *
         * @throws IOException si falla el cierre
         */
        @Override
        public void close() throws IOException {
            flush();
            archivo.close();
        }
    }
}
