package view;

import controller.MinesweeperController;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import model.MinesweeperModel;
import tools.Board;
import tools.Loc;

import java.io.File;
import java.util.*;

@SuppressWarnings("deprecation")
public class GameWindow extends Application implements Observer {

    private MinesweeperController controller;
    private final int WIDTH_COUNT;
    private final int HEIGHT_COUNT;
    private final int THEME;
    private final int BOMB_COUNT;
    private Button[][] button_array;

    private String COLOR_ONE;
    private String COLOR_TWO;
    private String COLOR_THREE;

    private String colors;
    private int animation_counter = 0;
    final Image BOMB_IMAGE = new Image("Explosion.png", false);
    public GameWindow(MinesweeperController controller, String colors){
        super();
        this.colors = colors;
        if (colors.equals("0")) {
            COLOR_ONE = "777777";
            COLOR_TWO = "999999";
            COLOR_THREE = "FF0000";
        } else {
            try {
                String[] colorList = RGBtoHex(colors);
                COLOR_ONE = colorList[0];
                COLOR_TWO = colorList[1];
                COLOR_THREE = colorList[2];
            } catch (Exception e) {
                COLOR_ONE = "777777";
                COLOR_TWO = "999999";
                COLOR_THREE = "FF0000";
            }
        }
        int[] starting_vars = controller.getStartingVars();
        WIDTH_COUNT = starting_vars[1];
        HEIGHT_COUNT = starting_vars[0];
        BOMB_COUNT = starting_vars[2];
        THEME = 0;
        this.controller = controller;
        controller.addObserver(this);
    }

    private static String[] RGBtoHex(String colors) {
        String[] splitColors = colors.split("-");
        int index = 0;
        for (String val : splitColors) {
            String hexNum = "";
            String[] ind_val = val.replaceAll("\\(", "").replaceAll("\\)", "")
                    .split(",");
            for (String num : ind_val) {
                String hex_val = "";
                num = num.replaceAll(" ", "");
                int int_val = Integer.parseInt(num);
                hex_val = Integer.toHexString(int_val);
                if (hex_val.length() == 1) {
                    hex_val = "0" + hex_val;
                }
                hexNum+=hex_val;
            }
            splitColors[index] = hexNum;
            index+=1;
        }
        return splitColors;
    }

    @Override
    public void start(Stage stage) {
        // Main Menu Bar
        MenuBar menuBar = new MenuBar();
        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem saveGame = new MenuItem("Save Game");
        saveGame.setOnAction(e -> saveGameState(stage));
        MenuItem loadGame = new MenuItem("Load Game");
        loadGame.setOnAction(e -> {
            try {
                loadGameState(stage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        MenuItem exitGame = new MenuItem("Exit");
        exitGame.setOnAction(e -> stage.close());
        fileMenu.getItems().addAll(saveGame, loadGame, exitGame);
        menuBar.getMenus().add(fileMenu);
        // Game Grid
        GridPane root = create_visual_board();
        // VBox Containing Main Menu Bar and GameGrid
        //VBox vBox = new VBox(menuBar, root, game_time);
        //Scene scene = new Scene(vBox);  // Add VBox to Scene
        Label game_time = new Label();
        Long start_time = System.currentTimeMillis();
        Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            public void run() {
                Long cur_time = System.currentTimeMillis();
                Long time_elapsed = cur_time - start_time;
                time_elapsed = time_elapsed / 1000;
                String str_time_elapsed = time_elapsed.toString();
                Platform.runLater(() -> game_time.setText(str_time_elapsed + " " + "Seconds"));
            };
        };
        timer.scheduleAtFixedRate(tt, new Date(), 1000);
        //GridPane root = create_visual_board();
        /*
        VBox box = new VBox();
        box.getChildren().addAll(root, game_time);
        Scene scene = new Scene(box);
        */
        VBox vBox = new VBox(menuBar, root, game_time);
        Scene scene = new Scene(vBox);  // Add VBox to Scene
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Sends a save game state request to the controller.
     * @param stage the currently active stage in View.
     */
    private void saveGameState(Stage stage) {
        // Build FileChooser Window
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Current Game");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Minesweeper Game File",".mnswpr"));
        // Retrieve file from fileChooser.
        File saveFile = fileChooser.showSaveDialog(stage);
        if (saveFile != null) {
            // Store backend in saveFile via FileOutputStream
            controller.saveGameState(saveFile.getPath(), this.colors);
        }
    }

    /**
     * Sends a load game state request to the controller.
     * @param stage The currently active stage in View.
     */
    private void loadGameState(Stage stage) {
        // Build FileChooser Window
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Current Game");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Minesweeper Game File",".minesweeper"));
        File loadFile = fileChooser.showOpenDialog(stage);
        if (loadFile != null) {
            // Loads the saved backend state to the active stage.
            controller.loadGameState(loadFile.getPath(), stage);
        }
    }
    /**
     * Creates the visual board made of buttons that is shown on the screen
     * @return a GridPane that holds all the buttons used as tiles for the game
     */
    public GridPane create_visual_board(){
        Long start_time = System.currentTimeMillis();
        GridPane board_representation = new GridPane();
        button_array = new Button[HEIGHT_COUNT][WIDTH_COUNT];
        // Makes it so the window is at max 300 px in width and height. If it's less, the tiles will
        // always be 30x30
        int button_dim = Math.min(50, 1000/Math.max(HEIGHT_COUNT, WIDTH_COUNT));
        for (int j = 0; j < HEIGHT_COUNT; j++){
            for (int i = 0; i < WIDTH_COUNT; i++){
        /*
        int button_dim = Math.min(30, 300/Math.max(HEIGHT_COUNT, WIDTH_COUNT));
        int j = 0;
        int i = 0;
        for (j = 0; j < HEIGHT_COUNT; j++){
            for (i = 0; i < WIDTH_COUNT; i++) {
        */
                /* Button Aesthetics */
                Button temp_button = new Button();
                temp_button.setMinSize(button_dim, button_dim);
                temp_button.setMaxSize(button_dim, button_dim);
                temp_button.setStyle("-fx-background-radius: 0px;");
                button_array[j][i] = temp_button;
                board_representation.add(temp_button, i, j);

                final int fin_i = i;
                final int fin_j = j;
                /* Button Functionality TODO */
                temp_button.setOnMouseClicked((MouseEvent e) -> {
                    System.out.println(e);
                    if (e.getButton() == MouseButton.PRIMARY) {
                        controller.move(fin_j, fin_i);
                        System.out.println("Clicked " + fin_i + fin_j);
                    } else if (e.getButton() == MouseButton.SECONDARY){
                        controller.mark(fin_j, fin_i);
                    }
                });
            }
        }
        //board_representation = add_animation_tile(board_representation);
        return board_representation;
    }

    /*
    private GridPane add_animation_tile(GridPane board) {
        int y = HEIGHT_COUNT / 2;
        int x = WIDTH_COUNT / 2;

        return board;
    }
    */
    public void updateTiles(Board board){
        for (int j = 0; j < HEIGHT_COUNT; j++){
            for (int i = 0; i < WIDTH_COUNT; i++){
                Loc cur_loc = board.getLoc(j, i);
                Button cur_button = button_array[j][i];
                if (cur_loc.getIsVisited()){
                    if (cur_loc.getNeighbor() == 0){
                        cur_button.setStyle("-fx-background-radius: 0px; -fx-background-color: #" + COLOR_ONE + ";");
                    } else {
                        cur_button.setStyle("-fx-background-radius: 0px; -fx-background-color: #" + COLOR_TWO + ";");
                        cur_button.setText(""+cur_loc.getNeighbor());
                        cur_button.autosize();
                    }
                } else {
                    if (cur_loc.getFlagged()) {
                        if(cur_loc.getFlagCount() == 2) {
                            cur_button.setStyle("-fx-background-radius: 0px; -fx-background-color: #006400;");
                        } else {
                            cur_button.setStyle("-fx-background-radius: 0px; -fx-background-color: #DDDD22;");
                        }
                    } else {
                        cur_button.setStyle("-fx-background-radius: 0px;");
                    }
                }
            }
        }
    }

    public void updateEndState(Board board, boolean won){
        for (int j = 0; j < HEIGHT_COUNT; j++){
            for (int i = 0; i < WIDTH_COUNT; i++){
                Loc cur_loc = board.getLoc(j, i);
                Button cur_button = button_array[j][i];
                cur_button.setMouseTransparent(true);
                if (cur_loc.getHasBomb()){
                    if (won){
                        cur_button.setStyle("-fx-background-radius: 0px; -fx-background-color: #00FF00;");
                    } else {
                        bombAnimation();
                        cur_button.setStyle("-fx-background-radius: 0px; -fx-background-color: #" + COLOR_THREE + ";");
                    }
                } else if (cur_loc.getIsVisited()){
                    if (cur_loc.getNeighbor() == 0){
                        cur_button.setStyle("-fx-background-radius: 0px; -fx-background-color: #" + COLOR_ONE + ";");
                    } else {
                        cur_button.setStyle("-fx-background-radius: 0px; -fx-background-color: #" + COLOR_TWO + ";");
                        cur_button.setText(""+cur_loc.getNeighbor());
                        cur_button.autosize();
                    }
                } else {
                    if (cur_loc.getFlagged()) {
                        if (cur_loc.getFlagCount() == 2) {
                            cur_button.setStyle("-fx-background-radius: 0px; -fx-background-color: #006400;");
                        } else {
                            cur_button.setStyle("-fx-background-radius: 0px; -fx-background-color: #DDDD22;");
                        }
                    }
                }
            }
        }
    }

    private void bombAnimation() {
        animation_counter+=1;
        final int SPRITE_WIDTH = 999;
        final int NUM_SPRITES = 9;
        final int S_WIDTH = SPRITE_WIDTH / NUM_SPRITES;
        final int S_HEIGHT = 110;
        final int OFFSET_X = 5;
        final int OFFSET_Y = 0;
        ImageView imageView = new ImageView(BOMB_IMAGE);
        imageView.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, S_WIDTH, S_HEIGHT));
        Animation animation = new AnimateBomb(imageView, Duration.millis(800), NUM_SPRITES, NUM_SPRITES,
                OFFSET_X, OFFSET_Y, S_WIDTH, S_HEIGHT);
        animation.setCycleCount(5);
        animation.playFromStart();
        if (animation_counter == 1) {
            Stage s = new Stage(StageStyle.TRANSPARENT);
            Scene new_scene = new Scene(new Group(imageView));
            new_scene.setFill(Color.TRANSPARENT);
            s.setScene(new_scene);
            s.show();
        }
    }

    public class AnimateBomb extends Transition {
        private final ImageView imageView;
        private final int count;
        private final int cols;
        private final int offset_x;
        private final int offset_y;
        private final int width;
        private final int height;
        private int lastIndex;

        public AnimateBomb(ImageView imageView, Duration duration,
                           int count, int cols, int offset_x, int offset_y,
                           int width, int height) {
            int length = 10;
            this.imageView = imageView;
            this.count = count;
            this.cols = cols;
            this.offset_x = offset_x;
            this.offset_y = offset_y;
            this.width = width;
            this.height = height;
            setCycleDuration(duration);
            setInterpolator(Interpolator.LINEAR);
        }
        @Override
        protected void interpolate(double v) {
            int x = 0;
            int y = 0;
            final int index = Math.min((int) Math.floor(v*count), count - 1);
            x = (index % cols) * width + offset_x;
            y = (index / cols) * width + offset_y;
            imageView.setViewport(new Rectangle2D(x, y, width, height));
            lastIndex = index;
            }
        }

    @Override
    public void update(Observable o, Object arg) {
        assert (arg instanceof Board);
        Board returned_board = (Board) arg;
        if (((MinesweeperModel) o).isGameOver()){
            updateEndState(returned_board, ((MinesweeperModel) o).getBombLeft() == 0);
        } else {
            updateTiles((Board) arg);
        }
    }
}
