package com.titan.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class ClientGUI extends JFrame {
    private JTextArea messageArea;
    private JTextField inputField;
    private ChatClient client;
    private String userName;

    public ClientGUI() {
        super("Titan Messenger");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();

        userName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (userName == null || userName.trim().isEmpty()) System.exit(0);
        setTitle("Titan Messenger - Connected as: " + userName);

        client = new ChatClient(message -> {
            SwingUtilities.invokeLater(() -> {
                messageArea.append(message + "\n");
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
            });
        });

        try {
            client.connect(userName);
            messageArea.append("[SYSTEM]: Connected to Server.\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Server not found!");
            System.exit(1);
        }
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        messageArea.setBackground(new Color(30, 30, 30));
        messageArea.setForeground(Color.WHITE);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) sendMessage();
            }
        });

        // --- NEW: Buttons Panel ---
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        JButton fileButton = new JButton("📎 Attach"); // File Button
        fileButton.addActionListener(e -> selectFile());

        buttonsPanel.add(fileButton);
        buttonsPanel.add(sendButton);

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(buttonsPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void sendMessage() {
        String content = inputField.getText();
        if (!content.trim().isEmpty()) {
            client.sendMessage(userName, content);
            inputField.setText("");
        }
    }

    // --- NEW: File Chooser Logic ---
    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Client logic ko bolo file bhejne ko
            client.sendFile(userName, selectedFile);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}