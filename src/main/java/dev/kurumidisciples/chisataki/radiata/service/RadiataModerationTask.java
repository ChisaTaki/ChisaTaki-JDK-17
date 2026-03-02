package dev.kurumidisciples.chisataki.radiata.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RadiataModerationTask {

    private final String hash;
    private final String dataUrl;
    private final MessageReceivedEvent event;

    public RadiataModerationTask(byte[] imageBytes, String contentType, MessageReceivedEvent event) throws NoSuchAlgorithmException{
        this.event = event;
        // 1️⃣ Hash first
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        this.hash = HexFormat.of().formatHex(digest.digest(imageBytes));

        // 2️⃣ Encode once
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        this.dataUrl = "data:" + contentType + ";base64," + base64;
    }

    public MessageReceivedEvent event() { return event; }
    public String getDataUrl() { return dataUrl; }
    public String getHash() { return hash; }
}