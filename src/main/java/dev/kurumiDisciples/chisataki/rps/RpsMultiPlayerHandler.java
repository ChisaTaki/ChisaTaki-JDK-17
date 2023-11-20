package dev.kurumidisciples.chisataki.rps;

import dev.kurumidisciples.chisataki.enums.GifEnum;
import dev.kurumidisciples.chisataki.utils.ColorUtils;
import dev.kurumidisciples.chisataki.utils.MessageUtils;
import dev.kurumidisciples.chisataki.utils.TimeUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class RpsMultiPlayerHandler extends RpsInteractionHandler {

	public static void startMatch(SlashCommandInteractionEvent event) {
		Member opponent = event.getOption("challenge").getAsMember();
		MessageCreateData matchStartMessage = getMatchStartMessage(opponent);
		event.getHook().sendMessage(matchStartMessage).addActionRow(RpsLogic.getRpsButtons(opponent)).queue();	
	}

	private static MessageCreateData getMatchStartMessage(Member opponent) {
		return MessageUtils.buildMessageCreateData("> You have requested a match against *" + opponent.getEffectiveName() + "*", getChallengerEmbed());
	}
	
	public static MessageCreateData getOpponentMessage(Member challenger, Member opponent, long matchStartTime) {
		String messageTitle = "You have been challenged by " + challenger.getEffectiveName();
		MessageEmbed opponentEmbed = MessageUtils.buildEmbed(messageTitle, GifEnum.ROCK_COMES_FIRST.getUrl(), ColorUtils.PURPLE);
		String messageContent = opponent.getAsMention() + " you have been challenged! You must respond <t:" + TimeUtils.getUnixTimeIn10Minutes(matchStartTime) + ":R>!";
		return MessageUtils.buildMessageCreateData(messageContent, opponentEmbed);
	}
	
	public static void handleMatchResults(ButtonInteractionEvent event) {
		// btnRpsMR-<opponentId>-<opponentChoice>-<challengerId>-<challengerChoice>
		Member opponent = event.getMember();
		String[] btnComponents = event.getComponentId().split("-");
		RpsChoice opponentChoice = RpsChoice.valueOf(btnComponents[2].toUpperCase());
		Member challenger = event.getGuild().getMemberById(btnComponents[3]);
		RpsChoice challengerChoice = RpsChoice.valueOf(btnComponents[4].toUpperCase());
	
		MessageEmbed embed = getMatchResultEmbed(challengerChoice, opponentChoice, challenger, opponent);
		event.getHook().deleteOriginal().complete();
		event.getGuildChannel().sendMessageEmbeds(embed).queue();
	}
}
