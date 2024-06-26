package dev.kurumidisciples.chisataki.tictactoe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.ErrorResponse;

@SuppressWarnings("null")
public class TTTInteractionHandler extends ListenerAdapter{

    private final static ExecutorService tttExecutor = Executors.newCachedThreadPool();

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event){
        tttExecutor.execute(() -> {
            if (event.getComponent().getId().startsWith("menu:TTT-")){
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
                TTTGameSetup setup = TTTUtils.rebuildGameSetupFromMenu(event, event.getComponentId());
                setup.setPlayer1Choice(TTTChoice.getChoice(event.getSelectedOptions().get(0).getValue()));
                
                event.getChannel().sendMessage(setup.getPlayer2().getAsMention() + " you've been requested to play Tic Tac Toe by " + setup.getPlayer1().getAsMention() + ".")
                .setEmbeds(createRequestEmbed(setup))
                .setActionRow(createRequestButtons(setup))
                .queue(message -> {
                    message.delete().queueAfter(10L, java.util.concurrent.TimeUnit.MINUTES, null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
                });
            }
        }); 
    }

    private MessageEmbed createRequestEmbed(TTTGameSetup setup){
        return new EmbedBuilder()
                .setTitle("Tic Tac Toe")
                .setDescription(setup.getPlayer1().getAsMention() + " has requested to play Tic Tac Toe with you.")
                .addField("Player 1", setup.getPlayer1Choice().getEmojString(), true)
                .addField("Player 2", setup.getPlayer2Choice().getEmojString(), true)
                .setImage("https://media.tenor.com/RFanknJESW4AAAAd/tic-tac-toe-kyper.gif")
                .setColor(ColorUtils.PURPLE)
                .build();
    }

    private List<Button> createRequestButtons(TTTGameSetup setup){
        String acceptButtonId = generateButtonIdForReq("TTTReqAp", setup);
        String rejectButtonId = generateButtonIdForReq("TTTReqRe", setup);
        return List.of(
            Button.of(ButtonStyle.SUCCESS, acceptButtonId, "Accept", Emoji.fromUnicode("✔️")),
            Button.of(ButtonStyle.DANGER, rejectButtonId, "Reject", Emoji.fromUnicode("❌"))
        );
    }

    private String generateButtonIdForReq(String front, TTTGameSetup setup){
        String player1Id = setup.getPlayer1().getId();
        String player1Choice = setup.getPlayer1Choice().getString().toLowerCase();
        String player2Id = setup.getPlayer2().getId();
        String player2Choice = setup.getPlayer2Choice().getString();
        return String.format("%s-%s-%s-%s-%s", front, player1Id, player1Choice, player2Id, player2Choice);
    }
}
