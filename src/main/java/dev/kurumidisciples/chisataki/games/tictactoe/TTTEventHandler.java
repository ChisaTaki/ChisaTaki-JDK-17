package dev.kurumidisciples.chisataki.games.tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.enums.GifEnum;
import dev.kurumidisciples.chisataki.games.rps.RpsLogic;
import dev.kurumidisciples.chisataki.games.rps.RpsResult;
import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

@SuppressWarnings("null")
public class TTTEventHandler extends ListenerAdapter {

    private static final String TTT_PREFIX = "TTT-";
    private static final String TTT_REQ_AP_PREFIX = "TTTReqAp-";
    private static final String TTT_REQ_RE_PREFIX = "TTTReqRe-";
    private static final Logger LOGGER = LoggerFactory.getLogger(TTTEventHandler.class);
    private final Set<String> consumedTurns = ConcurrentHashMap.newKeySet();

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        if (!buttonId.startsWith(TTT_PREFIX) && !buttonId.startsWith(TTT_REQ_AP_PREFIX)
                && !buttonId.startsWith(TTT_REQ_RE_PREFIX)) {
            return;
        }
        if (event.getGuild() == null || event.getMember() == null) {
            cannotInteract(event);
            return;
        }

        if (buttonId.startsWith(TTT_PREFIX)) {
            handleTTTSelection(event);
        } else if (buttonId.startsWith(TTT_REQ_AP_PREFIX)) {
            handleTTTRequestAcceptance(event);
        } else {
            handleTTTRequestRejection(event);
        }
    }

    private void handleTTTRequestAcceptance(ButtonInteractionEvent event) {
        // Request IDs: action-player1-piece1-player2-piece2.
        boolean isSinglePlayer = event.getComponentId().split("-")[3].equals(event.getJDA().getSelfUser().getId());
        TTTGameSetup setup = TTTUtils.rebuildGameSetupFromRequest(event, event.getComponentId(), isSinglePlayer);
        if (!playersAvailable(event, setup)) {
            return;
        }
        if (!event.getUser().getId().equals(setup.getPlayer2().getId())) {
            cannotInteract(event);
            return;
        }
        event.deferEdit().queue(hook -> hook.deleteOriginal().queue(
            ignored -> TTTUtils.startGame(event.getChannel(), setup),
            new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)));
    }

    private void handleTTTRequestRejection(ButtonInteractionEvent event) {
        boolean isSinglePlayer = event.getComponentId().split("-")[3].equals(event.getJDA().getSelfUser().getId());
        TTTGameSetup setup = TTTUtils.rebuildGameSetupFromRequest(event, event.getComponentId(), isSinglePlayer);
        if (!playersAvailable(event, setup)) {
            return;
        }
        if (!event.getUser().getId().equals(setup.getPlayer2().getId())) {
            cannotInteract(event);
            return;
        }
        event.deferEdit().queue(hook -> hook.deleteOriginal().queue(ignored -> {
            setup.getPlayer1().getUser().openPrivateChannel().queue(channel -> {
                channel.sendMessage("Your request to play Tic Tac Toe with "
                    + setup.getPlayer2().getAsMention() + " has been rejected.")
                    .queue(null, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER, ErrorResponse.UNKNOWN_USER));
            }, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER, ErrorResponse.UNKNOWN_USER));
        }, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)));
    }

    private boolean playersAvailable(ButtonInteractionEvent event, TTTGameSetup setup) {
        if (setup.getPlayer1() == null || setup.getPlayer2() == null) {
            event.reply("A player is no longer available. Please start a new game.").setEphemeral(true).queue();
            return false;
        }
        return true;
    }

    private void cannotInteract(ButtonInteractionEvent event) {
        event.reply("You cannot interact with this button.").setEphemeral(true).queue();
    }

    private void handleTTTSelection(ButtonInteractionEvent event) {
        // Board IDs: TTT-row-column-player1-piece1-player2-piece2-currentPlayer.
        String[] parts = event.getComponentId().split("-");
        if (parts.length != 8 || !event.getUser().getId().equals(parts[7])) {
            cannotInteract(event);
            return;
        }
        boolean isSinglePlayer = parts[5].equals(event.getJDA().getSelfUser().getId());
        TTTGameSetup setup = TTTUtils.rebuildGameSetupFromButton(event, event.getComponentId(), isSinglePlayer);
        if (!playersAvailable(event, setup)) {
            return;
        }
        Member player = event.getMember();
        if (!player.getId().equals(setup.getPlayer1().getId())
                && !player.getId().equals(setup.getPlayer2().getId())) {
            cannotInteract(event);
            return;
        }

        int row = Integer.parseInt(parts[1]);
        int column = Integer.parseInt(parts[2]);
        List<List<Button>> buttons = extractButtonsFromMessage(event.getMessage());
        if (row < 0 || row > 2 || column < 0 || column > 2 || buttons.size() != 3
                || buttons.stream().anyMatch(buttonRow -> buttonRow.size() != 3)
                || buttons.get(row).get(column).isDisabled()) {
            cannotInteract(event);
            return;
        }
        char[][] board = TTTUtils.discordButtonsToCharBoardFromButton(buttons);
        if (board[row][column] != ' ' || TTTLogic.isWin(board) || TTTLogic.isFull(board)) {
            cannotInteract(event);
            return;
        }

        // Keep successful snapshots consumed too: a queued click can contain the old board.
        String turnKey = event.getMessageId() + ":" + Arrays.deepToString(board);
        if (!consumedTurns.add(turnKey)) {
            event.reply("That turn is already being played. Please use the updated board.")
                .setEphemeral(true).queue();
            return;
        }
        try {
            playTurn(event, setup, board, row, column, turnKey);
        } catch (RuntimeException failure) {
            reportUpdateFailure(event, turnKey, failure);
        }
    }

    private void playTurn(ButtonInteractionEvent event, TTTGameSetup setup, char[][] board, int row, int column,
            String turnKey) {
        boolean player1Turn = event.getUser().getId().equals(setup.getPlayer1().getId());
        TTTChoice choice = player1Turn ? setup.getPlayer1Choice() : setup.getPlayer2Choice();
        board[row][column] = choice.getString().charAt(0);
        Member nextPlayer = player1Turn ? setup.getPlayer2() : setup.getPlayer1();

        if (setup.isSinglePlayer() && player1Turn) {
            TTTLogic.findBestMove(board, setup.getPlayer2Choice()).ifPresent(move ->
                board[move.row()][move.column()] = setup.getPlayer2Choice().getString().charAt(0));
            nextPlayer = setup.getPlayer1();
        }

        List<List<Button>> updatedBoard = TTTUtils.createBoard(setup, board, nextPlayer);
        TTTChoice winner = TTTLogic.getWinner(board);
        boolean finished = winner != null || TTTLogic.isDraw(board);
        MessageEditBuilder update = new MessageEditBuilder().setReplace(true);
        if (winner != null) {
            update.setEmbeds(generateWinnerEmbed(setup, setup.getPlayerFromChoice(winner), updatedBoard));
        } else if (finished) {
            update.setEmbeds(generateDrawEmbed(setup, updatedBoard));
        } else {
            update.setContent(nextPlayer.getAsMention() + " it's your turn!")
                .setComponents(updatedBoard.stream().map(ActionRow::of).toList());
        }

        // Acknowledge the click by editing its source message with BOTH moves in one request.
        event.editMessage(update.build()).queue(hook -> {
            CompletableFuture.delayedExecutor(10L, TimeUnit.MINUTES)
                .execute(() -> consumedTurns.remove(turnKey));
            if (finished) {
                TTTUtils.cancelBoardExpiry(event.getMessageId());
            } else {
                TTTUtils.scheduleBoardExpiry(event.getMessage());
            }
        }, failure -> reportUpdateFailure(event, turnKey, failure));
    }

    private void reportUpdateFailure(ButtonInteractionEvent event, String turnKey, Throwable failure) {
        consumedTurns.remove(turnKey);
        LOGGER.error("Could not update tic tac toe message {} in channel {}",
            event.getMessageId(), event.getChannel().getId(), failure);
    }

    private List<List<Button>> extractButtonsFromMessage(Message message) {
        List<List<Button>> buttons = new ArrayList<>();
        message.getComponents().forEach(actionRow -> buttons.add(actionRow.asActionRow().getButtons()));
        return buttons;
    }

    private MessageEmbed generateDrawEmbed(TTTGameSetup setup, List<List<Button>> board) {
        return new EmbedBuilder()
            .setTitle(setup.getPlayer1().getEffectiveName() + " vs " + setup.getPlayer2().getEffectiveName())
            .addField("Game results", "The game has ended in a draw!", false)
            .addField("Board", boardToString(board), false)
            .setImage(RpsLogic.TIE_GIF.getUrl())
            .setColor(RpsResult.TIE.getColor())
            .build();
    }

    private MessageEmbed generateWinnerEmbed(TTTGameSetup setup, Member winner, List<List<Button>> board) {
        return new EmbedBuilder()
            .setTitle(winner.getEffectiveName() + " has won the game!")
            .addField("Players", setup.getPlayer1().getEffectiveName() + " vs " + setup.getPlayer2().getEffectiveName(), true)
            .addField("Game results", boardToString(board), false)
            .setImage(GifEnum.CHISATO_SIP.getUrl())
            .setColor(ColorUtils.PURPLE)
            .build();
    }

    private String boardToString(List<List<Button>> board) {
        StringBuilder sb = new StringBuilder();
        for (List<Button> row : board) {
            sb.append("|");
            for (Button button : row) {
                sb.append(button.getEmoji() == null ? " " : button.getEmoji().getFormatted());
                sb.append("|");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
