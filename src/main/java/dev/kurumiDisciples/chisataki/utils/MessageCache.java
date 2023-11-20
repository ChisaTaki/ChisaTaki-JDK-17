package dev.kurumidisciples.chisataki.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import net.dv8tion.jda.api.entities.Message;

public class MessageCache {

    private static final LinkedHashMap<String, Message> messageMap;
    private static int maxSize;

    static {
        messageMap = new LinkedHashMap<String, Message>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Message> eldest) {
                return size() > maxSize;
            }
        };
    }

    public static void storeMessage(Message message) {
        messageMap.put(message.getId(), message);
    }

    public static Message getMessageById(String messageId) {
        return messageMap.get(messageId);
    }

    public static int getMaxSize() {
        return maxSize;
    }

    public static void setMaxSize(int maxSize) {
        MessageCache.maxSize = maxSize;
    }

    public static void updateMessage(Message message) {
        storeMessage(message);
    }

    public static int getSize() {
        return messageMap.size();
    }
}
