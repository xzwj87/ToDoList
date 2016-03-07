package com.github.xzwj87.todolist.schedule.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.ui.adapter.ScheduleAdapter;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleDetailFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleListFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduleListActivity extends AppCompatActivity
        implements ScheduleListFragment.Callbacks {
    private static final String LOG_TAG = ScheduleListActivity.class.getSimpleName();

    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.schedule_detail_container) != null) {
            mTwoPane = true;
            Log.v(LOG_TAG, "onCreate(): mTwoPane = " + mTwoPane);
        }
    }

    @OnClick(R.id.fab)
    public void pickStartDate(View view) {
        Intent intent = new Intent(ScheduleListActivity.this, AddScheduleActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(long id, ScheduleAdapter.ViewHolder vh) {
        Log.v(LOG_TAG, "onItemSelected(): id = " + id);

        if (mTwoPane) {
            ScheduleDetailFragment fragment = ScheduleDetailFragment.newInstance(id);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.schedule_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, ScheduleDetailActivity.class);
            intent.putExtra(ScheduleDetailActivity.SCHEDULE_ID, id);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.v(LOG_TAG, "onOptionsItemSelected(): item = " + item);
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
