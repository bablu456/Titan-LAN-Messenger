package com.titan.common;

import java.io.Serializable;
import java.util.Set;

public record Message(
        String sender,
        String type,
        String content,
        String targetUser,
        String fileName,
        byte[] fileData,
        Set<String> activeUsers
) implements Serializable {

    // 1. Constructor for Normal Chat (3 Args - Backward Compatibility)
    public Message(String sender, String type, String content) {
        this(sender, type, content, null, null, null, null);
    }

    // 2. Constructor for Private Chat (4 Args)
    // Structure: (String, String, String, String)
    public Message(String sender, String type, String content, String targetUser) {
        this(sender, type, content, targetUser, null, null, null);
    }

    // 3. Constructor for User List Update
    public Message(String type, Set<String> activeUsers) {
        this("SERVER", type, null, null, null, null, activeUsers);
    }

    // 4. Constructor for File Message (CHANGED ORDER)
    // Structure: (String, String, byte[], String) <-- SWAPPED to fix Ambiguity
    public Message(String sender, String type, byte[] fileData, String fileName) {
        this(sender, type, "Sent a file: " + fileName, null, fileName, fileData, null);
    }
}