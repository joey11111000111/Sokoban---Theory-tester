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

        char[][] allItems = new char[colLength][rowLength];

        // Add level content
        for (int i = 0; i < rowLength; i++) {
            System.out.println();
            for (int j = 0; j < colLength; j++) {
                char item;
                Cell.Type type = cells[i][j].getType();
                switch (type) {
                    case WALL: item = LevelIO.WALL; break;
                    case BSPACE: item = LevelIO.BSPACE; break;
                    case MARKED_BOX:
                    case BOX: item = LevelIO.BOX; break;
                    case PLAYER: item = LevelIO.PLAYER; break;
                    case FIELD:
                    case EMPTY: item = LevelIO.EMPTY; break;
                    case PLAYER_ON_BSPACE: item = LevelIO.PLAYER_ON_BSPACE; break;
                    case BOX_ON_BSPACE: item = LevelIO.BOX_ON_BSPACE; break;
                    default: throw new RuntimeException("Unhandled cell type: \"" + type.name() + "\"");
                }
                allItems[j][i] = item;
            }
        }

        return new LevelState(allItems);
    }//method

    public Cell[][] convertToCells(LevelState levelState) {
        char[][] allItems = levelState.getAllItems();
        int rowLength = allItems.length;
        int colLength = allItems[0].length;

        Cell[][] cells = new Cell[colLength][rowLength];
        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < colLength; j++) {
                cells[j][i] = new Cell();
                switch (allItems[i][j]) {
                    case LevelIO.WALL: cells[j][i].setType(Cell.Type.WALL); break;
                    case LevelIO.BSPACE: cells[j][i].setType(Cell.Type.BSPACE); break;
                    case LevelIO.BOX: cells[j][i].setType(Cell.Type.BOX); break;
                    case LevelIO.PLAYER: cells[j][i].setType(Cell.Type.PLAYER); break;
                    case LevelIO.EMPTY: cells[j][i].setType(Cell.Type.EMPTY); break;
                    case LevelIO.PLAYER_ON_BSPACE: cells[j][i].setType(Cell.Type.PLAYER_ON_BSPACE); break;
                    case LevelIO.BOX_ON_BSPACE: cells[j][i].setType(Cell.Type.BOX_ON_BSPACE); break;
                }
            }
        }

        return cells;
    }

}
