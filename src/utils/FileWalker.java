package utils;

import core.FileProcessor; // Importamos la clase orquestadora
import java.io.File;
import java.io.IOException;

public class FileWalker {

    /**
     * Procesa recursivamente una carpeta para comprimir/encriptar.
     * @param inputFolder    La carpeta de entrada con los archivos originales.
     * @param outputFolder   La carpeta de salida donde se guardarán los archivos procesados.
     * @param processor      La instancia de FileProcessor para hacer el trabajo.
     * @param operation      La operación a realizar (de FileProcessor.OP_...)
     * @param password       La contraseña (si es necesaria).
     * @param newExtension   La extensión que se añadirá (ej. ".cmp", ".enc", ".ec").
     */
    public void processFolder(File inputFolder, File outputFolder, FileProcessor processor, int operation, String password, String newExtension) throws IOException {
        
        // Si la carpeta de salida no existe, la creamos.
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // Llamamos al método recursivo privado
        walkAndProcess(inputFolder, outputFolder, processor, operation, password, newExtension);
    }

    /**
     * Método recursivo privado para procesar carpetas.
     */
    private void walkAndProcess(File currentInput, File currentOutput, FileProcessor processor, int operation, String password, String newExtension) throws IOException {
        
        // 1. Listar todos los archivos y carpetas del directorio actual
        File[] files = currentInput.listFiles();
        if (files == null) {
            return; // Carpeta vacía o error
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // --- Si es un Directorio ---
                // 1. Creamos el nuevo directorio correspondiente en la salida
                File newOutputDir = new File(currentOutput, file.getName());
                newOutputDir.mkdir();
                
                // 2. Llamada recursiva: entramos a la subcarpeta
                walkAndProcess(file, newOutputDir, processor, operation, password, newExtension);

            } else if (file.isFile()) {
                // --- Si es un Archivo ---
                // 1. Definimos la ruta de entrada
                String inputPath = file.getAbsolutePath();
                
                // 2. Definimos la ruta de salida (con la nueva extensión)
                String outputPath = new File(currentOutput, file.getName() + newExtension).getAbsolutePath();
                
                // 3. Llamamos al FileProcessor para que haga el trabajo
                System.out.println("Procesando archivo: " + inputPath);
                processor.processFile(inputPath, outputPath, operation, password);
            }
        }
    }

    /**
     * Recupera recursivamente una carpeta.
     * @param inputFolder  La carpeta de entrada con archivos (.cmp, .enc, .ec).
     * @param outputFolder La carpeta de salida donde se guardarán los archivos originales.
     * @param processor    La instancia de FileProcessor.
     * @param password     La contraseña (si es necesaria).
     */
    public void recoverFolder(File inputFolder, File outputFolder, FileProcessor processor, String password) throws IOException {
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        
        // Llamamos al método recursivo privado
        walkAndRecover(inputFolder, outputFolder, processor, password);
    }

    /**
     * Método recursivo privado para recuperar carpetas.
     */
    private void walkAndRecover(File currentInput, File currentOutput, FileProcessor processor, String password) throws IOException {
        
        File[] files = currentInput.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // --- Si es un Directorio ---
                File newOutputDir = new File(currentOutput, file.getName());
                newOutputDir.mkdir();
                walkAndRecover(file, newOutputDir, processor, password);

            } else if (file.isFile()) {
                // --- Si es un Archivo ---
                String inputPath = file.getAbsolutePath();
                
                // 2. Obtenemos el nombre original (quitando la extensión .cmp, .enc, o .ec)
                String originalName = getOriginalName(file.getName());
                String outputPath = new File(currentOutput, originalName).getAbsolutePath();

                // 3. Llamamos al FileProcessor para que recupere el archivo
                System.out.println("Recuperando archivo: " + inputPath);
                processor.recoverFile(inputPath, outputPath, password);
            }
        }
    }

    /**
     * Utilidad para quitar la extensión (ej. "texto.txt.cmp" -> "texto.txt")
     */
    private String getOriginalName(String fileName) {
        if (fileName.endsWith(".cmp") || fileName.endsWith(".enc") || fileName.endsWith(".ec")) {
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0) {
                return fileName.substring(0, lastDot);
            }
        }
        return fileName; // Si no tiene una extensión conocida, devuelve el nombre tal cual
    }
}