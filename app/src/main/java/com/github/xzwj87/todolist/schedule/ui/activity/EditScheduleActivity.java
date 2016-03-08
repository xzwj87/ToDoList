package com.github.xzwj87.todolist.schedule.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.xzwj87.todolist.R;

public class EditScheduleActivity extends AppCompatActivity {
    private static final String LOG_TAG = ScheduleDetailActivity.class.getSimpleName();

    public static final String SCHEDULE_ID = "id";

    private long mScheduleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24dp);

        if (savedInstanceState == null) {
            mScheduleId = getIntent().getLongExtra(SCHEDULE_ID, 0);
            Log.v(LOG_TAG, "onCreate(): mScheduleId = " + mScheduleId);
        }
    }

}
