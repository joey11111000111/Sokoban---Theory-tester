package logic;

import io.LevelIO;
import io.LevelIOException;
import io.LevelState;
import io.NoSQLLevelIO;
import util.UnmodScreenCoord;
import util.Directions;

import java.io.File;

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
    public void putItem(Cell.Type type, UnmodScreenCoord coord) {
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


    /*
    @Override
    public String getLevelName() {
        return level.getLevelName();
    }

    @Override
    public void setLevelName(String levelName) {
        level.setLevelName(levelName);
    }

    @Override
    public List<UnmodScreenCoord> getWalls() {
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
    public Cell[][] getCellContent() {
        return level.getCellContent();
    }

    @Override
    public void movePlayer(Directions dir) {
        level.movePlayer(dir);
    }
*/

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



}
