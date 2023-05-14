package dev.kurumiDisciples.chisataki.commands.slash;

import java.awt.Color;
import java.time.Instant;
import java.util.List;

import dev.kurumiDisciples.chisataki.enums.ChannelEnum;
import dev.kurumiDisciples.chisataki.utils.ColorUtils;
import dev.kurumiDisciples.chisataki.utils.MessageUtils;
import dev.kurumiDisciples.chisataki.utils.RoleUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class EmbedCommand extends SlashCommand {

	private static final String CHANNEL = "channel";
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String AUTHOR = "author";
	private static final String FOOTER = "footer";
	private static final String COLOR = "color";

	public EmbedCommand() {
		super("embed", "create an embed message (mod only)", Permission.CREATE_PRIVATE_THREADS);
		this.options = List.of(
				new OptionData(OptionType.CHANNEL, CHANNEL, "the channel to post to", true),
				new OptionData(OptionType.STRING, TITLE, "set the title", true),
				new OptionData(OptionType.STRING, DESCRIPTION, "set the description"),
				new OptionData(OptionType.STRING, AUTHOR, "set the author"),
				new OptionData(OptionType.STRING, FOOTER, "set the footer"),
				new OptionData(OptionType.STRING, COLOR, "hex code for the color")
				);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();

		try {
			TextChannel textChannel = getChannel(event).asTextChannel();
			String hexColor = getColor(event);
			
			if (!ColorUtils.isValidHexCode(hexColor)) {
				event.getHook().editOriginal("Invalid hex color. Color must start with `#` and be followed by `6 digits`").queue();
				return;
			}
			
			MessageEmbed messageEmbed = MessageUtils.buildEmbed(getTitle(event), getDescription(event), getAuthor(event), getFooter(event), Color.decode(hexColor));
			textChannel.sendMessageEmbeds(messageEmbed).queue();
			event.getHook().editOriginal("Successfully sent Embed to channel " + textChannel.getAsMention()).queue();
			
			MessageEmbed logEmbed = createLogEmbed(event);
			event.getGuild().getTextChannelById(ChannelEnum.SERVER_LOGS.getId())
							.sendMessageEmbeds(logEmbed).queue();
		} catch (IllegalStateException e) {
			event.getHook().editOriginal("Invalid Text Channel.").queue();
		}
	}

	@Override
	public boolean isAllowed(SlashCommandInteractionEvent event) {
		return RoleUtils.isMemberStaff(event.getMember());
	}

	@Override
	public String getErrorMessage() {
		return "Sorry, but you do not have permission to use this command.";
	}
	
	private static MessageEmbed createLogEmbed(SlashCommandInteractionEvent event){
		String channel = getChannel(event).getAsMention();
		String memberName = event.getMember().getEffectiveName();
		
		String title = memberName + " created a new embed for " + channel;
		String description = "Title: " + getTitle(event) + 
							"\nDescription: " + getDescription(event) +
							"\nAuthor: " + getAuthor(event) +
							"\nFooter: " + getFooter(event) + 
							"\nColor: " + String.valueOf(getColor(event));

		return MessageUtils.buildEmbed(title, description, memberName, Instant.now());
	}

	private static String getTitle(SlashCommandInteractionEvent event) {
		return event.getOption(TITLE).getAsString();
	}
	
	private static String getDescription(SlashCommandInteractionEvent event) {
		return event.getOption(DESCRIPTION) == null ? "" : event.getOption(DESCRIPTION).getAsString();
	}
	
	private static String getAuthor(SlashCommandInteractionEvent event) {
		return event.getOption(AUTHOR) == null ? "" : event.getOption(AUTHOR).getAsString();
	}
	
	private static String getFooter(SlashCommandInteractionEvent event) {
		return event.getOption(FOOTER) == null ? "" : event.getOption(FOOTER).getAsString();
	}
	
	private static String getColor(SlashCommandInteractionEvent event) {
		return event.getOption(COLOR) == null ? ColorUtils.DEFAULT_HEX  : event.getOption(COLOR).getAsString();
	}
	
	private static GuildChannelUnion getChannel(SlashCommandInteractionEvent event) {
		return event.getOption(CHANNEL).getAsChannel();
	}
}