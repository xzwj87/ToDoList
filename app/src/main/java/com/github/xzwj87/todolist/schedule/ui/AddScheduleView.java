package com.github.xzwj87.todolist.schedule.ui;

import android.content.Context;

import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

public interface AddScheduleView {

    void showPickStartDateDlg(int year, int monthOfYear, int dayOfMonth);
    void showPickEndDateDlg(int year, int monthOfYear, int dayOfMonth);
    void showPickStartTimeDlg(int hourOfDay, int minute, int second);
    void showPickEndTimeDlg(int hourOfDay, int minute, int second);
    void showPickAlarmTypeDlg(@ScheduleModel.AlarmType String alarmType);
    void showPickScheduleTypeDlg(@ScheduleModel.ScheduleType String scheduleType);
    void showMessageDialog(String title, String message);
    void showErrorIndicationOnStartTime(boolean isError);

    void updateScheduleTitle(String title);
    void updateScheduleNote(String note);
    void updateStartDateDisplay(String startDate);
    void updateEndDateDisplay(String endDate);
    void updateStartTimeDisplay(String startTime);
    void updateEndTimeDisplay(String endTime);
    void updateAlarmTypeDisplay(String alarmTypeText);
    void updateScheduleTypeDisplay(String scheduleTypeText);

    void finishView();
    String getScheduleTitle();
    // TODO: Inject context to Presenter
    Context getViewContext();
}
