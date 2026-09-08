package dev.kurumidisciples.chisataki.games.tictactoe;

import java.util.List;

import javax.annotation.Nonnull;

import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

@SuppressWarnings("null")
public class TTTInteractionHandler extends ListenerAdapter{

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event){
        String id = event.getComponentId();
        if (!id.startsWith("menu:TTT-")) {
            return;
        }
        String[] parts = id.split("-");
        if (parts.length != 3 || !event.getUser().getId().equals(parts[1])) {
            event.reply("Only the player who started this game can choose a piece.").setEphemeral(true).queue();
            return;
        }
        TTTGameSetup setup = TTTUtils.rebuildGameSetupFromMenu(event, id);
        if (setup.getPlayer1() == null || setup.getPlayer2() == null) {
            event.reply("A player is no longer available. Please start a new game.").setEphemeral(true).queue();
            return;
        }
        TTTChoice choice = event.getValues().size() == 1 ? TTTChoice.getChoice(event.getValues().get(0)) : null;
        if (choice == null) {
            event.reply("Please choose X or O.").setEphemeral(true).queue();
            return;
        }
        setup.setPlayer1Choice(choice);

        event.deferEdit().queue(hook -> hook.deleteOriginal().queue(ignored -> {
            if (setup.isSinglePlayer()) {
                TTTUtils.startGame(event.getChannel(), setup);
            } else {
                event.getChannel().sendMessage(setup.getPlayer2().getAsMention() + " you've been requested to play Tic Tac Toe by " + setup.getPlayer1().getAsMention() + ".")
                .setEmbeds(createRequestEmbed(setup))
                .setComponents(ActionRow.of(createRequestButtons(setup)))
                .queue(message -> {
                    message.delete().queueAfter(10L, java.util.concurrent.TimeUnit.MINUTES, null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
                });
            }
        }, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)));
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
