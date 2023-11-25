package dev.kurumidisciples.chisataki.rps;

import java.awt.Color;

import dev.kurumidisciples.chisataki.enums.GifEnum;
import dev.kurumidisciples.chisataki.utils.ColorUtils;
import dev.kurumidisciples.chisataki.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * A Rock-Paper-Scissors game session consists of matches (only one (1) supported at the moment).
 * A challenger initiates a game session against an opponent.
 * The first player to win X matches is the game session winner
 * 
 * Challenger: User that triggers the /rps command
 * Opponent: User challenged (ChisaTaki bot or another user)
 */
public abstract class RpsInteractionHandler {
	
	public static MessageEmbed getChallengerEmbed() {
		return MessageUtils.buildEmbed("> Rock comes first! Rock-Paper-Scissors, go!", GifEnum.ROCK_COMES_FIRST.getUrl(), ColorUtils.PURPLE);
	}
	
	/**
	 * 1. Calculate match output
	 * 2. Build match results
	 * 3. Send match results
	 */
	protected static MessageEmbed getMatchResultEmbed(RpsChoice challengerChoice, RpsChoice opponentChoice, Member challenger, Member opponent) {
		RpsResult matchResult = RpsLogic.compareMatch(challengerChoice, opponentChoice);
		String description = RpsLogic.getMatchResultMessage(matchResult, challengerChoice, opponentChoice, challenger, opponent);
		String gifUrl = RpsLogic.getMatchResultGif(matchResult, challengerChoice, opponentChoice);
		Color color = RpsLogic.getMatchResultColor(matchResult, opponent);
		return MessageUtils.buildEmbed("Rock-Paper-Scissors Result", description, gifUrl, color);
	}
}