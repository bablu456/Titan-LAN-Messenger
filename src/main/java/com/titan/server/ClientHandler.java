package com.titan.server;

import com.titan.common.Message;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String clientName; // Is client ka naam store karenge

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // 1. PEHLA MESSAGE: Sirf naam register karne ke liye
            Message firstMsg = (Message) in.readObject();
            this.clientName = firstMsg.sender();

            // Server Map mein add karo
            ServerMain.addClient(clientName, this);

            // 2. MAIN LOOP
            while (true) {
                Message msg = (Message) in.readObject();

                if ("EXIT".equalsIgnoreCase(msg.content())) break;

                // Server ko bolo route karne ko
                ServerMain.routeMessage(msg);
            }

        } catch (IOException | ClassNotFoundException e) {
            // Connection lost
        } finally {
            ServerMain.removeClient(clientName);
        }
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}