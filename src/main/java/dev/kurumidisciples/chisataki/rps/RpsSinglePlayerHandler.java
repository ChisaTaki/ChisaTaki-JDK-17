package dev.kurumidisciples.chisataki.rps;

import dev.kurumidisciples.chisataki.utils.UserUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class RpsSinglePlayerHandler extends RpsInteractionHandler {
	
	public static void executeSingleRpsMatch(ButtonInteractionEvent event) {
		// btnRps-<challengerChoice>
		String choiceText = event.getComponentId().split("-")[1].toUpperCase();
		RpsChoice challengerChoice = RpsChoice.valueOf(choiceText);
		RpsChoice opponentChoice = RpsLogic.getBotChoice(challengerChoice);
		
		@SuppressWarnings("null")
		Member opponent = event.getGuild().getMemberById(UserUtils.CHISATAKI_BOT_ID);
		MessageEmbed embed = getMatchResultEmbed(challengerChoice, opponentChoice, event.getMember(), opponent);
		
		event.getHook().deleteOriginal().queue();
		event.getGuildChannel().sendMessageEmbeds(embed).queue();
	}
}
