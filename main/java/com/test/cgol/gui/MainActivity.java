package com.test.cgol.gui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.test.cgol.Cell;
import com.test.cgol.R;
import com.test.cgol.Universe;
import com.test.cgol.utils.Dialogs;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, Observer,
        SeekBar.OnSeekBarChangeListener, Dialogs.ValueSetListener<String> {

    private static final int UNIVERSE_DIMENTION_IN_CELLS = 20;
    private static final boolean GAME_STOPPED = false;
    private int cellSizeInDp;
    private static int mCellSizeInPx;
    public static int mScaledCellSizeInPx;
    private static Universe mUniverse = Universe.getInstance();
    private static boolean mIsGameRunning = GAME_STOPPED;
    private UniverseView mUniverseView;
    private Button mClrBtn;
    private Button mStartBtn;
    private Button mRandomizeBtn;
    private Button mStepBtn;
    private Button mSaveBtn;
    private Button mLoadBtn;
    private SeekBar mSpeedSlider;
    private SeekBar mZoomSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mUniverseView = (UniverseView) findViewById(R.id.universeView);
        mUniverseView.setOnTouchListener(this);
        mClrBtn = (Button) findViewById(R.id.btnClear);
        mStartBtn = (Button) findViewById(R.id.btnStartStop);
        mRandomizeBtn = (Button) findViewById(R.id.btnRandom);
        mStepBtn = (Button) findViewById(R.id.btnStep);
        mSaveBtn = (Button) findViewById(R.id.btnSave);
        mLoadBtn = (Button) findViewById(R.id.btnLoad);
        mClrBtn.setOnClickListener(this);
        mStartBtn.setOnClickListener(this);
        mRandomizeBtn.setOnClickListener(this);
        mStepBtn.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        mLoadBtn.setOnClickListener(this);
        mSpeedSlider = (SeekBar) findViewById(R.id.sbSpeedSlider);
        mSpeedSlider.setOnSeekBarChangeListener(this);
        mZoomSlider = (ZoomSlider) findViewById(R.id.sbZoom);
        mZoomSlider.setOnSeekBarChangeListener(this);
        Configuration configuration = getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;
        if (screenWidthDp < 400) {
            cellSizeInDp = 10;
        } else {
            if (screenWidthDp < 600) {
                cellSizeInDp = 16;
            } else {
                cellSizeInDp = 20;
            }
        }
//        cellSizeInDp = (screenWidthDp < 600) ? 10 : 20;
        mCellSizeInPx = dpToPx(cellSizeInDp);
        mScaledCellSizeInPx = mCellSizeInPx;
        mUniverse.init(UNIVERSE_DIMENTION_IN_CELLS);
        mUniverse.addObserver(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Intent intent = new Intent(this, AboutActivity.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) (event.getX() / mScaledCellSizeInPx);
            int y = (int) (event.getY() / mScaledCellSizeInPx);
            Cell cell = mUniverse.getCellByXY(x, y);
            if (cell != null) {
                cell.switchState();
            }
            v.invalidate();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                mUniverse.clear();
                break;
            case R.id.btnStartStop:
                switchGameState();
                break;
            case R.id.btnRandom:
                mUniverse.randomize();
                break;
            case R.id.btnStep:
                mUniverse.evolve();
                break;
            case R.id.btnSave:
                Dialogs.createInputSaveConfigNameDialog(this, getString(R.string.name_dialog_title),
                        getString(R.string.name_dialog_save_prompt), this).show();
                break;
            case R.id.btnLoad:
                Dialogs.createInputLoadConfigNameDialog(this, getString(R.string.name_dialog_title),
                        getString(R.string.name_dialog_load_prompt), this).show();
                break;
            default:
                break;
        }
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public void update(Observable observable, Object data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUniverseView.invalidate();
            }
        });
    }

    public void switchGameState() {
        mIsGameRunning = !mIsGameRunning;
        if (mIsGameRunning) {
            mUniverse.getClock().startTimer();
            mStartBtn.setText(R.string.btn_start_text_started);
            mClrBtn.setEnabled(false);
            mRandomizeBtn.setEnabled(false);
            mStepBtn.setEnabled(false);
            mUniverseView.setEnabled(false);
            mSaveBtn.setEnabled(false);
            mLoadBtn.setEnabled(false);
        } else {
            mUniverse.getClock().stopTimer();
            mStartBtn.setText(R.string.btn_start_text_stopped);
            mClrBtn.setEnabled(true);
            mRandomizeBtn.setEnabled(true);
            mStepBtn.setEnabled(true);
            mUniverseView.setEnabled(true);
            mSaveBtn.setEnabled(true);
            mLoadBtn.setEnabled(true);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sbSpeedSlider:
                mUniverse.getClock().updateTimeSpeed(mSpeedSlider.getProgress());
                break;
            case R.id.sbZoom:
                mScaledCellSizeInPx = mCellSizeInPx * (mZoomSlider.getProgress());
                mUniverseView.invalidate();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoadConfigValueSet(String value) {
        if (value != null) {
            mUniverse.load(this, value);
        }
    }

    @Override
    public void onSaveConfigValueSet(String value) {
        if (value != null) {
            mUniverse.save(this, value);
        }
    }


    public static class UniverseView extends View {

        private Paint mPaint;
        private Rect mRect;

        public UniverseView(Context context) {
            super(context);
            initView();
        }

        public UniverseView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initView();
        }

        public UniverseView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initView();
        }

        private void initView() {
            mPaint = new Paint();
            mPaint.setColor(Color.LTGRAY);
            mPaint.setStrokeWidth(1);
            mRect = new Rect();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(getResources().getColor(R.color.colorPrimary));
            int height = getHeight();
            int width = getWidth();
            if (mScaledCellSizeInPx != 0) {
                for (int i = 0; i < (height / mScaledCellSizeInPx); i++) {
                    int y = i * mScaledCellSizeInPx;
                    canvas.drawLine(0, y, width, y, mPaint);
                }
                for (int j = 0; j < (width / mScaledCellSizeInPx); j++) {
                    int x = j * mScaledCellSizeInPx;
                    canvas.drawLine(x, 0, x, height, mPaint);
                }
                for (Cell cell : mUniverse.getAllCells()) {
                    if (cell.isAlive()) {
                        mRect.set(cell.getX() * MainActivity.mScaledCellSizeInPx,
                                cell.getY() * MainActivity.mScaledCellSizeInPx,
                                (cell.getX() + 1) * MainActivity.mScaledCellSizeInPx,
                                (cell.getY() + 1) * MainActivity.mScaledCellSizeInPx);
                        canvas.drawRect(mRect, mPaint);
                    }
                }
            }
        }
    }
}