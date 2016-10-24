package logic;

import io.LevelIOException;
import util.Coord;
import util.Directions;

import java.io.File;
import java.util.List;

public interface Core {

    void put(int x, int y, Cell.Type type);
    void remove(int x, int y);
    void removeAllFields();
    void clear();
    void calcFieldOf(int x, int y);
    Cell[][] getCells();
    List<Coord> getWalls();
    void save(File file) throws LevelIOException;
    void loadLevel(File levelFile) throws LevelIOException;
    String getSaveDirectoryPath();
    String getLevelName();
    void setLevelName(String levelName);
    void movePlayer(Directions dir);
}
