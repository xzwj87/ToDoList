package com.github.xzwj87.todolist.schedule.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.ui.adapter.ScheduleAdapter;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleDetailFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleListFragment;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduleListActivity extends AppCompatActivity
        implements ScheduleListFragment.
        Callbacks, NavigationView.OnNavigationItemSelectedListener {
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        mTwoPane = findViewById(R.id.schedule_detail_container) != null;
        Log.v(LOG_TAG, "onCreate(): mTwoPane = " + mTwoPane);

        if (savedInstanceState == null) {
            ScheduleListFragment fragment = ScheduleListFragment.newInstance(null);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.schedule_list_container, fragment)
                    .commit();
        }

        handleIntent(getIntent());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_schedule_type_all) {
            Log.v(LOG_TAG, "onNavigationItemSelected(): nav_schedule_type_all");
            replaceScheduleListWithType(null);
        } else if (id == R.id.nav_schedule_type_meeting) {
            Log.v(LOG_TAG, "onNavigationItemSelected(): nav_schedule_type_meeting");
            replaceScheduleListWithType(ScheduleModel.SCHEDULE_TYPE_MEETING);
        } else if (id == R.id.nav_schedule_type_entertainment) {
            Log.v(LOG_TAG, "onNavigationItemSelected(): nav_schedule_type_entertainment");
            replaceScheduleListWithType(ScheduleModel.SCHEDULE_TYPE_ENTERTAINMENT);
        } else if (id == R.id.nav_date) {
            Log.v(LOG_TAG, "onNavigationItemSelected(): nav_date");
            replaceScheduleListWithType(ScheduleModel.SCHEDULE_TYPE_DATE);
        } else if (id == R.id.nav_settings) {
            Log.v(LOG_TAG, "onNavigationItemSelected(): nav_settings");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_schedule_list, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.v(LOG_TAG, "onNewIntent()");
        handleIntent(intent);
    }

    @SuppressWarnings("unused")
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

    private void replaceScheduleListWithType(String scheduleType) {
        ScheduleListFragment fragment = ScheduleListFragment.newInstance(scheduleType);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.schedule_list_container, fragment)
                .commit();
    }

    private void handleIntent(Intent intent) {
        Log.v(LOG_TAG, "handleIntent()");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v(LOG_TAG, "handleIntent(): query = " + query);

            Intent searchActivityIntent = new Intent(this, SearchScheduleActivity.class);
            searchActivityIntent.putExtra(SearchScheduleActivity.QUERY, query);
            startActivity(searchActivityIntent);
        }
    }
}
