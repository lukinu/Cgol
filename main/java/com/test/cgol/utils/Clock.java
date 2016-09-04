package com.test.cgol.utils;

import android.util.Log;

import com.test.cgol.BuildConfig;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

/*
* Universal Clock generates ticks for the Universe
*
*
**/
public class Clock extends Observable {

    private static final String TAG = "CLOCK";
    // basic time quantum:
    private static final int TIME_GRANULE = 10000;

    private static Clock ourInstance = new Clock();
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int mTimeSpeed;

    public static Clock getInstance() {
        return ourInstance;
    }

    private Clock() {
        this.mTimeSpeed = 1;
    }

    public void startTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "run: timer task started");
                }
                setChanged();
                notifyObservers();
            }
        };
        mTimer.schedule(mTimerTask, 0, TIME_GRANULE / (mTimeSpeed + 1));
    }

    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    // change time speed on the fly:
    public void updateTimeSpeed(int timeSpeed) {
        this.mTimeSpeed = timeSpeed;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            startTimer();
        }
    }
}