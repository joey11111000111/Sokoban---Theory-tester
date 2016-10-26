package logic;

import io.LevelIOException;
import util.UnmodScreenCoord;
import util.Directions;

import java.io.File;

public interface Core {

    Cell[][] getCellContent();
    void putItem(Cell.Type type, UnmodScreenCoord coord);
    void putItem(Cell.Type type, int w, int h);
    void setToDefaultState();
    void removeAllFields();
    String getLevelName();
    void setLevelName(String levelName);
    void movePlayer(Directions dir);
    void addOrRemoveCellLayersOnSide(int layerCount, Directions dir);

    void save(File file) throws LevelIOException;
    void loadLevel(File levelFile) throws LevelIOException;
    String getSaveDirectoryPath();

    void calcFieldOf(int x, int y);
}
