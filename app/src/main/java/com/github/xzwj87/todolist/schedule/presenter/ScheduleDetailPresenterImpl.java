package com.github.xzwj87.todolist.schedule.presenter;


import android.support.annotation.NonNull;

import com.github.xzwj87.todolist.schedule.ui.ScheduleDetailView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

public class ScheduleDetailPresenterImpl implements ScheduleDetailPresenter {
    private static final String LOG_TAG = ScheduleDetailPresenterImpl.class.getSimpleName();

    private static final int INVALID_ID = -1;

    private ScheduleDetailView mScheduleDetailView;
    private int mScheduleId = INVALID_ID;

    @Override
    public void setView(@NonNull ScheduleDetailView view) {
        mScheduleDetailView = view;
    }

    @Override
    public void setScheduleId(int id) {
        mScheduleId = id;
    }

    @Override
    public void initialize() {
        if (mScheduleId != INVALID_ID) {
            mScheduleDetailView.renderSchedule(getSchedule(mScheduleId));
        }
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        mScheduleDetailView = null;
    }

    private ScheduleModel getSchedule(int id) {
        ScheduleModel dummy = new ScheduleModel();
        dummy.setId(id);
        return dummy;
    }
}
