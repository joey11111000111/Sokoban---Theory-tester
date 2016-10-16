package logic;


import util.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Level {

    public static String DEFAULT_LEVEL_NAME = "new level";
    private String levelName;
    private final int ROW_LENGTH;
    private final int COL_LENGTH ;
    private Cell[][] cells;

    private boolean isSurroundingWall(int x, int y) {
        return x == 0 || y == 0 || x == ROW_LENGTH -1 || y == COL_LENGTH - 1;
    }

    Level() {
        this(14, 9);
    }

    Level(int rowLength, int colLength) {
        if (rowLength < 4 || colLength < 4)
            throw new IllegalArgumentException("The size of the level must be at least 4x4");

        levelName = DEFAULT_LEVEL_NAME;
        ROW_LENGTH = rowLength;
        COL_LENGTH = colLength;
        cells = new Cell[ROW_LENGTH][COL_LENGTH];
        for (int x = 0; x < ROW_LENGTH; x++) {
            for (int y = 0; y < COL_LENGTH; y++) {
                cells[x][y] = new Cell();
                if (isSurroundingWall(x, y))
                    cells[x][y].setType(Cell.Type.WALL);
            }
        }
    }

    Level(Cell[][] cells, String levelName) {
        this.levelName = levelName;
        ROW_LENGTH = cells.length;
        if (ROW_LENGTH < 4)
            throw new IllegalArgumentException("Given level is not wide enough!");
        COL_LENGTH = cells[0].length;
        if (COL_LENGTH < 4)
            throw new IllegalArgumentException("Given level is not tall enough!");
        this.cells = cells;
    }

    boolean isValidCoord(int x, int y) {
        return !(x < 0 || x >= ROW_LENGTH || y < 0 || y >= COL_LENGTH);
    }
    boolean isValidCoord(Coord coord) {
        return isValidCoord(coord.getX(), coord.getY());
    }

    void validateIndexes(int x, int y) {
        if (x < 0 || x >= ROW_LENGTH || y < 0 || y > COL_LENGTH)
            throw new IndexOutOfBoundsException("(X, Y) = (" + x + ", " + y + ')');
    }

    public String getLevelName() {
        return levelName;
    }
    public void setLevelName(String levelName) {
        if (levelName == null)
            throw new IllegalArgumentException("Level name must not be null!");
        if (levelName.length() == 0)
            throw new IllegalArgumentException("Level name must not be empty string!");

        this.levelName = levelName;
    }

    Cell[][] getCells() {
        return cells;
    }

    void put(int x, int y, Cell.Type type) {
        validateIndexes(x, y);
        if (isSurroundingWall(x, y))
            return;
        if (type == Cell.Type.FIELD)
            throw new IllegalArgumentException("Field must be set along with it's value, using another method!");

        cells[x][y].setType(type);
    }
    void put(Coord c, Cell.Type type) {
        put(c.getX(), c.getY(), type);
    }

    void putField(int x, int y, int fieldValue) {
        validateIndexes(x, y);
        if (isSurroundingWall(x, y))
            return;
        cells[x][y].setType(Cell.Type.FIELD);
        cells[x][y].setFieldValue(fieldValue);
    }
    void putField(Coord c, int fieldValue) {
        putField(c.getX(), c.getY(), fieldValue);
    }

    Cell.Type getTypeOf(int x, int y) {
        validateIndexes(x, y);
        return cells[x][y].getType();
    }
    Cell.Type getTypeOf(Coord c) {
        return getTypeOf(c.getX(), c.getY());
    }

    Integer getFieldValueOf(int x, int y) {
        validateIndexes(x, y);
        if (cells[x][y].getType() != Cell.Type.FIELD)
            throw new IllegalArgumentException("Cell at given index (" + x + ", " + y+ ") is not a field!");

        return cells[x][y].getFieldValue();
    }

    void remove(int x, int y) {
        validateIndexes(x, y);
        if (isSurroundingWall(x, y))
            return;
        cells[x][y].setType(Cell.Type.EMPTY);
    }

    void clear() {
        for (int x = 0; x < ROW_LENGTH; x++)
            for (int y = 0; y < COL_LENGTH; y++)
                if (!isSurroundingWall(x, y) && cells[x][y].getType() != Cell.Type.EMPTY)
                    cells[x][y].setType(Cell.Type.EMPTY);
    }

    List<Coord> getWalls() {
        List<Coord> walls = new ArrayList<>();
        for (int x = 0; x < ROW_LENGTH; x++) {
            for (int y = 0; y < COL_LENGTH; y++) {
                if (cells[x][y].getType() == Cell.Type.WALL)
                    walls.add(new Coord(x, y));
            }
        }

        return walls;
    }

    void removeAllFields() {
        Arrays.stream(cells)
                .flatMap(Arrays::stream)
                .forEach(c -> {
                    if (c.getType() == Cell.Type.FIELD)
                        c.setType(Cell.Type.EMPTY);
                    else if (c.getType() == Cell.Type.MARKED_BOX)
                        c.setType(Cell.Type.BOX);
                });
    }
}
