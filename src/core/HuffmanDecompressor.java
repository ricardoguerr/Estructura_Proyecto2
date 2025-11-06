package core;

import utils.HuffmanNode;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanDecompressor {

    private HuffmanNode huffmanTreeRoot; // La raíz del árbol
    private Map<Byte, Integer> freqTable; // La tabla de frecuencias

    public HuffmanDecompressor() {
        this.freqTable = new HashMap<>();
    }

    // --- PASO 1: Leer el Encabezado ---
    // Lee el encabezado del archivo .cmp y guarda la tabla de frecuencias
    private void readHeader(DataInputStream dis) throws IOException {
        // 1. Leer cuántas entradas hay en la tabla
        int tableSize = dis.readInt();

        // 2. Limpiar la tabla por si se usa la clase varias veces
        this.freqTable.clear();

        // 3. Leer cada entrada (byte y su frecuencia)
        for (int i = 0; i < tableSize; i++) {
            byte b = dis.readByte();
            int freq = dis.readInt();
            this.freqTable.put(b, freq);
        }

        // 4. Calcular el total de bytes originales (lo necesitaremos)
        // (Esto lo haremos después, al leer los datos)
    }

    // --- PASO 2: Reconstruir el Árbol ---
    // (Este método es IDÉNTICO al de HuffmanCompressor)
    private void buildHuffmanTree() {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

        // 1. Crear hojas y meterlas a la cola
        for (Map.Entry<Byte, Integer> entry : this.freqTable.entrySet()) {
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

        // 3. Guardar la raíz del árbol
        this.huffmanTreeRoot = pq.poll();
    }

    // --- PASO 3: Descomprimir los Datos ---
    private void decodeData(DataInputStream dis, FileOutputStream fos) throws IOException {

        // Calculamos el número total de bytes que debemos escribir
        long totalBytes = 0;
        for (int freq : this.freqTable.values()) {
            totalBytes += freq;
        }

        long bytesWritten = 0;
        HuffmanNode currentNode = this.huffmanTreeRoot; // Empezamos en la raíz

        int byteRead;
        // 1. Leer el resto del archivo, byte por byte
        while ((byteRead = dis.read()) != -1) {

            // 2. Procesar cada bit dentro de ese byte
            for (int i = 7; i >= 0; i--) {
                // Si ya escribimos todos los bytes, paramos.
                if (bytesWritten >= totalBytes) {
                    return;
                }

                // 3. Obtenemos el bit (0 o 1)
                // (byteRead >> i) & 1: Desplaza el bit 'i' a la posición 0 y lo aísla
                int bit = (byteRead >> i) & 1;

                // 4. Navegamos por el árbol
                if (bit == 0) {
                    currentNode = currentNode.left;
                } else { // bit == 1
                    currentNode = currentNode.right;
                }

                // 5. Si llegamos a una hoja...
                if (currentNode.isLeaf()) {
                    // Escribimos el byte original en el archivo de salida
                    fos.write(currentNode.data);
                    bytesWritten++;

                    // Y volvemos a la raíz para buscar el siguiente byte
                    currentNode = this.huffmanTreeRoot;
                }
            }
        }
    }

    /**
     * Descomprime un archivo .cmp de Huffman.
     * @param inputFilePath Archivo de entrada (ej. "archivo.cmp")
     * @param outputFilePath Archivo de salida (ej. "archivo_original.txt")
     */
    public void decompress(String inputFilePath, String outputFilePath) throws IOException {

        // Usamos try-with-resources para que se cierren solos
        try (FileInputStream fis = new FileInputStream(inputFilePath);
             DataInputStream dis = new DataInputStream(fis);
             FileOutputStream fos = new FileOutputStream(outputFilePath)) {

            // 1. Leer el encabezado para llenar la tabla de frecuencias
            readHeader(dis);

            // 2. Reconstruir el árbol de Huffman
            buildHuffmanTree();

            // 3. Decodificar los datos y escribir el archivo original
            decodeData(dis, fos);
        }

        System.out.println("¡Archivo descomprimido exitosamente en: " + outputFilePath + "!");
    }
}