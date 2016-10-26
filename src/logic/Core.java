package logic;

import io.LevelIOException;
import util.Coord;
import util.Directions;

import java.io.File;
import java.util.List;

public interface Core {

//    void remove(int x, int y);
//    void put(int x, int y, Cell.Type type);
//    void clear();

    Cell[][] getCellContent();
    void putItem(Cell.Type type, Coord coord);
    void putItem(Cell.Type type, int w, int h);
    void setToDefaultState();
    void removeAllFields();
    String getLevelName();
    void setLevelName(String levelName);
    void movePlayer(Directions dir);

    void save(File file) throws LevelIOException;
    void loadLevel(File levelFile) throws LevelIOException;
    String getSaveDirectoryPath();


    void calcFieldOf(int x, int y);
}
