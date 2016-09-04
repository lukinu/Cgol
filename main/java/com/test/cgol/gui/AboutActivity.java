package com.test.cgol.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.test.cgol.R;

public class AboutActivity extends AppCompatActivity {

    private TextView mAboutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mAboutView = (TextView) findViewById(R.id.tvAbout);
    }
}