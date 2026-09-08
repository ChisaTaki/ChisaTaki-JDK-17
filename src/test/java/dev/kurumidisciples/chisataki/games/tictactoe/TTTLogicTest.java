package dev.kurumidisciples.chisataki.games.tictactoe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TTTLogicTest {

    @Test
    public void takesAWinBeforeBlocking() {
        assertEquals(new TTTLogic.Move(0, 2),
            TTTLogic.findBestMove(board("oo ", "xx ", "x  "), TTTChoice.O).orElseThrow());
    }

    @Test
    public void blocksAnImmediateLoss() {
        assertEquals(new TTTLogic.Move(0, 2),
            TTTLogic.findBestMove(board("xx ", " o ", "   "), TTTChoice.O).orElseThrow());
    }

    @Test
    public void preventsAnOppositeCornerFork() {
        TTTLogic.Move move = TTTLogic.findBestMove(board("x  ", " o ", "  x"), TTTChoice.O).orElseThrow();
        assertEquals(1, (move.row() + move.column()) % 2, "An edge prevents the fork");
    }

    @Test
    public void playsTheLastEmptySquare() {
        assertEquals(new TTTLogic.Move(1, 2),
            TTTLogic.findBestMove(board("xox", "xo ", "oxo"), TTTChoice.X).orElseThrow());
    }

    @Test
    public void neverMovesAfterAWinOrDraw() {
        for (TTTChoice choice : TTTChoice.values()) {
            assertTrue(TTTLogic.findBestMove(board("xxx", "oo ", "   "), choice).isEmpty());
            assertTrue(TTTLogic.findBestMove(board("ooo", "xx ", "x  "), choice).isEmpty());
            assertTrue(TTTLogic.findBestMove(board("xox", "xox", "oxo"), choice).isEmpty());
        }
    }

    @Test
    public void recognizesAWinOnTheNinthMoveInsteadOfADraw() {
        char[][] board = board("xox", "oxo", "xox");
        assertEquals(TTTChoice.X, TTTLogic.getWinner(board));
        assertFalse(TTTLogic.isDraw(board));
    }

    @Test
    public void cannotLoseToAnyHumanMoveSequenceWithEitherPiece() {
        for (TTTChoice human : TTTChoice.values()) {
            int games = exploreHumanMoves(board("   ", "   ", "   "), human);
            assertTrue(games > 100, "Must explore complete games for " + human);
        }
    }

    private int exploreHumanMoves(char[][] board, TTTChoice human) {
        int games = 0;
        TTTChoice bot = TTTChoice.getAlternate(human);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (board[row][column] != ' ') {
                    continue;
                }
                board[row][column] = human.getString().charAt(0);
                assertNotEquals(human, TTTLogic.getWinner(board), "The bot allowed a human win");
                if (TTTLogic.isDraw(board)) {
                    games++;
                } else {
                    char[][] beforeSearch = {board[0].clone(), board[1].clone(), board[2].clone()};
                    TTTLogic.Move move = TTTLogic.findBestMove(board, bot).orElseThrow();
                    for (int i = 0; i < 3; i++) {
                        assertArrayEquals(beforeSearch[i], board[i], "Search must preserve the board");
                    }
                    assertEquals(' ', board[move.row()][move.column()]);
                    board[move.row()][move.column()] = bot.getString().charAt(0);
                    if (TTTLogic.isWin(board) || TTTLogic.isDraw(board)) {
                        games++;
                    } else {
                        games += exploreHumanMoves(board, human);
                    }
                    board[move.row()][move.column()] = ' ';
                }
                board[row][column] = ' ';
            }
        }
        return games;
    }

    private char[][] board(String... rows) {
        return new char[][] {rows[0].toCharArray(), rows[1].toCharArray(), rows[2].toCharArray()};
    }
}
