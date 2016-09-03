package com.test.cgol;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public class Cell {

    public static final boolean STATE_EMPTY = false;
    public static final boolean STATE_ALIVE = true;
    private int mX;
    private int mY;
    private Rect mRect;
    private boolean mIsAlive;
    private boolean mNextState;

    public Cell(int x, int y) {
        this.mX = x;
        this.mY = y;
        this.mRect = new Rect(x * MainActivity.mCellSizeInPx, y * MainActivity.mCellSizeInPx,
                (x + 1) * MainActivity.mCellSizeInPx, (y + 1) * MainActivity.mCellSizeInPx);
        this.mIsAlive = STATE_EMPTY;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public Rect getRect() {
        return mRect;
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

    public void evolve() {
        mIsAlive = mNextState;
    }

}