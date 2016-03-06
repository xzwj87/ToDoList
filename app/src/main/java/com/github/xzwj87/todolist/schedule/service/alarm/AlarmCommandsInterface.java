package com.github.xzwj87.todolist.schedule.service.alarm;

import android.content.Context;

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

    void setAlarm(String title, long alarmTime,int interval,int duration);
    void setRepeatAlarm(String title,long firstTime,int interval,int duration);
    void cancelAlarm(String title, long alarmTime,int interval,int duration,String type);
}
