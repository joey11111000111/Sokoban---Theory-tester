package logic;

import io.LevelIOException;
import logic.items.Field;
import util.UnmodGridCoord;
import util.Directions;

import java.io.File;
import java.util.function.Consumer;

public interface Core {

    Cell[][] getCellContent();
    void putItem(Cell.Type type, UnmodGridCoord coord);
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
    void calcAllFields();

    void setChosenFieldType(Field.FieldTypes type);
    Field.FieldTypes getChosenFieldType();
    void setChosenItem(UnmodGridCoord coord);

    // Test methods
    void setMergedContentChangeAction(Consumer<UnmodGridCoord> action);
}
