package com.github.xzwj87.todolist.schedule.alarm.service;

import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

/**
 * Created by JasonWang on 2016/3/2.
 */
public interface AlarmCommandsInterface {
    public final String ALARM_TYPE_ONE_TIME = "OneTimeAlarm";
    public final String ALARM_TYPE_REPEAT = "RepeatAlarm";

    public final String ALARM_TITLE = "AlarmTitle";
    public final String ALARM_START_TIME = "AlarmStartTime";
    // how long an alarm keeps
    public final String ALARM_DURATION_TIME = "AlarmDurationTime";
    public final String ALARM_REPEAT_INTERVAL = "AlarmRepeatInterval";

    void addScheduleEntity(ScheduleModel entity);
    ScheduleModel getScheduleEntity(String title);
    void setAlarm();
    void setRepeatAlarm();
    void cancelAlarm(String type);
    void onDestroy();
}
