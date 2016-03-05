package com.github.xzwj87.todolist.schedule.service.alarm;

import android.content.Context;

/**
 * Created by JasonWang on 2016/3/2.
 */
public interface AlarmService {
    public final String ALARM_TYPE_ONE_TIME = "OneTimeAlarm";
    public final String ALARM_TYPE_REPEAT = "RepeatAlarm";

    void setAlarm(String title, long alarmTime);
    void setRepeatAlarm(String title,long firstTime,int interval);
    void cancelAlarm(String title, long alarmTime,String type);
}
