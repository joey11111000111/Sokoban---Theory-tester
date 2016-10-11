package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by joey on 2016.10.11..
 */
public class NoDBLevelIO implements LevelIO {



    @Override
    public LevelState readLevel(String levelName) {
        return null;
    }

    @Override
    public void saveLevel(LevelState levelState, String name) throws LevelIOException {
        if (!levelState.isSavable())
            throw new LevelIOException("The given level is not savable in it's current state!");

        File levelFile = new File("../../Levels/" + name + ".slvl");
        if (!levelFile.exists()) {
            try {
                if (!levelFile.createNewFile())
                    throw new LevelIOException("Failed attempt to create file \"" + name + ".slvl\"");
            } catch (IOException e) {
                throw new LevelIOException("Exception while creating file \"" + name + ".slvl\""
                        + "\n" + e.getMessage());
            }
        }

        RandomAccessFile writer;
        try {
            writer = new RandomAccessFile(levelFile, "w");
        } catch (FileNotFoundException e) {
            throw new LevelIOException("Unexpected exception, it should have not happened at all!"
                     + "\n" + e.getMessage());
        }

        // Write level state to file
        char[][] allItems = levelState.getAllItems();
        try {
            for (int x = 0; x < allItems.length; x++) {
                for (int y = 0; y < allItems[0].length; y++)
                    writer.write(allItems[x][y]);
                writer.write('\n');
            }//for
        } catch (IOException ioe) {
            throw new LevelIOException("Exception was thrown while writing to file!"
                    + "\n" + ioe.getMessage());
        }
    }//saveLevel

}//class
