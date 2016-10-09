package backend;

import util.Coord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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



    @Override
    public void calcFieldOf(int x, int y) {
        level.validateIndexes(x, y);
        Cell.Type type = level.getTypeOf(x, y);
        if (type != Cell.Type.BOX && type != Cell.Type.BOX_SPACE)
            throw new IllegalArgumentException(type.name() + " cell cannot have a field!");

        Set<Coord> currentCells = new HashSet<Coord>();
        Set<Coord> nextCells = new HashSet<Coord>();

        currentCells.add(new Coord(x, y));
        int fieldValue = 0;
        while (!currentCells.isEmpty()) {
            ++fieldValue;
            currentCells.forEach(c -> System.out.print(c + " "));
            System.out.println();
            for (Coord current : currentCells) {
                Coord[] neighbours = new Coord[4];
                neighbours[0] = new Coord(current.getX() - 1, current.getY());
                neighbours[1] = new Coord(current.getX(), current.getY() - 1);
                neighbours[2] = new Coord(current.getX() + 1, current.getY());
                neighbours[3] = new Coord(current.getX(), current.getY() + 1);

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
                        }
                    }
                }

                currentCells = nextCells;
                nextCells = new HashSet<>();
            }//for
        }//while
    }//method

}//class
