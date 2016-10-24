package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class NoSQLLevelIO implements LevelIO {

    private final String EXTENSION = ".slvl";

    @Override
    public LevelState readLevel(File levelFile) throws LevelIOException {
        RandomAccessFile reader = setupRandomAccessFile(levelFile, "r");

        char[][] allItems;
        try {
            // Read level sizes
            long position = reader.getFilePointer();
            String line;
            int rows = 0, cols = 0;
            while ((line = reader.readLine()) != null) {
                ++rows;
                if (line.length() > cols)
                    cols = line.length();
            }
            reader.seek(position);

            allItems = new char[rows][cols];
            for (int i = 0; i < allItems.length; i++) {
                line = reader.readLine();
                for (int j = 0; j < line.length(); j++)
                    allItems[i][j] = line.charAt(j);
                for (int j = line.length(); j < allItems[0].length; j++)
                    allItems[i][j] = EMPTY;
            }

        } catch (IOException ioe) {
            throw new LevelIOException("Exception was thrown while reading the level!\n" + ioe.getMessage());
        }

        try {
            reader.close();
        } catch (IOException ioe) {
            System.err.println("Cannot close reader!\n" + ioe.getMessage());
        }

        return new LevelState(allItems);

    }

    private RandomAccessFile setupRandomAccessFile(File levelFile, String mode) throws LevelIOException {
        try {
            return new RandomAccessFile(levelFile, mode);
        } catch (FileNotFoundException e) {
            throw new LevelIOException("Cannot setup RandomAccessFile with mode: " + mode);
        }
    }

    private void createAndValidateFile(File levelFile) throws LevelIOException {
        if (!levelFile.exists()) {
            try {
                if (!levelFile.createNewFile())
                    throw new LevelIOException("Failed attempt to create file " + levelFile.getName());
            } catch (IOException e) {
                throw new LevelIOException("Exception while creating file " + levelFile.getName()
                        + "\n" + e.getMessage());
            }
        }
    }

    @Override
    public void saveLevel(LevelState levelState, File levelFile) throws LevelIOException {
        createAndValidateFile(levelFile);
        RandomAccessFile writer = setupRandomAccessFile(levelFile, "rw");

        char[][] allItems = levelState.getAllItems();
        try {
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
    }

}//class
