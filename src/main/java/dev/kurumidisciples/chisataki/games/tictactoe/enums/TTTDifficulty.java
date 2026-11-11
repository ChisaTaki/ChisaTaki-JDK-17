package dev.kurumidisciples.chisataki.games.tictactoe.enums;

public enum TTTDifficulty {
    EASY("e", "Easy"),
    MEDIUM("m", "Medium"),
    HARD("h", "Hard");

    private final String id;
    private final String label;

    TTTDifficulty(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static TTTDifficulty fromId(String id) {
        for (TTTDifficulty difficulty : values()) {
            if (difficulty.id.equals(id) || difficulty.label.equalsIgnoreCase(id)) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("Unknown tic tac toe difficulty");
    }
}
