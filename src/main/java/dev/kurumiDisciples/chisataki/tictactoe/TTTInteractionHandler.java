package dev.kurumiDisciples.chisataki.tictactoe;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TTTInteractionHandler extends ListenerAdapter{
    
    private List<List<ItemComponent>> board;

    private final static ExecutorService tttExecutor = Executors.newCachedThreadPool();


    

    public void onStringSelectInteraction(StringSelectInteraction event){
        tttExecutor.execute(() -> {
            if (event.getComponentId().startsWith("menu:TTT-")){
                event.deferEdit().queue();
                TTTGameSetup setup = TTTUtils.rebuildGameSetupFromMenu(event, event.getComponentId());
                setup.setPlayer1Choice(TTTChoice.getChoice(event.getSelectedOptions().get(0).getValue()));
                event.getMessage().delete().queue();
                board = createTicTacToeBoard(setup);
                event.getChannel().sendMessage("TTT Test").addActionRow(board.get(0)).addActionRow(board.get(1)).addActionRow(board.get(2)).queue();
            }
        }); 
    }


    private List<List<ItemComponent>> createTicTacToeBoard(TTTGameSetup setup) {
        List<List<ItemComponent>> ttt = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<ItemComponent> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                // i represents the row, j represents the column
                row.add(Button.of(ButtonStyle.SECONDARY, "TTT-" + i + "-" + j + "-" + setup.getPlayer1().getId() + "-" + setup.getPlayer1Choice().getString() + "-" + setup.getPlayer2().getId() + "-" +  setup.getPlayer2Choice().getString(), " "));
            }
            ttt.add(row);
        }
        return ttt;
    }
    

   
}
