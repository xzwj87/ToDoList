package com.github.xzwj87.todolist.settings;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.ui.activity.ScheduleListActivity;

public class SettingsActivity extends AppCompatActivity {
    public static final String LOG_TAG = "SettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        actionBar.setTitle(getResources().getString(R.string.action_settings));

        if(savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.v(LOG_TAG,"onOptionsItemSelected()");

        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, ScheduleListActivity.class);
            NavUtils.navigateUpTo(this,intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
