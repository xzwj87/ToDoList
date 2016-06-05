package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.internal.di.component.ScheduleComponent;
import com.github.xzwj87.todolist.schedule.presenter.ScheduleDetailPresenterImpl;
import com.github.xzwj87.todolist.schedule.ui.ScheduleDetailView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.schedule.utility.ScheduleUtility;
import com.github.xzwj87.todolist.share.ScheduleShareActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScheduleDetailFragment extends BaseFragment implements ScheduleDetailView {
    private static final String LOG_TAG = ScheduleDetailFragment.class.getSimpleName();

    public static final String SCHEDULE_ID = "id";

    private long mScheduleId = 0;

    @Inject
    ScheduleDetailPresenterImpl mScheduleDetailPresenter;

    private CollapsingToolbarLayout mAppBarLayout;
    @Bind(R.id.tv_schedule_date) TextView mTvScheduleDate;
    @Bind(R.id.tv_schedule_time) TextView mTvScheduleTime;
    @Bind(R.id.tv_schedule_alarm_time) TextView mTvAlarmTime;
    @Bind(R.id.tv_schedule_type) TextView mTvScheduleType;
    @Bind(R.id.tv_schedule_priority) TextView mTvSchedulePriority;
    @Bind(R.id.tv_schedule_note) TextView mTvScheduleNote;

    public ScheduleDetailFragment() {
    }

    public static ScheduleDetailFragment newInstance(long scheduleId) {
        ScheduleDetailFragment fragment = new ScheduleDetailFragment();

        Bundle args = new Bundle();
        args.putLong(SCHEDULE_ID, scheduleId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mAppBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_detail, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mScheduleId = arguments.getLong(SCHEDULE_ID);
            Log.v(LOG_TAG, "onCreateView(): mScheduleId = " + mScheduleId);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            initialize();
        } else {
            loadScheduleData();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                mScheduleDetailPresenter.onDeleteSchedule(false);
                return true;
            case R.id.action_share:
                doShare();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScheduleDetailPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScheduleDetailPresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScheduleDetailPresenter.destroy();
    }

    @Override
    public void requestConfirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.delete_schedule_confirm_message));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mScheduleDetailPresenter.onDeleteSchedule(true);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void finishView() {
        getActivity().finish();
    }

    @Override
    public void updateScheduleTitle(String title) {
        mAppBarLayout.setTitle(title);
    }

    @Override
    public void updateScheduleDate(String datePeriod) {
        mTvScheduleDate.setText(datePeriod);
    }

    @Override
    public void updateScheduleTime(String timePeriod) {
        mTvScheduleTime.setText(timePeriod);
    }

    @Override
    public void updateAlarmTime(String time) {
        mTvAlarmTime.setText(time);
    }

    @Override
    public void updateScheduleType(String type) {
        mTvScheduleType.setText(type);
    }

    @Override
    public void updateScheduleNote(String note) {
        mTvScheduleNote.setText(note);
    }

    @Override
    public void updateSchedulePriority(int priority) {
        mTvSchedulePriority.setText(ScheduleUtility.getSchedulePriorityText(priority));
    }

    private void initialize() {
        getComponent(ScheduleComponent.class).inject(this);

        mScheduleDetailPresenter.setView(this);

        loadScheduleData();
    }

    private void loadScheduleData() {
        if (mScheduleDetailPresenter != null)
            mScheduleDetailPresenter.initialize();
    }

    /* share something with other social Apps */
    /* Todo: use Presenter to get the data instead of resolver */
    public void doShare(){
        Log.v(LOG_TAG, "doShare()");
        Intent intent = new Intent(getContext(), ScheduleShareActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        /* put schedule data to it */
        Uri uri = ScheduleContract.ScheduleEntry.buildScheduleUri(mScheduleId);
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();

        ScheduleModelDataMapper mapper = new ScheduleModelDataMapper();
        ScheduleModel schedule = mapper.transform(cursor);

        Bundle bundle = new Bundle();
        bundle.putString(ScheduleContract.ScheduleEntry.COLUMN_TITLE, mAppBarLayout.getTitle().toString());
        bundle.putLong(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME,
                schedule.getAlarmTime().getTime());
        if(schedule.getDoneStatus().equals(ScheduleModel.DONE)){
            bundle.putBoolean(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE,true);
        }else{
            bundle.putBoolean(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE,false);
        }

        intent.putExtras(bundle);
        startActivity(intent);
    }

}
