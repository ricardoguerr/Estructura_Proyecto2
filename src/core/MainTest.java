package core;

import java.io.FileWriter;
import java.io.IOException;

public class MainTest {

    public static void main(String[] args) {

        String inputFile = "test.txt";
        String compressedFile = "test.cmp";
        String decompressedFile = "test_decompressed.txt";

        try {
            // --- 1. Crear un archivo de prueba ---
            System.out.println("Creando archivo de prueba...");
            FileWriter writer = new FileWriter(inputFile);
            writer.write("Hola este es un texto de prueba.");
            writer.write("Vamos a ver si el algoritmo de Huffman funciona correctamente.");
            writer.write("Repetir, repetir, repetir para que haya frecuencias.");
            writer.close();

            // --- 2. Comprimir el archivo ---
            System.out.println("Iniciando compresión...");
            HuffmanCompressor compressor = new HuffmanCompressor();
            compressor.compress(inputFile, compressedFile);
            System.out.println("Compresión finalizada.");

            // --- 3. Descomprimir el archivo ---
            System.out.println("Iniciando descompresión...");
            HuffmanDecompressor decompressor = new HuffmanDecompressor();
            decompressor.decompress(compressedFile, decompressedFile);
            System.out.println("Descompresión finalizada.");

            System.out.println("-------------------------------------------------");
            System.out.println("¡Proceso completado!");
            System.out.println("Por favor, revisa los archivos:");
            System.out.println("1. " + inputFile + " (Original)");
            System.out.println("2. " + compressedFile + " (Comprimido - no será legible)");
            System.out.println("3. " + decompressedFile + " (Descomprimido)");
            System.out.println("Verifica que " + inputFile + " y " + decompressedFile + " sean idénticos.");


        } catch (IOException e) {
            System.err.println("Ocurrió un error durante la prueba:");
            e.printStackTrace();
        }
    }
}