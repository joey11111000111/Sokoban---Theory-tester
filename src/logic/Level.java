package logic;

import util.UnmodScreenCoord;
import util.Directions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Level {

    private static final String DEFAULT_LEVEL_NAME = "mylevel";

    private String levelName;
    private LevelStructure structure;
    private UnmodScreenCoord player;
    private Map<UnmodScreenCoord, Box> boxes;
    private Map<UnmodScreenCoord, BoxSpace> bspaces;
    private Map<UnmodScreenCoord, List<Field>> fields;

    private Cell[][] mergedContent;
    private Cell.Type chosenItemType;
    private Field chosenField;

    public Level() {
        this(14, 9);
    }

    public Level(int widthCellCount, int heightCellCount) {
        levelName = DEFAULT_LEVEL_NAME;
        structure = new LevelStructure(widthCellCount, heightCellCount);
        mergedContent = new Cell[structure.getWidthCellCount()][structure.getHeightCellCount()];

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

    public void setchosenItemType(Cell.Type type) {
        if (type != Cell.Type.BSPACE && type != Cell.Type.BOX)
            throw new IllegalArgumentException("Only a box space or a box can be a source of field!");
        chosenItemType = type;
    }

    public void setchosenField(Field field) {
        if (!field.isTemplateField())
            throw new IllegalArgumentException("The chosen field must be a template field!");
        chosenField = field;
    }

    public Optional<Integer> getIdOfSource(UnmodScreenCoord coord) {
        Map<UnmodScreenCoord, ? extends IdentifiableItem> searchMap;
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
        putItem(type, new UnmodScreenCoord(w, h));
    }
    public void putItem(Cell.Type type, UnmodScreenCoord coord) {
        if (!structure.isActiveLevelCell(coord)) {
            return;
        }
        switch (type) {
            case WALL: structure.excludeCell(coord); return;
            case EMPTY: structure.includeCell(coord); return;
            case FIELD: throw new IllegalArgumentException("Field type cell cannot only be added" +
                    "along with it's value!");
        }

        if (type.containsBox()) {
            Box newBox = new Box();
            if (type.containsMarkedBox())
                newBox.setMarked(true);
            boxes.put(coord, newBox);
        }
        if (type.containsBoxSpace())
            bspaces.put(coord, new BoxSpace());
        if (type.containsPlayer() && player == null)
            player = coord;

        calcMergeContentOfCoord(coord);
    }

    public void removeAllFields() {
        fields.values().forEach(List::clear);
        // Replace fields to empty cells in the merged content.
        for (int w = 0; w < mergedContent.length; w++) {
            for (int h = 0; h < mergedContent[0].length; h++) {
                if (mergedContent[w][h].getType() == Cell.Type.FIELD)
                    mergedContent[w][h].setType(Cell.Type.EMPTY);
            }
        }
    }

    public void movePlayer(Directions dir) {
        if (player == null)
            return;

        UnmodScreenCoord newPlayerCoord = calcNeighbourCoordOf(player, dir);
        if (!structure.isActiveLevelCell(newPlayerCoord))
            return;
        if (boxes.containsKey(newPlayerCoord)) {
            if (!moveBox(newPlayerCoord, dir))
                return;
        }

        UnmodScreenCoord oldPlayerCoord = player;
        player = newPlayerCoord;
        calcMergeContentOfCoord(oldPlayerCoord);
        calcMergeContentOfCoord(newPlayerCoord);
    }

    private boolean moveBox(UnmodScreenCoord boxCoord, Directions dir) {
        UnmodScreenCoord newBoxCoord = calcNeighbourCoordOf(boxCoord, dir);
        if (!structure.isActiveLevelCell(newBoxCoord))
            return false;
        if (boxes.containsKey(newBoxCoord))
            return false;
        boxes.put(newBoxCoord, boxes.remove(boxCoord));
        calcMergeContentOfCoord(newBoxCoord);
        return true;
    }

    private UnmodScreenCoord calcNeighbourCoordOf(UnmodScreenCoord coord, Directions dir) {
        int modW = 0, modH = 0;
        switch (dir) {
            case LEFT: modW = -1; break;
            case UP: modH = -1; break;
            case RIGHT: modW = 1; break;
            case DOWN: modH = 1; break;
        }
        return new UnmodScreenCoord(coord.getW() + modW, coord.getH() + modH);
    }

    public void addOrRemoveCellLayersOnSide(int layerCount, Directions dir) {
        structure.addOrRemoveCellLayersOnSide(layerCount, dir);
        int modW = 0, modH = 0;
        switch (dir) {
            case LEFT: modW = layerCount; break;
            case UP: modH = layerCount; break;
            default: return;
        }

        rearrangeItemCoordinatesOf(boxes, modW, modH);
        rearrangeItemCoordinatesOf(bspaces, modW, modH);
        rearrangeItemCoordinatesOf(fields, modW, modH);

        UnmodScreenCoord newPlayerCoord = new UnmodScreenCoord(player.getW() + modW, player.getH() + modH);
        if (structure.isActiveLevelCell(newPlayerCoord))
            player = newPlayerCoord;
        else
            player = null;

        mergedContent = new Cell[structure.getWidthCellCount()][structure.getHeightCellCount()];
        recalculateAllMergedContent();
    }

    void putField(Field field, UnmodScreenCoord coord) {
        if (!structure.isActiveLevelCell(coord))
            throw new IllegalArgumentException("Field cannot be put outside the active map!");
        List<Field> fieldList = fields.get(coord);
        fieldList.forEach(f -> {
            if (f.getItemID().equals(field.getItemID()) && (f.getFieldType() == f.getFieldType())) {
                throw new IllegalArgumentException("The given type of field from that source is already present!");
            }
        });
        fieldList.add(field);

        if (field.isSameTypeAndSource(chosenField))
            calcMergeContentOfCoord(coord);
    }
    void putField(Field field, int w, int h) {
        putField(field, new UnmodScreenCoord(w, h));
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String name) {
        this.levelName = name;
    }

    public Cell[][] getMergedContent() {
        return mergedContent;
    }


    private void recalculateAllMergedContent() {
        for (int w = 0; w < mergedContent.length; w++)
            for (int h = 0; h < mergedContent[0].length; h++)
                calcMergeContentOfCoord(new UnmodScreenCoord(w, h));
    }

    private void calcMergeContentOfCoord(UnmodScreenCoord coord) {
        int w = coord.getW(), h = coord.getH();
        if (!structure.isCellOfLevel(w, h)) {
            mergedContent[w][h].setType(Cell.Type.WALL);
            return;
        }
        if (boxes.containsKey(coord)) {
            setToBoxType(coord);
            return;
        }

        if (player.equals(coord)) {
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

    private void buildLevelByMergedContent() {
        for (int w = 0; w < mergedContent.length; w++) {
            for (int h = 0; h < mergedContent[0].length; h++) {

            }
        }
    }

    private void setToBoxType(UnmodScreenCoord coord) {
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

    private void setToPlayerType(UnmodScreenCoord coord) {
        int w = coord.getW(), h = coord.getH();
        if (bspaces.containsKey(coord))
            mergedContent[w][h].setType(Cell.Type.PLAYER_ON_BSPACE);
        else
            mergedContent[w][h].setType(Cell.Type.PLAYER);
    }

    private void setToFieldType(UnmodScreenCoord coord) {
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

    private void decodeStructureAndItems() {
        for (int w = 0; w < mergedContent.length; w++) {
            for (int h = 0; h < mergedContent[0].length; h++) {
                Cell.Type type = mergedContent[w][h].getType();
                UnmodScreenCoord coord = new UnmodScreenCoord(w, h);
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

    private <V> void rearrangeItemCoordinatesOf(Map<UnmodScreenCoord, V> items, int modW, int modH) {
        for (Map.Entry<UnmodScreenCoord, V> entry : items.entrySet()) {
            UnmodScreenCoord oldCoord = entry.getKey();
            UnmodScreenCoord newCoord = new UnmodScreenCoord(oldCoord.getW() + modW, oldCoord.getH() + modH);
            if (structure.isActiveLevelCell(newCoord))
                items.put(newCoord, items.remove(oldCoord));
        }
    }

}//class
