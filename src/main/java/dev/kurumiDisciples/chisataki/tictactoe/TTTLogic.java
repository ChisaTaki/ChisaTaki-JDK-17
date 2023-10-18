package dev.kurumiDisciples.chisataki.tictactoe;

public class TTTLogic {
    


    public static boolean isWin(char[][] board){
        return isHorizontalWin(board) || isVerticalWin(board) || isDiagonalWin(board);
    }

    public static boolean isHorizontalWin(char[][] board){
        for (int i = 0; i < board.length; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]){
                return true;
            }
        }
        return false;
    }

    public static boolean isVerticalWin(char[][] board){
        for (int i = 0; i < board.length; i++) {
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]){
                return true;
            }
        }
        return false;
    }

    public static boolean isDiagonalWin(char[][] board){
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]){
            return true;
        } else if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]){
            return true;
        }
        return false;
    }

    public static TTTChoice whoIsWinner(char[][] board){
        if (isHorizontalWin(board)){
            for (int i = 0; i < board.length; i++) {
                if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]){
                    return TTTChoice.getChoiceFromChar(board[i][0]);
                }
            }
        } else if (isVerticalWin(board)){
            for (int i = 0; i < board.length; i++) {
                if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]){
                    return TTTChoice.getChoiceFromChar(board[0][i]);
                }
            }
        } else if (isDiagonalWin(board)){
            if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]){
                return TTTChoice.getChoiceFromChar(board[0][0]);
            } else if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]){
                return TTTChoice.getChoiceFromChar(board[0][2]);
            }
        }
        return null;
    }
}
