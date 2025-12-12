package com.titan.common;

import java.io.Serializable;

public record Message(
        String sender,
        String type,       // "CHAT" or "FILE"
        String content,    // Text message logic
        String fileName,   // File ka naam (e.g., notes.pdf)
        byte[] fileData    // Asli maal (Binary Data)
) implements Serializable {

    // Convenience constructor for Text Messages
    public Message(String sender, String type, String content) {
        this(sender, type, content, null, null);
    }

    // Convenience constructor for File Messages
    public Message(String sender, String type, String fileName, byte[] fileData) {
        this(sender, type, "Sent a file: " + fileName, fileName, fileData);
    }
}