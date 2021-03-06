package gui;

import logic.Cell;
import logic.Core;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import logic.items.Field;
import util.UnmodGridCoord;

import java.util.HashMap;
import java.util.Map;

import static logic.Cell.Type.EMPTY;
import static logic.Cell.Type.FIELD;

class LevelUI {

    private final int ROW_LENGTH;
    private final int COL_LENGTH;
    static int CELL_WIDTH;
    static int CELL_HEIGHT;

    static final int OFFSET = 20;
    private static int WIDTH;
    private static int HEIGHT;

    private Group root;
    private Group items;
    private Map<UnmodGridCoord, Shape> drawnItems;
    private Core core;

    LevelUI(int cellWidth, int cellHeight, Core core) {
        this.core = core;
        ROW_LENGTH = core.getCellContent().length;
        COL_LENGTH = core.getCellContent()[0].length;
        CELL_WIDTH = cellWidth;
        CELL_HEIGHT = cellHeight;
        root = new Group();
        items = new Group();
        drawnItems = new HashMap<>();
        createBackground();
        root.getChildren().add(items);

        WIDTH = CELL_WIDTH * ROW_LENGTH + OFFSET;
        HEIGHT = CELL_HEIGHT * COL_LENGTH + OFFSET;

        core.setMergedContentChangeAction(this::drawItemAt);

        redrawAllItems();
    }

    private void createBackground() {
        Rectangle background = new Rectangle(CELL_WIDTH * ROW_LENGTH + OFFSET, CELL_HEIGHT * COL_LENGTH + OFFSET);
        background.setStroke(Color.ORANGE);
        background.setFill(Color.BLACK);
        root.getChildren().add(background);
        createBaseLines();
    }

    private void createBaseLines() {
        int lineWidth = CELL_WIDTH * ROW_LENGTH;
        int lineHeight = CELL_HEIGHT * COL_LENGTH;

        for (int cellIdx = 0; cellIdx < COL_LENGTH + 1; cellIdx++) {
            Line line = new Line();
            line.setStroke(Color.WHEAT);
            line.setStartX(OFFSET / 2);
            line.setEndX(lineWidth + OFFSET / 2);
            int lineY = cellIdx * CELL_HEIGHT + OFFSET / 2;
            line.setStartY(lineY);
            line.setEndY(lineY);

            root.getChildren().add(line);
        }//for

        for (int cellIdx = 0; cellIdx < ROW_LENGTH + 1; cellIdx++) {
            Line line = new Line();
            line.setStroke(Color.WHEAT);
            line.setStartY(OFFSET / 2);
            line.setEndY(lineHeight + OFFSET / 2);
            int lineX = cellIdx * CELL_WIDTH + OFFSET / 2;
            line.setStartX(lineX);
            line.setEndX(lineX);

            root.getChildren().add(line);
        }//for
    }//method


    Group getRoot() {
        return root;
    }

    public int getFullWidth() {
        return WIDTH;
    }
    public int getFullHeight() {
        return HEIGHT;
    }

    private Shape createItem(Cell.Type type, Integer fieldValue) {
        if (type == FIELD) {
            Text fieldText = new Text(fieldValue.toString());
            if (core.getChosenFieldType() == Field.FieldTypes.PLAYER_FIELD)
                fieldText.setStroke(Color.rgb(150, 200, 230));
            else
                fieldText.setStroke(Color.rgb(200, 150, 50));
            fieldText.setOpacity(0.9);
            int numOfDigits = fieldValue.toString().length();
            fieldText.setTranslateX(CELL_WIDTH / 2 - numOfDigits * 4);
            fieldText.setTranslateY(CELL_HEIGHT / 2 + 4);
            return fieldText;
        }
        Rectangle item = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
        item.setArcWidth(20);
        item.setArcHeight(20);

        switch (type) {
            case WALL: item.setFill(Color.rgb(50, 50, 30)); break;
            case BSPACE: item.setFill(Color.SILVER); break;
            case BOX: item.setFill(Color.rgb(150, 100, 50)); break;
            case MARKED_BOX: item.setFill(Color.YELLOWGREEN); break;
            case PLAYER: item.setFill(Color.TEAL); break;
            case PLAYER_ON_BSPACE: item.setFill(Color.rgb(160, 160, 255)); break;
            case BOX_ON_BSPACE: item.setFill(Color.rgb(190, 170, 70)); break;
            case MARKED_BOX_ON_BSPACE: item.setFill(Color.GOLD); break;
            default: item.setFill(Color.RED);
        }

        return item;
    }

    private void redrawAllItems() {
        items.getChildren().clear();
        drawnItems.clear();
        Cell[][] cells = core.getCellContent();
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                if (cells[x][y].getType() == EMPTY)
                    continue;

                double coordX = x * CELL_WIDTH;
                double coordY = y * CELL_HEIGHT;

                Shape item = createItem(cells[x][y].getType(), cells[x][y].getFieldValue());
                item.setLayoutX(coordX + OFFSET / 2);
                item.setLayoutY(coordY + OFFSET / 2);
                items.getChildren().add(item);
                drawnItems.put(new UnmodGridCoord(x, y), item);
            }//for
        }
    }

    void drawItemAt(UnmodGridCoord coord) {
        if (coord == null)
            return;
        items.getChildren().remove(drawnItems.get(coord));
        drawnItems.remove(coord);
        int x = coord.getW();
        int y = coord.getH();
        Cell[][] cells = core.getCellContent();
        if (cells[x][y].getType() == EMPTY)
            return;

        double coordX = x * CELL_WIDTH;
        double coordY = y * CELL_HEIGHT;

        Shape item = createItem(cells[x][y].getType(), cells[x][y].getFieldValue());
        item.setLayoutX(coordX + OFFSET / 2);
        item.setLayoutY(coordY + OFFSET / 2);
        items.getChildren().add(item);
        drawnItems.put(coord, item);
    }

}//class
