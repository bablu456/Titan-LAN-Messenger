package com.titan.client;

import com.titan.common.Message;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.function.Consumer;

public class ChatClient {
    private String serverAddress = "127.0.0.1";
    private int port = 5000;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Consumer<String> onMessageReceived;

    public ChatClient(Consumer<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public void connect(String name) throws IOException {
        socket = new Socket(serverAddress, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        new Thread(() -> {
            try {
                while (true) {
                    Message msg = (Message) in.readObject();

                    if ("FILE".equals(msg.type())) {
                        // Agar file aayi hai, toh save karo
                        saveFile(msg);
                        onMessageReceived.accept("[" + msg.sender() + "]: Shared a file -> " + msg.fileName());
                    } else {
                        // Normal Text
                        onMessageReceived.accept("[" + msg.sender() + "]: " + msg.content());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                onMessageReceived.accept("[SYSTEM]: Connection Lost.");
            }
        }).start();
    }

    public void sendMessage(String name, String content) {
        try {
            // Constructor 1 use hoga (Text wala)
            Message msg = new Message(name, "CHAT", content);
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- NEW: Send File Logic ---
    public void sendFile(String name, File file) {
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            // Constructor 2 use hoga (File wala)
            Message msg = new Message(name, "FILE", file.getName(), fileBytes);
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- NEW: Save Received File ---
    private void saveFile(Message msg) {
        try {
            // Project folder mein 'received_files' naam ka folder banayega
            File dir = new File("received_files");
            if (!dir.exists()) dir.mkdir();

            // File wahan save karega (Duplicate naam se bachega using System time)
            File fileToSave = new File(dir, System.currentTimeMillis() + "_" + msg.fileName());

            FileOutputStream fileOut = new FileOutputStream(fileToSave);
            fileOut.write(msg.fileData());
            fileOut.close();

            System.out.println("File saved at: " + fileToSave.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}