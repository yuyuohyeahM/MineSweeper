package tools;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Loc implements Serializable{
    public final int row;
    public final int col;
    private int neighbor;
    private boolean hasBomb;
    private boolean isVisited;
    private boolean revealed;
    private boolean flagged;
    private int flag_count;

    //constructor
    //x is row, y is column
    public Loc(int x, int y) {
        this.row = x;
        this.col = y;
        this.hasBomb = false;
        this.isVisited = false;
        this.revealed = false;
        this.flagged = false;
        this.neighbor = 0;
        this.flag_count = 0;

    }

    public boolean getHasBomb() {
        return this.hasBomb;
    }
    public boolean getIsVisited() {
        return this.isVisited;
    }
    public boolean getRevealed() {
        return this.revealed;
    }
    public boolean getFlagged() {
        return this.flagged;
    }

    public void setVisited() {
        this.isVisited = true;
    }
    public void setFlagged() {
        this.flagged = !this.flagged;
    }
    public int getFlagCount() {return this.flag_count;}
    public void setFlagCount() {
        if (this.flag_count < 2) {
            this.flag_count++;
        } else {
            this.flag_count = 0;
        }
    }
    public void plantBomb() {
        this.hasBomb = true;
    }

    public void addNeighbor() {
        this.neighbor += 1;
    }
    public int getNeighbor() {
        return this.neighbor;
    }

    public void reveal() {
        this.revealed = true;
    }

    @Override
    public String toString() {
        if (hasBomb){
            return "B";
        }
        if (isVisited){
            if (hasBomb){
                return "B";
            } else if (neighbor == 0){
                return "0";
            } else {
                return "" + neighbor;
            }
        } else {
            return "_";
        }

    }
}
