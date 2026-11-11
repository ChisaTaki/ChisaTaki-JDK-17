package dev.kurumidisciples.chisataki.games.tictactoe;

import dev.kurumidisciples.chisataki.games.tictactoe.enums.TTTChoice;
import dev.kurumidisciples.chisataki.games.tictactoe.enums.TTTDifficulty;
import net.dv8tion.jda.api.entities.Member;

public class TTTGameSetup {
    
    private TTTChoice player1Choice;
    private TTTChoice player2Choice;
    private Member player1;
    // if player2 is the chisataki bot then the game is single player and the bot will be the second player
    private Member player2;
    private boolean isSinglePlayer;
    private TTTDifficulty difficulty;

    public TTTGameSetup(Member player1, Member player2, boolean isSinglePlayer, TTTDifficulty difficulty) {
        this.player1 = player1;
        this.player2 = player2;
        this.isSinglePlayer = isSinglePlayer;
        this.difficulty = difficulty;
    }
    
    public TTTGameSetup(Member player1, Member player2, boolean isSinglePlayer) {
        this(player1, player2, isSinglePlayer, null);
    }


    public void setPlayer1Choice(TTTChoice player1Choice) {
        this.player1Choice = player1Choice;
        this.player2Choice = TTTChoice.getAlternate(player1Choice);
    }

    public TTTChoice getPlayer1Choice() {
        return this.player1Choice;
    }

    public TTTChoice getPlayer2Choice() {
        return this.player2Choice;
    }

    public Member getPlayer1() {
        return this.player1;
    }

    public Member getPlayer2() {
        return this.player2;
    }

    public TTTDifficulty getDifficulty() {
        return this.difficulty;
    }

    public boolean isSinglePlayer() {
        return isSinglePlayer;
    }

    public Member getPlayerFromChoice(TTTChoice choice){
        if (this.player1Choice.equals(choice)) {
            return this.player1;
        } else if (this.player2Choice.equals(choice)) {
            return this.player2;
        }
        return null;
    }
}
