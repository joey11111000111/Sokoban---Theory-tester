package logic;

import io.LevelIO;
import io.LevelIOException;
import io.LevelState;
import io.NoDBLevelIO;
import util.Coord;

import java.util.*;

/**
 * Created by joey on 2016.10.05..
 */
public class LinearFieldAlgorithm implements Core {

    private Level level;
    private LevelIO levelIO;
    private Set<Coord> markedBoxes;

    public LinearFieldAlgorithm() {
        this.level = new Level();
        levelIO = new NoDBLevelIO();
        markedBoxes = new HashSet<>();
    }

    @Override
    public List<Coord> getWalls() {
        return level.getWalls();
    }

    @Override
    public void put(int x, int y, Cell.Type type) {
        level.put(x, y, type);
    }

    @Override
    public void remove(int x, int y) {
        level.remove(x, y);
    }

    @Override
    public void removeAllFields() {
        level.removeAllFields();
    }

    @Override
    public void clear() {
        level.clear();
    }

    @Override
    public Cell[][] getCells() {
        return level.getCells();
    }

    private List<Coord> emptyOrBoxNeighboursOf(Coord coord) {
        int x = coord.getX(), y = coord.getY();
        List<Coord> neighbours = new ArrayList<>(4);
        neighbours.add(new Coord(x - 1, y));
        neighbours.add(new Coord(x + 1, y));
        neighbours.add(new Coord(x, y - 1));
        neighbours.add(new Coord(x, y + 1));

        List<Coord> invalids = new ArrayList<>();
        for (Coord c : neighbours) {
            // Remove if the coord is not valid cell or it is neither EMPTY nor BOX
            if (!(level.isValidCoord(c)
                && (level.getTypeOf(c) == Cell.Type.EMPTY
                || level.getTypeOf(c) == Cell.Type.BOX))) {
                invalids.add(c);
            }
        }
        neighbours.removeAll(invalids);

        return neighbours;
    }//method

    private boolean isFieldOmittingCell(int x, int y) {
        return (level.isValidCoord(x, y) && level.getTypeOf(x, y) == Cell.Type.BOX_SPACE);
    }

    @Override
    public void calcFieldOf(int cellX, int cellY) {
        if (!isFieldOmittingCell(cellX, cellY))
            return;

        Set<Coord> currentCells = new HashSet<>();
        Set<Coord> nextCells = new HashSet<>();
        currentCells.add(new Coord(cellX, cellY));

        int fieldValue = 0;
        while (!currentCells.isEmpty()) {
            ++fieldValue;
            for (Coord c : currentCells) {
                List<Coord> neighbours = emptyOrBoxNeighboursOf(c);

                for (Coord nc : neighbours) {
                    switch (level.getTypeOf(nc)) {
                        case EMPTY: level.putField(nc, fieldValue);
                                    nextCells.add(nc);
                                    break;
                        case BOX:   level.put(nc, Cell.Type.MARKED_BOX);
                                    break;
                    }
                }//for
            }//for

            currentCells = nextCells;
            nextCells = new HashSet<>();
        }//while
    }//method

    @Override
    public void save() throws LevelIOException {
        LevelStateConverter converter = new LevelStateConverter();
        Cell[][] cells = level.getCells();
        LevelState levelState = converter.convertToLevelState(cells);
        levelIO.saveLevel(levelState, "test");
    }
}//class
