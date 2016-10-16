package logic;

import io.LevelIO;
import io.LevelIOException;
import io.LevelState;
import io.NoSQLLevelIO;
import util.Coord;

import java.util.List;

abstract class AbstractCoreAdapter implements Core {

    protected Level level;
    protected LevelIO levelIO;

    protected AbstractCoreAdapter() {
        level = new Level();
        levelIO = new NoSQLLevelIO();
    }

    @Override
    public String getSaveDirectoryPath() {
        return LevelIO.PARENT_PATH;
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
    public void save(String name) throws LevelIOException {
        if (!name.endsWith(".slvl"))
            name = name + ".slvl";
        LevelStateConverter converter = new LevelStateConverter();
        Cell[][] cells = level.getCells();
        LevelState levelState = converter.convertToLevelState(cells);
        levelIO.saveLevel(levelState, name);
    }

    @Override
    public void loadLevel(String levelName) throws LevelIOException {
        LevelState levelState = levelIO.readLevel(levelName);
        LevelStateConverter converter = new LevelStateConverter();
        Cell[][] cells = converter.convertToCells(levelState);
        level = new Level(cells, levelName);
    }

}
