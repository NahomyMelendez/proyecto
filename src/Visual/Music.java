/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Visual;

import JavaServe.ClientCommunication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;

/**
 * La clase Music representa la interfaz gráfica para la lista de música.
 * Permite al usuario autenticado seleccionar y descargar canciones desde el
 * servidor. Se conecta al servidor para obtener la lista de canciones
 * disponibles y muestra un reproductor de música para la canción seleccionada.
 * Extiende javax.swing.JFrame.
 *
 * @see javax.swing.JFrame
 */
public class Music extends javax.swing.JFrame {

    private static final String SERVER_ADDRESS = "192.168.1.35"; // Cambia esto con la dirección IP de tu servidor
    private static final int SERVER_PORT = 5050;

    /**
     * Constructor de la clase Music. Inicializa los componentes, se conecta al
     * servidor y configura el listener para la selección de canciones.
     *
     * @param auth Estado de autenticación del usuario.
     * @param admin Estado de rol de administrador del usuario.
     * @throws IOException Si ocurre un error durante la conexión al servidor.
     */
    public Music(Boolean auth, Boolean admin) throws IOException {
        initComponents();
        connectToServer();
        initializeJavaFX();
        jListMus.getSelectionModel().
                addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                // Obtiene la canción seleccionada
                String selectedSong = jListMus.getSelectedValue();
                if (selectedSong != null) {
                    try {
                        downloadFileFromServer(selectedSong, "MUSIC");
                        showMusicPlayer(selectedSong);
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }

                }
            }
        });
    }

    /**
     * Se conecta al servidor para obtener la lista de canciones disponibles.
     * Envía una solicitud al servidor y procesa la respuesta para actualizar la
     * lista de canciones.
     *
     * @throws IOException Si ocurre un error durante la conexión al servidor.
     */
    private void connectToServer() throws IOException {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); 
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.
             getInputStream())); OutputStream binaryOut = socket.
            getOutputStream(); InputStream binaryIn = socket.getInputStream()) {
            ClientCommunication clientCommunication = 
                 new ClientCommunication(socket, out, in, binaryOut, binaryIn);
            // Enviar solicitud al servidor para obtener la lista de videos
            clientCommunication.sendMessage("GET_MUSIC" + 
                    "-" + Login.shareRol + "-" + Login.sharedAuth);

            // Recibir respuesta del servidor
            String serverResponse = clientCommunication.receiveMessage();
            System.out.println("Respuesta del servidor para el cliente: "
                    + serverResponse);
            // Procesar la respuesta del servidor y actualizar la lista de música
            ListModel<String> songs = processServerResponse(serverResponse);
            updateSongList(songs);
            clientCommunication.close();
        } catch (IOException e) {
        }
    }

    /**
     * Descarga un archivo del servidor.
     *
     * @param file Nombre del archivo a descargar.
     * @param tipo Tipo de archivo a descargar (por ejemplo, "MUSIC").
     * @throws IOException Si ocurre un error durante la descarga del archivo.
     */
    // Esta función sirve para descargar un unico archivo
    private void downloadFileFromServer(String file,
            String tipo) throws IOException {
        int TIMEOUT = 500;

        // Verificar si el usuario está autenticado
        if (Login.sharedAuth) {
            // Crear una nueva instancia de ClientCommunication y un nuevo socket
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); 
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); 
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.
           getInputStream())); OutputStream binaryOut = socket.getOutputStream()
                   ; InputStream binaryIn = socket.getInputStream()) {

                ClientCommunication clientCommunication =
                 new ClientCommunication(socket, out, in, binaryOut, binaryIn);
                clientCommunication.sendMessage("DOWNLOAD_" + tipo + 
                "_" + file + "-" + Login.shareRol + "-" + Login.sharedAuth);
                clientCommunication.receiveFileFromServer("download/" + file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Usuario no autenticado, no se puede descargar el archivo
            System.err.println("El usuario no está autenticado.");
        }

        // Agregar un tiempo de espera entre cada solicitud
        try {
            TimeUnit.MILLISECONDS.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra la interfaz para reproducir la canción seleccionada.
     *
     * @param selectedSong Canción seleccionada.
     * @throws IOException Si ocurre un error al mostrar el reproductor de
     * música.
     */
    private void showMusicPlayer(String selectedSong) throws IOException {
        // Mostrar la interfaz para reproducir la canción seleccionada
        MusicM playerFrame = new MusicM(selectedSong);
        playerFrame.setVisible(true);
    }

    /**
     * Actualiza la lista de canciones en la interfaz de usuario.
     *
     * @param songs Modelo de lista de canciones.
     */
    public void updateSongList(ListModel<String> songs) {
        // Actualizar la lista de canciones en la interfaz de usuario
        jListMus.setModel(songs);
    }

    /**
     * Procesa la respuesta del servidor y crea un modelo de lista.
     *
     * @param response Respuesta del servidor.
     * @return Modelo de lista de canciones.
     */
    private ListModel<String> processServerResponse(String response) {
        // Procesar la respuesta del servidor y crear un modelo de lista
        DefaultListModel<String> songListModel = new DefaultListModel<>();
        String[] songsArray = response.split(";"); // Suponiendo que ';' es el delimitador

        for (String song : songsArray) {
            if (song.equals("END_OF_LIST")) {
                break; // Detener el ciclo si encuentra la cadena "END_OF_LIST"
            }
            songListModel.addElement(song);
        }

        return songListModel;
    }

    private void initializeJavaFX() {
        // Inicializar JavaFX si es necesario
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {
                // No es necesario hacer nada aquí
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListMus = new javax.swing.JList<>();
        button1 = new java.awt.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jDesktopPane1.setBackground(new java.awt.Color(204, 153, 255));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel1.setText("Musica Disponible");

        jListMus.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(jListMus);

        button1.setLabel("Volver");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        jDesktopPane1.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(button1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(jLabel1))
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        /*Client volver= new Client();
            volver.setVisible(true);
            this.dispose();*/
    }//GEN-LAST:event_button1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button1;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jListMus;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
