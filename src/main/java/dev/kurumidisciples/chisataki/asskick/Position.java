package dev.kurumidisciples.chisataki.asskick;

/**
 * Holds the position data for the butt kick gif. Specifically where the two avatar images should be placed depending on the frame of the gif.
 * <p>The X position is the first index and the Y position is the second index.</p>
 */
public class Position {

    private final int x;
    private final int y;


    public Position(int x, int y){
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
