package dev.kurumiDisciples.chisataki.rps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import dev.kurumiDisciples.chisataki.enums.GifEnum;

public class RpsLogic {

  public static double PLAYER_WIN_PERCENTAGE = 0.65;
  public static GifEnum TIE_GIF = GifEnum.CHISATO_UNAMUSED;
  
  private static Random random = new Random();

  /**
   * Returns: 1 if playerOne wins 0 if tie -1 if playerTwo wins
   */
  public static int compareMatch(RpsChoice p1Choice, RpsChoice p2Choice) {
    if (p1Choice == null || p2Choice == null) {
      throw new IllegalArgumentException("RpsChoices must not be null");
    }

    if (p1Choice.equals(p2Choice)) {
      return 0;
    }

    RpsChoice weakAgainstP1 = RpsChoice.getWeakAgainst(p1Choice);
    return p2Choice.equals(weakAgainstP1) ? 1 : -1;
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

  public static String getRandomGifUrl(RpsChoice p1Choice, RpsChoice p2Choice) {
    List<GifEnum> rpsGifs = getRpsGifsFor(p1Choice, p2Choice);
    int index = random.nextInt(rpsGifs.size());
    return rpsGifs.get(index).getUrl();
  }
}