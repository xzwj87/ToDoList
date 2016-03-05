package com.github.xzwj87.todolist.schedule.service.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.github.xzwj87.todolist.schedule.ui.fragment.AlarmDialogFragment;

import java.util.HashMap;

/**
 * Created by JasonWang on 2016/3/2.
 */
public class AlarmReceiver extends BroadcastReceiver implements AlarmService{
    protected static final String LOG_TAG = "AlarmReceiver";

    private static AlarmReceiver mInstance = new AlarmReceiver();
    // a HashMap to save event title/alarm time
    private  HashMap<String,Long> mEventMap = null;
    private PowerManager.WakeLock mWakeLock = null;
    private AlarmManager mAlarmMgr = null;
    private Context mContext = null;
    private AlarmDialogFragment mAlarmDialog;

    public AlarmReceiver(){
        mEventMap = new HashMap<>();
        mAlarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public AlarmReceiver getInstance(Context context){
        this.mContext = context;

        return mInstance;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG, "onReceive(): action = " + intent.getAction().toString());

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"");
        mWakeLock.acquire();
        // here need to remove this alarm from HashMap based on Intent extras
        // now present a alarm dialog to user
        Toast.makeText(context,"Something you need to do!!!",Toast.LENGTH_LONG).show();
        startAlarmDialog(mContext);
        mWakeLock.release();
    }

    @Override
    public void setAlarm(String title,long alarmTime) {
        Log.v(LOG_TAG, "setAlarm(): " + title);
        mEventMap.put(title, alarmTime);

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(AlarmService.ALARM_TYPE_ONE_TIME);
        // put extra information to the intent
        alarmIntent.putExtra(title, alarmTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);

        try {
            mAlarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }catch (Exception e){
            Log.e(LOG_TAG,"setAlarm(): fail to set alarm");
        }
    }

    @Override
    public void setRepeatAlarm(String title,long firstTime,int interval){
        Log.e(LOG_TAG, "setRepeatAlarm(): " + title);

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(AlarmService.ALARM_TYPE_REPEAT);
        alarmIntent.putExtra(title, firstTime);

        PendingIntent pi = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);

        try{
            mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,firstTime,interval,pi);
        }catch (Exception e){
            Log.e(LOG_TAG,"setRepeatAlarm(): fail to set repeating alarm");
        }
    }
    @Override
    public void cancelAlarm(String title,long alarmTime,String type) {
        Log.v(LOG_TAG, "cancelAlarm(): " + title);
        if(!(type.equals(AlarmService.ALARM_TYPE_ONE_TIME) || type.equals(AlarmService.ALARM_TYPE_REPEAT))){
            Log.e(LOG_TAG,"cancelAlarm(): wrong alarm type");
            return;
        }

        Intent cancelIntent = new Intent(mContext,AlarmReceiver.class);
        cancelIntent.setAction(type);
        cancelIntent.putExtra(title, alarmTime);
        PendingIntent sender = PendingIntent.getBroadcast(mContext,0,cancelIntent,0);

        try {
            mAlarmMgr.cancel(sender);
        }catch (Exception e){
            Log.e(LOG_TAG,"cancelAlarm(): fail to cancel the alarm");
        }
    }

    private void startAlarmDialog(Context context){
        Log.v(LOG_TAG,"startAlarmDialog()");

        mAlarmDialog = new AlarmDialogFragment();
    }
}
