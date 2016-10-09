package frontend;

import backend.Cell;
import backend.Core;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.Coord;

import static frontend.LevelUI.*;

/**
 * Created by joey on 2016.10.06..
 */
public class StartFX extends Application {

    private LevelUI levelUI;
    private static Core core;

    private Cell.Type itemType = Cell.Type.WALL;


    public static void setCore(Core core) {
        StartFX.core = core;
    }

    private void setupLevelUI() {
        levelUI = new LevelUI(60, 60, core);
    }

    public static void start() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Sokoban Theory-Tester");
        primaryStage.setResizable(false);

        setupLevelUI();
        ScrollPane root = new ScrollPane(levelUI.getRoot());

        root.getStylesheets().add(StartFX.class.getClassLoader().getResource("frontend/styling.css").toExternalForm());
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        root.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        if (LevelUI.WIDTH > 1200) {
            root.setMinWidth(1200);
            root.setMaxWidth(1200);
        } else {
            root.setMinWidth(LevelUI.WIDTH + 3);
            root.setMaxWidth(LevelUI.WIDTH + 3);
        }
        if (LevelUI.HEIGHT > 650) {
            root.setMinHeight(650);
            root.setMaxHeight(650);
        } else {
            root.setMinHeight(LevelUI.HEIGHT + 3);
            root.setMaxHeight(LevelUI.HEIGHT + 3);
        }

        Scene scene = new Scene(root);
        setupMouseClickEvent(root);
        setupKeyPressEvent(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupMouseClickEvent(ScrollPane scrollPane) {
        scrollPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            double unseenWidth = WIDTH - scrollPane.getWidth();
            double unseedHeight = HEIGHT - scrollPane.getHeight();
            double cellX = (event.getSceneX() - OFFSET / 2
                    + unseenWidth * scrollPane.getHvalue()) / CELL_WIDTH;
            double cellY = (event.getSceneY() - OFFSET / 2
                    + unseedHeight * scrollPane.getVvalue()) / CELL_HEIGHT;
            Coord coord = new Coord((int)cellX, (int)cellY);

            if (event.getButton() == MouseButton.PRIMARY) {
                core.put(coord.getX(), coord.getY(), itemType);
            }
            else if (event.getButton() == MouseButton.SECONDARY) {
                try {
                    core.calcFieldOf(coord.getX(), coord.getY());
                } catch (IllegalArgumentException iae) {}
            }
            else {
                core.remove(coord.getX(), coord.getY());
            }

            levelUI.drawItems();
        });
    }

    private void setupKeyPressEvent(ScrollPane scrollPane) {
        scrollPane.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            String key = event.getCharacter();
            switch (key) {
                case "w": itemType = Cell.Type.WALL; break;
                case "s": itemType = Cell.Type.BOX_SPACE; break;
                case "b": itemType = Cell.Type.BOX; break;
                case "m": itemType = Cell.Type.MARKED_BOX; break;
                case "p": itemType = Cell.Type.PLAYER; break;
                case "e": itemType = Cell.Type.EMPTY; break;
                case "c": core.clear();
                    levelUI.drawItems();
                    break;
                case "C": core.removeAllFields();
                    levelUI.drawItems();
                    break;
            }
        });
    }


}
