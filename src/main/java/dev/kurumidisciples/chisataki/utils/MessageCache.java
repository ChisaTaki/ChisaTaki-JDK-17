package dev.kurumidisciples.chisataki.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class MessageCache {

    private static final LinkedHashMap<GuildMessageChannel, List<Message>> messageMap;
    private static int maxSize;

    static {
        messageMap = new LinkedHashMap<GuildMessageChannel, List<Message>>() {
            private static final long serialVersionUID = 1L;
    
            @Override
            protected boolean removeEldestEntry(Map.Entry<GuildMessageChannel, List<Message>> eldest) {
                return size() > maxSize;
            }
        };
    }

    public static List<Message> getMessages(TextChannel channel) {
        return messageMap.get(channel);
    } 

    public static void storeMessage(Message message) {
        GuildMessageChannel channel = message.getChannel().asGuildMessageChannel();
        List<Message> messages = messageMap.get(channel);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        messageMap.put(channel, messages);
    }

    public static Message getMessageById(String messageId) {
        for (List<Message> messages : messageMap.values()) {
            for (Message message : messages) {
                if (message.getId().equals(messageId)) {
                    return message;
                }
            }
        }
        return null;
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
        int size = 0;
        for (List<Message> messages : messageMap.values()) {
            size += messages.size();
        }
        return size;
    }
}
