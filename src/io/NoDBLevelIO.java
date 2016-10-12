package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class NoDBLevelIO implements LevelIO {

    private final String PARENT_PATH = "Levels/";
    private final String EXTENSION = ".slvl";

    @Override
    public LevelState readLevel(String levelName) throws LevelIOException {
        File levelFile = new File(PARENT_PATH + levelName + EXTENSION);
        if (!levelFile.exists() && levelFile.isFile()) {
            throw new LevelIOException("No level file found by the name " + levelFile.getName());
        }

        RandomAccessFile reader = setupRandomAccessFile(levelFile, "r");

        char[][] allItems;
        try {
            // Read level sizes
            String sizeLine = reader.readLine();
            String[] sizes = sizeLine.split(" ");
            int rows = Integer.parseInt(sizes[0]);
            int cols = Integer.parseInt(sizes[1]);

            allItems = new char[rows][cols];
            for (int i = 0; i < allItems.length; i++) {
                String line = reader.readLine();
                for (int j = 0; j < allItems[0].length; j++) {
                    allItems[i][j] = line.charAt(j);
                }
            }

        } catch (IOException ioe) {
            throw new LevelIOException("Exception was thrown while reading the level!\n" + ioe.getMessage());
        }

        try {
            reader.close();
        } catch (IOException ioe) {
            System.err.println("Cannot close reader!\n" + ioe.getMessage());
        }

        LevelState levelState = new LevelState(allItems);
        System.out.println(levelState.toString());
        return levelState;
//        return new LevelState(allItems);
    }


    private RandomAccessFile setupRandomAccessFile(File levelFile, String mode) throws LevelIOException {
        try {
            return new RandomAccessFile(levelFile, mode);
        } catch (FileNotFoundException e) {
            throw new LevelIOException("Cannot setup RandomAccessFile with mode: " + mode);
        }
    }

    private File createAndValidateFile(String fileName) throws LevelIOException {
        File levelFile = new File(PARENT_PATH + fileName + EXTENSION);
        if (!levelFile.exists()) {
            try {
                if (!levelFile.createNewFile())
                    throw new LevelIOException("Failed attempt to create file " + levelFile.getName());
            } catch (IOException e) {
                throw new LevelIOException("Exception while creating file " + levelFile.getName()
                        + "\n" + e.getMessage());
            }
        }

        return levelFile;
    }

    @Override
    public void saveLevel(LevelState levelState, String fileName) throws LevelIOException {
        if (!levelState.isSavable())
            throw new LevelIOException("The given level is not savable in it's current state!");

        File levelFile = createAndValidateFile(fileName);

        RandomAccessFile writer = setupRandomAccessFile(levelFile, "rw");

        // Write level sizes to file
        char[][] allItems = levelState.getAllItems();
        try {
            String sizes = allItems.length + " " + allItems[0].length;
            for (int i = 0; i < sizes.length(); i++) {
                writer.write(sizes.charAt(i));
            }
            writer.write('\n');

        // Write level state to file
            for (int x = 0; x < allItems.length; x++) {
                for (int y = 0; y < allItems[0].length; y++)
                    writer.write(allItems[x][y]);
                writer.write('\n');
            }//for
        } catch (IOException ioe) {
            throw new LevelIOException("Exception was thrown while writing to file!"
                    + "\n" + ioe.getMessage());
        }

        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("Cannot close writer!");
        }
    }//saveLevel

}//class
