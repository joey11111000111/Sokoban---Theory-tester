package logic;

import logic.items.Box;
import logic.items.BoxSpace;
import logic.items.Field;
import logic.items.IdentifiableItem;
import util.UnmodGridCoord;

import java.util.*;

public class LinearFieldAlgorithm extends AbstractCoreAdapter {


    @Override
    public void calcFieldOf(int w, int h) {
        Map<UnmodGridCoord, Box> boxes = level.getBoxes();
        Map<UnmodGridCoord, BoxSpace> bspaces = level.getBoxSpaces();

        UnmodGridCoord sourceCoord = new UnmodGridCoord(w, h);
        if (boxes.containsKey(sourceCoord))
            calcPlayerFieldOf(boxes.get(sourceCoord), sourceCoord);
        if (bspaces.containsKey(sourceCoord))
            calcPlayerFieldOf(bspaces.get(sourceCoord), sourceCoord);

        System.out.println("Eljutott eddig");
    }

    private void calcPlayerFieldOf(IdentifiableItem source, UnmodGridCoord sourceCoord) {
        Map<UnmodGridCoord, Box> boxes = level.getBoxes();
        Map<UnmodGridCoord, BoxSpace> bspaces = level.getBoxSpaces();
        Map<UnmodGridCoord, List<Field>> fields = level.getFields();

        Set<UnmodGridCoord> currentCells = new HashSet<>();
        Set<UnmodGridCoord> nextCells = new HashSet<>();
        currentCells.add(sourceCoord);

        Field fieldItem = new Field(source.getId(), Field.FieldTypes.PLAYER_FIELD, 0);
        level.putField(fieldItem, sourceCoord);
        while (!currentCells.isEmpty()) {
            fieldItem = Field.incrementFieldValue(fieldItem);
            for (UnmodGridCoord currentCoord : currentCells) {
                Set<UnmodGridCoord> validNeighbours = getValidNeighboursForPlayerField(currentCoord, fieldItem);
                for (UnmodGridCoord currentNeighbour : validNeighbours) {
                    if (boxes.containsKey(currentNeighbour)) {
                        boxes.get(currentNeighbour).setMarked(true);
                        continue;
                    }

                    level.putField(fieldItem, currentNeighbour);
                    nextCells.add(currentNeighbour);
                }
            }

            currentCells = nextCells;
            nextCells = new HashSet<>();
        }

        level.setchosenField(Field.createFieldTemplate(fieldItem));
    }

    private Set<UnmodGridCoord> getValidNeighboursForPlayerField(UnmodGridCoord coord, Field templateField) {
        Set<UnmodGridCoord> validNeighbours = new HashSet<>();
        NEIGHBOUR_LOOP:
        for (UnmodGridCoord currentNeighbour : getAllNeighboursOf(coord)) {
            if (!level.isActiveLevelCell(currentNeighbour))
                continue;
            if (level.getBoxes().containsKey(currentNeighbour)) {
                validNeighbours.add(currentNeighbour);
                continue;
            }
            List<Field> fieldsOfNeighbour = level.getFields().get(currentNeighbour);
            if (fieldsOfNeighbour != null) {
                for (Field f : fieldsOfNeighbour) {
                    if (templateField.isSameTypeAndSource(f))
                        continue NEIGHBOUR_LOOP;
                }
            }

            validNeighbours.add(currentNeighbour);
        }

        return validNeighbours;
    }

    private Set<UnmodGridCoord> getAllNeighboursOf(UnmodGridCoord coord) {
        Set<UnmodGridCoord> neighbours = new HashSet<>(4);
        int w = coord.getW();
        int h = coord.getH();
        neighbours.add(new UnmodGridCoord(w + 1, h));
        neighbours.add(new UnmodGridCoord(w - 1, h));
        neighbours.add(new UnmodGridCoord(w, h + 1));
        neighbours.add(new UnmodGridCoord(w, h - 1));

        return neighbours;
    }

    /*
    @Override
    public void calcFieldOf(int cellX, int cellY) {

        if (!isFieldOmittingCell(cellX, cellY))
            return;

        Set<UnmodGridCoord> currentCells = new HashSet<>();
        Set<UnmodGridCoord> nextCells = new HashSet<>();
        currentCells.add(new UnmodGridCoord(cellX, cellY));

        int fieldValue = 0;
        while (!currentCells.isEmpty()) {
            ++fieldValue;
            for (UnmodGridCoord c : currentCells) {
                List<UnmodGridCoord> neighbours = emptyOrBoxNeighboursOf(c);

                for (UnmodGridCoord nc : neighbours) {
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
*/

/*
private List<UnmodGridCoord> emptyOrBoxNeighboursOf(UnmodGridCoord coord, Field fieldTemplate) {
    int x = coord.getW(), y = coord.getH();
    List<UnmodGridCoord> neighbours = new ArrayList<>(4);
    neighbours.add(new UnmodGridCoord(x - 1, y));
    neighbours.add(new UnmodGridCoord(x + 1, y));
    neighbours.add(new UnmodGridCoord(x, y - 1));
    neighbours.add(new UnmodGridCoord(x, y + 1));

    List<UnmodGridCoord> invalids = new ArrayList<>();
    for (UnmodGridCoord c : neighbours) {
        // Remove if the coord is not valid cell or it is neither EMPTY nor BOX
        if ( !(level.isBoxAt(c) || level.isEmptyAt(c, fieldTemplate)) )
            invalids.add(c);
    }
    neighbours.removeAll(invalids);

    return neighbours;
}//method
*/


}//class
