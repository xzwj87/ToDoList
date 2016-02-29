package com.github.xzwj87.todolist.schedule.ui;

public interface AddScheduleView {

    void showPickStartDateDlg(int year, int monthOfYear, int dayOfMonth);
    void showPickStartTimeDlg(int hourOfDay, int minute, int second);
    void showPickEndDateDlg(int year, int monthOfYear, int dayOfMonth);
    void showPickEndTimeDlg(int hourOfDay, int minute, int second);

    void onStartDateSet(int year, int monthOfYear, int dayOfMonth);
    void onStartTimeSet(int hourOfDay, int minute, int second);
    void onEndDateSet(int year, int monthOfYear, int dayOfMonth);
    void onEndTimeSet(int hourOfDay, int minute, int second);

    void onSave();

}
