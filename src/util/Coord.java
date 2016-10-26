package util;

/**
 * Created by joey on 2016.10.07..
 */
public class Coord {

    private int w;
    private int h;

    public Coord(int w, int h) {
        this.w = w;
        this.h = h;
    }

    // Make a deep copy
    public Coord(Coord c) {
        w = c.getW();
        h = c.getH();
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public void setW(int w) {
        this.w = w;
    }

    public void setH(int h) {
        this.h = h;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(w).append(", ").append(h).append(')');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coord coord = (Coord) o;

        if (w != coord.w) return false;
        return h == coord.h;

    }

    @Override
    public int hashCode() {
        int result = w;
        result = 31 * result + h;
        return result;
    }
}