package com.github.xzwj87.todolist.schedule.alarm.service;

import android.content.Context;

import com.github.xzwj87.todolist.schedule.data.entity.ScheduleEntity;

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

    void addScheduleEntity(ScheduleEntity entity);
    ScheduleEntity getScheduleEntity(String title);
    void setAlarm(String title);
    void setRepeatAlarm(String title);
    void cancelAlarm(String title,String type);
    /* whether AlarmService has such alarm */
    boolean hasAlarm(String title);
    void destroy();
}
