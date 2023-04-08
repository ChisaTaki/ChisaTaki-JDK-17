package dev.kurumiDisciples.chisataki.commands.slash;

import java.util.List;

import dev.kurumiDisciples.chisataki.listeners.IgnoreInteraction;
import dev.kurumiDisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class MentionableCommand extends SlashCommand {
	
	private String messageFormat;
	private String selfMessage;
	private String gifUrl;
	private String footer;

	public MentionableCommand(String name, String description, String userDescription, String messageFormat, String selfMessage, String gifUrl, 
			String footer) {
		super(name, description);
		this.options = List.of(new OptionData(OptionType.USER, "user", userDescription, true)); 
		this.messageFormat = messageFormat;
		this.selfMessage = selfMessage;
		this.gifUrl = gifUrl;
		this.footer = footer;
	}

	private MessageEmbed buildEmbed(Member initiator, Member selectedMember) {
		String description = String.format(messageFormat, initiator.getEffectiveName(), selectedMember.getEffectiveName());
		
		return new EmbedBuilder().setColor(ColorUtils.PURPLE)
				.setDescription(description).setImage(gifUrl)
				.setFooter(footer)
				.build();
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		Member initiator = event.getMember();
		OptionMapping userOption = event.getOption("user");
		Member selectedMember = event.getOption("user") == null ? initiator : userOption.getAsMember();
		boolean isSameMember = initiator.getId().equals(selectedMember.getId());
		
		if (!isSameMember && IgnoreInteraction.inList(selectedMember)) {
			event.getHook().sendMessage(getIgnoreMessage()).queue();
			return;
		}
		
		if (isSameMember) {
			event.getHook().sendMessage(selfMessage).queue();
		} else {
			event.getHook().sendMessage(selectedMember.getAsMention()).setEmbeds(buildEmbed(initiator, selectedMember)).queue();
		}
	}

}
