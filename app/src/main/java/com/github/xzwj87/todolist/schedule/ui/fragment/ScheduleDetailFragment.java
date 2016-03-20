package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.interactor.GetScheduleDetail;
import com.github.xzwj87.todolist.schedule.interactor.QueryUseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.presenter.ScheduleDetailPresenter;
import com.github.xzwj87.todolist.schedule.presenter.ScheduleDetailPresenterImpl;
import com.github.xzwj87.todolist.schedule.ui.ScheduleDetailView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScheduleDetailFragment extends Fragment implements ScheduleDetailView {
    private static final String LOG_TAG = ScheduleDetailFragment.class.getSimpleName();

    public static final String SCHEDULE_ID = "id";

    private long mScheduleId = 0;
    private ScheduleDetailPresenter mScheduleDetailPresenter;

    private CollapsingToolbarLayout mAppBarLayout;
    @Bind(R.id.tv_schedule_date) TextView mTvScheduleDate;
    @Bind(R.id.tv_schedule_time) TextView mTvScheduleTime;
    @Bind(R.id.tv_schedule_alarm_time) TextView mTvAlarmTime;
    @Bind(R.id.tv_schedule_type) TextView mTvScheduleType;
    @Bind(R.id.tv_schedule_note) TextView mTvScheduleNote;

    public ScheduleDetailFragment() { }

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
        initialize();
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


    private void initialize() {
        QueryUseCase useCase = new GetScheduleDetail(mScheduleId);
        ScheduleModelDataMapper mapper = new ScheduleModelDataMapper();
        mScheduleDetailPresenter = new ScheduleDetailPresenterImpl(useCase, mapper);
        mScheduleDetailPresenter.setView(this);

        loadScheduleData();
    }
    private void loadScheduleData() {
        mScheduleDetailPresenter.initialize();
    }
}
