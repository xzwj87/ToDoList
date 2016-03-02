package com.github.xzwj87.todolist.schedule.service.alarm;

import android.content.Context;

/**
 * Created by JasonWang on 2016/3/2.
 */
public interface AlarmService {
    void setAlarm(String title, long alarmTime);
    void cancelAlarm(String title, long alarmTime);
}
