package com.titan.client;

import com.titan.common.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int port = 5000;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        try {
            Socket socket = new Socket(serverAddress, port);
            System.out.println("[CLIENT] Connected to Titan Server!");

            // Important: Output stream pehle create karo (Server rule)
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // --- THREAD 1: SERVER LISTENER (Sunne wala) ---
            // Ye background mein chalta rahega aur messages print karega
            Thread listenerThread = new Thread(() -> {
                try {
                    while (true) {
                        Message msg = (Message) in.readObject();
                        System.out.println("\n[" + msg.sender() + "]: " + msg.content());
                        System.out.print("You: "); // Wapas prompt dikhane ke liye
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("\n[SYSTEM] Server connection lost.");
                    System.exit(0);
                }
            });
            listenerThread.start();

            // --- THREAD 2: MAIN THREAD (Bolne wala) ---
            // Ye loop user se input lega aur server ko bhejega
            while (true) {
                System.out.print("You: ");
                String content = scanner.nextLine();

                if (content.equalsIgnoreCase("exit")) {
                    socket.close();
                    break;
                }

                Message msg = new Message(name, "CHAT", content);
                out.writeObject(msg);
                out.flush(); // Ensure data turant jaye
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}