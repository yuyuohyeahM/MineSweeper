/**
 * Controller for game of Minesweeper
 */

package controller;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.MinesweeperModel;
import tools.Board;
import view.GameWindow;

import java.io.*;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class MinesweeperController implements Serializable {
    private MinesweeperModel mod;
    private boolean first_round;

    public MinesweeperController(MinesweeperModel mod){
        this.first_round = true;
        this.mod = mod;
    }



    /**
     * true if game is over, should check bombleft for winning or not.
     * @return
     */
    public boolean isGameOver() {
        return mod.isGameOver();
    }


    /**
     * This should be call after the user make the first move
     * @param firstMoveR
     * @param firstMoveC
     */
    public void placeBomb(int firstMoveR, int firstMoveC) {
        mod.placeBomb(firstMoveR,firstMoveC);
    }

    /**
     * Check if the move contain a bomb or not, it yes, set this.gameover to true.
     * Else, reveal a location.
     * notify the Observer.
     * @param row
     * @param col
     */
    public void move(int row, int col) {
        if (first_round){
            this.placeBomb(row, col);
            this.first_round = false;
        }
        this.mod.move(row, col);
    }

    /**
     * flag or unflag a location.
     * flag: guess if a location contains a bomb.
     * @param row
     * @param col
     */
    public void mark(int row, int col) {
        this.mod.mark(row, col);
    }

    /**
     * return the bombs left in the game.
     * @return numbers of bomb left.
     */
    public int getBombLeft() {
        return mod.getBombLeft();
    }

    /**
     * return the board.
     * @return board
     */
    public Board getBoard() {
        return mod.getBoard();
    }

    public int[] getStartingVars(){
        return mod.getStartingVars();
    }

    public void addObserver(Observer observer){
        mod.addObserver(observer);
    }

    /**
     * Saves the current state of the controller, model, board and loc Objects
     * to the selected filename.
     *
     * @param filename The name of the save file.
     * @param colors
     */
    public void saveGameState(String filename, String colors) {
        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream output = new ObjectOutputStream(file);
            output.writeObject(this);
            output.writeObject(colors);
            output.close();
            file.close();
        }
        catch (IOException e) {
            System.out.println("Unable to save current game.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * loads a saved game located at filename to the stage.
     * @param filename the name of the save file.
     * @param stage the currently active stage in View.
     */
    public void loadGameState(String filename, Stage stage) {
        try {
            // Load Controller From File Input Stream
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream input = new ObjectInputStream(file);
            MinesweeperController controller = (MinesweeperController) input.readObject();
            String colors = (String) input.readObject();
            input.close();
            file.close();
            // Load Previous Game Controller and reset game with retrieved back end.
            GameWindow game = new GameWindow(controller, colors);
            game.init();
            Stage game_stage = new Stage();
            game_stage.setOnCloseRequest((WindowEvent e) -> stage.show());
            try {
                game.start(game_stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stage.hide();
            // Update new View.
            game.update(controller.mod, controller.getBoard());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
