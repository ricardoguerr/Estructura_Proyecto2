package core;

import utils.LogManager;
import java.io.File;
import java.io.IOException;

public class FileProcessor {

    private HuffmanCompressor compressor;
    private HuffmanDecompressor decompressor;
    private Encryptor encryptor;

    // Constantes para las operaciones
    public static final int OP_COMPRESS_ONLY = 1;
    public static final int OP_ENCRYPT_ONLY = 2;
    public static final int OP_COMPRESS_AND_ENCRYPT = 3;

    public FileProcessor() {
        this.compressor = new HuffmanCompressor();
        this.decompressor = new HuffmanDecompressor();
        this.encryptor = new Encryptor();
    }

    /**
     * Procesa un archivo de entrada basado en la operación seleccionada.
     * @param inputPath  Archivo original
     * @param outputPath Archivo final
     * @param operation  El tipo de operación (COMPRESS_ONLY, ENCRYPT_ONLY, etc.)
     * @param password   La contraseña (necesaria para encriptar)
     */
    public void processFile(String inputPath, String outputPath, int operation, String password) throws IOException {

        long startTime = System.nanoTime();
        File inputFile = new File(inputPath);
        long originalSize = inputFile.length();
        String operationName = "";

        // Usaremos un nombre de archivo temporal
        String tempFile = inputPath + ".temp";

        try {
            switch (operation) {
                case OP_COMPRESS_ONLY: // Solo Comprimir (.cmp)
                    operationName = "Compresión";
                    System.out.println("Procesando: Solo Compresión...");
                    compressor.compress(inputPath, outputPath);
                    break;

                case OP_ENCRYPT_ONLY: // Solo Encriptar (.enc)
                    operationName = "Encriptación";
                    System.out.println("Procesando: Solo Encriptación...");
                    encryptor.processFile(inputPath, outputPath, password);
                    break;

                case OP_COMPRESS_AND_ENCRYPT: // Ambos (.ec)
                    operationName = "Compresión y Encriptación";
                    System.out.println("Procesando: Compresión y Encriptación...");

                    // 1. Comprimir a un archivo temporal
                    compressor.compress(inputPath, tempFile);

                    // 2. Encriptar el archivo temporal al archivo de salida
                    encryptor.processFile(tempFile, outputPath, password);

                    // 3. Borrar el archivo temporal
                    new File(tempFile).delete();
                    break;

                default:
                    throw new IllegalArgumentException("Operación no válida: " + operation);
            }

            // Calcular tiempo y tasa de compresión
            long endTime = System.nanoTime();
            long timeMs = (endTime - startTime) / 1_000_000; // Convertir a milisegundos

            File outputFile = new File(outputPath);
            long finalSize = outputFile.length();

            // Calcular tasa de compresión (solo si hay compresión)
            double compressionRatio = 0;
            if (operation == OP_COMPRESS_ONLY || operation == OP_COMPRESS_AND_ENCRYPT) {
                compressionRatio = (double) originalSize / finalSize;
            }

            // Registrar en el log
            LogManager.logOperation(operationName, inputFile.getName(), timeMs, compressionRatio, originalSize, finalSize);

            System.out.println("Proceso de guardado finalizado en: " + outputPath);

        } catch (Exception e) {
            LogManager.logError(operationName, inputFile.getName(), e.getMessage());
            throw e;
        }
    }

    /**
     * Recupera (descomprime/desencripta) un archivo.
     * Detecta la operación necesaria basado en la extensión.
     * @param inputPath  Archivo a recuperar (.cmp, .enc, .ec)
     * @param outputPath Archivo de salida (el original)
     ** @param password   La contraseña (necesaria para desencriptar)
     */
    public void recoverFile(String inputPath, String outputPath, String password) throws IOException {

        long startTime = System.nanoTime();
        File inputFile = new File(inputPath);
        long originalSize = inputFile.length();
        String operationName = "";

        String tempFile = outputPath + ".temp";

        try {
            if (inputPath.endsWith(".cmp")) {
                // Solo Descomprimir
                operationName = "Descompresión";
                System.out.println("Recuperando: Solo Descompresión...");
                decompressor.decompress(inputPath, outputPath);

            } else if (inputPath.endsWith(".enc")) {
                // Solo Desencriptar
                operationName = "Desencriptación";
                System.out.println("Recuperando: Solo Desencriptación...");
                encryptor.processFile(inputPath, outputPath, password);

            } else if (inputPath.endsWith(".ec")) {
                // Desencriptar Y Descomprimir
                operationName = "Desencriptación y Descompresión";
                System.out.println("Recuperando: Desencriptación y Descompresión...");

                // 1. Desencriptar a un archivo temporal
                encryptor.processFile(inputPath, tempFile, password);

                // 2. Descomprimir el temporal al archivo de salida
                decompressor.decompress(tempFile, outputPath);

                // 3. Borrar el archivo temporal
                new File(tempFile).delete();

            } else {
                throw new IllegalArgumentException("Extensión de archivo no reconocida para recuperación: " + inputPath);
            }

            // Calcular tiempo
            long endTime = System.nanoTime();
            long timeMs = (endTime - startTime) / 1_000_000;

            File outputFile = new File(outputPath);
            long finalSize = outputFile.length();

            // Para recuperación, la "tasa de compresión" sería inversa
            double compressionRatio = 0;
            if (inputPath.endsWith(".cmp") || inputPath.endsWith(".ec")) {
                compressionRatio = (double) finalSize / originalSize;
            }

            // Registrar en el log
            LogManager.logOperation(operationName, inputFile.getName(), timeMs, compressionRatio, originalSize, finalSize);

            System.out.println("Proceso de recuperación finalizado en: " + outputPath);

        } catch (Exception e) {
            LogManager.logError(operationName, inputFile.getName(), e.getMessage());
            throw e;
        }
    }
}