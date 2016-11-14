package logic;

import io.LevelIO;
import io.LevelIOException;
import io.LevelState;
import io.NoSQLLevelIO;
import logic.items.Field;
import util.UnmodGridCoord;
import util.Directions;

import java.io.File;
import java.util.function.Consumer;

abstract class AbstractCoreAdapter implements Core {

    protected Level level;
    protected LevelIO levelIO;

    protected AbstractCoreAdapter() {
        level = new Level();
        levelIO = new NoSQLLevelIO();
    }

    @Override
    public Cell[][] getCellContent() {
        return level.getMergedContent();
    }

    @Override
    public void putItem(Cell.Type type, UnmodGridCoord coord) {
        level.putItem(type, coord);
    }

    @Override
    public void putItem(Cell.Type type, int w, int h) {
        level.putItem(type, w, h);
    }

    @Override
    public void setToDefaultState() {
        level.setToDefaultState();
    }

    @Override
    public void removeAllFields() {
        level.removeAllFields();
    }

    @Override
    public String getLevelName() {
        return level.getLevelName();
    }

    @Override
    public void setLevelName(String levelName) {
        level.setLevelName(levelName);
    }

    @Override
    public void movePlayer(Directions dir) {
        level.movePlayer(dir);
    }

    @Override
    public void addOrRemoveCellLayersOnSide(int layerCount, Directions dir) {
        level.addOrRemoveCellLayersOnSide(layerCount, dir);
    }

    @Override
    public void save(File file) throws LevelIOException {
        LevelStateConverter converter = new LevelStateConverter();
        Cell[][] cells = level.getMergedContent();
        LevelState levelState = converter.convertToLevelState(cells);
        levelIO.saveLevel(levelState, file);
    }

    @Override
    public String getSaveDirectoryPath() {
        return LevelIO.PARENT_PATH;
    }

    @Override
    public void loadLevel(File levelFile) throws LevelIOException {
        LevelState levelState = levelIO.readLevel(levelFile);
        LevelStateConverter converter = new LevelStateConverter();
        Cell[][] cells = converter.convertToCells(levelState);
        level = new Level(cells, levelFile.getName());
    }

    @Override
    public void setChosenFieldType(Field.FieldTypes type) {
        level.setChosenFieldType(type);
    }

    @Override
    public void setChosenItem(UnmodGridCoord coord) {
        level.setChosenItem(coord);
    }

    @Override
    public Field.FieldTypes getChosenFieldType() {
        return level.getChosenFieldType();
    }

    // test methods


    @Override
    public void setMergedContentChangeAction(Consumer<UnmodGridCoord> action) {
        level.setMergedContentChangeAction(action);
    }

    @Override
    public void calcAllFields() {
        level.getBoxes().keySet()
                .forEach(coord -> calcFieldOf(coord.getW(), coord.getH()));
        level.getBoxSpaces().keySet()
                .forEach(coord -> calcFieldOf(coord.getW(), coord.getH()));
    }
}
