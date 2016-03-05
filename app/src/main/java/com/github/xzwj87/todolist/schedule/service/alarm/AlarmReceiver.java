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
        // put extra information to the intent
        alarmIntent.putExtra(title, alarmTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);
        mAlarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    @Override
    public void cancelAlarm(String title,long alarmTime) {
        Log.v(LOG_TAG, "cancelAlarm(): " + title);

        Intent cancelIntent = new Intent(mContext,AlarmReceiver.class);
        cancelIntent.putExtra(title,alarmTime);
        PendingIntent sender = PendingIntent.getBroadcast(mContext,0,cancelIntent,0);
        mAlarmMgr.cancel(sender);
    }

    private void startAlarmDialog(Context context){
        Log.v(LOG_TAG,"startAlarmDialog()");

        mAlarmDialog = new AlarmDialogFragment();
    }
}
