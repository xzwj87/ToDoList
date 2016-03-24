package com.github.xzwj87.todolist.schedule.ui.activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
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
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleSuggestionProvider;
import com.github.xzwj87.todolist.schedule.ui.adapter.ScheduleAdapter;
import com.github.xzwj87.todolist.schedule.ui.adapter.SearchSuggestionAdapter;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleDetailFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleListFragment;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.schedule.utility.ScheduleSuggestionUtility;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduleListActivity extends AppCompatActivity
        implements ScheduleListFragment.
        Callbacks, NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = ScheduleListActivity.class.getSimpleName();

    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment";
    private static final String SEARCH_RESULT_FRAGMENT_TAG = "search_result_fragment";
    private boolean mTwoPane;
    private String mTypeFilter;
    private SearchSuggestionAdapter mSuggestionAdapter;

    @Bind(R.id.fab) FloatingActionButton mFab;

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
            ScheduleListFragment fragment = ScheduleListFragment.newInstanceByType(null);
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
            mTypeFilter = null;
            replaceScheduleListWithType(mTypeFilter);
        } else if (id == R.id.nav_schedule_type_meeting) {
            Log.v(LOG_TAG, "onNavigationItemSelected(): nav_schedule_type_meeting");
            mTypeFilter = ScheduleModel.SCHEDULE_TYPE_MEETING;
            replaceScheduleListWithType(mTypeFilter);
        } else if (id == R.id.nav_schedule_type_entertainment) {
            Log.v(LOG_TAG, "onNavigationItemSelected(): nav_schedule_type_entertainment");
            mTypeFilter = ScheduleModel.SCHEDULE_TYPE_ENTERTAINMENT;
            replaceScheduleListWithType(mTypeFilter);
        } else if (id == R.id.nav_date) {
            Log.v(LOG_TAG, "onNavigationItemSelected(): nav_date");
            mTypeFilter = ScheduleModel.SCHEDULE_TYPE_DATE;
            replaceScheduleListWithType(mTypeFilter);
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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        setupSearchView(searchItem);

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
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
        ScheduleListFragment fragment = ScheduleListFragment.newInstanceByType(scheduleType);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.schedule_list_container, fragment)
                .commit();
        mFab.show();
    }

    private void replaceScheduleListWithSearchResult(String query) {
        ScheduleListFragment fragment = ScheduleListFragment.newInstanceByQuery(query);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.schedule_list_container, fragment, SEARCH_RESULT_FRAGMENT_TAG)
                .commit();
        mFab.hide();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v(LOG_TAG, "handleIntent(): query = " + query);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    ScheduleSuggestionProvider.AUTHORITY, ScheduleSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            replaceScheduleListWithSearchResult(query);
        }
    }

    private void setupSearchView(MenuItem searchItem) {
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        Log.v(LOG_TAG, "onMenuItemActionCollapse()");
                        replaceScheduleListWithType(mTypeFilter);
                        return true;
                    }
                });

        mSuggestionAdapter = new SearchSuggestionAdapter(this, null, 0);
        searchView.setSuggestionsAdapter(mSuggestionAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor cursor = ScheduleSuggestionUtility.getRecentSuggestions(
                        ScheduleListActivity.this, newText, 10);
                mSuggestionAdapter.swapCursor(cursor);
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                searchView.setQuery(mSuggestionAdapter.getSuggestionText(position), true);
                return true;
            }
        });
    }
}
