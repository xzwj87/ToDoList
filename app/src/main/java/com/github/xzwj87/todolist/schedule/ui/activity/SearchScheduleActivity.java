package com.github.xzwj87.todolist.schedule.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.xzwj87.todolist.R;

public class SearchScheduleActivity extends AppCompatActivity {
    private static String LOG_TAG = SearchScheduleActivity.class.getSimpleName();

    public static final String QUERY = "search_keyword";

    private String mKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            mKeyword = getIntent().getStringExtra(QUERY);
            Log.v(LOG_TAG, "onCreate(): mKeyword = " + mKeyword);
        }
    }

}
