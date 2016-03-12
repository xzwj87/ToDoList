package com.github.xzwj87.todolist.schedule.alarm.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.util.HashMap;

/**
 * Created by JasonWang on 2016/3/6.
 */
public class AlarmService implements AlarmCommandsInterface{
    public static final String LOG_TAG = "AlarmService";

    private AlarmManager mAlarmMgr;
    private Context mContext;
    private ScheduleModel mSchedule;
    // an observer to observe the state of alarm(add/delete/cancel)
    private Uri mUri;
    private AlarmObserver mAlarmObserver;
    private HashMap<String,ScheduleModel> mAlarmSchedule = new HashMap<>();

    public AlarmService(){
        // empty constructor
    }

    public AlarmService(Context context){
        this.mContext = context;
        mAlarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // initialize the observer
        mUri = ScheduleContract.ScheduleEntry.CONTENT_URI;
        mAlarmObserver = new AlarmObserver(new Handler());
        mContext.getContentResolver().registerContentObserver(mUri,true,mAlarmObserver);
    }

    @Override
    public void addScheduleEntity(ScheduleModel entity){
        mSchedule = entity;
        mAlarmSchedule.put(entity.getTitle(),entity);
    }

    @Override
    public ScheduleModel getScheduleEntity(String title){
        ScheduleModel schedule = mAlarmSchedule.get(title);

        return schedule;
    }


    @Override
    public void setAlarm() {
        Log.v(LOG_TAG, "setAlarm(): " + mSchedule.getTitle());

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(ALARM_TYPE_ONE_TIME);
        // put extra information to the intent
        alarmIntent.putExtra(ALARM_TITLE, mSchedule.getTitle());
        // a dummy data to keep consistent extra
        alarmIntent.putExtra(ALARM_REPEAT_INTERVAL,mSchedule.getRepeatAlarmInterval());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);
        try {
            mAlarmMgr.set(AlarmManager.RTC_WAKEUP, mSchedule.getAlarmTime().getTime(), pendingIntent);
        }catch (Exception e){
            Log.e(LOG_TAG,"setAlarm(): fail to set alarm");
        }
    }

    @Override
    public void setRepeatAlarm(){
        Log.e(LOG_TAG, "setRepeatAlarm(): " + mSchedule.getTitle());

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(ALARM_TYPE_REPEAT);
        // put extra information to intent
        alarmIntent.putExtra(ALARM_TITLE, mSchedule.getTitle());
        alarmIntent.putExtra(ALARM_REPEAT_INTERVAL,mSchedule.getRepeatAlarmInterval());

        PendingIntent pi = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);
        try{
            mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, mSchedule.getAlarmTime().getTime(), mSchedule.getRepeatAlarmInterval(), pi);
        }catch (Exception e){
            Log.e(LOG_TAG,"setRepeatAlarm(): fail to set repeating alarm");
        }
    }

    @Override
    public void cancelAlarm(String type) {
        Log.v(LOG_TAG, "cancelAlarm(): type =  " + type + " title = " + mSchedule.getTitle());
        if(!(type.equals(ALARM_TYPE_ONE_TIME) || type.equals(ALARM_TYPE_REPEAT))){
            Log.e(LOG_TAG,"cancelAlarm(): wrong alarm type");
            return;
        }

        Intent cancelIntent = new Intent(mContext,AlarmReceiver.class);
        cancelIntent.setAction(type);
        // put extra data to intent
        cancelIntent.putExtra(ALARM_TITLE, mSchedule.getTitle());
        cancelIntent.putExtra(ALARM_REPEAT_INTERVAL,mSchedule.getRepeatAlarmInterval());

        PendingIntent sender = PendingIntent.getBroadcast(mContext,0,cancelIntent,0);
        try {
            mAlarmMgr.cancel(sender);
        }catch (Exception e){
            Log.e(LOG_TAG,"cancelAlarm(): fail to cancel the alarm");
        }
    }

    @Override
    public void onDestroy(){
        // unreigster the observers
        mContext.getContentResolver().unregisterContentObserver(mAlarmObserver);
    }
}
