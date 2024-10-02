import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileEncryptDecryptGUI extends JFrame {

    private JTextField filePathField;
    private JPasswordField keyField; // Changed to JPasswordField
    private JButton encryptButton, decryptButton, browseButton;

    // Constructor to set up GUI components
    public FileEncryptDecryptGUI() {
        setupUIComponents();
        setTitle("File Encryption and Decryption");
        setSize(600, 300); // Increased height for checkbox
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Initialize and set up GUI components
    private void setupUIComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Set padding between components

        // File Path Label, TextField, and Browse Button
        JLabel fileLabel = new JLabel("File Path:");
        filePathField = new JTextField(20);
        filePathField.setToolTipText("Select the file to encrypt or decrypt");
        browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> chooseFile());

        // Encryption Key Label and TextField
        JLabel keyLabel = new JLabel("Key (8 characters):"); // Updated label
        keyField = new JPasswordField(20); // Changed to JPasswordField
        keyField.setToolTipText("Enter an 8-character encryption key"); // Updated tooltip

        // Checkbox to toggle password visibility
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                keyField.setEchoChar((char) 0); // Show password
            } else {
                keyField.setEchoChar('•'); // Mask password
            }
        });

        // Buttons for Encryption and Decryption
        encryptButton = createButton("Encrypt", new Color(60, 179, 113), e -> performEncryption());
        decryptButton = createButton("Decrypt", new Color(70, 130, 180), e -> performDecryption());

        // Layout settings using GridBagLayout
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addComponent(panel, fileLabel, gbc, 0, 0, 1);
        addComponent(panel, filePathField, gbc, 1, 0, 2);
        addComponent(panel, browseButton, gbc, 3, 0, 1);
        addComponent(panel, keyLabel, gbc, 0, 1, 1);
        addComponent(panel, keyField, gbc, 1, 1, 2);
        
        // Add the checkbox for showing/hiding password
        addComponent(panel, showPasswordCheckBox, gbc, 1, 2, 2); // Add checkbox to the panel

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        panel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc); // Add a horizontal separator

        gbc.gridwidth = 1;
        addComponent(panel, encryptButton, gbc, 1, 4, 1);
        addComponent(panel, decryptButton, gbc, 2, 4, 1);

        add(panel); // Add panel to the JFrame
    }

    // Helper method to create a button with given properties
    private JButton createButton(String text, Color color, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.addActionListener(actionListener);
        return button;
    }

    // Method to add components to the GridBagLayout
    private void addComponent(JPanel panel, Component component, GridBagConstraints gbc, int x, int y, int width) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        panel.add(component, gbc);
    }

    // File chooser method for selecting file path
    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    // Perform file encryption
    private void performEncryption() {
        String filePath = filePathField.getText();
        String key = new String(keyField.getPassword()); // Get password from JPasswordField

        if (validateInput(filePath, key)) {
            try {
                encryptFile(filePath, key);
                JOptionPane.showMessageDialog(this, "File encrypted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Encryption error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Perform file decryption
    private void performDecryption() {
        String filePath = filePathField.getText();
        String key = new String(keyField.getPassword()); // Get password from JPasswordField

        if (validateInput(filePath, key)) {
            try {
                decryptFile(filePath, key);
                JOptionPane.showMessageDialog(this, "File decrypted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Decryption error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Validate file path and key length
    private boolean validateInput(String filePath, String key) {
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a file.");
            return false;
        }
        if (key.length() != 8) { // Updated length check
            JOptionPane.showMessageDialog(this, "Key must be exactly 8 characters long.");
            return false;
        }
        return true;
    }

    // AES Encryption
    private void encryptFile(String filePath, String key) throws Exception {
        File file = new File(filePath);
        byte[] fileContent = readFile(file);
        byte[] encryptedData = encrypt(fileContent, key);
        writeFile(new File(file.getAbsolutePath() + ".encrypted"), encryptedData);
    }

    // AES Decryption
    private void decryptFile(String filePath, String key) throws Exception {
        File file = new File(filePath);
        byte[] fileContent = readFile(file);
        byte[] decryptedData = decrypt(fileContent, key);
        String newFilePath = filePath.replace(".encrypted", "_decrypted");
        writeFile(new File(newFilePath), decryptedData);
    }

    // Read file into byte array
    private byte[] readFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return data;
    }

    // Write byte array to file
    private void writeFile(File file, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
    }

    // AES encryption method
    private byte[] encrypt(byte[] data, String key) throws Exception {
        return applyCipher(data, key, Cipher.ENCRYPT_MODE);
    }

    // AES decryption method
    private byte[] decrypt(byte[] data, String key) throws Exception {
        return applyCipher(data, key, Cipher.DECRYPT_MODE);
    }

    // Common method for encryption/decryption
    private byte[] applyCipher(byte[] data, String key, int mode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(mode, secretKey);
        return cipher.doFinal(data);
    }

    // Main method to run the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileEncryptDecryptGUI());
    }
}
