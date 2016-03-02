package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.ui.adapter.ScheduleAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScheduleListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ScheduleListFragment.class.getSimpleName();

    private static final int SCHEDULE_LOADER = 0;

    private static final String[] SCHEDULE_COLUMNS = {
            ScheduleContract.ScheduleEntry._ID,
            ScheduleContract.ScheduleEntry.COLUMN_TITLE,
            ScheduleContract.ScheduleEntry.COLUMN_DETAIL,
            ScheduleContract.ScheduleEntry.COLUMN_TYPE,
            ScheduleContract.ScheduleEntry.COLUMN_DATE_START,
            ScheduleContract.ScheduleEntry.COLUMN_DATE_END,
            ScheduleContract.ScheduleEntry.COLUMN_REPEAT_SCHEDULE,
            ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME,
            ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES,
            ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL
    };

    public static final int COL_SCHEDULE_ID = 0;
    public static final int COL_SCHEDULE_TITLE = 1;
    public static final int COL_SCHEDULE_DETAIL = 2;
    public static final int COL_SCHEDULE_TYPE = 3;
    public static final int COL_SCHEDULE_DATE_START = 4;
    public static final int COL_SCHEDULE_DATE_END = 5;
    public static final int COL_SCHEDULE_REPEAT_SCHEDULE = 6;
    public static final int COL_SCHEDULE_ALARM_TIME = 7;
    public static final int COL_SCHEDULE_REPEAT_ALARM_TIMES = 8;
    public static final int COL_SCHEDULE_REPEAT_ALARM_INTERVAL = 9;

    public interface Callbacks {
        void onItemSelected(Uri uri, ScheduleAdapter.ViewHolder vh);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Uri uri, ScheduleAdapter.ViewHolder vh) { }
    };

    private Callbacks mCallbacks = sDummyCallbacks;

    @Bind(R.id.rv_schedule_list) RecyclerView mRvScheduleList;

    private ScheduleAdapter mScheduleAdapter;

    public ScheduleListFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_list, container, false);
        ButterKnife.bind(this, rootView);

        setupRecyclerView();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ScheduleContract.ScheduleEntry.COLUMN_DATE_START + " ASC";

        return new CursorLoader(getActivity(),
                ScheduleContract.ScheduleEntry.CONTENT_URI,
                SCHEDULE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished(): size = " + data.getCount());
        mScheduleAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset()");
        mScheduleAdapter.swapCursor(null);
    }

    private void setupRecyclerView() {
        mScheduleAdapter = new ScheduleAdapter();
        mScheduleAdapter.setOnItemClickListener(new ScheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ScheduleAdapter.ViewHolder vh) {
                Log.v(LOG_TAG, "onItemClick(): position = " + position);
                Cursor cursor = mScheduleAdapter.getCursor();
                cursor.moveToPosition(position);
                int id = cursor.getInt(COL_SCHEDULE_ID);
                Uri uri = ScheduleContract.ScheduleEntry.buildScheduleUri(id);

                ((Callbacks) getActivity()).onItemSelected(uri, vh);
            }
        });
        mRvScheduleList.setAdapter(mScheduleAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvScheduleList.setLayoutManager(layoutManager);

        mRvScheduleList.setHasFixedSize(true);
    }


}
