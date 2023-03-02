package dev.kurumiDisciples.chisataki.listeners;

import dev.kurumiDisciples.chisataki.enums.EmojiEnum;
import dev.kurumiDisciples.chisataki.shrine.ShrineInteractionFactory;
import dev.kurumiDisciples.chisataki.shrine.ShrineInteractionHandler;
import dev.kurumiDisciples.chisataki.utils.FileUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ShrineInteraction extends ListenerAdapter {

  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    Thread shrine = new Thread() {
      public void run() {
        if (event.getName().equals("count")) {
          event.deferReply(true).queue();
          /* what message will look like */
          /*
           * {chiango emote} - (bold{count}) {sakana emote} - (bold{count})
           */

          int chisatoCount = FileUtils.getFileContent("data/chisatoHeart.json").getInt("count");
          int takinaCount = FileUtils.getFileContent("data/takina.json").getInt("count");

          String message = String.format("%s - **%d**\n%s - **%d**", EmojiEnum.CHISATO_HEART.getAsText(), chisatoCount,
              EmojiEnum.SAKANA.getAsText(), takinaCount);
          event.getHook().editOriginal(message).queue();
        }
      }
    };
    shrine.setName("Count-Thread");
    shrine.setPriority(1);
    shrine.start();
  }

  public void onMessageReceived(MessageReceivedEvent event) {
    Thread shrine = new Thread() {
      public void run() {
        ShrineInteractionHandler shrineHandler = ShrineInteractionFactory
            .getShrineInteractionHandler(event.getChannel().getId());
        if (shrineHandler != null) {
          shrineHandler.handleShrineInteraction(event);
        }
      }
    };
    shrine.setName("Shrine Interaction");
    shrine.start();
  }
}