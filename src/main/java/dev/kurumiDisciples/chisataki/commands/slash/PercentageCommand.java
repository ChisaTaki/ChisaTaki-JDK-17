package dev.kurumidisciples.chisataki.commands.slash;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PercentageCommand extends SlashCommand {
	
	private String messageFormat;
	private String gifUrl;
	private String footer;
	
	public PercentageCommand(String name, String description, String userDescription, String messageFormat, String gifUrl, String footer) {
		super(name, description);
		this.options = List.of(new OptionData(OptionType.USER, "user", userDescription)); 
		this.messageFormat = messageFormat;
		this.gifUrl = gifUrl;
		this.footer = footer;
	}

	private MessageEmbed buildEmbed(Member member) {
		String effectiveName = member.getEffectiveName();
		int percentage = ThreadLocalRandom.current().nextInt(101);
		String description = String.format(messageFormat, effectiveName, effectiveName, percentage);
		
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
		
		if (!isSameMember && IgnoreCommand.isMemberIgnored(selectedMember.getId())) {
			event.getHook().sendMessage(getIgnoreMessage()).queue();
			return;
		}
		
		String message = isSameMember ? "" : selectedMember.getAsMention();
		event.getHook().sendMessage(message).setEmbeds(buildEmbed(selectedMember)).queue();
	}
}
