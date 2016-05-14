package com.github.xzwj87.todolist.alarm.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.github.xzwj87.todolist.alarm.ui.activity.AlarmAlertActivity;

/**
 * Created by JasonWang on 2016/3/2.
 */

/* here, remember that if the device turn off and rebooted, registered alarm
   will be cleared; so, we need to listen to boot-up event
 */

public class AlarmReceiver extends BroadcastReceiver{
    protected static final String LOG_TAG = "AlarmReceiver";

    private PowerManager pm;
    private Context mContext = null;
    private AlarmService mAlarmService;

    public AlarmReceiver() {
    }

    public AlarmReceiver(Context context) {
        Log.d(LOG_TAG, "creating AlarmReceiver");
        this.mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive(): action = " + intent.getAction().toString());

        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            mAlarmService = AlarmService.getInstance(context);
            mAlarmService.initAlarms();

            return;
        }

        mContext = context;
        pm = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,AlarmReceiver.class.getName());
        wl.acquire();
        // now present a alarm dialog to user
        startAlarmAlertActivity(intent.getExtras());

        wl.release();
    }

    private void startAlarmAlertActivity(Bundle bundle){
        Log.v(LOG_TAG,"startAlarmAlertActivity()");

        Intent intent = new Intent(mContext,AlarmAlertActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);

        mContext.startActivity(intent);
    }
}
