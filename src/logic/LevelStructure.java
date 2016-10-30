package logic;

import util.Size;
import util.UnmodScreenCoord;
import util.Directions;

public class LevelStructure {

    public static final int MIN_WIDTH = 6;
    public static final int MIN_HEIGHT = 6;

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
        return isValidCoord(w, h) && !isSurroundingWallOf(structure, w, h) && structure[w][h];
    }

    public boolean addOrRemoveCellLayersOnSide(int layerCount, Directions dir) {
        int dirW = dir.getDirWidth();
        int dirH = dir.getDirHeight();

        Size oldSize = new Size(structure.length, structure[0].length);
        Size newSize = calcNewSize(oldSize, layerCount, dirW, dirH);
        if (newSize.getWidth() < MIN_WIDTH || newSize.getHeight() < MIN_HEIGHT)
            return false;
        Size smallerSize = Size.smallerSize(oldSize, newSize);

        boolean[][] newStructure = new boolean[newSize.getWidth()][newSize.getHeight()];
        setToDefaultState(newStructure);

        int startW = (isCutFromBeginning(layerCount, dirW)) ? 1 + oldSize.getWidth() - newSize.getWidth() : 1;
        int startH = (isCutFromBeginning(layerCount, dirH)) ? 1 + oldSize.getHeight() - newSize.getHeight() : 1;
        int endW = startW + smallerSize.getWidth() - 2;
        int endH = startH + smallerSize.getHeight() - 2;
        for (int w = startW; w < endW; w++) {
            for (int h = startH; h < endH; h++) {
                int newW = (dir == Directions.LEFT) ? w + layerCount : w;
                int newH = (dir == Directions.UP) ? h + layerCount : h;
                newStructure[newW][newH] = structure[w][h];
            }
        }

        structure = newStructure;
        return true;
    }

    public boolean isValidCoord(int w, int h) {
        return !(w < 0 || h < 0 || w >= structure.length || h >= structure[0].length);
    }

    public boolean isValidCoord(UnmodScreenCoord coord) {
        return isValidCoord(coord.getW(), coord.getH());
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

    private void setToDefaultState(boolean[][] lvlStructure) {
        for (int w = 0; w < lvlStructure.length; w++) {
            for (int h = 0; h < lvlStructure[0].length; h++) {
                lvlStructure[w][h] = !isSurroundingWallOf(lvlStructure, w, h);
            }
        }
    }

    private boolean isCutFromBeginning(int layerCount, int modDir) {
        return layerCount < 0 && modDir == -1;
    }

    private Size calcNewSize(Size oldSize, int layerCount, int dirW, int dirH) {
        int sign = (layerCount < 0) ? -1 : 1;
        int newWidth = oldSize.getWidth() + sign * Math.abs(dirW * layerCount);
        int newHeight = oldSize.getHeight() + sign * Math.abs(dirH * layerCount);
        return new Size(newWidth, newHeight);
    }

    private boolean isSurroundingWallOf(boolean[][] lvlStructure, int w, int h) {
        return w == 0 || w == lvlStructure.length - 1 || h == 0 || h == lvlStructure[0].length - 1;
    }

}
