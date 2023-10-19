package dev.kurumiDisciples.chisataki.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class TTTEventHandler extends ListenerAdapter{
    
    private final static ExecutorService tttExecutor = Executors.newCachedThreadPool();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        tttExecutor.execute(() -> {
            if (event.getButton().getId().startsWith("TTT-")){
               event.deferEdit().queue();
               handleTTTSelection(event);
            }
            else if (event.getButton().getId().startsWith("TTTReqAp-")){
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
            else if (event.getButton().getId().startsWith("TTTReqRe-")){
                event.deferEdit().queue();
                TTTGameSetup setup = TTTUtils.rebuildGameSetupFromRequest(event, event.getButton().getId());
                event.getHook().deleteOriginal().queue();
                Member player1 = setup.getPlayer1();
                player1.getUser().openPrivateChannel().queue((channel) -> {
                    channel.sendMessage("Your request to play Tic Tac Toe with " + setup.getPlayer2().getAsMention() + " has been rejected.").queue(null, (error) -> {
                        System.out.println("Failed to send Tic Tac Toe request rejection message to " + player1.getUser().getAsMention()+ " (" + player1.getId() + ")");
                    });
                });
            }
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


    private void handleTTTSelection(ButtonInteractionEvent event){
        TTTGameSetup setup = TTTUtils.rebuildGameSetupFromButton(event, event.getButton().getId());

        /* Grab all buttons from message */

        List<List<Button>> buttons = new ArrayList<>();
        for (int o = 0; o < event.getMessage().getActionRows().size(); o++){
            buttons.add(event.getMessage().getActionRows().get(o).getButtons());
        }

        /* Grab the button that was pressed */
        Button pressedButton = event.getButton();
        
        /* Grab the row and column of the button that was pressed */
        int row = Integer.valueOf(pressedButton.getId().split("-")[1]);
        int column = Integer.valueOf(pressedButton.getId().split("-")[2]);

        /* Compile into ButtonInfo Class */
        ButtonInfo buttonInfo = new ButtonInfo(pressedButton, row, column);

        /* Grab the player who pressed the button */
        Member player = event.getMember();

        /* Check if the player who pressed the button is the person whos turn it is */
        Member currentPlayer = TTTUtils.getCurrentPlayerFromTTTBoard(event, buttonInfo.getButton());

        /* Does currentplayer match player who pressed button? */
        if (!currentPlayer.getId().equals(player.getId())){
            /* do nothing */
            return;
        }
        List<List<Button>> updatedBoard = new ArrayList<>(); 
        /* Identify Player in game setup */
        if (player.getId().equals(setup.getPlayer1().getId())){
            /* Player 1 */
            updatedBoard = modifyBoard(buttons, setup, buttonInfo, setup.getPlayer1Choice().getEmoji(), setup.getPlayer2());
        }
        else if (player.getId().equals(setup.getPlayer2().getId())){
            /* Player 2 */
            updatedBoard = modifyBoard(buttons, setup, buttonInfo, setup.getPlayer2Choice().getEmoji(), setup.getPlayer1());
        }

        /* Check if the game is over */
        char[][] charBoard = TTTUtils.discordButtonsToCharBoardFromButton(updatedBoard);
        if (TTTLogic.isWin(charBoard)){
            /* Determine who winner is  */

            Member winner = setup.getPlayerFromChoice(TTTLogic.getWinner(charBoard));
            
        }
    }

}
