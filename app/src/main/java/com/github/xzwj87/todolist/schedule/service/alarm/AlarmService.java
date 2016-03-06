package com.github.xzwj87.todolist.schedule.service.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by JasonWang on 2016/3/6.
 */
public class AlarmService implements AlarmCommandsInterface{
    public static final String LOG_TAG = "AlarmService";

    private HashMap<String,Long> mEventMap;
    private AlarmManager mAlarmMgr;
    private Context mContext;

    public AlarmService(){
        // empty constructor
    }

    public AlarmService(Context context){
        this.mContext = context;
        mAlarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void setAlarm(String title,long alarmTime,int interval,int duration) {
        Log.v(LOG_TAG, "setAlarm(): " + title);
        mEventMap.put(title, alarmTime);

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(AlarmCommandsInterface.ALARM_TYPE_ONE_TIME);
        // put extra information to the intent
        alarmIntent.putExtra(AlarmCommandsInterface.ALARM_TITLE, title);
        alarmIntent.putExtra(AlarmCommandsInterface.ALARM_START_TIME, alarmTime);
        // a dummy data to keep consistent extra
        alarmIntent.putExtra(AlarmCommandsInterface.ALARM_REPEAT_INTERVAL,interval);
        alarmIntent.putExtra(AlarmCommandsInterface.ALARM_DURATION_TIME, duration);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);
        try {
            mAlarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }catch (Exception e){
            Log.e(LOG_TAG,"setAlarm(): fail to set alarm");
        }
    }

    @Override
    public void setRepeatAlarm(String title,long firstTime,int interval,int duration){
        Log.e(LOG_TAG, "setRepeatAlarm(): " + title);

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(AlarmCommandsInterface.ALARM_TYPE_REPEAT);
        // put extra information to intent
        alarmIntent.putExtra(AlarmCommandsInterface.ALARM_TITLE, title);
        alarmIntent.putExtra(AlarmCommandsInterface.ALARM_START_TIME,firstTime);
        alarmIntent.putExtra(AlarmCommandsInterface.ALARM_REPEAT_INTERVAL,interval);
        alarmIntent.putExtra(AlarmCommandsInterface.ALARM_DURATION_TIME,duration);

        PendingIntent pi = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);
        try{
            mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,firstTime,interval,pi);
        }catch (Exception e){
            Log.e(LOG_TAG,"setRepeatAlarm(): fail to set repeating alarm");
        }
    }

    @Override
    public void cancelAlarm(String title,long alarmTime,int interval,int duration,String type) {
        Log.v(LOG_TAG, "cancelAlarm(): " + title);
        if(!(type.equals(AlarmCommandsInterface.ALARM_TYPE_ONE_TIME) || type.equals(AlarmCommandsInterface.ALARM_TYPE_REPEAT))){
            Log.e(LOG_TAG,"cancelAlarm(): wrong alarm type");
            return;
        }

        Intent cancelIntent = new Intent(mContext,AlarmReceiver.class);
        cancelIntent.setAction(type);
        // put extra data to intent
        cancelIntent.putExtra(AlarmCommandsInterface.ALARM_TITLE, title);
        cancelIntent.putExtra(AlarmCommandsInterface.ALARM_START_TIME,alarmTime);
        cancelIntent.putExtra(AlarmCommandsInterface.ALARM_REPEAT_INTERVAL,interval);
        cancelIntent.putExtra(AlarmCommandsInterface.ALARM_DURATION_TIME,duration);

        PendingIntent sender = PendingIntent.getBroadcast(mContext,0,cancelIntent,0);
        try {
            mAlarmMgr.cancel(sender);
        }catch (Exception e){
            Log.e(LOG_TAG,"cancelAlarm(): fail to cancel the alarm");
        }
    }
}
