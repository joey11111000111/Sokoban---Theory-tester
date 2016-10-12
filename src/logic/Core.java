package logic;

import io.LevelIOException;
import util.Coord;

import java.util.List;

/**
 * Created by joey on 2016.10.05..
 */
public interface Core {

    void put(int x, int y, Cell.Type type);
    void remove(int x, int y);
    void removeAllFields();
    void clear();
    void calcFieldOf(int x, int y);
    Cell[][] getCells();
    List<Coord> getWalls();
    void save() throws LevelIOException;
    void loadLevel(String levelName) throws LevelIOException;
}
