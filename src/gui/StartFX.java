package gui;

import io.LevelIOException;
import logic.Cell;
import logic.Core;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.Coord;

import static gui.LevelUI.*;

public class StartFX extends Application {

    public static LevelUI levelUI;
    public static void makeDraw() {
        levelUI.drawItems();
    }
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
        primaryStage.setTitle("Sokoban Theory ~ Tester");
        primaryStage.setResizable(false);

        setupLevelUI();
        ScrollPane scrollPane = new ScrollPane(levelUI.getRoot());
        scrollPane.getStylesheets().add(StartFX.class.getClassLoader().getResource("gui/styling.css").toExternalForm());
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setScreenSize(1200, 650, scrollPane);

        Scene scene = new Scene(scrollPane);
        setupMouseClickEvent(scrollPane);
        setupKeyPressEvent(scrollPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }//start

    private void setScreenSize(int width, int height, ScrollPane scrollPane) {
        int levelWidth = levelUI.getFullWidth();
        int levelHeight = levelUI.getFullHeight();
        width = (levelWidth > width) ? width : levelWidth + 3;
        height = (levelHeight > height) ? height : levelHeight + 3;
        scrollPane.setMinWidth(width);
        scrollPane.setMaxWidth(width);
        scrollPane.setMinHeight(height);
        scrollPane.setMaxHeight(height);
    }

    private void setupMouseClickEvent(ScrollPane scrollPane) {
        scrollPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            // Calculate grid-coordinates of the clicked cell
            double unseenWidth = levelUI.getFullWidth() - scrollPane.getWidth();
            double unseedHeight = levelUI.getFullHeight() - scrollPane.getHeight();
            double cellX = (event.getSceneX() - OFFSET / 2
                            + unseenWidth * scrollPane.getHvalue()) / CELL_WIDTH;
            double cellY = (event.getSceneY() - OFFSET / 2
                            + unseedHeight * scrollPane.getVvalue()) / CELL_HEIGHT;
            Coord coord = new Coord((int)cellX, (int)cellY);

            switch (event.getButton()) {
                case PRIMARY:   core.put(coord.getX(), coord.getY(), itemType);
                                break;
                case SECONDARY: try {core.calcFieldOf(coord.getX(), coord.getY());}
                                catch (IllegalArgumentException iae) {
                                    System.out.println(iae.getMessage());
                                }
                                break;
                default:    System.out.println("No middle-button click function is available yet.");
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
                case "S": try {
                    core.save();
                } catch (LevelIOException lioe) {
                    System.out.println(lioe.getMessage());
                }
                break;
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
