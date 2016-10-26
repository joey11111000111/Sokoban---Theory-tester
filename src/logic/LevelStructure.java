package logic;

import util.UnmodScreenCoord;
import util.Directions;

public class LevelStructure {

    public static final int MIN_WIDTH = 4;
    public static final int MIN_HEIGHT = 4;

    private boolean[][] structure;

    public LevelStructure(int widthCellCount, int heightCellCount) {
        if (widthCellCount < MIN_WIDTH || heightCellCount < MIN_HEIGHT)
            throw new IllegalArgumentException("The size of the level must be at least "
                    + MIN_WIDTH + "x" + MIN_HEIGHT);

        structure = new boolean[widthCellCount][heightCellCount];
        setToDefaultState(structure);
    }

    public void setToDefaultState() {
        setToDefaultState(structure);
    }

    public boolean isCellOfLevel(UnmodScreenCoord c) {
        return isCellOfLevel(c.getW(), c.getH());
    }
    public boolean isCellOfLevel(int w, int h) {
        if (!isValidCoord(w, h))
            return false;
        return structure[w][h];
    }

    public void excludeCell(UnmodScreenCoord c) {
        excludeCell(c.getW(), c.getH());
    }
    public void excludeCell(int w, int h) {
        validateCoord(w, h);
        if (!isSurroundingWallOf(structure, w, h))
            structure[w][h] = false;
    }

    public void includeCell(UnmodScreenCoord c) {
        includeCell(c.getW(), c.getH());
    }
    public void includeCell(int w, int h) {
        validateCoord(w, h);
        if (!isSurroundingWallOf(structure, w, h))
            structure[w][h] = true;
    }

    public boolean isActiveLevelCell(UnmodScreenCoord coord) {
        return isActiveLevelCell(coord.getW(), coord.getH());
    }
    public boolean isActiveLevelCell(int w, int h) {
        return isValidCoord(w, h) && !isSurroundingWallOf(structure, w, h);
    }

    public void addCellsToSide(int layerCount, Directions dir) {
        int modW = 0, modH = 0;
        switch (dir) {
            case LEFT: modW = -1; break;
            case UP: modH = -1; break;
            case RIGHT: modW = 1; break;
            case DOWN: modH = 1; break;
        }

        int newWidth = Math.abs(modW * layerCount) + structure.length;
        int newHeight = Math.abs(modH * layerCount) + structure[0].length;

        boolean[][] newStructure = new boolean[newWidth][newHeight];
        setToDefaultState(newStructure);
        // Don't copy the surrounding walls
        for (int w = 1; w < structure.length - 1; w++) {
            for (int h = 1; h < structure[0].length - 1; h++) {
                int newW = (modW == -1) ? layerCount + w : w;
                int newH = (modH == -1) ? layerCount + h : h;
                newStructure[newW][newH] = structure[w][h];
            }
        }

        structure = newStructure;
    }

    /* This method should not be touched */
    public void addOrRemoveCellLayersOnSide(int layerCount, Directions dir) {
        int modW = 0, modH = 0;
        switch (dir) {
            case LEFT: modW = -1; break;
            case UP: modH = -1; break;
            case RIGHT: modW = 1; break;
            case DOWN: modH = 1; break;
        }

        int sign = sign(layerCount);
        int newWidth = structure.length + sign * Math.abs(modW * layerCount);
        int newHeight = structure[0].length + sign * Math.abs(modH * layerCount);
        boolean[][] newStructure = new boolean[newWidth][newHeight];
        setToDefaultState(newStructure);

        int smallerCellWidth = (structure.length < newWidth) ? structure.length : newWidth;
        int smallerCellHeight = (structure[0].length < newHeight) ? structure[0].length : newHeight;
        boolean isCut = smallerCellWidth == newWidth;
        int startW = (isCut) ? 1 + structure.length - newWidth : 1;
        int startH = (isCut) ? 1 + structure[0].length - newHeight : 1;
        int endW = startW + smallerCellWidth - 2;
        int endH = startH + smallerCellHeight - 2;
        for (int w = startW; w < endW; w++) {
            for (int h = startH; h < endH; h++) {
                int newW = (modW == -1) ? w + layerCount : w;
                int newH = (modH == -1) ? h + layerCount : h;
                newStructure[newW][newH] = structure[w][h];
            }
        }

        structure = newStructure;
    }

    public int getWidthCellCount() {
        return structure.length;
    }
    public int getHeightCellCount() {
        return structure[0].length;
    }

    private void validateCoord(int w, int h) {
        if (!isValidCoord(w, h))
            throw new IllegalArgumentException("The given cell is out of the level!");
    }

    private boolean isValidCoord(int w, int h) {
        return !(w < 0 || h < 0 || w >= structure.length || h >= structure[0].length);
    }

    private void setToDefaultState(boolean[][] lvlStructure) {
        for (int w = 0; w < lvlStructure.length; w++) {
            for (int h = 0; h < lvlStructure[0].length; h++) {
                structure[w][h] = !isSurroundingWallOf(lvlStructure, w, h);
            }
        }
    }

    private boolean isSurroundingWallOf(boolean[][] lvlStructure, int w, int h) {
        return w == 0 || w == lvlStructure.length - 1 || h == 0 || h == lvlStructure[0].length - 1;
    }

    private int sign(int num) {
        if (num == 0)
            return 1;
        return num / Math.abs(num);
    }
}
