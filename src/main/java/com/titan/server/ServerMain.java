package com.titan.server;

import java.util.HashSet;
import com.titan.common.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    // Thread-Safe Map: Name -> ClientHandler
    private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 5000;
        System.out.println("[SERVER] Starting Titan Server...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- LOGIC: REGISTER USER ---
    public static void addClient(String name, ClientHandler handler) {
        clients.put(name, handler);
        System.out.println("[SERVER] " + name + " joined.");
        broadcastUserList(); // Sabko batao list update hui hai
    }

    // --- LOGIC: REMOVE USER ---
    public static void removeClient(String name) {
        if (name != null) {
            clients.remove(name);
            System.out.println("[SERVER] " + name + " left.");
            broadcastUserList();
        }
    }

    // --- LOGIC: BROADCAST USER LIST ---
    public static void broadcastUserList() {
        // ERROR FIX:
        // clients.keySet() direct mat bhejo, wo map se juda hota hai.
        // Uski ek nayi Copy banao (HashSet) jisme sirf Strings hon.
        Set<String> safeUserList = new HashSet<>(clients.keySet());

        Message listMsg = new Message("USER_LIST", safeUserList);

        for (ClientHandler client : clients.values()) {
            client.sendMessage(listMsg);
        }
    }
    // --- LOGIC: ROUTING (Private vs Public) ---
    public static void routeMessage(Message msg) {
        if (msg.targetUser() != null && clients.containsKey(msg.targetUser())) {
            // PRIVATE MESSAGE
            ClientHandler target = clients.get(msg.targetUser());
            target.sendMessage(msg);

            // Sender ko bhi copy dikhni chahiye ki usne kya bheja
            ClientHandler sender = clients.get(msg.sender());
            if (sender != null) sender.sendMessage(msg);

        } else {
            // BROADCAST (Public Chat)
            for (ClientHandler client : clients.values()) {
                client.sendMessage(msg);
            }
        }
    }
}