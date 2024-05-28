package JavaServe;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * La clase FileUserAuthentication proporciona métodos para autenticar y
 * registrar usuarios utilizando el cifrado SHA-256 y una sal aleatoria. Los
 * usuarios y sus contraseñas cifradas se almacenan en un archivo de texto.
 */
public class FileUserAuthentication {

    private static final String FILE_PATH = "users.txt";

    /**
     * Genera una sal aleatoria de 16 bytes.
     *
     * @return un array de bytes que representa la sal
     */
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Hashea la contraseña junto con la sal utilizando el algoritmo SHA-256.
     *
     * @param password la contraseña a hashear
     * @param salt la sal a utilizar en el hash
     * @return una cadena que representa la contraseña hasheada
     * @throws NoSuchAlgorithmException si el algoritmo SHA-256 no está
     * disponible
     */
    // Hashea la contraseña junto con la sal utilizando el algoritmo SHA-256
    private String hashPassword(String password, byte[] salt) 
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes());
        return bytesToBinaryString(hashedPassword);
    }

    /**
     * Verifica si la contraseña ingresada coincide con la contraseña
     * almacenada.
     *
     * @param password la contraseña ingresada por el usuario
     * @param hashedPassword la contraseña hasheada almacenada
     * @param salt la sal utilizada para hashear la contraseña almacenada
     * @return true si la contraseña ingresada coincide con la almacenada, false
     * en caso contrario
     * @throws NoSuchAlgorithmException si el algoritmo SHA-256 no está
     * disponible
     */
    // Verifica si la contraseña ingresada coincide con la contraseña almacenada
    private boolean verifyPassword(String password, String hashedPassword,
            byte[] salt) throws NoSuchAlgorithmException {
        String hashedAttempt = hashPassword(password, salt);
        System.out.println(hashedAttempt.equals(hashedPassword));
        return hashedAttempt.equals(hashedPassword);
    }

    /**
     * Autentica un usuario verificando su nombre de usuario y contraseña.
     *
     * @param username el nombre de usuario
     * @param password la contraseña del usuario
     * @return "true-<rol>" si la autenticación es exitosa, "false" en caso
     * contrario
     */
    // Método para autenticar un usuario
    public String authenticateUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader
        (new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Divide la línea en usuario, contraseña hasheada y sal usando un delimitador (por ejemplo, coma)
                String[] parts = line.split(",");
                System.out.println("Linea: " + line);
                String storedUsername = parts[0];
                String hashedPassword = parts[1];
                byte[] salt = binaryStringToBytes(parts[2]);
                String rol = parts[3];

                // Verifica si el usuario y la contraseña coinciden
                if (username.equals(storedUsername) &&
                        verifyPassword(password, hashedPassword, salt)) {
                    return "true-" + rol; // Autenticación exitosa
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error al leer el archivo de usuarios: " 
                    + e.getMessage());
        }
        return "false"; // Autenticación fallida
    }

    /**
     * Registra un nuevo usuario agregando su nombre de usuario, contraseña
     * hasheada y rol al archivo de usuarios.
     *
     * @param username el nombre de usuario
     * @param password la contraseña del usuario
     * @param rol el rol del usuario
     */
    // Método para registrar un nuevo usuario
    public void registerUser(String username, String password, String rol) {
        try (BufferedWriter writer = new BufferedWriter
        (new FileWriter(FILE_PATH, true))) {
            // Genera una sal aleatoria
            byte[] salt = generateSalt();
            // Hashea la contraseña junto con la sal
            String hashedPassword = hashPassword(password, salt);
            // Codifica la sal en una cadena binaria
            String encodedSalt = bytesToBinaryString(salt);
            // Escribe el nuevo usuario en el archivo
            writer.write(username + "," + hashedPassword + "," 
                    + encodedSalt + "," + rol);
            writer.newLine();
            System.out.println("Usuario registrado con éxito.");
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error al escribir en el archivo de usuarios: " 
                    + e.getMessage());
        }
    }

    /**
     * Codifica un array de bytes en una cadena binaria.
     *
     * @param bytes el array de bytes a codificar
     * @return una cadena que representa el array de bytes en formato binario
     */
    // Método para codificar un array de bytes en una cadena binaria
    private String bytesToBinaryString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%8s", 
           Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return sb.toString();
    }

    /**
     * Decodifica una cadena binaria a un array de bytes.
     *
     * @param binaryString la cadena binaria a decodificar
     * @return un array de bytes que representa la cadena binaria decodificada
     */

    // Método para decodificar una cadena binaria a un array de bytes
    private byte[] binaryStringToBytes(String binaryString) {
        int byteLength = binaryString.length() / 8;
        byte[] bytes = new byte[byteLength];
        for (int i = 0; i < byteLength; i++) {
            bytes[i] = (byte) Integer.parseInt(binaryString
                    .substring(8 * i, 8 * i + 8), 2);
        }
        return bytes;
    }
}
