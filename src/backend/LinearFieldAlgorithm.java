package backend;

import util.Coord;

import java.util.*;

/**
 * Created by joey on 2016.10.05..
 */
public class LinearFieldAlgorithm implements Core {

    private Level level;
    private Set<Coord> markedBoxes;

    public LinearFieldAlgorithm() {
        this.level = new Level();
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


    /*
    @Override
    public void calcFieldOf(int x, int y) {
        level.validateIndexes(x, y);
        Cell.Type type = level.getTypeOf(x, y);
        if (type != Cell.Type.BOX && type != Cell.Type.BOX_SPACE)
            throw new IllegalArgumentException(type.name() + " cell cannot have a field!");

        Set<Coord> currentCells = new HashSet<>();
        Set<Coord> nextCells = new HashSet<>();

        currentCells.add(new Coord(x, y));
        int fieldValue = 0;
        while (!currentCells.isEmpty()) {
            ++fieldValue;
            for (Coord current : currentCells) {
                Coord[] neighbours = new Coord[4];
                neighbours[0] = new Coord(current.getX() - 1, current.getY());
                neighbours[1] = new Coord(current.getX(), current.getY() - 1);
                neighbours[2] = new Coord(current.getX() + 1, current.getY());
                neighbours[3] = new Coord(current.getX(), current.getY() + 1);
                Arrays.stream(neighbours).forEach(System.out::print);
                System.out.println();

                // Check neighbours of the lastly reached field cells
                for (int i = 0; i < 4; i++) {
                    if (level.isValidCoord(neighbours[i].getX(), neighbours[i].getY())) {
                        Cell.Type neighbourType = level.getTypeOf(neighbours[i].getX(), neighbours[i].getY());
                        if (neighbourType == Cell.Type.EMPTY) {
                            level.putField(neighbours[i].getX(), neighbours[i].getY(), fieldValue);
                            nextCells.add(neighbours[i]);
                        } else if (neighbourType == Cell.Type.BOX) {
                            level.put(neighbours[i].getX(), neighbours[i].getY(), Cell.Type.MARKED_BOX);
                            markedBoxes.add(neighbours[i]);
                        } else
                            System.out.println("Not Empty nor Box: " + neighbours[i].toString() + " "
                                    + level.getTypeOf(neighbours[i].getX(), neighbours[i].getY()));
                    }//if
                }//for

                currentCells = nextCells;
                nextCells = new HashSet<>();
                nextCells.forEach(System.out::print);
                System.out.println();
            }//for
        }//while
    }//method
*/

}//class
