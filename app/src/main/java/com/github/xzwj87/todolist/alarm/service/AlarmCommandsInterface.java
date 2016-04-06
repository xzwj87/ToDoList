package com.github.xzwj87.todolist.alarm.service;

import com.github.xzwj87.todolist.schedule.data.entity.ScheduleEntity;

/**
 * Created by JasonWang on 2016/3/2.
 */
public interface AlarmCommandsInterface {
    public final String ACTION_ONE_TIME_ALARM = "com.github.xzwj87.action.one_time_alarm";
    public final String ACTION_REPEAT_ALARM = "com.github.xzwj87.action.repeat_alarm";

    public final String ALARM_TITLE = "AlarmTitle";
    public final String ALARM_START_TIME = "AlarmStartTime";
    // how long an alarm keeps
    public final String ALARM_DURATION_TIME = "AlarmDurationTime";
    public final String ALARM_REPEAT_INTERVAL = "AlarmRepeatInterval";

    void addScheduleEntity(long id,ScheduleEntity entity);
    void updateScheduleEntity(long id,ScheduleEntity entity);
    void deleteScheduleEntity(long id,ScheduleEntity entity);
    /* whether AlarmService has such alarm */
    boolean hasAlarm(long id);

    void setAlarm(ScheduleEntity entity);
    void setOneTimeAlarm(ScheduleEntity entity);
    void setRepeatAlarm(ScheduleEntity entity);
    void cancelAlarm(ScheduleEntity entity);
}
