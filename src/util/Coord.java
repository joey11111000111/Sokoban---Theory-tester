package util;

/**
 * Created by joey on 2016.10.07..
 */
public class Coord {

    private int x;
    private int y;

    public Coord(int x,int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(x).append(", ").append(y).append(')');
        return sb.toString();
    }

}