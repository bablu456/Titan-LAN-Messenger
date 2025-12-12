package com.titan.server;

import com.titan.common.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            // Streams setup
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            // --- CONTINUOUS LOOP ---
            while (true) {
                // Ye line wait karegi jab tak naya message na aaye
                Message messageFromClient = (Message) in.readObject();

                System.out.println("[SERVER] " + messageFromClient.sender() + ": " + messageFromClient.content());

                // Agar client "EXIT" bole, toh loop todo
                if (messageFromClient.content().equalsIgnoreCase("exit")) {
                    break;
                }

                // Khud reply karne ki jagah, ab hum BROADCAST karenge
                ServerMain.broadcast(messageFromClient, this);
            }

        } catch (IOException | ClassNotFoundException e) {
            // Error tab aayega jab client abruptly disconnect kar dega
            System.err.println("Client Disconnected unexpectedly.");
        } finally {
            // CLEANUP CODE
            ServerMain.removeClient(this);
            try {
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Ye helper method ServerMain use karega message bhejne ke liye
    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush(); // Ensure karo data buffer mein na phase, turant jaye
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}