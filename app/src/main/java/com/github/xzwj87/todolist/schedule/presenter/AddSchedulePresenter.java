package com.github.xzwj87.todolist.schedule.presenter;

import android.support.annotation.NonNull;

import com.github.xzwj87.todolist.schedule.ui.AddScheduleView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.util.Date;

public interface AddSchedulePresenter extends Presenter {

    void setView(@NonNull AddScheduleView view);

    void setStartDate();
    void setEndDate();
    void setStartTime();
    void setEndTime();
    void setAlarmTime();

    void onStartDateSet(int year, int monthOfYear, int dayOfMonth);
    void onEndDateSet(int year, int monthOfYear, int dayOfMonth);
    void onStartTimeSet(int hourOfDay, int minute, int second);
    void onEndTimeSet(int hourOfDay, int minute, int second);
    void onAlarmTimeSet(@ScheduleModel.AlarmType String alarmType, Date alarmTime);

    void onSave();

}
