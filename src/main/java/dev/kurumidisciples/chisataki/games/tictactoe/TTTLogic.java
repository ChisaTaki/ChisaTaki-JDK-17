package dev.kurumidisciples.chisataki.games.tictactoe;

import java.util.Optional;

public class TTTLogic {

    public record Move(int row, int column) {}

    /** Finds an optimal legal move without changing the supplied board. */
    public static Optional<Move> findBestMove(char[][] board, TTTChoice botChoice) {
        if (isWin(board) || isFull(board)) {
            return Optional.empty();
        }

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (board[row][column] != ' ') {
                    continue;
                }
                board[row][column] = botChoice.getString().charAt(0);
                int score = minimax(board, botChoice, false, 1);
                board[row][column] = ' ';
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new Move(row, column);
                }
            }
        }
        return Optional.ofNullable(bestMove);
    }

    private static int minimax(char[][] board, TTTChoice botChoice, boolean botTurn, int depth) {
        TTTChoice winner = getWinner(board);
        if (winner != null) {
            return winner == botChoice ? 10 - depth : depth - 10;
        }
        if (isFull(board)) {
            return 0;
        }

        int bestScore = botTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        char piece = (botTurn ? botChoice : TTTChoice.getAlternate(botChoice)).getString().charAt(0);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (board[row][column] != ' ') {
                    continue;
                }
                board[row][column] = piece;
                int score = minimax(board, botChoice, !botTurn, depth + 1);
                board[row][column] = ' ';
                bestScore = botTurn ? Math.max(bestScore, score) : Math.min(bestScore, score);
            }
        }
        return bestScore;
    }

    public static boolean isWin(char[][] board) {
        return getWinner(board) != null;
    }

    public static TTTChoice getWinner(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return TTTChoice.getChoiceFromChar(board[i][0]);
            }
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return TTTChoice.getChoiceFromChar(board[0][i]);
            }
        }

        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return TTTChoice.getChoiceFromChar(board[0][0]);
        }

        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return TTTChoice.getChoiceFromChar(board[0][2]);
        }

        return null;
    }

    public static boolean isDraw(char[][] board) {
        return !isWin(board) && isFull(board);
    }

    public static boolean isFull(char[][] board) {
        for (char[] chars : board) {
            for (char aChar : chars) {
                if (aChar == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}
