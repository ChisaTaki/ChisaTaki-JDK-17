package dev.kurumiDisciples.chisataki.utils;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MessageHistoryUtils {
  private static final String BOT_ID = "1070074991653167144";
  private static final int DEFAULT_RETRIEVE_COUNT = 2;
    
	public static boolean isConsecutiveMessage(Member member, TextChannel textChannel, String sentMessageId) {
        for (Message message : getRetrievedHistory(textChannel)) {
        	if (!message.getId().equals(sentMessageId) && message.getAuthor().getId().equals(member.getId())) {
                return true;
            }
        }
        return false;
    }
	
	public static Message getLastBotMessage(TextChannel textChannel) {
		for (Message message : getRetrievedHistory(textChannel)) {
			if (message.getAuthor().getId().equals(BOT_ID)) {
                return message;
            }
        }
        return null;
    }
	
	public static List<Message> getRetrievedHistory(TextChannel textChannel) {
		try {
            MessageHistory history = textChannel.getHistory();
            history.retrievePast(DEFAULT_RETRIEVE_COUNT).submit().get();
            return history.getRetrievedHistory();
        } catch (Exception e) {
            return null;
        }
	}
}
