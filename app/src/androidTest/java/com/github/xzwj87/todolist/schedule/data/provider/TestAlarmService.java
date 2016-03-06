package com.github.xzwj87.todolist.schedule.data.provider;

import android.os.SystemClock;
import android.test.AndroidTestCase;

import com.github.xzwj87.todolist.schedule.service.alarm.AlarmReceiver;
import com.github.xzwj87.todolist.schedule.service.alarm.AlarmCommandsInterface;
import com.github.xzwj87.todolist.schedule.service.alarm.AlarmService;

/**
 * Created by JasonWang on 2016/3/5.
 */
public class TestAlarmService extends AndroidTestCase {
    static final String TAG = "TestAlarmService";
    AlarmCommandsInterface mAlarmCommandsInterface;

    String title = "you got to do this!";
    long timeInterval = 5*1000;
    long currentTime = System.currentTimeMillis();
    long startTime = currentTime + timeInterval;
    int repeatInterval = 5*1000;
    int duration = 30*1000;

    public void testAlarmReceiver(){
        mAlarmCommandsInterface = new AlarmService(getContext());
        mAlarmCommandsInterface.setAlarm(title, startTime,0,duration);
        mAlarmCommandsInterface.setRepeatAlarm(title, startTime + repeatInterval, repeatInterval,duration);

        SystemClock.sleep(timeInterval * 5);

        //mAlarmCommandsInterface.cancelAlarm(title, startTime, AlarmCommandsInterface.ALARM_TYPE_ONE_TIME);
        mAlarmCommandsInterface.cancelAlarm(title,startTime,repeatInterval,duration,AlarmCommandsInterface.ALARM_TYPE_REPEAT);
    }



}
