package dev.kurumiDisciples.chisataki.tictactoe;

public class TTTLogic {

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
