package logic;

import util.UnmodScreenCoord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LinearFieldAlgorithm extends AbstractCoreAdapter {

    private List<UnmodScreenCoord> emptyOrBoxNeighboursOf(UnmodScreenCoord coord) {
        int x = coord.getW(), y = coord.getH();
        List<UnmodScreenCoord> neighbours = new ArrayList<>(4);
        neighbours.add(new UnmodScreenCoord(x - 1, y));
        neighbours.add(new UnmodScreenCoord(x + 1, y));
        neighbours.add(new UnmodScreenCoord(x, y - 1));
        neighbours.add(new UnmodScreenCoord(x, y + 1));

        List<UnmodScreenCoord> invalids = new ArrayList<>();
        for (UnmodScreenCoord c : neighbours) {
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

        Set<UnmodScreenCoord> currentCells = new HashSet<>();
        Set<UnmodScreenCoord> nextCells = new HashSet<>();
        currentCells.add(new UnmodScreenCoord(cellX, cellY));

        int fieldValue = 0;
        while (!currentCells.isEmpty()) {
            ++fieldValue;
            for (UnmodScreenCoord c : currentCells) {
                List<UnmodScreenCoord> neighbours = emptyOrBoxNeighboursOf(c);

                for (UnmodScreenCoord nc : neighbours) {
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
