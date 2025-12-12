package com.titan.client;

import com.titan.common.Message;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Set;
import java.util.function.Consumer;

public class ChatClient {
    private String serverAddress = "127.0.0.1";
    private int port = 5000;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Listeners for GUI updates
    private Consumer<Message> onMessageReceived;
    private Consumer<Set<String>> onUserListUpdate;

    public ChatClient(Consumer<Message> onMessageReceived, Consumer<Set<String>> onUserListUpdate) {
        this.onMessageReceived = onMessageReceived;
        this.onUserListUpdate = onUserListUpdate;
    }

    public void connect(String name) throws IOException {
        Socket socket = new Socket(serverAddress, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        // Join message bhejo
        sendMessage(new Message(name, "JOIN", "Joining", null));

        // Listener Thread
        new Thread(() -> {
            try {
                while (true) {
                    Message msg = (Message) in.readObject();

                    if ("USER_LIST".equals(msg.type())) {
                        onUserListUpdate.accept(msg.activeUsers());
                    } else {
                        onMessageReceived.accept(msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- YE RAHA WO MISSING METHOD (With Fix) ---
    public void sendFile(String name, File file) {
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // Yahan humne Ambiguity fix kar di hai (Swap: fileBytes pehle)
            Message msg = new Message(name, "FILE", fileBytes, file.getName());

            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}