package gui;

import io.LevelIOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.Cell;
import logic.Core;
import util.Coord;
import util.Directions;

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
        levelUI = new LevelUI(60, 60, core);
    }

    private void setupSceneAndStage() {
        rootContainer = new ScrollPane(levelUI.getRoot());
        rootContainer.getStylesheets().add(cssString);
        rootContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rootContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setScreenSize(1200, 650, rootContainer);
        setupMouseClickEvent(rootContainer);
        setupKeyPressEvent(rootContainer);

        scene = new Scene(rootContainer);
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
            // Calculate grid-coordinates of the clicked cell
            double unseenWidth = levelUI.getFullWidth() - scrollPane.getWidth();
            double unseedHeight = levelUI.getFullHeight() - scrollPane.getHeight();
            double cellX = (event.getSceneX() - OFFSET / 2
                            + unseenWidth * scrollPane.getHvalue()) / CELL_WIDTH;
            double cellY = (event.getSceneY() - OFFSET / 2
                            + unseedHeight * scrollPane.getVvalue()) / CELL_HEIGHT;
            Coord coord = new Coord((int)cellX, (int)cellY);

            switch (event.getButton()) {
                case PRIMARY:   core.put(coord.getW(), coord.getH(), itemType);
                                break;
                case SECONDARY: try {core.calcFieldOf(coord.getW(), coord.getH());}
                                catch (IllegalArgumentException iae) {
                                    System.out.println(iae.getMessage());
                                }
                                break;
                default:    System.out.println("No middle-button click function is available yet.");
            }

            levelUI.drawItems();
        });
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
    }

    private void setupKeyPressEvent(ScrollPane scrollPane) {
        scrollPane.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            String key = event.getCharacter();
            switch (key) {
                case "q": core.movePlayer(Directions.LEFT);
                    levelUI.drawItems();
                    break;
                case "w": itemType = Cell.Type.WALL; break;
                case "s": itemType = Cell.Type.BSPACE; break;
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
    }


}
