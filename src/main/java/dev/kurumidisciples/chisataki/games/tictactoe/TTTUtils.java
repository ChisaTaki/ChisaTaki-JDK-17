package dev.kurumidisciples.chisataki.games.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.ErrorResponse;

@SuppressWarnings("all")
public class TTTUtils {

    public static void startGame(MessageChannel channel, TTTGameSetup setup) {
        char[][] board = {{' ', ' ', ' '}, {' ', ' ', ' '}, {' ', ' ', ' '}};
        sendBoard(channel, createBoard(setup, board, setup.getPlayer1()), setup.getPlayer1());
    }

    public static List<List<Button>> createBoard(TTTGameSetup setup, char[][] board, Member currentPlayer) {
        List<List<Button>> buttons = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            List<Button> buttonRow = new ArrayList<>();
            for (int column = 0; column < 3; column++) {
                String id = "TTT-" + row + "-" + column + "-" + setup.getPlayer1().getId()
                    + "-" + setup.getPlayer1Choice().getString() + "-" + setup.getPlayer2().getId()
                    + "-" + setup.getPlayer2Choice().getString() + "-" + currentPlayer.getId();
                TTTChoice choice = TTTChoice.getChoiceFromChar(board[row][column]);
                buttonRow.add(choice == null
                    ? Button.of(ButtonStyle.SECONDARY, id, "_")
                    : Button.of(ButtonStyle.SECONDARY, id, choice.getEmoji()).asDisabled());
            }
            buttons.add(buttonRow);
        }
        return buttons;
    }

    public static void sendBoard(MessageChannel channel, List<List<Button>> board, Member currentPlayer) {
        channel.sendMessage(currentPlayer.getAsMention() + " it's your turn!")
            .setComponents(ActionRow.of(board.get(0)), ActionRow.of(board.get(1)), ActionRow.of(board.get(2)))
            .queue(message -> message.delete().queueAfter(10L, TimeUnit.MINUTES, null,
                new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)));
    }

    private static Member resolveMember(Interaction event, String id) {
        if (event.getGuild() == null) {
            return null;
        }
        if (event.getGuild().getSelfMember().getId().equals(id)) {
            return event.getGuild().getSelfMember();
        }
        if (event.getMember() != null && event.getMember().getId().equals(id)) {
            return event.getMember();
        }
        return event.getGuild().getMemberById(id);
    }
    
     public static TTTGameSetup rebuildGameSetupFromMenu(Interaction event, String id, boolean isSinglePlayer){
        String[] ids = id.split("-");
        return new TTTGameSetup(resolveMember(event, ids[1]), resolveMember(event, ids[2]), isSinglePlayer);
    }

    public static TTTGameSetup rebuildGameSetupFromButton(Interaction event, String buttonId, boolean isSinglePlayer){
        String[] ids = buttonId.split("-");
        TTTGameSetup setup = new TTTGameSetup(resolveMember(event, ids[3]), resolveMember(event, ids[5]), isSinglePlayer);
        setup.setPlayer1Choice(TTTChoice.getChoice(ids[4]));
        return setup;
    }

    public static TTTGameSetup rebuildGameSetupFromRequest(Interaction event, String id, boolean isSinglePlayer){
        String[] ids = id.split("-");
        TTTGameSetup setup = new TTTGameSetup(resolveMember(event, ids[1]), resolveMember(event, ids[3]), isSinglePlayer);
        setup.setPlayer1Choice(TTTChoice.getChoice(ids[2]));
        return setup;
    }


    public static char[][] discordButtonsToCharBoard(List<ActionRow> actionRows){
        // ActionRow to Buttons to char[][]
        List<List<Button>> buttons = new ArrayList<>();
        for (int o = 0; o < actionRows.size(); o++){
            buttons.add(actionRows.get(o).getButtons());
        }
        char[][] board = new char[3][3];
        for (int i = 0; i < buttons.size(); i++) {
            for (int j = 0; j < buttons.get(i).size(); j++) {
                if (buttons.get(i).get(j).getEmoji() == null) {
                    board[i][j] = ' ';
                } else if (buttons.get(i).get(j).getEmoji().getAsReactionCode().equals("<:Chinanago:1120915801680134185>")) {
                    board[i][j] = 'o';
                } else if (buttons.get(i).get(j).getEmoji().getAsReactionCode().equals("<:Sakana:1016650006662496326>")) {
                    board[i][j] = 'x';
                }
            }
        }
        return board;
    }

    public static char[][] discordButtonsToCharBoardFromButton(List<List<Button>> buttons){
        // ActionRow to Buttons to char[][]
        char[][] board = new char[3][3];
        for (int i = 0; i < buttons.size(); i++) {
            for (int j = 0; j < buttons.get(i).size(); j++) {
                if (buttons.get(i).get(j).getEmoji() == null) {
                    board[i][j] = ' ';
                } else if (buttons.get(i).get(j).getEmoji().getFormatted().equals("<:Chinanago:1120915801680134185>")) {
                    board[i][j] = 'o';
                } else if (buttons.get(i).get(j).getEmoji().getFormatted().equals("<:Sakana:1016650006662496326>")) {
                    board[i][j] = 'x';
                }
            }
        }
        return board;
    }

    public static Member getCurrentPlayerFromTTTBoard(Interaction event, Button button){
        String[] ids = button.getCustomId().split("-");
        return resolveMember(event, ids[7]);
    }
}
