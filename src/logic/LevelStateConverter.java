package logic;

import io.LevelIO;
import io.LevelState;

public class LevelStateConverter {

    public LevelState convertToLevelState(Cell[][] cells) {
        if (cells == null)
            throw new IllegalArgumentException("Given cells must not be null!");
        if (cells.length < LevelState.MIN_ROW_LENGTH || cells[0].length < LevelState.MIN_COL_LENGTH)
            throw new IllegalArgumentException("Given level is too small!");

        int rowLength = cells.length;
        int colLength = cells[0].length;

        char[][] allItems = new char[rowLength + 2][colLength + 2];     // +2 for the surrounding walls
        // Add surrounding walls
        for (int i = 0; i < allItems.length; i++) {
            allItems[i][0] = LevelIO.WALL;
            allItems[i][allItems[0].length - 1] = LevelIO.WALL;
        }
        for (int i = 0; i < allItems[0].length; i++) {
            allItems[0][i] = LevelIO.WALL;
            allItems[allItems.length - 1][i] = LevelIO.WALL;
        }

        // Add actual level content
        for (int x = 0; x < rowLength; x++) {
            for (int y = 0; y < colLength; y++) {
                char item;
                Cell.Type type = cells[x+1][y+1].getType();
                switch (type) {
                    case WALL: item = LevelIO.WALL; break;
                    case BOX_SPACE: item = LevelIO.BSPACE; break;
                    case MARKED_BOX:
                    case BOX: item = LevelIO.BOX; break;
                    case PLAYER: item = LevelIO.PLAYER; break;
                    case FIELD:
                    case EMPTY: item = LevelIO.EMPTY; break;
                    default: throw new RuntimeException("Unhandled cell type: \"" + type.name() + "\"");
                }
                allItems[x + 1][y + 1] = item;
            }
        }

        return new LevelState(allItems);
    }//method

    public Cell[][] convertToCells(LevelState levelState) {
        char[][] allItems = levelState.getAllItems();
        int rowLength = allItems.length;
        int colLength = allItems[0].length;

        Cell[][] cells = new Cell[rowLength - 2][colLength - 2];
        for (int x = 1; x < rowLength - 2; x++) {
            for (int y = 1; y < colLength - 2; y++) {
                cells[x-1][y-1] = new Cell();
                switch (allItems[x][y]) {
                    case LevelIO.WALL: cells[x-1][y-1].setType(Cell.Type.WALL); break;
                    case LevelIO.BSPACE: cells[x-1][y-1].setType(Cell.Type.BOX_SPACE); break;
                    case LevelIO.BOX: cells[x-1][y-1].setType(Cell.Type.BOX); break;
                    case LevelIO.PLAYER: cells[x-1][y-1].setType(Cell.Type.PLAYER); break;
                    case LevelIO.EMPTY: cells[x-1][y-1].setType(Cell.Type.EMPTY); break;
                }
            }
        }

        return cells;
    }

}
