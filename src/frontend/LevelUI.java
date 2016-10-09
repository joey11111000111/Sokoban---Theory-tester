package frontend;

import backend.Cell;
import backend.Core;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import util.Coord;

import java.util.function.BiConsumer;

import static backend.Cell.Type.*;
/**
 * Created by joey on 2016.10.06..
 */
public class LevelUI {

    private final int SIZE_X;
    private final int SIZE_Y;

    public static int CELL_WIDTH;
    public static int CELL_HEIGHT;

    private BiConsumer<Coord, Cell.Type> leftClickAction;

    public static final int OFFSET = 20;
    public static int WIDTH;
    public static int HEIGHT;

    private Group root;
    private Group items;
    private Core core;

    public LevelUI(int cellWidth, int cellHeight, Core core) {
        this.core = core;
        SIZE_X = core.getCells().length;
        SIZE_Y = core.getCells()[0].length;
        CELL_WIDTH = cellWidth;
        CELL_HEIGHT = cellHeight;
        root = new Group();
        items = new Group();
        createBackground();
        createBaseLines();
        root.getChildren().add(items);

        WIDTH = CELL_WIDTH * SIZE_X + OFFSET;
        HEIGHT = CELL_HEIGHT * SIZE_Y + OFFSET;
    }

    private void createBackground() {
        Rectangle background = new Rectangle(CELL_WIDTH * SIZE_X + OFFSET, CELL_HEIGHT * SIZE_Y + OFFSET);
        background.setStroke(Color.RED);
        background.setFill(Color.BLACK);
        root.getChildren().add(background);
    }

    private void createBaseLines() {
        int lineWidth = CELL_WIDTH * SIZE_X;
        int lineHeight = CELL_HEIGHT * SIZE_Y;

        for (int cellIdx = 0; cellIdx < SIZE_Y + 1; cellIdx++) {
            Line line = new Line();
            line.setStroke(Color.WHEAT);
            line.setStartX(OFFSET / 2);
            line.setEndX(lineWidth + OFFSET / 2);
            int lineY = cellIdx * CELL_HEIGHT + OFFSET / 2;
            line.setStartY(lineY);
            line.setEndY(lineY);

            root.getChildren().add(line);
        }//for

        for (int cellIdx = 0; cellIdx < SIZE_X + 1; cellIdx++) {
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


    public Group getRoot() {
        return root;
    }

    public void setLeftClickOnCell(BiConsumer<Coord, Cell.Type> action) {
        leftClickAction = action;
    }

    private Shape createItem(Cell.Type type, Integer fieldValue) {
        if (type == FIELD) {
            Text fieldText = new Text(fieldValue.toString());
            fieldText.setStroke(Color.rgb(150, 200, 230));
            fieldText.setOpacity(0.9);
            int numOfDigits = fieldValue.toString().length();
            fieldText.setTranslateX(CELL_WIDTH / 2 - numOfDigits * 4);
            fieldText.setTranslateY(CELL_HEIGHT / 2 + 4);
            return fieldText;
        }
        Rectangle item = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
        item.setArcWidth(20);
        item.setArcHeight(20);

        if (type == WALL)
//            item.setFill(Color.CHOCOLATE);
            item.setFill(Color.rgb(50,50,30));
        else if (type == BOX_SPACE)
            item.setFill(Color.SILVER);
        else if (type == BOX)
            item.setFill(Color.rgb(150,100,50));
        else if (type == MARKED_BOX)
            item.setFill(Color.YELLOWGREEN);
        else if (type == PLAYER)
            item.setFill(Color.TEAL);

        return item;
    }

    public void drawItems() {
        items.getChildren().clear();
        Cell[][] cells = core.getCells();
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                if (cells[x][y].getType() == EMPTY)
                    continue;

                double coordX = x * CELL_WIDTH;
                double coordY = y * CELL_HEIGHT;

                Shape wall = createItem(cells[x][y].getType(), cells[x][y].getFieldValue());
                wall.setLayoutX(coordX + OFFSET / 2);
                wall.setLayoutY(coordY + OFFSET / 2);
                items.getChildren().add(wall);
            }//for
        }
    }

}//class
