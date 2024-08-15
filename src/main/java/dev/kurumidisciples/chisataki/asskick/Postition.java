package dev.kurumidisciples.chisataki.asskick;

import java.util.List;

/**
 * Holds the position data for the butt kick gif. Specifically where the two avatar images should be placed depending on the frame of the gif.
 * <p>The X position is the first index and the Y position is the second index.</p>
 */
public class Postition {

    private final int x;
    private final int y;
    
    private static List<Postition> takinaPosition = List.of(
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(139,95),
        new Postition(134,97),
        new Postition(134,92),
        new Postition(138, 81),
        new Postition(134,97),
        new Postition(134,92),
        new Postition(138, 81),
        new Postition(134,97),
        new Postition(134,92),
        new Postition(138, 81),
        new Postition(134,97),
        new Postition(134,92),
        new Postition(138, 81),
        new Postition(134,97),
        new Postition(134,92),
        new Postition(138, 81),
        new Postition(134,97),
        new Postition(134,92),
        new Postition(138, 81), // ass kick begins
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88),
        new Postition(140, 88),
        new Postition(145, 88);
        




    );  


    private int[][] chisatoPosition;


    public Postition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
