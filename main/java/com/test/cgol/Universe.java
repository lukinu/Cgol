package com.test.cgol;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.test.cgol.utils.Clock;
import com.test.cgol.utils.ObjectPersistantStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/*
* The Universe contains all the cells (i.e. creatures)
* and implements evolution logic of the game. Listens for a Clock's ticks
* and reports evolution events to GUI
*
* */
public class Universe extends Observable implements Observer {
    private static final String TAG = "UNIVERSE";
    private static Universe mUniverse = new Universe();
    // universal clock, it listens on
    private Clock mClock;
    // holds all the cells, i.e. Universe population:
    private List<Cell> mCells;

    public static Universe getInstance() {
        return mUniverse;
    }

    private Universe() {
        this.mClock = Clock.getInstance();
        mClock.addObserver(this);
        mCells = new ArrayList<>();
    }

    // populates the Universe with cells
    public void init(int universeDimentionInCells) {
        for (int j = 0; j < universeDimentionInCells; j++) {
            for (int i = 0; i < universeDimentionInCells; i++) {
                Cell cell = new Cell(i, j);
                mCells.add(cell);
            }
        }
    }

    // callback from universal clock - calls evolution and notifies GUI:
    @Override
    public void update(Observable o, Object arg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "update: the mUniverse gets notified by clock");
        }
        evolve();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "update: the universe notifies gui");
        }
        setChanged();
        notifyObservers();
    }

    public List<Cell> getAllCells() {
        return mCells;
    }

    // looks for a certain cell by coordinates:
    public Cell getCellByXY(int x, int y) {
        for (Cell cell : mCells) {
            if ((cell.getX() == x) && (cell.getY() == y)) {
                return cell;
            }
        }
        return null;
    }

    /*
    * runs evolution process in the Universe, that is, the Universe checks each cell against rules
    * and calculates next state
    */
    public void evolve() {
        // calculate next state:
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
        // all the cells go next state
        for (Cell cell : mCells) {
            cell.evolve();
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "evolve: the universe completed evolution");
        }
        // notify GUI:
        setChanged();
        notifyObservers();
    }

    // calculate number of a cell's alive neighbours:
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

    // kill all the creatures in the Universe;
    public void clear() {
        for (Cell cell : mCells) {
            cell.setState(Cell.STATE_EMPTY);
        }
        setChanged();
        notifyObservers();
    }

    // populate the Universe randomly:
    public void randomize() {
        Random rand = new Random();
        for (Cell cell : mCells) {
            int randInt = rand.nextInt(10);
            boolean isAlive = (randInt < 6) ? Cell.STATE_EMPTY : Cell.STATE_ALIVE;
            cell.setState(isAlive);
        }
        setChanged();
        notifyObservers();
    }

    // call storage to save the population:
    public void save(Context context, String name) {
        try {
            ObjectPersistantStorage.put(context, mCells, name);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "save: the universe saved configuration");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // call storage to restore the population from saved state:
    public void load(Context context, String name) {
        try {
            mCells = (List<Cell>) ObjectPersistantStorage.get(context, name);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "load: the universe loaded configuration");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(context, context.getString(R.string.io_exeption_toast) + name, Toast.LENGTH_SHORT);
            toast.show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers();
    }
}