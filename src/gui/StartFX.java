package gui;

import io.LevelIOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.Cell;
import logic.Core;
import util.Directions;
import util.UnmodGridCoord;

import java.io.File;

import static gui.LevelUI.*;

public class StartFX extends Application {

    private static Core core;

    private LevelUI levelUI;
    private String cssString;
    // In case of loading a level, these three will get new values, so they must be accessible from the methods
    private ScrollPane rootContainer;
    private Scene scene;
    private Stage stage;
    private boolean dragging = false;

    private Cell.Type itemType = Cell.Type.WALL;

    public static void setCore(Core core) {
        StartFX.core = core;
    }

    public static void start() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        cssString = StartFX.class.getClassLoader().getResource("gui/styling.css").toExternalForm();
        stage = primaryStage;
        stage.setTitle("Sokoban Theory ~ Tester");
        stage.setResizable(false);

        createNewLevelUIInstance();
        setupSceneAndStage();
        stage.show();
    }//start

    private void createNewLevelUIInstance() {
        levelUI = new LevelUI(55, 55, core);
    }

    private void setupSceneAndStage() {
        rootContainer = new ScrollPane(levelUI.getRoot());
        rootContainer.getStylesheets().add(cssString);
        rootContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rootContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setScreenSize(1300, 650, rootContainer);
        setupMouseClickEvent(rootContainer);
        setupKeyPressEvent(rootContainer);

        scene = new Scene(rootContainer);
        rootContainer.requestFocus();
        stage.setScene(scene);
    }

    private void setScreenSize(int width, int height, ScrollPane scrollPane) {
        int levelWidth = levelUI.getFullWidth();
        int levelHeight = levelUI.getFullHeight();
        width = (levelWidth > width) ? width : levelWidth + 3;
        height = (levelHeight > height) ? height : levelHeight + 3;
        scrollPane.setMaxWidth(width);
        scrollPane.setMaxHeight(height);
        scrollPane.setMinWidth(width);
        scrollPane.setMinHeight(height);
    }

    private void setupMouseClickEvent(ScrollPane scrollPane) {
        scrollPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            UnmodGridCoord coord = getScreenCoordOfMouse(scrollPane, event);
            switch (event.getButton()) {
                case PRIMARY:   core.putItem(itemType, coord.getW(), coord.getH());
                                break;
                case SECONDARY:
                    try {
                        core.calcFieldOf(coord.getW(), coord.getH());
                    }
                    catch (IllegalArgumentException iae) {
                        System.out.println(iae.getMessage());
                    }
                    break;
                default:    System.out.println("No middle-button click function is available yet.");
            }

            levelUI.drawItems();
        });

        scrollPane.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (!dragging)
                return;
            UnmodGridCoord coord = getScreenCoordOfMouse(scrollPane, event);
            core.putItem(itemType, coord.getW(), coord.getH());
            levelUI.drawItems();
        });
    }

    private UnmodGridCoord getScreenCoordOfMouse(ScrollPane scrollPane, MouseEvent event) {
        double unseenWidth = levelUI.getFullWidth() - scrollPane.getWidth();
        double unseedHeight = levelUI.getFullHeight() - scrollPane.getHeight();
        double cellX = (event.getSceneX() - OFFSET / 2
                + unseenWidth * scrollPane.getHvalue()) / CELL_WIDTH;
        double cellY = (event.getSceneY() - OFFSET / 2
                + unseedHeight * scrollPane.getVvalue()) / CELL_HEIGHT;
        return new UnmodGridCoord((int)cellX, (int)cellY);
    }

    private FileChooser setupFileChooser(String path, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        File parentDir = new File(path);
        fileChooser.setInitialDirectory(parentDir);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sokoban level", "*.slvl"));
        return fileChooser;
    }

    private void saveLevel(String initialName) throws LevelIOException {
        FileChooser fileChooser = setupFileChooser(core.getSaveDirectoryPath(), "Save level as...");
        fileChooser.setInitialFileName(initialName);
        File newFile = fileChooser.showSaveDialog(stage);
        if (newFile == null)
            return;

        core.save(newFile);

    }

    private void loadAndShowLevel() throws LevelIOException {
        FileChooser fileChooser = setupFileChooser(core.getSaveDirectoryPath(), "Open level...");
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile == null)
            return;

        core.loadLevel(selectedFile);
        createNewLevelUIInstance();
        rootContainer.setContent(levelUI.getRoot());
        setupSceneAndStage();
        levelUI.drawItems();
        rootContainer.requestFocus();
    }

    private int layerCount = 1;
    private void setupKeyPressEvent(ScrollPane scrollPane) {
        scrollPane.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            String key = event.getCharacter();
            switch (key) {
                case "d": dragging = !dragging; break;
                // item characters
                case "w": itemType = Cell.Type.WALL; break;
                case "s": itemType = Cell.Type.BSPACE; break;
                case "b": itemType = Cell.Type.BOX; break;
                case "m": itemType = Cell.Type.MARKED_BOX; break;
                case "p": itemType = Cell.Type.PLAYER; break;
                case "e": itemType = Cell.Type.EMPTY; break;
                // cleaning
                case "c": core.setToDefaultState();
                    levelUI.drawItems();
                    break;
                case "C": core.removeAllFields();
                    levelUI.drawItems();
                    break;
                // modify level sizes
                case "5": layerCount *= -1; break;
                case "4":
                    core.addOrRemoveCellLayersOnSide(layerCount, Directions.LEFT);
                    createNewLevelUIInstance();
                    setupSceneAndStage();
                    break;
                case "8":
                    core.addOrRemoveCellLayersOnSide(layerCount, Directions.UP);
                    createNewLevelUIInstance();
                    setupSceneAndStage();
                    break;
                case "6":
                    core.addOrRemoveCellLayersOnSide(layerCount, Directions.RIGHT);
                    createNewLevelUIInstance();
                    setupSceneAndStage();
                    break;
                case "2":
                    core.addOrRemoveCellLayersOnSide(layerCount, Directions.DOWN);
                    createNewLevelUIInstance();
                    setupSceneAndStage();
                    break;
                // save and load
                case "S":
                    try {
                        saveLevel(core.getLevelName());
                    } catch (LevelIOException lioe) {
                        System.out.println(lioe.getMessage());
                    }
                    break;
                case "L":
                    try {
                        loadAndShowLevel();
                    } catch (LevelIOException lioe) {
                        System.err.println("fail\n" + lioe.getMessage());
                    }
                    break;
            }
        });

        scrollPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            KeyCode keyCode = event.getCode();
            switch (keyCode) {
                case LEFT: core.movePlayer(Directions.LEFT);
                    levelUI.drawItems();
                    break;
                case UP: core.movePlayer(Directions.UP);
                    levelUI.drawItems();
                    break;
                case RIGHT: core.movePlayer(Directions.RIGHT);
                    levelUI.drawItems();
                    break;
                case DOWN: core.movePlayer(Directions.DOWN);
                    levelUI.drawItems();
                    break;
            }
            event.consume();
        });
    }


}
