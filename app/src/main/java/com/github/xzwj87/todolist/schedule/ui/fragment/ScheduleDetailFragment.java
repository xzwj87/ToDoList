package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.os.Bundle;
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

    @Bind(R.id.tv_schedule_detail) TextView mTvScheduleDetail;

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

//        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//        }

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
    public void renderSchedule(ScheduleModel schedule) {
        String text = "Schedule id: " + schedule.getId() + ", title = " + schedule.getTitle();
        mTvScheduleDetail.setText(text);
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
