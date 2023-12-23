package dev.kurumidisciples.chisataki.shrine;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;

public class ShrineInteractionFactory {

  public static ShrineInteractionHandler getShrineInteractionHandler(String channelId) {
    if (ChannelEnum.CHISATO_SHRINE.getId().equals(channelId)) {
      return new ChisatoShrineInteractionHandler();
    } else if (ChannelEnum.TAKINA_SHRINE.getId().equals(channelId)) {
      return new TakinaShrineInteractionHandler();
    }
    return null;
  }
}
