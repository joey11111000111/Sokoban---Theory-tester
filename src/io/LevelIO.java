package io;

import logic.Cell;

public interface LevelIO {

    // Item mapping
    char WALL = '#';
    char BSPACE = '.';
    char BOX = '$';
    char PLAYER = '@';
    char EMPTY = ' ';

    LevelState readLevel(String levelName) throws LevelIOException;
    void saveLevel(LevelState levelState, String name) throws LevelIOException;

}
