package gui;

import javax.swing.*;
import core.FileProcessor;
import utils.FileWalker;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainAppGUI extends JFrame {

    private JTextField txtFilePath;
    private JPasswordField txtPassword;
    private JRadioButton rbCompress, rbEncrypt, rbBoth;
    private JButton btnSelectFile, btnProcess, btnRecover;
    private JLabel lblFile, lblOperation, lblPassword;
    private JFileChooser fileChooser;

    private FileProcessor processor;
    private FileWalker walker;
    private File selectedFile;

    public MainAppGUI() {
        this.processor = new FileProcessor();
        this.walker = new FileWalker();
        this.fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        setTitle("Sistema de Gestión de Archivos Seguros y Eficientes");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        // Título
        JLabel lblTitle = new JLabel("SISTEMA DE GESTIÓN DE ARCHIVOS");
        lblTitle.setBounds(130, 10, 300, 25);
        lblTitle.setFont(lblTitle.getFont().deriveFont(14f));
        add(lblTitle);

        // Selección de Archivo
        lblFile = new JLabel("Archivo/Carpeta:");
        lblFile.setBounds(20, 50, 110, 25);
        add(lblFile);

        txtFilePath = new JTextField();
        txtFilePath.setBounds(140, 50, 270, 25);
        txtFilePath.setEditable(false);
        add(txtFilePath);

        btnSelectFile = new JButton("Seleccionar");
        btnSelectFile.setBounds(420, 50, 110, 25);
        add(btnSelectFile);

        // Opciones de Operación
        lblOperation = new JLabel("Operación:");
        lblOperation.setBounds(20, 100, 100, 25);
        add(lblOperation);

        rbCompress = new JRadioButton("Solo Comprimir (.cmp)");
        rbCompress.setBounds(140, 100, 200, 25);
        rbCompress.setSelected(true);

        rbEncrypt = new JRadioButton("Solo Encriptar (.enc)");
        rbEncrypt.setBounds(140, 130, 200, 25);

        rbBoth = new JRadioButton("Comprimir y Encriptar (.ec)");
        rbBoth.setBounds(140, 160, 200, 25);

        ButtonGroup opGroup = new ButtonGroup();
        opGroup.add(rbCompress);
        opGroup.add(rbEncrypt);
        opGroup.add(rbBoth);
        add(rbCompress);
        add(rbEncrypt);
        add(rbBoth);

        // Contraseña
        lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(20, 210, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, 210, 270, 25);
        add(txtPassword);

        JLabel lblNote = new JLabel("(Requerida para encriptación)");
        lblNote.setBounds(140, 235, 250, 20);
        lblNote.setFont(lblNote.getFont().deriveFont(10f));
        add(lblNote);

        // Botones de Acción
        btnProcess = new JButton("Procesar Archivo");
        btnProcess.setBounds(100, 280, 150, 35);
        add(btnProcess);

        btnRecover = new JButton("Recuperar Archivo");
        btnRecover.setBounds(280, 280, 150, 35);
        add(btnRecover);

        // Separador visual
        JSeparator separator = new JSeparator();
        separator.setBounds(20, 330, 510, 2);
        add(separator);

        JLabel lblInfo = new JLabel("Proyecto de Estructura de Datos II - 2024");
        lblInfo.setBounds(130, 340, 300, 20);
        lblInfo.setFont(lblInfo.getFont().deriveFont(10f));
        add(lblInfo);

        // ===== LÓGICA DE BOTONES =====

        // Botón "Seleccionar"
        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(MainAppGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    txtFilePath.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        // Botón "Procesar"
        btnProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarArchivo();
            }
        });

        // Botón "Recuperar"
        btnRecover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recuperarArchivo();
            }
        });
    }

    /**
     * Lógica para procesar (comprimir/encriptar) un archivo o carpeta.
     */
    private void procesarArchivo() {
        // Validaciones
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Error: Debe seleccionar un archivo o carpeta.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String password = new String(txtPassword.getPassword());
        if ((rbEncrypt.isSelected() || rbBoth.isSelected()) && password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Error: Debe ingresar una contraseña para esta operación.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Determinar operación y extensión
            int operation;
            String newExtension;

            if (rbCompress.isSelected()) {
                operation = FileProcessor.OP_COMPRESS_ONLY;
                newExtension = ".cmp";
            } else if (rbEncrypt.isSelected()) {
                operation = FileProcessor.OP_ENCRYPT_ONLY;
                newExtension = ".enc";
            } else {
                operation = FileProcessor.OP_COMPRESS_AND_ENCRYPT;
                newExtension = ".ec";
            }

            String inputPath = selectedFile.getAbsolutePath();

            if (selectedFile.isFile()) {
                // Procesar archivo individual
                String outputPath = inputPath + newExtension;
                processor.processFile(inputPath, outputPath, operation, password);

            } else if (selectedFile.isDirectory()) {
                // Procesar carpeta completa
                File outputFolder = new File(
                        selectedFile.getParent(),
                        selectedFile.getName() + "_" + newExtension.substring(1)
                );
                walker.processFolder(selectedFile, outputFolder, processor, operation, password, newExtension);
            }

            // Mostrar éxito
            JOptionPane.showMessageDialog(this,
                    "¡Proceso completado exitosamente!\n\nRevise el archivo log.txt para más detalles.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Limpiar campos
            txtFilePath.setText("");
            txtPassword.setText("");
            selectedFile = null;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: Ocurrió un error durante el proceso.\n" + ex.getMessage(),
                    "Error de Proceso", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Lógica para recuperar (descomprimir/desencriptar) un archivo o carpeta.
     */
    private void recuperarArchivo() {
        // Validaciones
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Error: Debe seleccionar un archivo o carpeta a recuperar.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String inputPath = selectedFile.getAbsolutePath();

        // Verificar que tenga una extensión válida
        if (!inputPath.endsWith(".cmp") && !inputPath.endsWith(".enc") && !inputPath.endsWith(".ec")) {
            JOptionPane.showMessageDialog(this,
                    "Error: El archivo debe tener extensión .cmp, .enc o .ec",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Si el archivo requiere contraseña, pedirla
        String password = new String(txtPassword.getPassword());
        if ((inputPath.endsWith(".enc") || inputPath.endsWith(".ec")) && password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Error: Debe ingresar la contraseña para desencriptar.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Solicitar ubicación de salida
        JFileChooser outputChooser = new JFileChooser();
        outputChooser.setDialogTitle("Seleccione dónde guardar el archivo/carpeta recuperado");
        outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = outputChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return; // Usuario canceló
        }

        File outputDir = outputChooser.getSelectedFile();

        try {
            if (selectedFile.isFile()) {
                // Recuperar archivo individual
                String originalName = getOriginalName(selectedFile.getName());
                String outputPath = new File(outputDir, originalName).getAbsolutePath();
                processor.recoverFile(inputPath, outputPath, password);

            } else if (selectedFile.isDirectory()) {
                // Recuperar carpeta completa
                String originalFolderName = getOriginalFolderName(selectedFile.getName());
                File outputFolder = new File(outputDir, originalFolderName);
                walker.recoverFolder(selectedFile, outputFolder, processor, password);
            }

            // Mostrar éxito
            JOptionPane.showMessageDialog(this,
                    "¡Recuperación completada exitosamente!\n\nRevise el archivo log.txt para más detalles.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Limpiar campos
            txtFilePath.setText("");
            txtPassword.setText("");
            selectedFile = null;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: Ocurrió un error durante la recuperación.\n" + ex.getMessage(),
                    "Error de Recuperación", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Obtiene el nombre original del archivo quitando la extensión procesada.
     */
    private String getOriginalName(String fileName) {
        if (fileName.endsWith(".cmp") || fileName.endsWith(".enc")) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        } else if (fileName.endsWith(".ec")) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return fileName;
    }

    /**
     * Obtiene el nombre original de la carpeta quitando el sufijo.
     */
    private String getOriginalFolderName(String folderName) {
        if (folderName.endsWith("_cmp") || folderName.endsWith("_enc")) {
            return folderName.substring(0, folderName.lastIndexOf('_'));
        } else if (folderName.endsWith("_ec")) {
            return folderName.substring(0, folderName.lastIndexOf('_'));
        }
        return folderName + "_recuperado";
    }

    public static void main(String[] args) {
        // Usar el Look and Feel del sistema para mejor apariencia
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainAppGUI app = new MainAppGUI();
                app.setVisible(true);
            }
        });
    }
}