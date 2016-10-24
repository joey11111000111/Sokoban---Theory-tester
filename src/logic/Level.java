package logic;


import util.Coord;
import util.Directions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Level {

    public static String DEFAULT_LEVEL_NAME = "new level.slvl";
    private String levelName;
    private final int ROW_LENGTH;
    private final int COL_LENGTH ;
    private Cell[][] cells;
    private Coord playerCoord;

    private boolean isSurroundingWall(int x, int y) {
        return x == 0 || y == 0 || x == ROW_LENGTH -1 || y == COL_LENGTH - 1;
    }

    Level() {
        this(14, 9);
    }

    Level(int rowLength, int colLength) {
        if (rowLength < 4 || colLength < 4)
            throw new IllegalArgumentException("The size of the level must be at least 4x4");

        playerCoord = null;
        levelName = DEFAULT_LEVEL_NAME;
        ROW_LENGTH = rowLength;
        COL_LENGTH = colLength;
        cells = new Cell[ROW_LENGTH][COL_LENGTH];
        for (int x = 0; x < ROW_LENGTH; x++) {
            for (int y = 0; y < COL_LENGTH; y++) {
                cells[x][y] = new Cell();
                if (isSurroundingWall(x, y))
                    cells[x][y].setType(Cell.Type.WALL);
            }
        }
    }

    Level(Cell[][] cells, String levelName) {
        this.levelName = levelName;
        ROW_LENGTH = cells.length;
        if (ROW_LENGTH < 4)
            throw new IllegalArgumentException("Given level is not wide enough!");
        COL_LENGTH = cells[0].length;
        if (COL_LENGTH < 4)
            throw new IllegalArgumentException("Given level is not tall enough!");
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                if (cells[i][j].getType() == Cell.Type.PLAYER) {
                    if (playerCoord == null)
                        playerCoord = new Coord(i, j);
                    else
                        throw new IllegalArgumentException("A level can only have one player!");
                }
            }
        }
        this.cells = cells;
    }

    boolean isValidCoord(int x, int y) {
        return !(x < 0 || x >= ROW_LENGTH || y < 0 || y >= COL_LENGTH);
    }
    boolean isValidCoord(Coord coord) {
        return isValidCoord(coord.getX(), coord.getY());
    }

    void validateIndexes(int x, int y) {
        if (x < 0 || x >= ROW_LENGTH || y < 0 || y > COL_LENGTH)
            throw new IndexOutOfBoundsException("(X, Y) = (" + x + ", " + y + ')');
    }

    public String getLevelName() {
        return levelName;
    }
    public void setLevelName(String levelName) {
        if (levelName == null)
            throw new IllegalArgumentException("Level name must not be null!");
        if (levelName.length() == 0)
            throw new IllegalArgumentException("Level name must not be empty string!");

        this.levelName = levelName;
    }

    Cell[][] getCells() {
        return cells;
    }

    void put(int x, int y, Cell.Type type) {
        validateIndexes(x, y);
        if (isSurroundingWall(x, y))
            return;
        if (type == Cell.Type.FIELD)
            throw new IllegalArgumentException("Field must be set along with it's value, using another method!");

        if ((new Coord(x, y)).equals(playerCoord))
            playerCoord = null;

        if (type == Cell.Type.PLAYER) {
            if (playerCoord == null)
                playerCoord = new Coord(x, y);
            else
                return;
        }



        cells[x][y].setType(type);
    }
    void put(Coord c, Cell.Type type) {
        put(c.getX(), c.getY(), type);
    }

    void putField(int x, int y, int fieldValue) {
        validateIndexes(x, y);
        if (isSurroundingWall(x, y))
            return;
        cells[x][y].setType(Cell.Type.FIELD);
        cells[x][y].setFieldValue(fieldValue);
    }
    void putField(Coord c, int fieldValue) {
        putField(c.getX(), c.getY(), fieldValue);
    }

    Cell.Type getTypeOf(int x, int y) {
        validateIndexes(x, y);
        return cells[x][y].getType();
    }
    Cell.Type getTypeOf(Coord c) {
        return getTypeOf(c.getX(), c.getY());
    }

    Integer getFieldValueOf(int x, int y) {
        validateIndexes(x, y);
        if (cells[x][y].getType() != Cell.Type.FIELD)
            throw new IllegalArgumentException("Cell at given index (" + x + ", " + y+ ") is not a field!");

        return cells[x][y].getFieldValue();
    }

    void remove(int x, int y) {
        validateIndexes(x, y);
        if (isSurroundingWall(x, y))
            return;
        if (getTypeOf(x, y) == Cell.Type.PLAYER)
            playerCoord = null;
        cells[x][y].setType(Cell.Type.EMPTY);
    }

    void clear() {
        playerCoord = null;
        for (int x = 0; x < ROW_LENGTH; x++)
            for (int y = 0; y < COL_LENGTH; y++)
                if (!isSurroundingWall(x, y) && cells[x][y].getType() != Cell.Type.EMPTY)
                    cells[x][y].setType(Cell.Type.EMPTY);
    }

    List<Coord> getWalls() {
        List<Coord> walls = new ArrayList<>();
        for (int x = 0; x < ROW_LENGTH; x++) {
            for (int y = 0; y < COL_LENGTH; y++) {
                if (cells[x][y].getType() == Cell.Type.WALL)
                    walls.add(new Coord(x, y));
            }
        }

        return walls;
    }

    void removeAllFields() {
        Arrays.stream(cells)
                .flatMap(Arrays::stream)
                .forEach(c -> {
                    if (c.getType() == Cell.Type.FIELD)
                        c.setType(Cell.Type.EMPTY);
                    else if (c.getType() == Cell.Type.MARKED_BOX)
                        c.setType(Cell.Type.BOX);
                });
    }

    private Coord getNewCoords(Coord coord, Directions dir) {
        int modX = 0, modY = 0;
        switch (dir) {
            case LEFT: modX = -1; break;
            case UP: modY = -1; break;
            case RIGHT: modX = 1; break;
            case DOWN: modY = 1; break;
        }

        int newX = coord.getX() + modX;
        int newY = coord.getY() + modY;
        return new Coord(newX, newY);
    }

    private boolean moveToFrom(Coord to, Coord from) {
        Cell fromCell = cells[from.getX()][from.getY()];
        Cell toCell = cells[to.getX()][to.getY()];
        Cell.Type fromType = fromCell.getType();
        Cell.Type toType = toCell.getType();
        System.out.println("fromType = " + fromType);
        System.out.println("toType = " + toType);

        switch (toType) {
            case WALL:
            case BOX:
            case MARKED_BOX:
            case BOX_ON_BSPACE:
            case MARKED_BOX_ON_BSPACE:
            case PLAYER:
            case PLAYER_ON_BSPACE:
                return false;
            case EMPTY:
            case FIELD:
                switch (fromType) {
                    case PLAYER:
                    case PLAYER_ON_BSPACE:
                        toCell.setType(Cell.Type.PLAYER);
                        break;
                    case BOX:
                    case MARKED_BOX:
                    case MARKED_BOX_ON_BSPACE:
                        toCell.setType(Cell.Type.BOX);
                        break;
                    default:
                        return false;
                }
                break;
            case BSPACE:
                switch (fromType) {
                    case PLAYER:
                    case PLAYER_ON_BSPACE:
                        toCell.setType(Cell.Type.PLAYER_ON_BSPACE);
                        break;
                    case BOX:
                    case MARKED_BOX:
                    case MARKED_BOX_ON_BSPACE:
                        toCell.setType(Cell.Type.BOX_ON_BSPACE);
                        break;
                    default: return false;
                }
                break;
            default: return false;
        }//switch

        switch (fromType) {
            case PLAYER:
                playerCoord = to;
            case BOX:
            case MARKED_BOX:
                fromCell.setType(Cell.Type.EMPTY);
                break;
            case PLAYER_ON_BSPACE:
                playerCoord = to;
            case BOX_ON_BSPACE:
            case MARKED_BOX_ON_BSPACE:
                fromCell.setType(Cell.Type.BSPACE);
        }

        return true;
    }

    public void movePlayer(Directions dir) {
        if (playerCoord == null)
            return;

        Coord newPlayerCoord = getNewCoords(playerCoord, dir);
        if (getTypeOf(newPlayerCoord) == Cell.Type.BOX ||
                getTypeOf(newPlayerCoord) == Cell.Type.MARKED_BOX ||
                getTypeOf(newPlayerCoord) == Cell.Type.BOX_ON_BSPACE ||
                getTypeOf(newPlayerCoord) == Cell.Type.MARKED_BOX_ON_BSPACE) {
            Coord newBoxCoord = getNewCoords(newPlayerCoord, dir);
            boolean movedBox = moveToFrom(newBoxCoord, newPlayerCoord);
            System.out.println("----------------------------------");
            if (!movedBox)
                return;
        }

        moveToFrom(newPlayerCoord, playerCoord);
/*
        Coord newCoord = getNewCoords(playerCoord, dir);
        // If the new coord is empty or field, make the move
        Cell.Type type = getTypeOf(newCoord.getX(), newCoord.getY());
        if (type == Cell.Type.FIELD || type == Cell.Type.EMPTY) {
            cells[playerCoord.getX()][playerCoord.getY()].setType(Cell.Type.EMPTY);
            cells[newCoord.getX()][newCoord.getY()].setType(Cell.Type.PLAYER);
            playerCoord = newCoord;
        }

        if (type == Cell.Type.BOX || type == Cell.Type.MARKED_BOX) {
            Coord boxCoord = getNewCoords(newCoord, dir);
            Cell.Type nextType = getTypeOf(boxCoord.getX(), boxCoord.getY());
            if (nextType == Cell.Type.FIELD || nextType == Cell.Type.EMPTY) {
                // move player
                cells[playerCoord.getX()][playerCoord.getY()].setType(Cell.Type.EMPTY);
                cells[newCoord.getX()][newCoord.getY()].setType(Cell.Type.PLAYER);
                playerCoord = newCoord;
                // move box
                cells[boxCoord.getX()][boxCoord.getY()].setType(Cell.Type.BOX);
            }
            if (nextType == Cell.Type.BSPACE) {
                // move player
                cells[playerCoord.getX()][playerCoord.getY()].setType(Cell.Type.EMPTY);
                cells[newCoord.getX()][newCoord.getY()].setType(Cell.Type.PLAYER);
                playerCoord = newCoord;
                // move box
                cells[boxCoord.getX()][boxCoord.getY()].setType(Cell.Type.BOX_ON_BSPACE);
            }

        }//if

        if (type == Cell.Type.BOX_ON_BSPACE) {

        }
*/

    }
}
