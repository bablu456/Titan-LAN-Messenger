package com.titan.client;

import com.titan.common.Message;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

public class ClientGUI extends JFrame {
    private JTextArea messageArea;
    private JTextField inputField;
    private JList<String> userList; // Sidebar Component
    private DefaultListModel<String> listModel; // Data for Sidebar
    private ChatClient client;
    private String userName;
    private String currentTarget = null; // null = Broadcast, Name = Private

    public ClientGUI() {
        super("Titan Messenger");
        setSize(700, 500); // Thoda chouda kiya
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userName = JOptionPane.showInputDialog(this, "Enter Name:");
        if (userName == null) System.exit(0);
        setTitle("Logged in as: " + userName);

        initComponents();

        // LOGIC INITIALIZATION
        client = new ChatClient(
                // 1. Handle Chat Message
                msg -> SwingUtilities.invokeLater(() -> {
                    String prefix = (msg.targetUser() == null) ? "[Public] " : "[Private] ";
                    if("FILE".equals(msg.type())) {
                        saveFile(msg); // Niche helper method hai
                        messageArea.append(prefix + msg.sender() + ": Sent a file -> " + msg.fileName() + "\n");
                    } else {
                        messageArea.append(prefix + msg.sender() + ": " + msg.content() + "\n");
                    }
                }),
                // 2. Handle User List Update
                users -> SwingUtilities.invokeLater(() -> updateSidebar(users))
        );

        try {
            client.connect(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // --- LEFT: SIDEBAR (User List) ---
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setBackground(new Color(230, 240, 255));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Click Listener
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                currentTarget = userList.getSelectedValue();
                // Khud ko select mat karne dena
                if (userName.equals(currentTarget)) currentTarget = null;
                inputField.setToolTipText(currentTarget == null ? "Broadcast" : "Private to: " + currentTarget);
            }
        });

        JScrollPane sidebarScroll = new JScrollPane(userList);
        sidebarScroll.setPreferredSize(new Dimension(150, 0));
        sidebarScroll.setBorder(BorderFactory.createTitledBorder("Online Users"));
        add(sidebarScroll, BorderLayout.WEST);

        // --- CENTER: CHAT AREA ---
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // --- BOTTOM: INPUT ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage()); // Enter key logic

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSidebar(Set<String> users) {
        listModel.clear();
        listModel.addElement("Broadcast (Click here)"); // Default option
        for (String user : users) {
            if (!user.equals(userName)) listModel.addElement(user);
        }
    }

    private void sendMessage() {
        String content = inputField.getText();
        if (content.isEmpty()) return;

        // Agar "Broadcast" selected hai ya null hai
        String target = (currentTarget != null && !currentTarget.contains("Broadcast")) ? currentTarget : null;

        Message msg = new Message(userName, "CHAT", content, target);
        client.sendMessage(msg);
        inputField.setText("");
    }

    // Helper to save file (Same as before, just added for compilation)
    private void saveFile(Message msg) {
        try {
            File dir = new File("received_files");
            if (!dir.exists()) dir.mkdir();
            File file = new File(dir, System.currentTimeMillis() + "_" + msg.fileName());
            Files.write(file.toPath(), msg.fileData());
        } catch(IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}