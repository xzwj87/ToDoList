package com.github.xzwj87.todolist.schedule.alarm.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.entity.ScheduleEntity;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by JasonWang on 2016/3/6.
 */
public class AlarmService implements AlarmCommandsInterface{
    public static final String LOG_TAG = "AlarmService";

    private AlarmManager mAlarmMgr;
    private Context mContext;
    private volatile static AlarmService mInstance;
    // an observer to observe the state of alarm(add/delete/cancel)
    static HashMap<Long,ScheduleEntity> AlarmSchedule = new HashMap<>();

    public AlarmService(){
        // empty constructor
    }

    public static AlarmService getInstance(Context context){
        if(mInstance == null){
            synchronized (AlarmService.class) {
                if(mInstance == null)
                    mInstance = new AlarmService(context);
            }
        }

        return mInstance;
    }


    public AlarmService(Context context){
        this.mContext = context;
        mAlarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void addScheduleEntity(long id,ScheduleEntity entity){
        AlarmSchedule.put(id, entity);
    }

    @Override
    public void updateScheduleEntity(long id,ScheduleEntity entity){
        Iterator iterator = AlarmSchedule.entrySet().iterator();
        HashMap.Entry entry;
        while(iterator.hasNext()) {
            entry = (HashMap.Entry)iterator.next();
            if(Long.getLong(entry.getKey().toString()) == id){
                entry.setValue(entity);
                break;
            }
        }
    }

    @Override
    public void deleteScheduleEntity(long id,ScheduleEntity entity){
        AlarmSchedule.remove(id);
    }

    @Override
    public boolean hasAlarm(long id){
        if(AlarmSchedule.get(id) != null){
            return true;
        }
        return false;
    }

    @Override
    public void setAlarm(ScheduleEntity entity){
        Log.d(LOG_TAG, "setAlarm(): title = " + entity.getTitle()
                + " alarmType = " +  entity.getType() + " alarmTime = " + entity.getAlarmTime());

       // String repeatType = entity.getScheduleRepeatType();
        // just for test; need to change if condition in future
        if(true){
            //ACTION_ONE_TIME_ALARM;
            setOneTimeAlarm(entity);
            //entity.setRepeatAlarmInterval(10 * 1000);
            //setRepeatAlarm(entity);
        }else{
            setRepeatAlarm(entity);
        }
    }

    @Override
    public void setOneTimeAlarm(ScheduleEntity entity) {
        Log.v(LOG_TAG, "setOneTimeAlarm(): title = " + entity.getTitle());

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(ACTION_ONE_TIME_ALARM);
        // put extra information to the intent
        alarmIntent.putExtra(ALARM_TITLE, entity.getTitle());
        alarmIntent.putExtra(ALARM_START_TIME,entity.getAlarmTime().getTime());
        // a dummy data to keep consistent extra
        int repeatAlarmInterval = entity.getRepeatAlarmInterval();
        alarmIntent.putExtra(ALARM_REPEAT_INTERVAL,repeatAlarmInterval);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);
        try {
            mAlarmMgr.set(AlarmManager.RTC_WAKEUP, entity.getAlarmTime().getTime(), pendingIntent);
        }catch (Exception e){
            Log.e(LOG_TAG,"setOneTimeAlarm(): fail to set alarm");
        }
    }

    @Override
    public void setRepeatAlarm(ScheduleEntity entity) {
        Log.e(LOG_TAG, "setRepeatAlarm(): " + entity.getTitle());

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(ACTION_REPEAT_ALARM);
        // put extra information to intent
        alarmIntent.putExtra(ALARM_TITLE, entity.getTitle());
        alarmIntent.putExtra(ALARM_START_TIME,entity.getAlarmTime());
        alarmIntent.putExtra(ALARM_REPEAT_INTERVAL, entity.getRepeatAlarmInterval());

        PendingIntent pi = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);
        try{
            mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, entity.getAlarmTime().getTime(), entity.getRepeatAlarmInterval(), pi);
        }catch (Exception e){
            Log.e(LOG_TAG,"setRepeatAlarm(): fail to set repeat alarm");
        }
    }

    @Override
    public void cancelAlarm(ScheduleEntity entity) {
        Log.v(LOG_TAG, "cancelAlarm(): type =  " + entity.getType() + " title = " + entity.getTitle());
        String type = entity.getType();
        String alarmType;
        if(type == ScheduleEntity.SCHEDULE_REPEAT_NONE){
            alarmType = ACTION_ONE_TIME_ALARM;
        }else{ alarmType = ACTION_REPEAT_ALARM; }

        Intent cancelIntent = new Intent(mContext,AlarmReceiver.class);
        cancelIntent.setAction(alarmType);
        // put extra data to intent
        cancelIntent.putExtra(ALARM_TITLE, entity.getTitle());
        cancelIntent.putExtra(ALARM_START_TIME,entity.getAlarmTime());
        cancelIntent.putExtra(ALARM_REPEAT_INTERVAL,entity.getRepeatAlarmInterval());

        PendingIntent sender = PendingIntent.getBroadcast(mContext,0,cancelIntent,0);
        try {
            mAlarmMgr.cancel(sender);
        }catch (Exception e){
            Log.e(LOG_TAG,"cancelAlarm(): fail to cancel the alarm");
        }
    }
}
