package com.github.xzwj87.todolist.schedule.data.provider;

import android.content.Context;
import android.test.AndroidTestCase;

import com.github.xzwj87.todolist.schedule.service.alarm.AlarmReceiver;
import com.github.xzwj87.todolist.schedule.service.alarm.AlarmService;

/**
 * Created by JasonWang on 2016/3/5.
 */
public class TestAlarmReceiver extends AndroidTestCase {
    static final String TAG = "TestAlarmReceiver";
    AlarmService mAlarmService;

    String title = "you got to do this!";
    long timeInterval = 5*1000;
    long currentTime = System.currentTimeMillis();
    long startTime = currentTime + timeInterval;
    int repeatInterval = 5*1000;

    public void testAlarmReceiver(){
        mAlarmService = new AlarmReceiver(getContext());
        mAlarmService.setAlarm(title,startTime);
        mAlarmService.setRepeatAlarm(title,startTime+repeatInterval,repeatInterval);
    }



}
