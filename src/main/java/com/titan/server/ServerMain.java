package com.titan.server;

import com.titan.common.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerMain {

    // Thread-safe list to store all active clients
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        int port = 5000;
        System.out.println("[SERVER] Starting Titan Server on Port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("[SERVER] Waiting for client...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] New Connection: " + clientSocket.getInetAddress());

                // Naye client ke liye handler banao
                ClientHandler worker = new ClientHandler(clientSocket);

                // ISKO LIST MEIN ADD KARO (Registration)
                clients.add(worker);

                // Thread start karo
                Thread thread = new Thread(worker);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // --- NEW: Broadcasting Logic ---
    // Ye method ek message lega aur SABHI clients ko bhej dega
    public static void broadcast(Message message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            // Sender ko wapas uska hi message mat bhejo (optional logic)
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Jab koi leave kare, use list se hata do
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("[SERVER] A client disconnected. Current Active: " + clients.size());
    }
}