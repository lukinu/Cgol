package com.test.cgol;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class ZoomSlider extends SeekBar {

    private static final int MAX_ZOOM_FACTOR = 5;

    public ZoomSlider(Context context) {
        super(context);
        setMax(MAX_ZOOM_FACTOR);
        setProgress(0);
    }

    public ZoomSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMax(MAX_ZOOM_FACTOR);
        setProgress(0);
    }

    public ZoomSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMax(MAX_ZOOM_FACTOR);
        setProgress(0);
    }

    @Override
    public synchronized int getProgress() {
        switch (super.getProgress()) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 4;
            case 3:
                return 5;
            case 4:
                return 10;
            case 5:
                return 20;
            default:
                return 1;
        }
    }
}