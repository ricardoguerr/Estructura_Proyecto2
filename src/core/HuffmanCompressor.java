package core;

import utils.HuffmanNode;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.FileOutputStream;
import java.io.DataOutputStream;


public class HuffmanCompressor {

    // Mapa para guardar los códigos generados (ej. 'A' -> "01")
    private Map<Byte, String> huffmanCodes;

    // El constructor
    public HuffmanCompressor() {
        this.huffmanCodes = new HashMap<>();
    }

    // --- PASO 1: Contar Frecuencias ---
    private Map<Byte, Integer> buildFrequencyTable(String filePath) throws IOException {
        Map<Byte, Integer> freqTable = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            int byteRead;
            while ((byteRead = fis.read()) != -1) {
                byte b = (byte) byteRead;
                freqTable.put(b, freqTable.getOrDefault(b, 0) + 1);
            }
        }
        return freqTable;
    }

    // --- PASO 2: Construir el Árbol ---
    private HuffmanNode buildHuffmanTree(Map<Byte, Integer> freqTable) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

        // 1. Crear hojas y meterlas a la cola
        for (Map.Entry<Byte, Integer> entry : freqTable.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        // 2. Construir el árbol
        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            int newFreq = left.frequency + right.frequency;
            HuffmanNode parent = new HuffmanNode(newFreq, left, right);
            pq.add(parent);
        }

        // 3. Devolver la raíz del árbol
        return pq.poll();
    }

    // --- PASO 3: Generar Códigos ---
    // Este método "llena" el mapa huffmanCodes
    private void generateCodes(HuffmanNode node, String code) {
        if (node == null) {
            return;
        }

        // Si es una hoja, encontramos el código para un byte
        if (node.isLeaf()) {
            huffmanCodes.put(node.data, code);
            return;
        }

        // Si no es hoja, seguimos recorriendo recursivamente
        // Vamos a la izquierda, agregando un '0' al código
        generateCodes(node.left, code + "0");
        // Vamos a la derecha, agregando un '1' al código
        generateCodes(node.right, code + "1");
    }

    // --- (PASO 4: Escribir el archivo comprimido - AÚN NO IMPLEMENTADO) ---
    // Este será el método público principal

    /**
     * Comprime un archivo usando el algoritmo de Huffman.
     * @param inputFilePath Archivo de entrada
     * @param outputFilePath Archivo de salida (ej. "archivo.cmp")
     */
    // Reemplaza el método compress() que tenías por este:

    /**
     * Comprime un archivo usando el algoritmo de Huffman.
     * @param inputFilePath Archivo de entrada
     * @param outputFilePath Archivo de salida (ej. "archivo.cmp")
     */
    public void compress(String inputFilePath, String outputFilePath) throws IOException {

        // --- PASOS 1, 2 y 3 (Ya los teníamos) ---

        // 1. Contar frecuencias
        Map<Byte, Integer> freqTable = buildFrequencyTable(inputFilePath);

        // 2. Construir el árbol
        HuffmanNode root = buildHuffmanTree(freqTable);

        // 3. Generar los códigos
        this.huffmanCodes.clear();
        generateCodes(root, "");

        // --- PASO 4: Escribir el archivo comprimido ---

        // Usamos try-with-resources para que los streams se cierren solos
        try (FileInputStream fis = new FileInputStream(inputFilePath);
             FileOutputStream fos = new FileOutputStream(outputFilePath);
             DataOutputStream dos = new DataOutputStream(fos)) {

            // --- 4.a: Escribir el Encabezado ---
            writeHeader(dos, freqTable);

            // --- 4.b: Escribir los Datos Comprimidos ---
            writeCompressedData(fis, dos, huffmanCodes);

        }

        System.out.println("¡Archivo comprimido exitosamente en: " + outputFilePath + "!");
    }

    /**
     * Escribe el encabezado del archivo comprimido.
     * El encabezado contendrá la tabla de frecuencias.
     */
    private void writeHeader(DataOutputStream dos, Map<Byte, Integer> freqTable) throws IOException {
        // 1. Escribir el número de entradas únicas (el tamaño del mapa)
        dos.writeInt(freqTable.size());

        // 2. Escribir cada entrada (byte y su frecuencia)
        for (Map.Entry<Byte, Integer> entry : freqTable.entrySet()) {
            dos.writeByte(entry.getKey());   // Escribe el byte
            dos.writeInt(entry.getValue()); // Escribe su frecuencia (int)
        }
    }

    /**
     * Escribe los datos comprimidos (bit a bit).
     */
    private void writeCompressedData(FileInputStream fis, DataOutputStream dos, Map<Byte, String> huffmanCodes) throws IOException {

        byte buffer = 0; // Buffer para acumular bits (un byte a la vez)
        int bitCount = 0; // Contador de cuántos bits hay en el buffer

        int byteRead;
        // 1. Volver a leer el archivo de entrada, byte por byte
        while ((byteRead = fis.read()) != -1) {
            // 2. Obtener el código Huffman para ese byte (ej. "01101")
            String code = huffmanCodes.get((byte) byteRead);

            // 3. Iterar sobre cada '0' o '1' en el código
            for (char c : code.toCharArray()) {
                // 4. Mover el buffer 1 espacio a la izquierda
                buffer = (byte) (buffer << 1);

                // 5. Si el bit es '1', ponemos un 1 en el espacio nuevo
                if (c == '1') {
                    buffer = (byte) (buffer | 1);
                }

                bitCount++; // Incrementamos el contador de bits

                // 6. Si el buffer está lleno (8 bits), lo escribimos al archivo
                if (bitCount == 8) {
                    dos.writeByte(buffer);
                    // Y reiniciamos el buffer y el contador
                    buffer = 0;
                    bitCount = 0;
                }
            }
        }

        // --- Manejar el último byte ---
        // 7. Al final, puede que el buffer no se haya llenado
        if (bitCount > 0) {
            // Rellenamos con '0' los bits restantes
            // (ej. si bitCount=5, movemos 3 espacios a la izq.)
            buffer = (byte) (buffer << (8 - bitCount));
            dos.writeByte(buffer); // Escribimos el último byte
        }
    }

    // (Aquí iría el método writeCompressedFile)
}