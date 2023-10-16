package dev.kurumiDisciples.chisataki.tictactoe;

import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.List;

public class TTTInteractionHandler {
    



    private List<ItemComponent>[] createTicTacToe() {
        List<ItemComponent>[] ttt = new List[3];
        for (int i = 0; i < 3; i++) {
            ttt[i] = new List<ItemComponent>();
            for (int j = 0; j < 3; j++) {
                //i represents the row, j represents the column
                ttt[i].add(Button.of(ButtonStyle.SECONDARY, "tic-tac-toe-" + i + "-" + j, " "));
            }
        }
        return ttt;
    }
}
