package dev.kurumiDisciples.chisataki.tictactoe;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum TTTChoice {
    O("o", "<:Chinanago:1120915801680134185>"),
    X("x", "<:Sakana:1016650006662496326>");


    private String string;
    private String emojString;

    private TTTChoice(String string, String emojString) {
        this.string = string;
    }


    public String getString() {
        return this.string;
    }

    public static TTTChoice getChoice(String string) {
        for (TTTChoice choice : TTTChoice.values()) {
            if (choice.getString().equals(string)) {
                return choice;
            }
        }
        return null;
    }

    public static TTTChoice getChoiceFromChar(char character){
        for (TTTChoice choice : TTTChoice.values()) {
            if (choice.getString().equals(String.valueOf(character))) {
                return choice;
            }
        }
        return null;
    }

    public String getEmojString() {
        return this.emojString;
    }

    public Emoji getEmoji(){
        return Emoji.fromFormatted(this.emojString);
    }

    public static TTTChoice getChoiceFromEmoji(String emojString) {
        for (TTTChoice choice : TTTChoice.values()) {
            if (choice.getEmojString().equals(emojString)) {
                return choice;
            }
        }
        return null;
    }

    public static TTTChoice getAlternate(TTTChoice choice){
        if (TTTChoice.O.equals(choice)) {
            return TTTChoice.X;
        } else if (TTTChoice.X.equals(choice)) {
            return TTTChoice.O;
        }
        return null;
    }
}
