package logic;

import util.Coord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LinearFieldAlgorithm extends AbstractCoreAdapter {

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
        return (level.isValidCoord(x, y) && level.getTypeOf(x, y) == Cell.Type.BSPACE);
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


}//class
