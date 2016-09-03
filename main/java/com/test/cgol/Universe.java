package com.test.cgol;

import android.util.Log;

import com.test.cgol.utils.Clock;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class Universe extends Observable implements Observer {
    private static final String TAG = "UNIVERSE";
    private static Universe mUniverse = new Universe();
    private Clock mClock;
    private List<Cell> mCells;

    public static Universe getInstance() {
        return mUniverse;
    }

    private Universe() {
        this.mClock = Clock.getInstance();
        mClock.addObserver(this);
        mCells = new ArrayList<>();
    }

    public void init(int universeSizeCells) {
        for (int j = 0; j < universeSizeCells; j++) {
            for (int i = 0; i < universeSizeCells; i++) {
                Cell cell = new Cell(i, j);
                mCells.add(cell);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d(TAG, "update: the mUniverse gets notified by clock");
        evolve();
        Log.d(TAG, "update: the universe notifies gui");
        setChanged();
        notifyObservers();
    }

    public List<Cell> getAllCells() {
        return mCells;
    }

    public Cell getCellByXY(int x, int y) {
        for (Cell cell : mCells) {
            if ((cell.getX() == x) && (cell.getY() == y)) {
                return cell;
            }
        }
        return null;
    }

    public void evolve() {
        for (Cell cell : mCells) {
            int aliveNeighboursCount = getAliveNeighboursCount(cell);
            cell.setNextState(cell.isAlive());
            //todo: check for the end of the game
            if (cell.isAlive()) {
                if ((aliveNeighboursCount > 3) || (aliveNeighboursCount < 2)) {
                    cell.setNextState(Cell.STATE_EMPTY);
                }
            } else {
                if (aliveNeighboursCount == 3) {
                    cell.setNextState(Cell.STATE_ALIVE);
                }
            }
        }
        for (Cell cell : mCells) {
            cell.evolve();
        }
        Log.d(TAG, "evolve: the universe completed evolution");
    }

    private int getAliveNeighboursCount(Cell cell) {
        int count = 0;
        Cell nearCell;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                nearCell = getCellByXY(cell.getX() + i, cell.getY() + j);
                if ((nearCell != null) && (nearCell != cell)) {
                    if (nearCell.isAlive()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public Clock getClock() {
        return mClock;
    }

    public void clear() {
        for (Cell cell : mCells) {
            cell.setState(Cell.STATE_EMPTY);
        }
    }

    public void randomize() {
        Random rand = new Random();
        for (Cell cell : mCells) {
            int randInt = rand.nextInt(10);
            boolean isAlive = (randInt < 6) ? Cell.STATE_EMPTY : Cell.STATE_ALIVE;
            cell.setState(isAlive);
        }
    }
}