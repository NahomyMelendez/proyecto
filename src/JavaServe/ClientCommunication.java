package JavaServe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * La clase ClientCommunication proporciona métodos para enviar y recibir
 * mensajes y archivos entre el cliente y el servidor utilizando sockets.
 */
public class ClientCommunication {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final OutputStream binaryOut;
    private final InputStream binaryIn;

    /**
     * Constructor de la clase ClientCommunication.
     *
     * @param socket el socket de conexión
     * @param out el flujo de salida de texto
     * @param in el flujo de entrada de texto
     * @param binaryOut el flujo de salida binaria
     * @param binaryIn el flujo de entrada binaria
     * @throws IOException si hay un error al inicializar los flujos de
     * entrada/salida
     */
    public ClientCommunication(Socket socket, PrintWriter out, 
            BufferedReader in, OutputStream binaryOut,
            InputStream binaryIn) throws IOException {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.binaryOut = binaryOut;
        this.binaryIn = binaryIn;
    }

    /**
     * Envía un mensaje al servidor.
     *
     * @param message el mensaje a enviar
     */

    // Método para enviar mensajes al servidor
    public void sendMessage(String message) {
        if (out != null) {
            System.out.println("Enviando mensaje al servidor: " + message);
            out.println(message);
            out.flush(); // Asegúrate de que el mensaje se envíe inmediatamente
        } else {
            System.err.println("PrintWriter no inicializado");
        }
    }

    /**
     * Recibe un mensaje del servidor.
     *
     * @return el mensaje recibido
     * @throws IOException si hay un error al leer el mensaje
     */
    public String receiveMessage() throws IOException {
        if (in != null) {
            String response = in.readLine();
            System.out.println("Mensaje recibido del servidor: " + response);
            return response;
        } else {
            System.err.println("BufferedReader no inicializado");
            return null;
        }
    }

    /**
     * Recibe un archivo del servidor y lo guarda en la ruta especificada.
     *
     * @param filePath la ruta donde se guardará el archivo recibido
     */
    public void receiveFileFromServer(String filePath) {
        System.out.println("Recibiendo archivo: " + filePath);
        byte[] buffer = new byte[8192];
        try (FileOutputStream fos = new FileOutputStream(filePath); 
                BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            int bytesRead;
            System.out.println("Iniciando bucle de lectura de datos...");
            while ((bytesRead = binaryIn.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
            System.out.println("Archivo recibido y guardado en: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cierra la conexión con el servidor y libera los recursos asociados.
     */
    public void close() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }
}
