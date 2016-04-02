package com.github.xzwj87.todolist.schedule.presenter;

import android.support.annotation.NonNull;

import com.github.xzwj87.todolist.schedule.ui.ScheduleDetailView;

public interface ScheduleDetailPresenter extends Presenter {

    void setView(@NonNull ScheduleDetailView view);

    void initialize();

    void onDeleteSchedule(boolean isConfirmed);

}
