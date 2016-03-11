package com.github.xzwj87.todolist.schedule.ui;

import android.content.Context;

import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.util.Date;

public interface AddScheduleView {

    void showPickStartDateDlg(int year, int monthOfYear, int dayOfMonth);
    void showPickEndDateDlg(int year, int monthOfYear, int dayOfMonth);
    void showPickStartTimeDlg(int hourOfDay, int minute, int second);
    void showPickEndTimeDlg(int hourOfDay, int minute, int second);
    void showPickAlarmTimeDlg(@ScheduleModel.AlarmType String alarmType, Date alarmTime);

    void updateStartDateDisplay(String startDate);
    void updateEndDateDisplay(String endDate);
    void updateStartTimeDisplay(String startTime);
    void updateEndTimeDisplay(String endTime);
    void updateAlarmTimeDisplay(String alarmTime);

    String getScheduleTitle();
    // TODO: Inject context to Presenter
    Context getViewContext();
}
