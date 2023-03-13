/**
 * View for game of minesweeper
 */

package view;

import controller.MinesweeperController;
import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.MinesweeperModel;

public class OptionsWindow extends Application{

    /**
     * Just launches the Application by calling "launch()"
     * @param args no input parameters
     * @throws Exception required method signature for Application parent class
     */
    public static void main(String[] args) throws Exception {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        GridPane options = new GridPane();
        TextField width_input = new TextField("Enter Width");
        TextField height_input = new TextField("Enter Height");
        TextField bomb_count_input = new TextField("Enter Bomb Count");
        TextField select_Theme = new TextField("Enter 3 Colors - Ex. (255, 0, 0)-(0, 255, 0)-(0, 0, 255)");
        Button start_button = new Button("Start!");
        start_button.setOnMouseClicked((Event e) -> {  // When the start button is pressed, start the round
            try {
                start_round(Integer.parseInt(width_input.getText()),
                            Integer.parseInt(height_input.getText()),
                            Integer.parseInt(bomb_count_input.getText()),
                            select_Theme.getText(),
                            stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        start_button.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER){
                try {
                    start_round(Integer.parseInt(width_input.getText()),
                            Integer.parseInt(height_input.getText()),
                            Integer.parseInt(bomb_count_input.getText()),
                            select_Theme.getText(),
                            stage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        options.add(width_input, 0, 0);
        options.add(height_input, 0, 1);
        options.add(bomb_count_input, 0, 2);
        options.add(start_button, 1, 1);
        options.add(select_Theme,  0, 3);
        Scene options_scene = new Scene(options);
        stage.setScene(options_scene);
        stage.show();
    }

    /**
     * Function called for starting a round of minesweeper
     *
     * This function is called after the user has put in their desired game parameters and wants
     * to start the round.
     *
     * @param width the number of tiles wide the game will be
     * @param height the number of tiles tall the game will be
     * @param bomb_count the number of bombs that the game will have
     * @param stage the stage that the options are on. Allows for the hiding and reshowing of the options
     * @throws Exception for creating a new Application and running it, this is required
     */
    private void start_round(int width, int height, int bomb_count, String colors, Stage stage) throws Exception {
        // Define the model, controller, and game view for a round
        MinesweeperModel model = new MinesweeperModel(height, width, bomb_count);
        MinesweeperController controller = new MinesweeperController(model);
        GameWindow game = new GameWindow(controller, colors);
        // The following lines allow the passing of the controller into the GameWindow constructor
        game.init();
        Stage game_stage = new Stage();
        game_stage.setOnCloseRequest((
                WindowEvent e) -> stage.show());
        try {
            game.start(game_stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stage.hide();
    }
}
