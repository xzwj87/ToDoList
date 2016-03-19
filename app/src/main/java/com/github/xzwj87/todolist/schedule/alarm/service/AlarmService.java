package com.github.xzwj87.todolist.schedule.alarm.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.entity.ScheduleEntity;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JasonWang on 2016/3/6.
 */
public class AlarmService implements AlarmCommandsInterface{
    public static final String LOG_TAG = "AlarmService";

    private AlarmManager mAlarmMgr;
    private Context mContext;
    // an observer to observe the state of alarm(add/delete/cancel)
    private Uri mUri;
    private AlarmObserver mAlarmObserver;
    private HashMap<String,ScheduleEntity> mAlarmSchedule = new HashMap<>();

    public AlarmService(){
        // empty constructor
    }

    public AlarmService(Context context){
        this.mContext = context;
        mAlarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // initialize the observer
        mUri = ScheduleContract.ScheduleEntry.CONTENT_URI;
        mAlarmObserver = new AlarmObserver(context,new Handler());
        mContext.getContentResolver().registerContentObserver(mUri,true,mAlarmObserver);
    }

    @Override
    public void addScheduleEntity(ScheduleEntity entity){
        mAlarmSchedule.put(entity.getTitle(), entity);
    }

    @Override
    public ScheduleEntity getScheduleEntity(String title){
        ScheduleEntity schedule = mAlarmSchedule.get(title);

        return schedule;
    }

    @Override
    public boolean hasAlarm(String title){
        if(mAlarmSchedule.get(title) != null){
            return true;
        }
        return false;
    }

    @Override
    public void setAlarm(String title) {
        Log.v(LOG_TAG, "setAlarm(): title = " + title );

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(ALARM_TYPE_ONE_TIME);
        // put extra information to the intent
        alarmIntent.putExtra(ALARM_TITLE, title);
        // a dummy data to keep consistent extra
        ScheduleEntity entity = mAlarmSchedule.get(title);
        int repeatAlarmInterval = entity.getRepeatAlarmInterval();
        alarmIntent.putExtra(ALARM_REPEAT_INTERVAL,repeatAlarmInterval);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);
        try {
            mAlarmMgr.set(AlarmManager.RTC_WAKEUP, entity.getAlarmTime().getTime(), pendingIntent);
        }catch (Exception e){
            Log.e(LOG_TAG,"setAlarm(): fail to set alarm");
        }
    }

    @Override
    public void setRepeatAlarm(String title){
        Log.e(LOG_TAG, "setRepeatAlarm(): " + title);

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(ALARM_TYPE_REPEAT);
        // put extra information to intent
        ScheduleEntity entity = mAlarmSchedule.get(title);
        alarmIntent.putExtra(ALARM_TITLE, entity.getTitle());
        alarmIntent.putExtra(ALARM_REPEAT_INTERVAL, entity.getRepeatAlarmInterval());

        PendingIntent pi = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);
        try{
            mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, entity.getAlarmTime().getTime(), entity.getRepeatAlarmInterval(), pi);
        }catch (Exception e){
            Log.e(LOG_TAG,"setRepeatAlarm(): fail to set repeating alarm");
        }
    }

    @Override
    public void cancelAlarm(String title,String type) {
        Log.v(LOG_TAG, "cancelAlarm(): type =  " + type + " title = ");
        if(!(type.equals(ALARM_TYPE_ONE_TIME) || type.equals(ALARM_TYPE_REPEAT))){
            Log.e(LOG_TAG,"cancelAlarm(): wrong alarm type");
            return;
        }

        Intent cancelIntent = new Intent(mContext,AlarmReceiver.class);
        cancelIntent.setAction(type);
        // put extra data to intent
        ScheduleEntity entity = mAlarmSchedule.get(title);
        cancelIntent.putExtra(ALARM_TITLE, title);
        cancelIntent.putExtra(ALARM_REPEAT_INTERVAL,entity.getRepeatAlarmInterval());

        PendingIntent sender = PendingIntent.getBroadcast(mContext,0,cancelIntent,0);
        try {
            mAlarmMgr.cancel(sender);
        }catch (Exception e){
            Log.e(LOG_TAG,"cancelAlarm(): fail to cancel the alarm");
        }
    }

    @Override
    public void destroy(){
        // unreigster the observers
        mContext.getContentResolver().unregisterContentObserver(mAlarmObserver);
    }
}
