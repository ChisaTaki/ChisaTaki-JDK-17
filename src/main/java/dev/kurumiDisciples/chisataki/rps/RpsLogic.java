package dev.kurumiDisciples.chisataki.rps;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import dev.kurumiDisciples.chisataki.enums.GifEnum;
import dev.kurumiDisciples.chisataki.utils.ColorUtils;
import dev.kurumiDisciples.chisataki.utils.UserUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class RpsLogic {

	public static final double PLAYER_WIN_PERCENTAGE = 0.65;
	public static final  GifEnum TIE_GIF = GifEnum.CHISATO_UNAMUSED;
	private static final Random random = new Random();

	public static RpsResult compareMatch(RpsChoice p1Choice, RpsChoice p2Choice) {
		if (p1Choice == null || p2Choice == null) {
			throw new IllegalArgumentException("RpsChoices must not be null");
		}

		if (p1Choice.equals(p2Choice)) {
			return RpsResult.TIE;
		}

		RpsChoice weakAgainstP1 = RpsChoice.getWeakAgainst(p1Choice);
		return p2Choice.equals(weakAgainstP1) ? RpsResult.WIN : RpsResult.LOSS;
	}

	public static RpsChoice getBotChoice(RpsChoice playerChoice) {
		if (random.nextDouble() < RpsLogic.PLAYER_WIN_PERCENTAGE) {
			return RpsChoice.getWeakAgainst(playerChoice);
		}

		return RpsChoice.getStrongAgainst(playerChoice);
	}

	private static boolean hasAny(RpsChoice choice, RpsChoice p1Choice, RpsChoice p2Choice) {
		return choice.equals(p1Choice) || choice.equals(p2Choice);
	}

	public static List<GifEnum> getRpsGifsFor(RpsChoice p1Choice, RpsChoice p2Choice) {
		List<GifEnum> rpsGifs = new ArrayList<>(
				Arrays.asList(GifEnum.CHISATO_GIGGLE, GifEnum.CHISATO_SIP, GifEnum.TAKINA_LOSES_RPS));

		if (hasAny(RpsChoice.ROCK, p1Choice, p2Choice)) {
			if (hasAny(RpsChoice.PAPER, p1Choice, p2Choice)) {
				rpsGifs.add(GifEnum.JANKEN_ROCK_PAPER);
			} else {
				rpsGifs.add(GifEnum.JANKEN_ROCK_SCISSORS);
			}
		} else if (RpsChoice.PAPER.equals(p1Choice) || RpsChoice.PAPER.equals(p2Choice)) {
			rpsGifs.add(GifEnum.JANKEN_PAPER_SCISSORS);
		}

		return rpsGifs;
	}
	
	public static String getMatchResultGif(RpsResult matchResult, RpsChoice challengerChoice, RpsChoice opponentChoice) {
		return RpsResult.TIE.equals(matchResult) ? RpsLogic.TIE_GIF.getUrl() : getRandomGifUrl(challengerChoice, opponentChoice);
	}

	private static String getRandomGifUrl(RpsChoice p1Choice, RpsChoice p2Choice) {
		List<GifEnum> rpsGifs = getRpsGifsFor(p1Choice, p2Choice);
		int index = random.nextInt(rpsGifs.size());
		return rpsGifs.get(index).getUrl();
	}
	
	public static List<Button> getRpsButtons() {
		return getRpsButtons(null, null, null);
	}

	public static List<Button> getRpsButtons(Member opponent) {
		return getRpsButtons(null, opponent, null);
	}

	public static List<Button> getRpsButtons(Member challenger, Member opponent, String challengerChoice) {
		List<Button> buttons = new ArrayList<>();
		for (RpsChoice choice : RpsChoice.values()) {
			String buttonId = RpsLogic.getButtonId(choice, challenger, opponent, challengerChoice);
			Emoji emoji = Emoji.fromUnicode(choice.getUnicode());
			Button button = Button.secondary(buttonId, choice.toString()).withEmoji(emoji);
			buttons.add(button);
		}
		return buttons;
	}
	
	private static String getButtonId(RpsChoice choice, Member challenger, Member opponent, String challengerChoice) {
		// Singleplayer
		if (opponent == null) {
			return "btnRps-" + choice;
		}
		
		// Multiplayer - challenger has to make a choice
		if (challenger == null) {
			return "btnRpsM-" + choice + "-" + opponent.getId();
		}
		
		// Multiplayer - opponent has to make a choice
		return "btnRpsMR-" + opponent.getId() + "-" + choice + "-" + challenger.getId() + "-" + challengerChoice;
	}
	
	public static String getMatchResultMessage(RpsResult matchResult, RpsChoice challengerChoice, RpsChoice opponentChoice, Member challenger, Member opponent) {

		String description;
		if (RpsResult.TIE.equals(matchResult)) {
			description = String.format("%s chose: **%s**\n%s: **%s**\n\nAnd it's a tie! No winner this time", 
					challenger.getEffectiveName(), challengerChoice, opponent.getEffectiveName(), opponentChoice);
		} else {
			String matchOutput = RpsResult.WIN.equals(matchResult) ? challenger.getUser().getAsMention() : opponent.getAsMention();
			description = String.format("%s chose: **%s**\n%s: **%s**\n\nWinner: %s", challenger.getEffectiveName(), 
					challengerChoice, opponent.getEffectiveName(), opponentChoice, matchOutput);
		}
		
		return description;
	}
	
	public static Color getMatchResultColor(RpsResult matchResult, Member opponent) {
		if (opponent.getId().equals(UserUtils.CHISATAKI_BOT_ID)) {
			return matchResult.getColor();		
		}
		
		return RpsResult.TIE.equals(matchResult) ? RpsResult.TIE.getColor() : ColorUtils.PURPLE;
	}
}