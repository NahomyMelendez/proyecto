package JavaServe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * La clase FileUserAuthenticationNoEncrypt proporciona métodos para autenticar
 * y registrar usuarios sin cifrado. Los usuarios y sus contraseñas se almacenan
 * en un archivo de texto.
 */
public class FileUserAuthenticationNoEncrypt {

    private static final String FILE_PATH = "users.txt";

    /**
     * Verifica la contraseña del usuario comparándola con la almacenada en el
     * archivo de usuarios.
     *
     * @param username el nombre de usuario
     * @param password la contraseña del usuario
     * @return "true-<rol>" si la autenticación es exitosa, "false," en caso
     * contrario
     */
    private String verifyPassword(String username, String password) {
        try (BufferedReader reader =
                new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Linea: " + line);
                String[] parts = line.split(",");
                System.out.println(parts);
                String storedUsername = parts[0];
                String storedPassword = parts[1];
                String rol = parts[2];

                // Verifica si el usuario y la contraseña coinciden
                if (username.equals(storedUsername) &&
                        password.equals(storedPassword)) {
                    return "true-" + rol; // Autenticación exitosa
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de usuarios: " 
                    
                    + e.getMessage());
        }
        return "false,"; // Autenticación fallida
    }

    /**
     * Autentica un usuario verificando su nombre de usuario y contraseña.
     *
     * @param username el nombre de usuario
     * @param password la contraseña del usuario
     * @return "true-<rol>" si la autenticación es exitosa, "false," en caso
     * contrario
     */
    public String authenticateUser(String username, String password) {
        return verifyPassword(username, password);
    }

    /**
     * Registra un nuevo usuario agregando su nombre de usuario, contraseña y
     * rol al archivo de usuarios.
     *
     * @param username el nombre de usuario
     * @param password la contraseña del usuario
     * @param rol el rol del usuario
     */

    public void registerUser(String username, String password, String rol) {
        System.out.println(username + " " + password);
        try (BufferedWriter writer = new BufferedWriter
        (new FileWriter(FILE_PATH, true))) {
            // Escribe el nuevo usuario en el archivo
            writer.write(username + "," + password + "," + rol);
            writer.newLine();
            System.out.println("Usuario registrado con éxito.");
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de usuarios: "
                    + e.getMessage());
        }
    }

}
