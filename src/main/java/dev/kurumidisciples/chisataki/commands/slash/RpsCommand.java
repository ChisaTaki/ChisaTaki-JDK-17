package dev.kurumidisciples.chisataki.commands.slash;

import java.util.List;

import dev.kurumidisciples.chisataki.rps.RpsLogic;
import dev.kurumidisciples.chisataki.rps.RpsMultiPlayerHandler;
import dev.kurumidisciples.chisataki.rps.RpsSinglePlayerHandler;
import dev.kurumidisciples.chisataki.utils.MessageUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class RpsCommand extends SlashCommand {

	public RpsCommand() {
		super("rps", "play rock paper scissors");
		this.subcommands = List.of(
				new SubcommandData("singleplayer", "play with the bot"),
				new SubcommandData("multiplayer", "play with other members").addOption(OptionType.USER, "challenge", "user to play with", true)
				);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();

		if (event.getSubcommandName().equals("singleplayer")) {
			MessageCreateData matchStartMessage = MessageUtils.buildMessageCreateData("> So you've decided to challenge me...", RpsSinglePlayerHandler.getChallengerEmbed());
			event.getHook().sendMessage(matchStartMessage).addActionRow(RpsLogic.getRpsButtons()).queue();
		} else if (event.getSubcommandName().equals("multiplayer")) {
			
			OptionMapping opponentOption = event.getOption("challenge");
			if (IgnoreCommand.isMemberIgnored(opponentOption.getAsMember().getId())) {
				event.getHook().editOriginal("This member wishes not to be challenged by other members").queue();
			} else if (opponentOption.getAsUser().isBot()){
				event.getHook().editOriginal("This member cannot be challenged to a match.").queue();
			} else if (opponentOption.getAsMember().getId().equals(event.getMember().getId())){
				event.getHook().editOriginal("You cannot challenge yourself to a match.").queue();
			} else {
				RpsMultiPlayerHandler.startMatch(event);	
			}
		}
	}

}
