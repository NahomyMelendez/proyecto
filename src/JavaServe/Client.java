package JavaServe;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * La clase Client es un servidor que maneja múltiples conexiones de
 * clientes, autenticación de usuarios y gestión de archivos. Utiliza un pool de
 * hilos para manejar múltiples conexiones concurrentemente.
 *
 * @autor Personal
 */
public class Client {
    private static final String SERVER_ADDRESS = "192.168.1.35";
    private static final int PORT = 5050;
    private static final int MAX_THREADS = 10;
    private static final ExecutorService executor = 
            Executors.newFixedThreadPool(MAX_THREADS);

    private static final FileUserAuthentication fileUserAuthentication =
            new FileUserAuthentication();

    private static final FileUserAuthenticationNoEncrypt
   fileUserAuthenticationNoEncrypt = new FileUserAuthenticationNoEncrypt();
    /**
     * Método principal que inicia el servidor y escucha las conexiones
     * entrantes.
     *
     * @param args Los argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        
        try (ServerSocket serverSocket = new ServerSocket(PORT, 0, 
                InetAddress.getLocalHost())) {
            System.out.println("Servidor escuchando en localhost en el puerto " 
                    + PORT);
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("Dirección IP del servidor: " 
                    + inetAddress.getHostAddress());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " 
                        + clientSocket.getInetAddress());
                Runnable clientHandler = 
              new ClientHandler(clientSocket, fileUserAuthentication);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
    /**
     * Clase interna ClientHandler que maneja la comunicación con un cliente
     * específico.
     */
    static class ClientHandler implements Runnable {

        private final Socket clientSocket;
        private final FileUserAuthentication fileUserAuthentication;
        /**
         * Constructor de ClientHandler.
         *
         * @param clientSocket El socket del cliente.
         * @param fileUserAuthentication La instancia de FileUserAuthentication
         * para autenticación de usuarios.
         */
        public ClientHandler(Socket clientSocket,
                FileUserAuthentication fileUserAuthentication) {
            this.clientSocket = clientSocket;
            this.fileUserAuthentication = fileUserAuthentication;
        }
        /**
         * Método run que maneja la conexión con el cliente. Este método se
         * ejecuta en un hilo separado.
         */
        @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(clientSocket
                   .getOutputStream(), true); BufferedReader in = 
                   new BufferedReader(new InputStreamReader
                   (clientSocket.getInputStream())); OutputStream fileOut 
                           = clientSocket.getOutputStream()) {
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Mensaje del cliente: " + inputLine);
                    String[] AuthParts = inputLine.split("-");
                    if (!Boolean.parseBoolean(AuthParts[2])) {
                        handleAuthenticationRequest(inputLine, out);
                    } else {
                        handleOtherRequests(AuthParts[0], 
                                out, fileOut, AuthParts[1]);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error al manejar la "
                        +"conexión con el cliente: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar el "
                            +"socket del cliente: " + e.getMessage());
                }
            }
        }
        /**
         * Maneja las solicitudes de autenticación de los clientes.
         *
         * @param inputLine La línea de entrada recibida del cliente.
         * @param out El PrintWriter para enviar respuestas al cliente.
         * @throws IOException Si ocurre un error durante la autenticación.
         */
        private void handleAuthenticationRequest(String inputLine, 
                PrintWriter out) throws IOException {
            String[] parts = inputLine.split(",");

            System.out.println("Autenticacion: " + inputLine);
            int operationCode = Integer.parseInt(parts[0]);
            String username = parts[1];
            String password = parts[2];

            String Rolparts = parts[3].substring(1);
            System.out.println("Partes del rol: " + Rolparts);
            String[] AuthUserparts = Rolparts.split("-");

            String rol = AuthUserparts[0];

            System.out.println("Rol del usuario: " + rol);

            switch (operationCode) {
                case 0:
                    String isAuthenticated = 
                   fileUserAuthentication.authenticateUser(username, password);

                    String[] Userparts = isAuthenticated.split("-");
                    System.out.println("Partes: " + isAuthenticated);
                    if (Boolean.parseBoolean(Userparts[0])) {
                        out.println("auth exitoso true-" + Userparts[1]);
                    } else {
                        out.println("auth exitoso false");
                        clientSocket.close();
                    }
                    break;
                case 1:
                    fileUserAuthentication.registerUser(username, password, rol);
                    out.println("auth exitoso true");
                    break;
                default:
                    System.err.println("Código de operación no válido");
                    break;
            }
        }
        /**
         * Maneja otras solicitudes de los clientes, como solicitudes de
         * archivos o listas de archivos.
         *
         * @param inputLine La línea de entrada recibida del cliente.
         * @param out El PrintWriter para enviar respuestas al cliente.
         * @param fileOut El OutputStream para enviar archivos al cliente.
         * @param rol El rol del usuario.
         */
        private void handleOtherRequests(String inputLine, PrintWriter out, 
                OutputStream fileOut, String rol) {
            if (inputLine.startsWith("GET_")) {
                String requestType = inputLine.substring(4);
                switch (requestType) {
                    case "VIDEOS":
                        List<File> videos = MediaManager.getVideos();
                        List<File> combinedVideos = new ArrayList<>(videos);

                        if (Boolean.parseBoolean(rol)) {
                        List<File> videosAdmin = MediaManager.getVideosAdmin();
                        combinedVideos.addAll(videosAdmin);
                        }
                        sendFilesList(out, combinedVideos);

                        break;

                    case "FILES":
                        List<File> files = MediaManager.getFiles();
                        sendFilesList(out, files);
                        break;
                    case "MUSIC":
                        List<File> music = MediaManager.getMusic();
                        sendFilesList(out, music);
                        break;
                    default:
                        System.err.println("Tipo de solicitud no válido: "
                                + requestType);
                        break;
                }
            } else if (inputLine.startsWith("DOWNLOAD_")) {

                String requestType = inputLine.substring(9);
                if (requestType.startsWith("MUSIC_")) {
                    String song = requestType.substring(6);
                    String songFilePath = "media/musica/" + song;

                    String AdminsongFilePath = "media/musicaAdmin/" + song;

                    sendFileToClient(songFilePath, AdminsongFilePath, 
                            rol, fileOut);

                } else if (requestType.startsWith("VIDEO_")) {
                    String video = requestType.substring(6);
                    String videoFilePath = "media/videos/" + video;

                    String AdminvideoFilePath = "media/videosAdmin/" + video;

                    sendFileToClient(videoFilePath, AdminvideoFilePath, 
                            rol, fileOut);
                } else if (requestType.startsWith("FILE_")) {
                    String file = requestType.substring(5);
                    String fileFilePath = "media/documentos/" + file;

                    String AdminfileFilePath = "media/documentosAdmin/" 
                            + file;

                    sendFileToClient(fileFilePath, AdminfileFilePath, 
                            rol, fileOut);
                } else {
                    System.out.println("Tipo de dato no existe");
                }
            }
        }
        /**
         * Envía una lista de archivos al cliente.
         *
         * @param out El PrintWriter para enviar la lista de archivos al
         * cliente.
         * @param files La lista de archivos a enviar.
         */
        private void sendFilesList(PrintWriter out, List<File> files) {
            StringBuilder sb = new StringBuilder();
            for (File file : files) {
                sb.append(file.getName()).append(";");
            }
            sb.append("END_OF_LIST");
            out.println(sb.toString());
        }
        /**
         * Envía un archivo específico al cliente.
         *
         * @param filePath La ruta del archivo para usuarios normales.
         * @param adminPath La ruta del archivo para usuarios administradores.
         * @param rol El rol del usuario.
         * @param out El OutputStream para enviar el archivo al cliente.
         */
        private void sendFileToClient(String filePath, String adminPath, 
                String rol, OutputStream out) {
            File file = new File(filePath);
            if (!file.exists()) {

                if (Boolean.parseBoolean(rol)) {
                    file = new File(adminPath);
                } else {
                    System.out.println("El archivo no existe");
                    return;
                }

            }

            try (FileInputStream fileInputStream = new FileInputStream(file); 
            BufferedInputStream bufferedInputStream = new 
            BufferedInputStream(fileInputStream)) {

                byte[] fileData = new byte[(int) file.length()];
                bufferedInputStream.read(fileData, 0, fileData.length);

                out.write(fileData, 0, fileData.length);
                out.flush();

                System.out.println("Archivo enviado: " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // Cerrar el flujo de salida
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
