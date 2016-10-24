package io;

import logic.Cell;

import java.io.File;

public interface LevelIO {

    // Item mapping
    char WALL = '#';
    char BSPACE = '.';
    char BOX = '$';
    char PLAYER = '@';
    char EMPTY = ' ';
    char PLAYER_ON_BSPACE = '+';
    char BOX_ON_BSPACE =  '*';

    String PARENT_PATH = "Levels/";

    LevelState readLevel(File file) throws LevelIOException;
    void saveLevel(LevelState levelState, File file) throws LevelIOException;

}
