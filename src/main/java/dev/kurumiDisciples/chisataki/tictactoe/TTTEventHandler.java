package dev.kurumiDisciples.chisataki.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class TTTEventHandler extends ListenerAdapter{
    
    private static final String TTT_PREFIX = "TTT-";
    private static final String TTT_REQ_AP_PREFIX = "TTTReqAp-";
    private static final String TTT_REQ_RE_PREFIX = "TTTReqRe-";

    private final static ExecutorService tttExecutor = Executors.newCachedThreadPool();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        tttExecutor.execute(() -> {
            String buttonId = event.getButton().getId();

            event.deferEdit().queue();

            if (buttonId.startsWith(TTT_PREFIX)) {
                handleTTTSelection(event);
            } else if (buttonId.startsWith(TTT_REQ_AP_PREFIX)) {
                handleTTTRequestAcceptance(event);
            } else if (buttonId.startsWith(TTT_REQ_RE_PREFIX)) {
                handleTTTRequestRejection(event);
            }
        });
    }

        private void handleTTTRequestAcceptance(ButtonInteractionEvent event) {
           event.deferEdit().queue();
                TTTGameSetup setup = TTTUtils.rebuildGameSetupFromRequest(event, event.getButton().getId());
                event.getHook().deleteOriginal().queue();
                List<List<ItemComponent>> ttt = createTicTacToeBoard(setup, /* Player1 is always the player that goes first */setup.getPlayer1());
                event.getChannel().sendMessage(setup.getPlayer1().getAsMention() + " its your turn!")
                .addActionRow(ttt.get(0))
                .addActionRow(ttt.get(1))
                .addActionRow(ttt.get(2))
                .queue(); 
        }
    
        private void handleTTTRequestRejection(ButtonInteractionEvent event) {
           event.deferEdit().queue();
                TTTGameSetup setup = TTTUtils.rebuildGameSetupFromRequest(event, event.getButton().getId());
                event.getHook().deleteOriginal().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE, ErrorResponse.MISSING_PERMISSIONS));
                Member player1 = setup.getPlayer1();
                player1.getUser().openPrivateChannel().queue((channel) -> {
                    channel.sendMessage("Your request to play Tic Tac Toe with " + setup.getPlayer2().getAsMention() + " has been rejected.").queue(null, 
                    new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER, ErrorResponse.UNKNOWN_USER));
                });
            }


     private List<List<ItemComponent>> createTicTacToeBoard(TTTGameSetup setup, Member currentPlayer) {
        List<List<ItemComponent>> ttt = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<ItemComponent> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                // i represents the row, j represents the column
                row.add(Button.of(ButtonStyle.SECONDARY, "TTT-" + i + "-" + j + "-" + setup.getPlayer1().getId() + "-" + setup.getPlayer1Choice().getString() + "-" + setup.getPlayer2().getId() + "-" +  setup.getPlayer2Choice().getString() + "-" + currentPlayer.getId(), " "));
            }
            ttt.add(row);
        }
        return ttt;
    }

    private List<List<Button>> modifyBoard(List<List<Button>> currentBoard, TTTGameSetup setup, ButtonInfo buttonToDisable, Emoji emojiToSet, Member nextPlayer){
        List<List<Button>> updatedBoard = new ArrayList<>();
        for (int i = 0; i < currentBoard.size(); i++) {
            List<Button> row = new ArrayList<>();
            for (int j = 0; j < currentBoard.get(i).size(); j++) {
                // i represents the row, j represents the column
                if (i == buttonToDisable.getRow() && j == buttonToDisable.getColumn()){
                    row.add(Button.of(ButtonStyle.SECONDARY, buttonToDisable.getButton().getId(), emojiToSet).asDisabled());
                }
                else{
                    row.add(currentBoard.get(i).get(j).withId("TTT-" + i + "-" + j + "-" + setup.getPlayer1().getId() + "-" + setup.getPlayer1Choice().getString() + "-" + setup.getPlayer2().getId() + "-" +  setup.getPlayer2Choice().getString() + "-" + nextPlayer.getId()));
                }
            }
            updatedBoard.add(row);
        }
        return updatedBoard;
    }


    private void handleTTTSelection(ButtonInteractionEvent event) {
        TTTGameSetup setup = TTTUtils.rebuildGameSetupFromButton(event, event.getButton().getId());
        Button pressedButton = event.getButton();
        int row = Integer.parseInt(pressedButton.getId().split("-")[1]);
        int column = Integer.parseInt(pressedButton.getId().split("-")[2]);
        ButtonInfo buttonInfo = new ButtonInfo(pressedButton, row, column);
        Member player = event.getMember();
        Member currentPlayer = TTTUtils.getCurrentPlayerFromTTTBoard(event, buttonInfo.getButton());
    
        if (!isCurrentPlayer(player, currentPlayer)) {
            return;
        }
    
        List<List<Button>> buttons = extractButtonsFromMessage(event.getMessage());
        List<List<Button>> updatedBoard;
        Member nextPlayer;
    
        if (isPlayer1(player, setup)) {
            updatedBoard = modifyBoard(buttons, setup, buttonInfo, setup.getPlayer1Choice().getEmoji(), setup.getPlayer2());
            nextPlayer = setup.getPlayer2();
        } else {
            updatedBoard = modifyBoard(buttons, setup, buttonInfo, setup.getPlayer2Choice().getEmoji(), setup.getPlayer1());
            nextPlayer = setup.getPlayer1();
        }
    
        updateMessageAndCheckWinner(event, updatedBoard, nextPlayer, setup);
    }
    
    private boolean isCurrentPlayer(Member player, Member currentPlayer) {
        return currentPlayer.getId().equals(player.getId());
    }
    
    private List<List<Button>> extractButtonsFromMessage(Message message) {
        List<List<Button>> buttons = new ArrayList<>();
        message.getActionRows().forEach(actionRow -> buttons.add(actionRow.getButtons()));
        return buttons;
    }
    
    private boolean isPlayer1(Member player, TTTGameSetup setup) {
        return player.getId().equals(setup.getPlayer1().getId());
    }
    
    private void updateMessageAndCheckWinner(ButtonInteractionEvent event, List<List<Button>> updatedBoard, Member nextPlayer, TTTGameSetup setup) {
        event.getHook().deleteOriginal().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
        Message message = event.getChannel()
                .sendMessage(nextPlayer.getAsMention() + " its your turn!")
                .addActionRow(updatedBoard.get(0))
                .addActionRow(updatedBoard.get(1))
                .addActionRow(updatedBoard.get(2))
                .complete();
    
        char[][] charBoard = TTTUtils.discordButtonsToCharBoardFromButton(updatedBoard);
    
        if (TTTLogic.isWin(charBoard)) {
            Member winner = setup.getPlayerFromChoice(TTTLogic.getWinner(charBoard));
            announceWinner(event, winner);
            message.delete().queueAfter(10L, java.util.concurrent.TimeUnit.SECONDS, null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
        }
    }
    
    private void announceWinner(ButtonInteractionEvent event, Member winner) {
        event.getChannel().sendMessage(winner.getAsMention() + " has won the game!").queue();
    }
    

}
