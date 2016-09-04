package com.test.cgol;

import java.io.Serializable;

/*
* Class representing am atomic place at the game files. Can be populated (alive)
* or empty (dead).
*
* */
public class Cell implements Serializable {

    public static final boolean STATE_EMPTY = false;
    public static final boolean STATE_ALIVE = true;
    // a cell knows its coordinates in the universe
    private int mX;
    private int mY;
    // current state:
    private boolean mIsAlive;
    // keeps track on the next state. Used for evolution
    private boolean mNextState;

    public Cell(int x, int y) {
        this.mX = x;
        this.mY = y;
        // cell is empty by default
        this.mIsAlive = STATE_EMPTY;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public void setState(boolean state) {
        mIsAlive = state;
    }

    public void switchState() {
        mIsAlive = !mIsAlive;
    }

    public boolean isAlive() {
        return mIsAlive;
    }

    public void setNextState(boolean nextState) {
        mNextState = nextState;
    }

    // evolution of a cell - cell goes next sate
    public void evolve() {
        mIsAlive = mNextState;
    }
}