package dev.kurumiDisciples.chisataki.tictactoe;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.kurumiDisciples.chisataki.utils.ColorUtils;

public class TTTInteractionHandler extends ListenerAdapter{

    private final static ExecutorService tttExecutor = Executors.newCachedThreadPool();

    public void onStringSelectInteraction(StringSelectInteraction event){
        tttExecutor.execute(() -> {
            if (event.getComponentId().startsWith("menu:TTT-")){
                event.deferEdit().queue();
                TTTGameSetup setup = TTTUtils.rebuildGameSetupFromMenu(event, event.getComponentId());
                setup.setPlayer1Choice(TTTChoice.getChoice(event.getSelectedOptions().get(0).getValue()));
                event.getMessage().delete().queue();
                
                event.getChannel().sendMessage(setup.getPlayer2().getAsMention() + " you've been requested to play Tic Tac Toe by " + setup.getPlayer1().getAsMention() + ".")
                .setEmbeds(createRequestEmbed(setup))
                .setActionRow(createRequestButtons(setup))
                .queue();
            }
        }); 
    }

    

   private MessageEmbed createRequestEmbed(TTTGameSetup setup){
        return new EmbedBuilder()
                .setTitle("Tic Tac Toe")
                .setDescription(setup.getPlayer1().getAsMention() + " has requested to play Tic Tac Toe with you.")
                .addField("Player 1", setup.getPlayer1().getAsMention(), true)
                .addField("Player 2", setup.getPlayer2().getAsMention(), true)
                .addField("Player 1 Choice", setup.getPlayer1Choice().getEmojString(), true)
                .addField("Player 2 Choice", setup.getPlayer2Choice().getEmojString(), true)
                .setImage("https://tenor.com/bgSGg.gif")
                .setColor(ColorUtils.PURPLE)
                .build();
   }

   private List<Button> createRequestButtons(TTTGameSetup setup){
         return List.of(Button.of(ButtonStyle.SUCCESS, generateButtonIdForReq("TTTReqAp", setup) , "Accept", Emoji.fromUnicode("✔️")),
         Button.of(ButtonStyle.DANGER, generateButtonIdForReq("TTTReqRe", setup), "Reject", Emoji.fromUnicode("❌")));
   }

   private String generateButtonIdForReq(String front, TTTGameSetup setup){
        return front + "-" + setup.getPlayer1().getId() + "-" + setup.getPlayer1Choice().getString().toLowerCase() + "-" + setup.getPlayer2().getId() + "-" + setup.getPlayer2Choice().getString();
   }
}
