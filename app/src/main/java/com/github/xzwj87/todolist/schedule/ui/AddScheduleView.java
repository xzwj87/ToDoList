package com.github.xzwj87.todolist.schedule.ui;

import android.content.Context;

public interface AddScheduleView {

    void showPickStartDateDlg(int year, int monthOfYear, int dayOfMonth);
    void showPickEndDateDlg(int year, int monthOfYear, int dayOfMonth);
    void showPickStartTimeDlg(int hourOfDay, int minute, int second);
    void showPickEndTimeDlg(int hourOfDay, int minute, int second);

    void updateStartDateDisplay(String startDate);
    void updateEndDateDisplay(String endDate);
    void updateStartTimeDisplay(String startTime);
    void updateEndTimeDisplay(String endTime);

    String getScheduleTitle();
    // TODO: Inject context to presenter
    Context getViewContext();
}
