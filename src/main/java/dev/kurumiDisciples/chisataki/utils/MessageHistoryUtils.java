package dev.kurumiDisciples.chisataki.utils;

import java.util.List;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MessageHistoryUtils {
    private static final String BOT_ID = "1070074991653167144";
    private static final int DEFAULT_RETRIEVE_COUNT = 2;

    public static boolean isConsecutiveMessage(Member member, TextChannel textChannel, String sentMessageId) {
        List<Message> retrievedHistory = getRetrievedHistory(textChannel);
        if (retrievedHistory != null) {
            for (Message message : retrievedHistory) {
                if (!message.getId().equals(sentMessageId) && message.getAuthor().getId().equals(member.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Message getLastBotMessage(TextChannel textChannel) {
        List<Message> retrievedHistory = getRetrievedHistory(textChannel);
        if (retrievedHistory != null) {
            for (Message message : retrievedHistory) {
                if (message.getAuthor().getId().equals(BOT_ID)) {
                    return message;
                }
            }
        }
        return null;
    }

    public static List<Message> getRetrievedHistory(TextChannel textChannel) {
        try {
            MessageHistory history = textChannel.getHistory();
            history.retrievePast(DEFAULT_RETRIEVE_COUNT).submit().get();
            return history.getRetrievedHistory();
        } catch (InterruptedException | ExecutionException e) {
            // Log the error or handle it appropriately
            return null;
        }
    }
}
