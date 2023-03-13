/**
 * Models a game of Minesweeper
 */

package model;

import tools.Board;

import java.io.Serializable;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class MinesweeperModel extends Observable implements Serializable {

	private Board board;
	private boolean gameover;
	private int rowSize;
	private int colSize;
	private int bomb;

	public MinesweeperModel(int row, int col,int bomb) {
		board = new Board(row,col,bomb);
		this.gameover = false;
		this.rowSize = row;
		this.colSize = col;
		this.bomb = bomb;
	}

	/**
	 * return the board.
	 * @return
	 */
	public Board getBoard() {
		return this.board;
	}

	/**
	 *  Place the bombs to the board.
	 *  The number of the bomb placed is depending on the board size.
	 *  This will automatically increment the neightbor location by 1 when a bomb
	 *  is placed.
	 */
	public void placeBomb(int firstMoveR, int firstMoveC) {
		this.board.placeBomb(firstMoveR,firstMoveC);
		setChanged();
		notifyObservers(this.board);
	}


	/**
	 * flag or unflag a location.
	 * flag: guess if a location contains a bomb.
	 * @param row
	 * @param col
	 */
	public void mark(int row, int col) {
		if (this.board.getLoc(row, col).getFlagged()) {
			if (this.board.getLoc(row, col).getFlagCount() == 1) {
				this.board.getLoc(row, col).setFlagCount();
			} else if (this.board.getLoc(row, col).getFlagCount() == 2){
				this.bomb+=1;
				this.board.getLoc(row, col).setFlagCount();
				this.board.getLoc(row, col).setFlagged();
			}
		} else {
			this.board.getLoc(row, col).setFlagged();
			this.board.getLoc(row, col).setFlagCount();
			this.bomb-=1;
		}
		if (this.bomb == 0) {
			if (this.checkAllFlagged()) {
				this.gameover = true;
			}
		}
		setChanged();
		notifyObservers(this.board);
	}

	/**
	 * Check if all flagged are correctly placed. i.e every flag is placed
	 * on a location that has a bomb.
	 * @return
	 */
	private boolean checkAllFlagged() {
		for (int i = 0; i < this.rowSize; i++) {
			for (int j = 0; j < this.rowSize; j++) {
				if (this.board.getLoc(i, j).getFlagged()) {
					if (this.board.getLoc(i, j).getHasBomb() == false) {
						return true;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Check if the move contain a bomb or not, it yes, set this.gameover to true.
	 * Else, reveal a location.
	 * notify the Observer.
	 * @param row
	 * @param col
	 */
	public void move(int row, int col) {
		if (this.board.getLoc(row, col).getFlagged()) {
			System.out.println("No change");
		} else {
			if (this.board.getLoc(row, col).getHasBomb()) {
				this.gameover = true;
				this.board.getLoc(row, col).reveal();
			} else {
				revLoc(row, col);
			}
		}
		setChanged();
		notifyObservers(this.board);
	}

	/**
	 * reveal a location, and if any of its neighbor's "neighbor (in Loc)" is 0
	 * recursive call this function on that location.
	 * Do not recurse if it's neighbor is not 0.
	 * @param row
	 * @param col
	 */
	private void revLoc(int row, int col) {
		this.board.getLoc(row, col).reveal();
		this.board.getLoc(row, col).setVisited();
		if (row - 1 >= 0 && col - 1 >= 0
				&& this.board.getLoc(row-1, col-1).getIsVisited() == false
				&& this.board.getLoc(row-1, col-1).getHasBomb() == false) {

			if (this.board.getLoc(row-1, col-1).getNeighbor() != 0) {
				this.board.getLoc(row-1, col-1).reveal();
				this.board.getLoc(row-1, col-1).setVisited();
			} else if (this.board.getLoc(row-1, col-1).getNeighbor() == 0) {
				revLoc(row-1,col-1);
			}
		}
		if (row - 1 >= 0 && col + 1 < this.colSize
				&& this.board.getLoc(row-1, col+1).getIsVisited() == false
				&& this.board.getLoc(row-1, col+1).getHasBomb() == false) {
			if (this.board.getLoc(row-1, col+1).getNeighbor() != 0) {
				this.board.getLoc(row-1, col+1).reveal();
				this.board.getLoc(row-1, col+1).setVisited();
			} else if (this.board.getLoc(row-1, col+1).getNeighbor() == 0) {
				revLoc(row-1,col+1);
			}
		}
		if (row - 1 >= 0
				&& this.board.getLoc(row-1, col).getIsVisited() == false
				&& this.board.getLoc(row-1, col).getHasBomb() == false) {

			if (this.board.getLoc(row-1, col).getNeighbor() != 0) {
				this.board.getLoc(row-1, col).reveal();
				this.board.getLoc(row-1, col).setVisited();
			} else if (this.board.getLoc(row-1, col).getNeighbor() == 0) {
				revLoc(row-1,col);
			}
		}
		if (col - 1 >= 0
				&& this.board.getLoc(row, col-1).getIsVisited() == false
				&& this.board.getLoc(row, col-1).getHasBomb() == false) {
			if (this.board.getLoc(row, col-1).getNeighbor() != 0) {
				this.board.getLoc(row, col-1).reveal();
				this.board.getLoc(row, col-1).setVisited();
			} else if (this.board.getLoc(row, col-1).getNeighbor() == 0) {
				revLoc(row,col-1);
			}

		}
		if (col + 1 < this.colSize
				&& this.board.getLoc(row, col+1).getIsVisited() == false
				&& this.board.getLoc(row, col+1).getHasBomb() == false) {
			if (this.board.getLoc(row, col+1).getNeighbor() != 0) {
				this.board.getLoc(row, col+1).reveal();
				this.board.getLoc(row, col+1).setVisited();
			} else if (this.board.getLoc(row, col+1).getNeighbor() == 0) {
				revLoc(row,col+1);
			}
		}
		if (row + 1 < this.rowSize && col - 1 >= 0
				&& this.board.getLoc(row+1, col-1).getIsVisited() == false
				&& this.board.getLoc(row+1, col-1).getHasBomb() == false) {

			if (this.board.getLoc(row+1, col-1).getNeighbor() != 0) {
				this.board.getLoc(row+1, col-1).reveal();
				this.board.getLoc(row+1, col-1).setVisited();
			} else if (this.board.getLoc(row+1, col-1).getNeighbor() == 0) {
				revLoc(row+1,col-1);
			}
		}
		if (row + 1 < this.rowSize && col + 1 < this.colSize
				&& this.board.getLoc(row+1, col+1).getIsVisited() == false
				&& this.board.getLoc(row+1, col+1).getHasBomb() == false) {
			if (this.board.getLoc(row+1, col+1).getNeighbor() != 0) {
				this.board.getLoc(row+1, col+1).reveal();
				this.board.getLoc(row+1, col+1).setVisited();
			} else if (this.board.getLoc(row+1, col+1).getNeighbor() == 0) {
				revLoc(row+1,col+1);
			}
		}
		if (row + 1 < this.rowSize
				&& this.board.getLoc(row+1, col).getIsVisited() == false
				&& this.board.getLoc(row+1, col).getHasBomb() == false) {
			if (this.board.getLoc(row+1, col).getNeighbor() != 0) {
				this.board.getLoc(row+1, col).reveal();
				this.board.getLoc(row+1, col).setVisited();
			} else if (this.board.getLoc(row+1, col).getNeighbor() == 0) {
				revLoc(row+1,col);
			}

		}

	}

	/**
	 * if this.gameover is true and bomb is 0, user win.
	 * else user lose.
	 * @return
	 */
	public boolean isGameOver() {
		return this.gameover;
	}

	/**
	 * return the bombs left in the game.
	 * @return numbers of bomb left.
	 */
	public int getBombLeft() {
		return this.bomb;
	}

	public int[] getStartingVars(){
		return new int[]{this.rowSize, this.colSize, this.bomb};
	}



}
