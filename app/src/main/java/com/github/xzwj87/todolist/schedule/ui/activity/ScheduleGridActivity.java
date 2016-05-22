package com.github.xzwj87.todolist.schedule.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.internal.di.HasComponent;
import com.github.xzwj87.todolist.schedule.internal.di.component.DaggerAppComponent;
import com.github.xzwj87.todolist.schedule.internal.di.component.DaggerScheduleComponent;
import com.github.xzwj87.todolist.schedule.internal.di.component.ScheduleComponent;
import com.github.xzwj87.todolist.schedule.internal.di.module.ScheduleModule;
import com.github.xzwj87.todolist.schedule.ui.adapter.ScheduleGridAdapter;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleDetailFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleGridFragment;

import javax.inject.Inject;

/**
 * Created by JasonWang on 2016/5/14.
 */

public class ScheduleGridActivity extends BaseActivity
            implements ScheduleGridFragment.GridCallBacks,HasComponent<ScheduleComponent>{
    public static final String LOG_TAG = "ScheduleGridActivity";

    private String SCHEDULE_GRID_DETAIL_FRAGMENT = "ScheduleGridDetail";
    private ScheduleComponent mScheduleComponent = null;
    private boolean mDualPanel = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG,"onCreate()");
        setContentView(R.layout.activity_schedule_grid);

        Intent intent = getIntent();
        String scheduleType = intent.getStringExtra(ScheduleListActivity.SCHEDULE_TYPE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.grid_view_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        mDualPanel = findViewById(R.id.schedule_grid_detail_container) != null;
        Log.v(LOG_TAG,"onCreate(): mDualPanel = " + mDualPanel);
        if(savedInstanceState == null){
            ScheduleGridFragment fragment = ScheduleGridFragment.getInstanceByType(scheduleType);
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.schedule_grid_container,fragment)
                                       .commit();
        }

        //Window window = getWindow();

        initializeInjector();
    }

    @Override
    public void onItemSelected(long id, ScheduleGridAdapter.GridViewHolder vh) {
        Log.v(LOG_TAG,"onItemSelected(): id " + id);
        if(mDualPanel){
            ScheduleDetailFragment fragment = ScheduleDetailFragment.newInstance(id);
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.schedule_grid_container,fragment,SCHEDULE_GRID_DETAIL_FRAGMENT)
                                       .commit();
         }else{
            Intent intent = new Intent(this,ScheduleDetailActivity.class);
            intent.putExtra(ScheduleDetailActivity.SCHEDULE_ID,id);
            intent.putExtra(ScheduleDetailActivity.PARENT_TAG,
                    ScheduleGridFragment.LOG_TAG);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_schedule_grid, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        int id  = menu.getItemId();
        Intent intent = null;
        if(id == android.R.id.home || id == R.id.action_more){
            intent = new Intent(this,ScheduleListActivity.class);
            NavUtils.navigateUpTo(this, new Intent(this, ScheduleListActivity.class));
        }else if(id == R.id.action_add){
            intent = new Intent(this,AddScheduleActivity.class);
            intent.putExtra(AddScheduleActivity.PARENT_TAG,LOG_TAG);
            startActivity(intent);
        }


        return true;
    }

    @Override
    public ScheduleComponent getComponent() {
        return mScheduleComponent;
    }

    private void initializeInjector(){
        mScheduleComponent = DaggerScheduleComponent.builder()
                            .appComponent(getApplicationComponent())
                            .activityModule(getActivityModule())
                            .scheduleModule(new ScheduleModule())
                            .build();

        mScheduleComponent.inject(this);
    }
}
