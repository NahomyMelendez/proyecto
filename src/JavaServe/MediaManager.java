package JavaServe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * La clase MediaManager proporciona métodos utilitarios para manejar archivos
 * multimedia como música, videos e imágenes. Soporta guardar archivos en el
 * directorio apropiado, copiar archivos y recuperar listas de archivos
 * multimedia específicos.
 */
public class MediaManager {

    private static final String MEDIA_FOLDER_PATH = "media";

    /**
     * Guarda el archivo dado en el directorio de medios apropiado según su
     * tipo.
     *
     * @param file el archivo a guardar
     * @return true si el archivo se guardó correctamente, false en caso
     * contrario
     */
    public static boolean saveFile(File file) {
        if (file != null && file.exists()) {
            String fileName = file.getName();
            String targetDir = MEDIA_FOLDER_PATH;

            if (isMusic(fileName)) {
                targetDir += "/musica";
            } else if (isVideo(fileName)) {
                targetDir += "/videos";
            } else if (fileName.toLowerCase().endsWith(".txt")) {
                targetDir += "/documentos";
            } else {
                return false;
            }

            File dir = new File(targetDir);

            File destinationFile = new File(dir, fileName);
            return copyFile(file, destinationFile);
        }
        return false;
    }

    /**
     * Copia el archivo fuente al archivo de destino.
     *
     * @param source el archivo fuente a copiar
     * @param dest el archivo de destino
     * @return true si el archivo se copió correctamente, false en caso
     * contrario
     */
    private static boolean copyFile(File source, File dest) {
        try (FileChannel sourceChannel = new FileInputStream(source)
            .getChannel(); FileChannel destChannel = new FileOutputStream(dest)
                    .getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera una lista de archivos de imágenes de la carpeta de medios.
     *
     * @return una lista de archivos de imágenes
     */
    // Función para obtener la lista de imágenes en la carpeta de medios
    public static List<File> getImages() {
        List<File> images = new ArrayList<>();
        File mediaFolder = new File(MEDIA_FOLDER_PATH);
        File subFolder = new File(mediaFolder, "imagenes");
        if (subFolder.exists() && subFolder.isDirectory()) {
            File[] files = subFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isImage(file.getName())) {
                        images.add(file);
                    }
                }
            }
        }
        return images;
    }

    /**
     * Recupera una lista de archivos de música de la carpeta de medios.
     *
     * @return una lista de archivos de música
     */
    public static List<File> getMusic() {
        List<File> musicFiles = new ArrayList<>();
        File musicFolder = new File(MEDIA_FOLDER_PATH); // Carpeta principal de medios
        File subFolder = new File(musicFolder, "musica");
        // Verificar si la carpeta de música existe y es un directorio
        if (subFolder.exists() && subFolder.isDirectory()) {
            // Obtener la lista de archivos en la carpeta de música
            File[] files = subFolder.listFiles();
            if (files != null) {
                // Iterar sobre los archivos para filtrar los archivos de música
                for (File file : files) {
                    if (file.isFile() && isMusic(file.getName())) {
                        musicFiles.add(file);
                    }
                }
            }
        }
        return musicFiles;
    }

    /**
     * Recupera una lista de archivos de video de la carpeta de medios.
     *
     * @return una lista de archivos de video
     */
    // Función para obtener la lista de videos en la carpeta de medios
    public static List<File> getVideos() {
        List<File> videos = new ArrayList<>();
        File Videosfolder = new File(MEDIA_FOLDER_PATH);
        File subFolder = new File(Videosfolder, "videos");
        if (subFolder.exists() && subFolder.isDirectory()) {
            File[] files = subFolder.listFiles();
            System.out.println("archivos: " + files);
            if (files != null) {
                for (File file : files) {
                    System.out.println("Video encontrado: " + file.getName());
                    if (file.isFile() && isVideo(file.getName())) {
                        System.out.println("Video añadido: " + file.getName());
                        videos.add(file);
                    }
                }
            }
        }
        return videos;
    }

    /**
     * Recupera una lista de archivos de video de administrador de la carpeta de
     * medios.
     *
     * @return una lista de archivos de video de administrador
     */
    public static List<File> getVideosAdmin() {
        List<File> videos = new ArrayList<>();
        File Videosfolder = new File(MEDIA_FOLDER_PATH);
        File subFolder = new File(Videosfolder, "videosAdmin");
        if (subFolder.exists() && subFolder.isDirectory()) {
            File[] files = subFolder.listFiles();
            System.out.println("archivos: " + files);
            if (files != null) {
                for (File file : files) {
                    System.out.println("Video encontrado: " + file.getName());
                    if (file.isFile() && isVideo(file.getName())) {
                        System.out.println("Video añadido: " + file.getName());
                        videos.add(file);
                    }
                }
            }
        }
        return videos;
    }

    /**
     * Recupera una lista de archivos de documentos de la carpeta de medios.
     *
     * @return una lista de archivos de documentos
     */
    // Función para obtener la lista de archivos en la carpeta de medios
    public static List<File> getFiles() {
        List<File> files = new ArrayList<>();
        File Filesfolder = new File(MEDIA_FOLDER_PATH);
        File subFolder = new File(Filesfolder, "documentos");

        if (subFolder.exists() && subFolder.isDirectory()) {
            File[] allFiles = subFolder.listFiles();
            if (allFiles != null) {
                for (File file : allFiles) {
                    if (file.isFile()) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }

    /**
     * Verifica si el nombre del archivo dado representa un archivo de imagen.
     *
     * @param fileName el nombre del archivo a verificar
     * @return true si el archivo es una imagen, false en caso contrario
     */
    private static boolean isImage(String fileName) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        for (String ext : imageExtensions) {
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si el nombre del archivo dado representa un archivo de música.
     *
     * @param fileName el nombre del archivo a verificar
     * @return true si el archivo es de música, false en caso contrario
     */
    private static boolean isMusic(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        // Aquí puedes agregar o modificar las extensiones de archivo de música que deseas admitir
        return extension.equalsIgnoreCase("mp3") ||
                extension.equalsIgnoreCase("wav")
                || extension.equalsIgnoreCase("flac");
    }

    /**
     * Verifica si el nombre del archivo dado representa un archivo de video.
     *
     * @param fileName el nombre del archivo a verificar
     * @return true si el archivo es un video, false en caso contrario
     */
    // Función auxiliar para verificar si un archivo es un video
    private static boolean isVideo(String fileName) {
        String[] videoExtensions = {".mp4", ".avi", ".mkv", ".mov", ".webm"};
        for (String ext : videoExtensions) {
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
