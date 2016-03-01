package com.github.xzwj87.todolist.schedule.presenter;

import android.support.annotation.NonNull;

import com.github.xzwj87.todolist.schedule.ui.AddScheduleView;

public interface AddSchedulePresenter extends presenter {

    void setView(@NonNull AddScheduleView view);

    void setStartDate();
    void setEndDate();
    void setStartTime();
    void setEndTime();

    void onStartDateSet(int year, int monthOfYear, int dayOfMonth);
    void onEndDateSet(int year, int monthOfYear, int dayOfMonth);
    void onStartTimeSet(int hourOfDay, int minute, int second);
    void onEndTimeSet(int hourOfDay, int minute, int second);

    void onSave();

}
