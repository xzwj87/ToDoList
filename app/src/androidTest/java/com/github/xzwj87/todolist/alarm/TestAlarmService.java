package com.github.xzwj87.todolist.alarm;

import android.os.SystemClock;
import android.test.AndroidTestCase;

import com.github.xzwj87.todolist.alarm.service.AlarmCommandsInterface;
import com.github.xzwj87.todolist.alarm.service.AlarmService;
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
        mSchedule.setScheduleRepeatType(ScheduleModel.SCHEDULE_REPEAT_NONE);

       // mAlarmCommandsInterface.addScheduleItem(mSchedule);
        mAlarmCommandsInterface.setAlarm(mSchedule);

        mSchedule.setScheduleRepeatType(ScheduleModel.SCHEDULE_REPEAT_EVERY_DAY);
        mAlarmCommandsInterface.setAlarm(mSchedule);

        SystemClock.sleep(timeInterval * 50);

        //mAlarmCommandsInterface.cancelAlarm(title, startTime, AlarmCommandsInterface.ACTION_ONE_TIME_ALARM);
        mAlarmCommandsInterface.cancelAlarm(mSchedule);
    }



}
