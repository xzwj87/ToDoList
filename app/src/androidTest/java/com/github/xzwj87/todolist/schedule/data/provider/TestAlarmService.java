package com.github.xzwj87.todolist.schedule.data.provider;

import android.os.SystemClock;
import android.test.AndroidTestCase;

import com.github.xzwj87.todolist.schedule.alarm.service.AlarmCommandsInterface;
import com.github.xzwj87.todolist.schedule.alarm.service.AlarmService;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.util.Date;

/**
 * Created by JasonWang on 2016/3/5.
 */
public class TestAlarmService extends AndroidTestCase {
    static final String TAG = "TestAlarmService";
    AlarmCommandsInterface mAlarmCommandsInterface;
    ScheduleModel mSchedule = new ScheduleModel();

    String title = "you got to do this!";
    long timeInterval = 5*1000;
    long currentTime = System.currentTimeMillis();
    long startTime = currentTime + timeInterval;
    int repeatInterval = 5*1000;
    int duration = 30*1000;

    public void testAlarmReceiver(){
        mAlarmCommandsInterface = new AlarmService(getContext());
        mSchedule.setTitle(title);
        mSchedule.setAlarmTime(new Date(startTime));
        mSchedule.setRepeatAlarmInterval(repeatInterval);

        mAlarmCommandsInterface.addScheduleEntity(mSchedule);
        mAlarmCommandsInterface.setAlarm();
        mAlarmCommandsInterface.setRepeatAlarm();

        SystemClock.sleep(timeInterval * 5);

        //mAlarmCommandsInterface.cancelAlarm(title, startTime, AlarmCommandsInterface.ALARM_TYPE_ONE_TIME);
        mAlarmCommandsInterface.cancelAlarm(AlarmCommandsInterface.ALARM_TYPE_REPEAT);
    }



}
