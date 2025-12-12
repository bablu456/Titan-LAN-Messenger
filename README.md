# 🛡️ Titan LAN Messenger

> **A Production-Grade, Multithreaded Client-Server Chat Application built strictly using Core Java.**

Titan Messenger is a robust, low-level networking application designed to facilitate real-time communication and file sharing within a local area network (LAN) without internet dependency. It demonstrates advanced concepts of **Distributed Systems**, **Socket Programming**, and **Concurrent Engineering**.

![Java](https://img.shields.io/badge/Java-17%2B-orange) ![Architecture](https://img.shields.io/badge/Architecture-Client--Server-blue) ![Protocol](https://img.shields.io/badge/Protocol-TCP%2FIP-green)

---

## 🚀 Key Features

* **⚡ Real-Time Broadcasting:** Instant messaging to all connected clients.
* **🔒 Private 1-on-1 Messaging:** Unicast routing logic using Thread-Safe Maps.
* **📂 Binary File Transfer:** Capable of transferring images, PDFs, and docs via Byte Streams.
* **👥 Live User List:** Real-time updates of online users using Push Notifications logic.
* **🧵 Scalable Architecture:** Handles multiple concurrent users via Multithreading.
* **🎨 GUI Interface:** Clean, user-friendly interface built with Java Swing (Dark Mode).

---

## 🏗️ System Architecture & Design

The system follows a **Decoupled Client-Server Architecture** utilizing custom TCP packet switching.

### 1. The Server (The Brain)
* Acts as a centralized router.
* **Concurrency Model:** Uses a *Thread-Per-Client* model.
* **Data Structure:** Replaced traditional `ArrayList` with **`ConcurrentHashMap<String, ClientHandler>`**.
    * *Reason:* To achieve **O(1)** time complexity for private message routing and to prevent `ConcurrentModificationException` during high-traffic broadcasting.

### 2. The Client (The Node)
* Follows **MVC (Model-View-Controller)** pattern separation.
* **Networking:** Runs two parallel threads:
    1.  **Main Thread:** Handles UI events and sending data.
    2.  **Listener Thread:** Continuously listens for incoming binary payloads from the Server.

### 3. The Protocol (Custom Packet)
Instead of raw strings, a custom `Message` Record (Java 17) is used to encapsulate data:
```java
public record Message(
    String sender,
    String type,        // "CHAT", "FILE", "USER_LIST"
    String targetUser,  // Routing Logic
    String content,     // Text Payload
    byte[] fileData     // Binary Payload
) implements Serializable {}
