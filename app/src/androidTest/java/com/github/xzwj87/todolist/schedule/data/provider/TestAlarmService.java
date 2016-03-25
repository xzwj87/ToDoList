package com.github.xzwj87.todolist.schedule.data.provider;

import android.os.SystemClock;
import android.test.AndroidTestCase;

import com.github.xzwj87.todolist.schedule.alarm.service.AlarmCommandsInterface;
import com.github.xzwj87.todolist.schedule.alarm.service.AlarmService;
import com.github.xzwj87.todolist.schedule.data.entity.ScheduleEntity;

import java.util.Date;

/**
 * Created by JasonWang on 2016/3/5.
 */
public class TestAlarmService extends AndroidTestCase {
    static final String TAG = "TestAlarmService";
    AlarmCommandsInterface mAlarmCommandsInterface;
    ScheduleEntity mSchedule = new ScheduleEntity();

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
        mSchedule.setScheduleRepeatType(ScheduleEntity.SCHEDULE_REPEAT_NONE);

       // mAlarmCommandsInterface.addScheduleEntity(mSchedule);
        mAlarmCommandsInterface.setAlarm(mSchedule);

        mSchedule.setScheduleRepeatType(ScheduleEntity.SCHEDULE_REPEAT_EVERY_DAY);
        mAlarmCommandsInterface.setAlarm(mSchedule);

        SystemClock.sleep(timeInterval * 50);

        //mAlarmCommandsInterface.cancelAlarm(title, startTime, AlarmCommandsInterface.ACTION_ONE_TIME_ALARM);
        mAlarmCommandsInterface.cancelAlarm(mSchedule);
    }



}
