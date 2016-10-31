package logic;

import logic.items.Box;
import logic.items.BoxSpace;
import logic.items.Field;
import logic.items.IdentifiableItem;
import util.UnmodGridCoord;
import util.Directions;

import java.util.*;

public class Level {

    private static final String DEFAULT_LEVEL_NAME = "mylevel";

    private String levelName;
    private LevelStructure structure;
    private UnmodGridCoord player;
    private Map<UnmodGridCoord, Box> boxes;
    private Map<UnmodGridCoord, BoxSpace> bspaces;
    private Map<UnmodGridCoord, List<Field>> fields;

    private Cell[][] mergedContent;
    private Cell.Type chosenItemType;
    private Field chosenField;

    public Level() {
        this(14, 10);
    }

    public Level(int widthCellCount, int heightCellCount) {
        levelName = DEFAULT_LEVEL_NAME;
        structure = new LevelStructure(widthCellCount, heightCellCount);
        initMergedContent();
        setToDefaultState();
    }

    public Level(Cell[][] mergedContent, String levelName) {
        this.structure = new LevelStructure(mergedContent.length, mergedContent[0].length);
        this.mergedContent = mergedContent;
        this.levelName = levelName;

        initItemHolders();
        decodeStructureAndItems();
    }

    public void setToDefaultState() {
        initItemHolders();
        structure.setToDefaultState();
        recalculateAllMergedContent();
    }

    public void setchosenField(Field field) {
        if (!field.isTemplateField())
            throw new IllegalArgumentException("The chosen field must be a template field!");
        chosenField = field;
        recalculateAllMergedContent();
    }

    public Optional<Integer> getIdOfSource(UnmodGridCoord coord) {
        Map<UnmodGridCoord, ? extends IdentifiableItem> searchMap;
        switch (chosenItemType) {
            case BOX: searchMap = boxes; break;
            case BSPACE: searchMap = bspaces; break;
            default: throw new IllegalStateException("Coding error!");
        }

        if (!searchMap.containsKey(coord))
            return Optional.empty();
        Integer id = searchMap.get(coord).getId();

        return Optional.of(id);
    }

    public void putItem(Cell.Type type, int w, int h) {
        putItem(type, new UnmodGridCoord(w, h));
    }
    public void putItem(Cell.Type type, UnmodGridCoord coord) {
        if (!structure.isValidCoord(coord)) {
            return;
        }
        switch (type) {
            case WALL: structure.excludeCell(coord);
                if (coord.equals(player))
                    player = null;
                calcMergeContentOfCoord(coord);
                return;
            case EMPTY: structure.includeCell(coord);
                boxes.remove(coord);
                bspaces.remove(coord);
                fields.remove(coord);
                if (coord.equals(player))
                    player = null;
                calcMergeContentOfCoord(coord);
                return;
            case FIELD: throw new IllegalArgumentException("Field type cell cannot only be added" +
                    "along with it's value!");
        }

        if (type.containsBox()) {
            if (coord.equals(player))
                return;
            Box newBox = new Box();
            if (type.containsMarkedBox())
                newBox.setMarked(true);
            boxes.put(coord, newBox);
        }
        if (type.containsBoxSpace())
            bspaces.put(coord, new BoxSpace());
        if (type.containsPlayer() && player == null
                && !boxes.containsKey(coord) && structure.isActiveLevelCell(coord))
            player = coord;

        calcMergeContentOfCoord(coord);
    }

    public void removeAllFields() {
        fields.clear();
        boxes.values().forEach(b -> b.setMarked(false));
        recalculateAllMergedContent();
    }

    public void movePlayer(Directions dir) {
        if (player == null)
            return;

        UnmodGridCoord newPlayerCoord = calcNeighbourCoordOf(player, dir);
        if (!structure.isActiveLevelCell(newPlayerCoord))
            return;
        if (boxes.containsKey(newPlayerCoord)) {
            if (!moveBox(newPlayerCoord, dir))
                return;
        }

        UnmodGridCoord oldPlayerCoord = player;
        player = newPlayerCoord;
        calcMergeContentOfCoord(oldPlayerCoord);
        calcMergeContentOfCoord(newPlayerCoord);
    }

    private boolean moveBox(UnmodGridCoord boxCoord, Directions dir) {
        UnmodGridCoord newBoxCoord = calcNeighbourCoordOf(boxCoord, dir);
        if (!structure.isActiveLevelCell(newBoxCoord))
            return false;
        if (boxes.containsKey(newBoxCoord))
            return false;
        boxes.put(newBoxCoord, boxes.remove(boxCoord));
        calcMergeContentOfCoord(newBoxCoord);
        return true;
    }

    private UnmodGridCoord calcNeighbourCoordOf(UnmodGridCoord coord, Directions dir) {
        int modW = 0, modH = 0;
        switch (dir) {
            case LEFT: modW = -1; break;
            case UP: modH = -1; break;
            case RIGHT: modW = 1; break;
            case DOWN: modH = 1; break;
        }
        return new UnmodGridCoord(coord.getW() + modW, coord.getH() + modH);
    }

    public void addOrRemoveCellLayersOnSide(int layerCount, Directions dir) {
        if (!structure.addOrRemoveCellLayersOnSide(layerCount, dir))
            return;

        int modW = 0, modH = 0;
        switch (dir) {
            case LEFT: modW = layerCount; break;
            case UP: modH = layerCount; break;
        }
        rearrangeItemCoordinatesOf(boxes, modW, modH);
        rearrangeItemCoordinatesOf(bspaces, modW, modH);
        rearrangeItemCoordinatesOf(fields, modW, modH);

        if (player != null) {
            UnmodGridCoord newPlayerCoord = new UnmodGridCoord(player.getW() + modW, player.getH() + modH);
            if (structure.isActiveLevelCell(newPlayerCoord))
                player = newPlayerCoord;
            else
                player = null;
        }

        initMergedContent();
        recalculateAllMergedContent();
    }

    void putField(Field field, UnmodGridCoord coord) {
        if (!structure.isActiveLevelCell(coord))
            throw new IllegalArgumentException("Field cannot be put outside the active map!");
        List<Field> fieldList = fields.get(coord);
        if (fieldList == null) {
            fieldList = new ArrayList<>();
            fields.put(coord, fieldList);
        } else {
            fieldList.forEach(f -> {
                if (f.getItemID().equals(field.getItemID()) && (f.getFieldType() == f.getFieldType())) {
                    throw new IllegalArgumentException("The given type of field from that source is already present!");
                }
            });
        }
        fieldList.add(field);

        if (chosenField != null && field.isSameTypeAndSource(chosenField))
            calcMergeContentOfCoord(coord);
    }
    void putField(Field field, int w, int h) {
        putField(field, new UnmodGridCoord(w, h));
    }
    void putField(Field template, UnmodGridCoord coord, int value) {
        Field field = new Field(template.getItemID(), template.getFieldType(), value);
        putField(field, coord);
    }

    public String getLevelName() {
        return levelName;
    }

    public Field getChosenField() {
        return chosenField;
    }

    public void setBoxMarkAt(UnmodGridCoord coord, boolean marked) {
        Box box = boxes.get(coord);
        if (box == null)
            throw new IllegalArgumentException("No box found at the given coordinate!");
        box.setMarked(marked);
        calcMergeContentOfCoord(coord);
    }

    public void setLevelName(String name) {
        this.levelName = name;
    }

    public Cell[][] getMergedContent() {
        return mergedContent;
    }

    // -----------------------------------------------------------
    public boolean isEmptyAt(UnmodGridCoord coord, Field respectedField) {
        boolean isEmptyOrField = structure.isActiveLevelCell(coord)
                && !boxes.containsKey(coord)
                && !bspaces.containsKey(coord)
                && !coord.equals(player);
        if (!isEmptyOrField)
            return false;

        if (!fields.containsKey(coord))
            return true;

        List<Field> fieldList = fields.get(coord);
        for (Field f : fieldList)
            if (respectedField.isSameTypeAndSource(f))
                return false;
        return true;
    }
    // --------------------------------------------------------------
    public Map<UnmodGridCoord, Box> getBoxes() {
        return Collections.unmodifiableMap(boxes);
    }
    public Map<UnmodGridCoord, BoxSpace> getBoxSpaces() {
        return Collections.unmodifiableMap(bspaces);
    }
    public Map<UnmodGridCoord, List<Field>> getFields() {
        Map<UnmodGridCoord, List<Field>> returnFields = new HashMap<>();
        for (Map.Entry<UnmodGridCoord, List<Field>> entry : fields.entrySet()) {
            returnFields.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return returnFields;
    }

    public boolean isActiveLevelCell(UnmodGridCoord coord) {
        return structure.isActiveLevelCell(coord);
    }

    private void recalculateAllMergedContent() {
        for (int w = 0; w < mergedContent.length; w++)
            for (int h = 0; h < mergedContent[0].length; h++)
                calcMergeContentOfCoord(new UnmodGridCoord(w, h));
    }

    private void calcMergeContentOfCoord(UnmodGridCoord coord) {
        int w = coord.getW(), h = coord.getH();
        if (!structure.isActiveLevelCell(w, h)) {
            mergedContent[w][h].setType(Cell.Type.WALL);
            return;
        }
        if (boxes.containsKey(coord)) {
            setToBoxType(coord);
            return;
        }

        if (coord.equals(player)) {
            setToPlayerType(coord);
            return;
        }

        if (bspaces.containsKey(coord)) {
            mergedContent[w][h].setType(Cell.Type.BSPACE);
            return;
        }

        if (fields.containsKey(coord)) {
            setToFieldType(coord);
            return;
        }

        mergedContent[w][h].setType(Cell.Type.EMPTY);
    }

    private void setToBoxType(UnmodGridCoord coord) {
        Box box = boxes.get(coord);
        int w = coord.getW(), h = coord.getH();
        if (bspaces.containsKey(coord)) {
            if (box.isMarked())
                mergedContent[w][h].setType(Cell.Type.MARKED_BOX_ON_BSPACE);
            else
                mergedContent[w][h].setType(Cell.Type.BOX_ON_BSPACE);
        } else {
            if (box.isMarked())
                mergedContent[w][h].setType(Cell.Type.MARKED_BOX);
            else
                mergedContent[w][h].setType(Cell.Type.BOX);
        }
    }

    private void setToPlayerType(UnmodGridCoord coord) {
        int w = coord.getW(), h = coord.getH();
        if (bspaces.containsKey(coord))
            mergedContent[w][h].setType(Cell.Type.PLAYER_ON_BSPACE);
        else
            mergedContent[w][h].setType(Cell.Type.PLAYER);
    }

    private void setToFieldType(UnmodGridCoord coord) {
        List<Field> fieldList = fields.get(coord);
        for (Field f : fieldList)
            if (f.isSameTypeAndSource(chosenField))
                mergedContent[coord.getW()][coord.getH()].setToFieldType(f.getValue());
    }

    private void initItemHolders() {
        player = null;
        boxes = new HashMap<>();
        bspaces = new HashMap<>();
        fields = new HashMap<>();
        chosenField = null;
    }

    private void initMergedContent() {
        mergedContent = new Cell[structure.getWidthCellCount()][structure.getHeightCellCount()];
        for (int w = 0; w < mergedContent.length; w++) {
            for (int h = 0; h < mergedContent[0].length; h++) {
                mergedContent[w][h] = new Cell();
            }
        }
    }

    private void decodeStructureAndItems() {
        for (int w = 0; w < mergedContent.length; w++) {
            for (int h = 0; h < mergedContent[0].length; h++) {
                Cell.Type type = mergedContent[w][h].getType();
                UnmodGridCoord coord = new UnmodGridCoord(w, h);
                if (type == Cell.Type.WALL) {
                    structure.excludeCell(coord);
                    continue;
                }
                if (type.containsBox())
                    boxes.put(coord, new Box());
                if (type.containsBoxSpace())
                    bspaces.put(coord, new BoxSpace());
                if (type.containsPlayer()) {
                    if (player != null)
                        throw new IllegalArgumentException("Given map has more than one players!");
                    player = coord;
                }
            }
        }
    }

    private <V> void rearrangeItemCoordinatesOf(Map<UnmodGridCoord, V> items, int modW, int modH) {
        if (items.size() == 0)
            return;
        Map<UnmodGridCoord, V> tempItemMap = new HashMap<>();
        for (Map.Entry<UnmodGridCoord, V> entry : items.entrySet()) {
            UnmodGridCoord oldCoord = entry.getKey();
            UnmodGridCoord newCoord = new UnmodGridCoord(oldCoord.getW() + modW, oldCoord.getH() + modH);
            if (structure.isActiveLevelCell(newCoord))
                tempItemMap.put(newCoord, items.get(oldCoord));
        }
        items.clear();
        items.putAll(tempItemMap);
    }

}//class
