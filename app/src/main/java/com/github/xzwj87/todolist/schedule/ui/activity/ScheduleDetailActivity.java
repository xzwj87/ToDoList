package com.github.xzwj87.todolist.schedule.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleDetailFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class ScheduleDetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = ScheduleDetailActivity.class.getSimpleName();

    public static final String SCHEDULE_ID = "id";

    private long mScheduleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mScheduleId = getIntent().getLongExtra(SCHEDULE_ID, 0);
            Log.v(LOG_TAG, "onCreate(): mScheduleId = " + mScheduleId);

            ScheduleDetailFragment fragment = ScheduleDetailFragment.newInstance(mScheduleId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.schedule_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_share_schedule, menu);
        // return true to display the share menu
        return super.onCreateOptionsMenu(menu);
    }

    // share something with other social Apps
    public void doShare(){
        Log.v(LOG_TAG,"doShare()");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        /* put data to it */
        Bundle bundle = new Bundle();
        bundle.putString("Title","I am doing something funny " +
                "in ToDoList; come with me");
        intent.putExtras(bundle);
        startActivity(Intent.createChooser(intent,getResources().getText(R.string.send_to)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, ScheduleListActivity.class));
            return true;
        }else if(id == R.id.menu_share){
            doShare();
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void editSchedule(View view) {
        Log.v(LOG_TAG, "editSchedule()");
        Intent intent = new Intent(this, AddScheduleActivity.class);
        intent.putExtra(AddScheduleActivity.SCHEDULE_ID, mScheduleId);
        startActivity(intent);
    }
}
