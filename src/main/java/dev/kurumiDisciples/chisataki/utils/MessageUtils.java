package dev.kurumiDisciples.chisataki.utils;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class MessageUtils {

	public static MessageEmbed buildEmbed(String title, String description, String image, Color color) {
		return new EmbedBuilder()
	            .setTitle(title)
	            .setDescription(description)
	            .setImage(image)
	            .setColor(color)
	            .build();
	}

	public static MessageEmbed buildEmbed(String title, String image, Color color) {
		return new EmbedBuilder()
				.setTitle(title)
				.setImage(image)
				.setColor(color)
				.build();
	}

	public static MessageCreateData buildMessageCreateData(String content, MessageEmbed embed) {
		return new MessageCreateBuilder()
				.setContent(content)
				.setEmbeds(embed)
				.build();
	}
}
