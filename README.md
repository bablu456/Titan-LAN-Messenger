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
<img width="1919" height="1079" alt="Screenshot 2025-12-13 124624" src="https://github.com/user-attachments/assets/6f6bbc9f-705f-4056-86b6-11fd419894c0" />

💻 How to Run Locally
Prerequisites
Java 17 or higher installed.

Steps
Clone the Repository

Bash

git clone [https://github.com/YOUR_USERNAME/Titan-LAN-Messenger.git](https://github.com/YOUR_USERNAME/Titan-LAN-Messenger.git)
cd Titan-LAN-Messenger
Compile the Code (Or simply open the project in IntelliJ IDEA / Eclipse)

Start the Server Run com.titan.server.ServerMain. Console Output: [SERVER] Starting Titan Server on Port 5000...

Start Clients Run com.titan.client.ClientGUI.

Enter a unique username (e.g., "Alice").

Run another instance and enter "Bob".

Test Features

Select "Bob" in the sidebar to send a private message.

Click "Attach" to send a file.

🧠 What I Learned (The "Why")
Why TCP over UDP? Prioritized data integrity over speed. In file transfers and chat, packet loss (common in UDP) is unacceptable.

Why Record vs Class? Utilized Java 17 Records for immutable data carriers, reducing boilerplate code and ensuring thread safety for message packets.

Why SwingUtilities.invokeLater? Learned that Swing components are not thread-safe. All UI updates from the background network thread must be queued on the Event Dispatch Thread (EDT).

🔮 Future Improvements
[ ] End-to-End Encryption (AES).

[ ] SQLite Database for Chat History.

[ ] Group Chat Rooms.

Author: [Your Name]


---

### **3. The `.gitignore` File (Bahut Important)**
Ye file batati hai ki "kya upload NAHI karna hai".
Agar ye nahi banaoge, toh tumhari compiled `.class` files aur settings upload ho jayengi jo **unprofessional** lagta hai.

Project root folder mein `.gitignore` naam ki file banao (no name, just extension) aur ye paste karo:

```text
# Compiled class file
*.class

# Log file
*.log

# BlueJ files
*.ctxt

# Mobile Tools for Java (J2ME)
.mtj.tmp/

# Package Files #
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# virtual machine crash logs
hs_err_pid*

# IDE Specific Files (Inhe upload nahi karte)
.idea/
*.iml
.settings/
.classpath
.project
/bin/
/target/
/out/
4. Upload kaise karein (Commands)
Ab apne folder mein Terminal/CMD open kar aur ye commands chala:

Bash
