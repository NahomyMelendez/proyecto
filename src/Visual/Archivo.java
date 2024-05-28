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
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;

/**
 * Constructor de la clase Archivo.
 *
 * @param UserName Nombre de usuario
 * @param auth Indicador de autenticación del usuario
 * @param admin Indicador de si el usuario es administrador
 * @throws IOException Si ocurre un error al conectarse al servidor
 */
public class Archivo extends javax.swing.JFrame {

    private static final String SERVER_ADDRESS = "192.168.1.35"; // Cambia esto con la dirección IP de tu servidor
    private static final int SERVER_PORT = 5050;

    public Archivo(String UserName, Boolean auth,
            Boolean admin) throws IOException {
        initComponents();
        connectToServer();

        jListArch.getSelectionModel().
                addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                // Obtiene la canción seleccionada
                String selectedFile = jListArch.getSelectedValue();
                if (selectedFile != null) {
                    try {
                        // Muestra la interfaz para reproducir la canción
                        downloadFileFromServer(selectedFile, "FILE");
                        showFilePlayer(selectedFile);
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }

                }
            }
        });
    }

    /**
     * Descarga un archivo del servidor.
     *
     * @param file Nombre del archivo a descargar
     * @param tipo Tipo de archivo a descargar
     * @throws IOException Si ocurre un error durante la descarga del archivo
     */
    private void downloadFileFromServer(String file, String tipo)
            throws IOException {
        int TIMEOUT = 500;

        // Verificar si el usuario está autenticado
        if (Login.sharedAuth) {
            // Crear una nueva instancia de ClientCommunication y un nuevo socket
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); 
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.
           getInputStream())); OutputStream binaryOut = socket.
           getOutputStream(); InputStream binaryIn = socket.getInputStream()) {

                ClientCommunication clientCommunication =
                new ClientCommunication(socket, out, in, binaryOut, binaryIn);
                clientCommunication.sendMessage("DOWNLOAD_" + 
                tipo + "_" + file + "-" + Login.shareRol +
                "-" + Login.sharedAuth);
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
     * Se conecta al servidor para obtener la lista de archivos disponibles.
     *
     * @throws IOException Si ocurre un error durante la conexión al servidor
     */
    private void connectToServer() throws IOException {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); OutputStream binaryOut = socket.getOutputStream(); InputStream binaryIn = socket.getInputStream()) {
            ClientCommunication clientCommunication = new ClientCommunication
            (socket, out, in, binaryOut, binaryIn);
            // Enviar solicitud al servidor para obtener la lista de videos
            clientCommunication.sendMessage("GET_FILES-" + Login.shareRol +
            "-" + Login.sharedAuth);

            // Recibir respuesta del servidor
            String serverResponseFiles = clientCommunication.receiveMessage();
            System.out.println("Files from server: " + serverResponseFiles);
            // Procesar la respuesta del servidor y actualizar la lista de archivos
            ListModel<String> fileListModel =
            processServerResponse(serverResponseFiles);
            updateFileList(fileListModel);

            clientCommunication.close();
        } catch (IOException e) {
        }
    }

    /**
     * Muestra la interfaz para reproducir el archivo seleccionado.
     *
     * @param selectedFile Nombre del archivo seleccionado
     */
    private void showFilePlayer(String selectedFile) {
        // Mostrar la interfaz para reproducir el video seleccionado
        ArchivoM archivo = new ArchivoM(selectedFile);
        archivo.setVisible(true);
    }

    /**
     * Procesa la respuesta del servidor para crear un modelo de lista de
     * archivos.
     *
     * @param response Respuesta del servidor
     * @return Modelo de lista de archivos
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

    /**
     * Actualiza la lista de archivos en la interfaz de usuario.
     *
     * @param files Modelo de lista de archivos
     */
    public void updateFileList(ListModel<String> files) {
        jListArch.setModel(files);
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
        jListArch = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jDesktopPane1.setBackground(new java.awt.Color(204, 153, 255));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel1.setText("Achivos Disponibles");

        jListArch.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(jListArch);

        jButton1.setText("Volver");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jDesktopPane1.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(jLabel1))
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jButton1)))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(28, 28, 28))
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        /* Client volverr= new Client();
            volverr.setVisible(true);
            this.dispose();*/
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jListArch;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
