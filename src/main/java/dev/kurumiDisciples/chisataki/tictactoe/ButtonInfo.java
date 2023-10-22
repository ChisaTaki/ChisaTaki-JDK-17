package dev.kurumiDisciples.chisataki.tictactoe;

import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ButtonInfo {
    private Button button;
    private int row;
    private int column;


    public ButtonInfo(Button button, int row, int column){
        this.button = button;
        this.row = row;
        this.column = column;
    }

    public Button getButton(){
        return this.button;
    }

    public int getRow(){
        return this.row;
    }

    public int getColumn(){
        return this.column;
    }
}
