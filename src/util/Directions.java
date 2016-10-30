package util;

/**
 * Created by joey on 2016.10.23..
 */
public enum Directions {
    LEFT(-1, 0), UP(0, -1), RIGHT(1, 0), DOWN(0, 1);

    int dirWidth;
    int dirHeight;

    Directions(int dirWidth, int dirHeight) {
        this.dirWidth = dirWidth;
        this.dirHeight = dirHeight;
    }

    public int getDirWidth() {
        return dirWidth;
    }

    public void setDirWidth(int dirWidth) {
        this.dirWidth = dirWidth;
    }

    public int getDirHeight() {
        return dirHeight;
    }

    public void setDirHeight(int dirHeight) {
        this.dirHeight = dirHeight;
    }
}
