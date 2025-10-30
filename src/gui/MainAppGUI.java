package gui;

// Importaciones de Swing para la GUI
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;

// Importaciones para la lógica
import core.FileProcessor;
import utils.FileWalker;

// Importaciones de Java
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// La clase extiende JFrame para que sea una ventana
public class MainAppGUI extends JFrame {

    // --- Componentes de la GUI ---
    private JTextField txtFilePath;
    private JPasswordField txtPassword;
    private JRadioButton rbCompress, rbEncrypt, rbBoth;
    private JButton btnSelectFile, btnProcess, btnRecover;
    private JLabel lblFile, lblOperation, lblPassword;
    private JFileChooser fileChooser;

    // --- Lógica del Proyecto ---
    private FileProcessor processor;
    private FileWalker walker;
    private File selectedFile; // Para guardar el archivo o carpeta seleccionado

    // --- Constructor: Aquí se arma la ventana ---
    public MainAppGUI() {
        // Instanciamos la lógica
        this.processor = new FileProcessor();
        this.walker = new FileWalker();
        this.fileChooser = new JFileChooser();
        // Permitir seleccionar archivos Y carpetas
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        // --- Configuración de la Ventana ---
        setTitle("Sistema de Gestión de Archivos (Proyecto 2)");
        setSize(500, 350); // Tamaño (ancho, alto)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cerrar al presionar 'X'
        setLayout(null); // Usamos layout manual (con setBounds)
        setResizable(false);
        setLocationRelativeTo(null); // Centrar en pantalla

        // --- 1. Selección de Archivo ---
        lblFile = new JLabel("Archivo/Carpeta:");
        lblFile.setBounds(20, 20, 100, 25);
        add(lblFile);

        txtFilePath = new JTextField();
        txtFilePath.setBounds(130, 20, 240, 25);
        txtFilePath.setEditable(false); // No se puede escribir, solo con el botón
        add(txtFilePath);

        btnSelectFile = new JButton("Seleccionar");
        btnSelectFile.setBounds(380, 20, 100, 25);
        add(btnSelectFile);

        // --- 2. Opciones de Operación [cite: 14, 16, 19] ---
        lblOperation = new JLabel("Operación:");
        lblOperation.setBounds(20, 70, 100, 25);
        add(lblOperation);

        rbCompress = new JRadioButton("Comprimir (.cmp)");
        rbCompress.setBounds(130, 70, 150, 25);
        rbCompress.setSelected(true); // Opción por defecto
        
        rbEncrypt = new JRadioButton("Encriptar (.enc)");
        rbEncrypt.setBounds(130, 100, 150, 25);
        
        rbBoth = new JRadioButton("Ambos (.ec)");
        rbBoth.setBounds(130, 130, 150, 25);

        // Agruparlos para que solo uno se pueda seleccionar
        ButtonGroup opGroup = new ButtonGroup();
        opGroup.add(rbCompress);
        opGroup.add(rbEncrypt);
        opGroup.add(rbBoth);
        add(rbCompress);
        add(rbEncrypt);
        add(rbBoth);

        // --- 3. Contraseña [cite: 27] ---
        lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(20, 180, 100, 25);
        add(lblPassword);
        
        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 180, 240, 25);
        add(txtPassword);

        // --- 4. Botones de Acción ---
        btnProcess = new JButton("Procesar");
        btnProcess.setBounds(130, 240, 100, 30);
        add(btnProcess);

        btnRecover = new JButton("Recuperar");
        btnRecover.setBounds(270, 240, 100, 30);
        add(btnRecover);

        // --- Lógica de los Botones (ActionListeners) ---

        // Botón "Seleccionar"
        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre la ventana de diálogo para elegir archivo
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
                // --- 1. Validar Entradas ---
                if (selectedFile == null) {
                    JOptionPane.showMessageDialog(MainAppGUI.this, 
                        "Error: Debe seleccionar un archivo o carpeta.", 
                        "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String password = new String(txtPassword.getPassword());
                if ((rbEncrypt.isSelected() || rbBoth.isSelected()) && password.isEmpty()) {
                    JOptionPane.showMessageDialog(MainAppGUI.this, 
                        "Error: Debe ingresar una contraseña para esta operación.", 
                        "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // --- 2. Determinar Operación y Extensión ---
                    int operation;
                    String newExtension;

                    if (rbCompress.isSelected()) {
                        operation = FileProcessor.OP_COMPRESS_ONLY;
                        newExtension = ".cmp";
                    } else if (rbEncrypt.isSelected()) {
                        operation = FileProcessor.OP_ENCRYPT_ONLY;
                        newExtension = ".enc";
                    } else { // rbBoth
                        operation = FileProcessor.OP_COMPRESS_AND_ENCRYPT;
                        newExtension = ".ec";
                    }

                    // --- 3. Procesar (Archivo o Carpeta) ---
                    String inputPath = selectedFile.getAbsolutePath();
                    
                    if (selectedFile.isFile()) {
                        // --- Es un Archivo ---
                        String outputPath = inputPath + newExtension;
                        processor.processFile(inputPath, outputPath, operation, password);

                    } else if (selectedFile.isDirectory()) {
                        // --- Es una Carpeta ---
                        // La salida será una nueva carpeta (ej. "MiCarpeta_cmp")
                        File outputFolder = new File(selectedFile.getParent(), selectedFile.getName() + "_" + newExtension.substring(1));
                        walker.processFolder(selectedFile, outputFolder, processor, operation, password, newExtension);
                    }

                    // --- 4. Mostrar Éxito ---
                    JOptionPane.showMessageDialog(MainAppGUI.this, 
                        "¡Proceso completado exitosamente!", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Limpiar campos
                    txtFilePath.setText("");
                    txtPassword.setText("");
                    selectedFile = null;

                } catch (Exception ex) {
                    // --- 5. Manejar Errores ---
                    JOptionPane.showMessageDialog(MainAppGUI.this, 
                        "Error: Ocurrió un error durante el proceso.\n" + ex.getMessage(), 
                        "Error de Proceso", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // Botón "Recuperar"
        btnRecover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // (Aquí pondremos la lógica para RECUPERAR)
                System.out.println("Botón 'Recuperar' presionado.");
                // TODO: Implementar lógica
                JOptionPane.showMessageDialog(MainAppGUI.this, "¡Lógica de 'Recuperar' AÚN NO IMPLEMENTADA!");
            }
        });
    }

    // --- Método Main para ejecutar la aplicación ---
    public static void main(String[] args) {
        // Crea y muestra la ventana
        MainAppGUI app = new MainAppGUI();
        app.setVisible(true);
    }
}