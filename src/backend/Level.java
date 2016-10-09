package backend;


import util.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by joey on 2016.10.05..
 */
public class Level {

    public static final int SIZE_X = 20;
    public static final int SIZE_Y = 15;
    private Cell[][] cells;

    public Level() {
        cells = new Cell[SIZE_X][SIZE_Y];
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                cells[x][y] = new Cell();
            }
        }
    }

    public boolean isValidCoord(int x, int y) {
        return !(x < 0 || x >= SIZE_X || y < 0 || y > SIZE_Y);
    }

    public void validateIndexes(int x, int y) {
        if (x < 0 || x >= SIZE_X || y < 0 || y > SIZE_Y)
            throw new IndexOutOfBoundsException("(X, Y) = (" + x + ", " + y + ')');
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void put(int x, int y, Cell.Type type) {
        validateIndexes(x, y);
        if (type == Cell.Type.FIELD)
            throw new IllegalArgumentException("Field must be set along with it's value, using another method!");

        cells[x][y].setType(type);
    }

    public void putField(int x, int y, int fieldValue) {
        validateIndexes(x, y);
        cells[x][y].setType(Cell.Type.FIELD);
        cells[x][y].setFieldValue(fieldValue);
    }

    public Cell.Type getTypeOf(int x, int y) {
        validateIndexes(x, y);
        return cells[x][y].getType();
    }

    public Integer getFieldValueOf(int x, int y) {
        validateIndexes(x, y);
        if (cells[x][y].getType() != Cell.Type.FIELD)
            throw new IllegalArgumentException("Cell at given index (" + x + ", " + y+ ") is not a field!");

        return cells[x][y].getFieldValue();
    }

    public void remove(int x, int y) {
        validateIndexes(x, y);
        cells[x][y].setType(Cell.Type.EMPTY);
    }

    public void clear() {
        for (int x = 0; x < SIZE_X; x++)
            for (int y = 0; y < SIZE_Y; y++)
                if (cells[x][y].getType() != Cell.Type.EMPTY)
                    cells[x][y].setType(Cell.Type.EMPTY);
    }

    public List<Coord> getWalls() {
        List<Coord> walls = new ArrayList<>();
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
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
