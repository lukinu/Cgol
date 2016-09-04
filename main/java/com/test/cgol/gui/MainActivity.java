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

/*
* Main Activity holds UI elements and listens for Universe's events.
*
* */
public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, Observer,
        SeekBar.OnSeekBarChangeListener, Dialogs.ValueSetListener<String> {

    private static final int UNIVERSE_DIMENTION_IN_CELLS = 20;
    private static final boolean AUTO_GEN_STOPPED = false;
    // size of cell in DP
    private int cellSizeInDp;
    // size of cell in pixels
    private static int mCellSizeInPx;
    // scaled size to zoom the game filed:
    public static int mScaledCellSizeInPx;
    // instatiate game universe:
    private static Universe mUniverse = Universe.getInstance();
    // the auto-mode is stopped initially:
    private static boolean mIsAutoGenRunning = AUTO_GEN_STOPPED;
    // a view holding main game field:
    private UniverseView mUniverseView;
    // app controls:
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

    /*
    * initialize data:
    *
    * */
    private void init() {
        mUniverseView = (UniverseView) findViewById(R.id.universeView);
        mUniverseView.setOnTouchListener(this);
        mClrBtn = (Button) findViewById(R.id.btnClear);
        mStartBtn = (Button) findViewById(R.id.btnStartStopAuto);
        mRandomizeBtn = (Button) findViewById(R.id.btnRandom);
        mStepBtn = (Button) findViewById(R.id.btnStep);
        mSaveBtn = (Button) findViewById(R.id.btnSave);
        mLoadBtn = (Button) findViewById(R.id.btnLoad);
        // setup listeners
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
        // trying to set zoom slider to the start
        mZoomSlider.setProgress(0); //todo: someth wrong with slider drawable
        // choose cell size in DP depending on screen size and layout:
        Configuration configuration = getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;
        // 4-inch screens:
        if (screenWidthDp < 400) {
            cellSizeInDp = 10;
        } else {
            // 5-inch screens:
            if (screenWidthDp < 600) {
                cellSizeInDp = 16;
            }
            // 7- and more inches:
            else {
                cellSizeInDp = 20;
            }
        }
        // calculate actual size of a cell in pixels:
        mCellSizeInPx = dpToPx(cellSizeInDp);
        // initial setup of a scaled size as equal to non-scaled (i.e. scaling 1:1):
        mScaledCellSizeInPx = mCellSizeInPx;
        // call init of universe, populating it with creatures
        mUniverse.init(UNIVERSE_DIMENTION_IN_CELLS);
        // make activity listening for events in the universe:
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

    // we calculate coordinates of a cell touched and change its state
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
            // clear the universe
            case R.id.btnClear:
                mUniverse.clear();
                break;
            // start and stop auto generation process
            case R.id.btnStartStopAuto:
                switchGameState();
                break;
            // create a random configuration in the universe
            case R.id.btnRandom:
                mUniverse.randomize();
                break;
            // go next generation
            case R.id.btnStep:
                mUniverse.evolve();
                break;
            // save current state of population in the universe
            case R.id.btnSave:
                Dialogs.createInputSaveConfigNameDialog(this, getString(R.string.name_dialog_title),
                        getString(R.string.name_dialog_save_prompt), this).show();
                break;
            // restore saved state of population in the universe
            case R.id.btnLoad:
                Dialogs.createInputLoadConfigNameDialog(this, getString(R.string.name_dialog_title),
                        getString(R.string.name_dialog_load_prompt), this).show();
                break;
            default:
                break;
        }
    }

    // convert sizes from pixel to DP
    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    // convert sizes from DP to pixels
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    // callback from the observable universe, updates field view:
    @Override
    public void update(Observable observable, Object data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUniverseView.invalidate();
            }
        });
    }

    // switch state of UI controls acording to the sate of the game:
    public void switchGameState() {
        mIsAutoGenRunning = !mIsAutoGenRunning;
        if (mIsAutoGenRunning) {
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
            // change universe clock speed according to auto-generation speed slider:
            case R.id.sbSpeedSlider:
                mUniverse.getClock().updateTimeSpeed(mSpeedSlider.getProgress());
                break;
            // change size of a cell depending on zoom slider value:
            case R.id.sbZoom:
                mScaledCellSizeInPx = mCellSizeInPx * (mZoomSlider.getProgress());
                mUniverseView.invalidate();
                break;
            default:
                break;
        }
    }

    // callback from config load dialog, calls method of the universe
    @Override
    public void onLoadConfigValueSet(String value) {
        if (value != null) {
            mUniverse.load(this, value);
        }
    }

    // callback from config save dialog, calls method of the universe
    @Override
    public void onSaveConfigValueSet(String value) {
        if (value != null) {
            mUniverse.save(this, value);
        }
    }

    // custo view, representing main field of the game:
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

        // setup graphic tools:
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
                // draw the grid:
                // draw horizontal lines:
                for (int i = 0; i < (height / mScaledCellSizeInPx); i++) {
                    int y = i * mScaledCellSizeInPx;
                    canvas.drawLine(0, y, width, y, mPaint);
                }
                // draw vertical lines:
                for (int j = 0; j < (width / mScaledCellSizeInPx); j++) {
                    int x = j * mScaledCellSizeInPx;
                    canvas.drawLine(x, 0, x, height, mPaint);
                }
                // draw all the alive cells in the universe:
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