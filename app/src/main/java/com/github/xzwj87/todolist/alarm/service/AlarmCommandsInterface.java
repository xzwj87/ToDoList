package com.github.xzwj87.todolist.alarm.service;

import android.content.Context;

import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

/**
 * Created by JasonWang on 2016/3/2.
 */
public interface AlarmCommandsInterface {
    public final String ACTION_ONE_TIME_ALARM = "com.github.xzwj87.action.one_time_alarm";
    public final String ACTION_REPEAT_ALARM = "com.github.xzwj87.action.repeat_alarm";

    void addAlarmSchedule(ScheduleModel item);
    void updateAlarmSchedule(ScheduleModel item);
    void deleteAlarmSchedule(ScheduleModel item);
    /* whether AlarmService has such alarm */
    boolean hasAlarm(long id);

    void setAlarm(ScheduleModel item);
    void setOneTimeAlarm(ScheduleModel item);
    void setRepeatAlarm(ScheduleModel item);
    void cancelAlarm(ScheduleModel item);
}
