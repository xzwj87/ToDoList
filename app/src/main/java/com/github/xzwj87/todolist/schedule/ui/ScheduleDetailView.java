package com.github.xzwj87.todolist.schedule.ui;

import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

public interface ScheduleDetailView {

    void updateScheduleTitle(String title);
    void updateScheduleDate(String datePeriod);
    void updateScheduleTime(String timePeriod);
    void updateAlarmTime(String time);
    void updateScheduleType(String type);
    void updateScheduleNote(String note);
    void updateSchedulePriority(int priority);

    void requestConfirmDelete();
    void finishView();
}
