package core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Encryptor {

    /**
     * Cifra o descifra un archivo usando una contraseña (cifrado XOR).
     * La operación es la misma para encriptar y desencriptar.
     * * @param inputFilePath  Ruta del archivo de entrada
     * @param outputFilePath Ruta del archivo de salida
     * @param password       La contraseña para el cifrado
     * @throws IOException
     */
    public void processFile(String inputFilePath, String outputFilePath, String password) throws IOException {

        // No podemos usar una contraseña vacía
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        // Convertimos la contraseña a bytes
        byte[] key = password.getBytes();
        int keyLength = key.length;

        // Usamos try-with-resources para el manejo automático de archivos
        try (InputStream is = new FileInputStream(inputFilePath);
             OutputStream os = new FileOutputStream(outputFilePath)) {

            int byteRead;
            int keyIndex = 0; // Para saber qué byte de la contraseña usar

            // Leemos el archivo de entrada byte por byte
            while ((byteRead = is.read()) != -1) {

                // --- El núcleo del cifrado XOR ---
                // 1. Tomamos el byte del archivo (byteRead)
                // 2. Tomamos el byte de la contraseña (key[keyIndex])
                // 3. Aplicamos la operación XOR (^)
                byte outputByte = (byte) (byteRead ^ key[keyIndex]);

                // Escribimos el byte resultante en el archivo de salida
                os.write(outputByte);

                // Movemos el índice de la contraseña
                keyIndex++;

                // Si llegamos al final de la contraseña, volvemos al inicio
                if (keyIndex == keyLength) {
                    keyIndex = 0;
                }
            }
        }

        System.out.println("Archivo procesado (encriptado/desencriptado) exitosamente.");
    }
}