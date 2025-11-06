package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {

    private static final String LOG_FILE = "log.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra una operación exitosa en el archivo de log.
     *
     * @param operationName     Nombre de la operación (Compresión, Encriptación, etc.)
     * @param originalFileName  Nombre del archivo original
     * @param timeMs            Tiempo de la operación en milisegundos
     * @param compressionRatio  Tasa de compresión (0 si no aplica)
     * @param originalSize      Tamaño original en bytes
     * @param finalSize         Tamaño final en bytes
     */
    public static void logOperation(String operationName, String originalFileName,
                                    long timeMs, double compressionRatio,
                                    long originalSize, long finalSize) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {

            String timestamp = dateFormat.format(new Date());

            pw.println("=".repeat(80));
            pw.println("OPERACIÓN: " + operationName);
            pw.println("Fecha y Hora: " + timestamp);
            pw.println("Archivo Original: " + originalFileName);
            pw.println("Tamaño Original: " + formatBytes(originalSize));
            pw.println("Tamaño Final: " + formatBytes(finalSize));
            pw.println("Tiempo de Operación: " + timeMs + " ms (" + (timeMs / 1000.0) + " segundos)");

            if (compressionRatio > 0) {
                pw.println("Tasa de Compresión: " + String.format("%.2f", compressionRatio) + ":1");
                double percentReduction = ((double)(originalSize - finalSize) / originalSize) * 100;
                pw.println("Reducción de Tamaño: " + String.format("%.2f", percentReduction) + "%");
            }

            pw.println("Estado: EXITOSO");
            pw.println("=".repeat(80));
            pw.println();

        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }

    /**
     * Registra un error en el archivo de log.
     *
     * @param operationName    Nombre de la operación que falló
     * @param originalFileName Nombre del archivo
     * @param errorMessage     Mensaje de error
     */
    public static void logError(String operationName, String originalFileName, String errorMessage) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {

            String timestamp = dateFormat.format(new Date());

            pw.println("=".repeat(80));
            pw.println("ERROR EN OPERACIÓN: " + operationName);
            pw.println("Fecha y Hora: " + timestamp);
            pw.println("Archivo: " + originalFileName);
            pw.println("Mensaje de Error: " + errorMessage);
            pw.println("Estado: FALLIDO");
            pw.println("=".repeat(80));
            pw.println();

        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }

    /**
     * Formatea bytes a un formato legible (KB, MB, GB).
     */
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.2f %s", bytes / Math.pow(1024, exp), pre);
    }
}