package backend;


import util.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by joey on 2016.10.05..
 */
class Level {

    static final int ROW_LENGTH = 20;
    static final int COL_LENGTH = 15;
    private Cell[][] cells;

    Level() {
        cells = new Cell[ROW_LENGTH][COL_LENGTH];
        for (int x = 0; x < ROW_LENGTH; x++)
            for (int y = 0; y < COL_LENGTH; y++)
                cells[x][y] = new Cell();
    }

    boolean isValidCoord(int x, int y) {
        return !(x < 0 || x >= ROW_LENGTH || y < 0 || y > COL_LENGTH);
    }

    void validateIndexes(int x, int y) {
        if (x < 0 || x >= ROW_LENGTH || y < 0 || y > COL_LENGTH)
            throw new IndexOutOfBoundsException("(X, Y) = (" + x + ", " + y + ')');
    }

    Cell[][] getCells() {
        return cells;
    }

    void put(int x, int y, Cell.Type type) {
        validateIndexes(x, y);
        if (type == Cell.Type.FIELD)
            throw new IllegalArgumentException("Field must be set along with it's value, using another method!");

        cells[x][y].setType(type);
    }

    void putField(int x, int y, int fieldValue) {
        validateIndexes(x, y);
        cells[x][y].setType(Cell.Type.FIELD);
        cells[x][y].setFieldValue(fieldValue);
    }

    Cell.Type getTypeOf(int x, int y) {
        validateIndexes(x, y);
        return cells[x][y].getType();
    }

    Integer getFieldValueOf(int x, int y) {
        validateIndexes(x, y);
        if (cells[x][y].getType() != Cell.Type.FIELD)
            throw new IllegalArgumentException("Cell at given index (" + x + ", " + y+ ") is not a field!");

        return cells[x][y].getFieldValue();
    }

    void remove(int x, int y) {
        validateIndexes(x, y);
        cells[x][y].setType(Cell.Type.EMPTY);
    }

    public void clear() {
        for (int x = 0; x < ROW_LENGTH; x++)
            for (int y = 0; y < COL_LENGTH; y++)
                if (cells[x][y].getType() != Cell.Type.EMPTY)
                    cells[x][y].setType(Cell.Type.EMPTY);
    }

    public List<Coord> getWalls() {
        List<Coord> walls = new ArrayList<>();
        for (int x = 0; x < ROW_LENGTH; x++) {
            for (int y = 0; y < COL_LENGTH; y++) {
                if (cells[x][y].getType() == Cell.Type.WALL)
                    walls.add(new Coord(x, y));
            }
        }

        return walls;
    }

    public void removeAllFields() {
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
