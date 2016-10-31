package util;

public class UnmodGridCoord {

    private int w;
    private int h;

    public UnmodGridCoord(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
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

        UnmodGridCoord coord = (UnmodGridCoord) o;

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