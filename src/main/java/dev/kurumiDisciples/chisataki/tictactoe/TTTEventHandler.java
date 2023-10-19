package dev.kurumiDisciples.chisataki.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.dv8tion.jda.api.entities.Member;
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
               TTTGameSetup setup = TTTUtils.rebuildGameSetupFromButton(event, event.getButton().getId());

            }
            else if (event.getButton().getId().startsWith("TTTReqAp-")){
                event.deferEdit().queue();
                TTTGameSetup setup = TTTUtils.rebuildGameSetupFromRequest(event, event.getButton().getId());
                event.getHook().deleteOriginal().queue();
                List<List<ItemComponent>> ttt = createTicTacToeBoard(setup);
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
