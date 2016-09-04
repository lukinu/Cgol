package com.test.cgol;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Cell implements Serializable {

    public static final boolean STATE_EMPTY = false;
    public static final boolean STATE_ALIVE = true;
    private int mX;
    private int mY;
    private boolean mIsAlive;
    private boolean mNextState;

    public Cell(int x, int y) {
        this.mX = x;
        this.mY = y;
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

    public void evolve() {
        mIsAlive = mNextState;
    }
}