package utils;

// Debe ser "Comparable" para que la PriorityQueue sepa cómo ordenarlos.
public class HuffmanNode implements Comparable<HuffmanNode> {

    public int frequency;
    public byte data; // El byte (0-255). Solo para las hojas.
    public HuffmanNode left, right;

    // Constructor para un nodo "hoja" (que tiene un caracter)
    public HuffmanNode(byte data, int frequency) {
        this.data = data;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    // Constructor para un nodo "interno" (que une dos nodos)
    public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
        this.data = 0; // Nodo interno no tiene dato
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    // Método para saber si es una hoja
    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    // Esto es lo que usa la PriorityQueue para ordenar.
    // Siempre pone primero el de MENOR frecuencia.
    @Override
    public int compareTo(HuffmanNode other) {
        return this.frequency - other.frequency;
    }
}