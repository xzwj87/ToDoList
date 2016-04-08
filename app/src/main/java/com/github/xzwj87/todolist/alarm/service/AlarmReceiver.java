package com.github.xzwj87.todolist.alarm.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.alarm.ui.activity.AlarmAlertActivity;

/**
 * Created by JasonWang on 2016/3/2.
 */

/* here, remember that if the device turn off and rebooted, registered alarm
   will be cleared; so, we need to listen to boot-up event
 */

public class AlarmReceiver extends BroadcastReceiver{
    protected static final String LOG_TAG = "AlarmReceiver";

    private DisplayManager mDisplayMgr;
    private PowerManager pm;
    private Context mContext = null;

    public AlarmReceiver() {
    }

    public AlarmReceiver(Context context) {
        Log.d(LOG_TAG,"creating AlarmReceiver");
        this.mContext = context;
    }

    @Deprecated
    private boolean isScreenOn(){
        // for API level >= 20(KK watch)
        if(Build.VERSION.SDK_INT >= 20){
            Display[] displays = mDisplayMgr.getDisplays();
            for(Display dp: displays){
                if(dp.getState() != Display.STATE_OFF){
                    return true;
                }
            }
        }
        // for API level < 20
        pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if(pm.isScreenOn()){
            return true;
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive(): action = " + intent.getAction().toString()
        + " title = " + intent.getStringExtra(AlarmCommandsInterface.ALARM_TITLE));
        mContext = context;
        /*mDisplayMgr = (DisplayManager)mContext.getSystemService(Context.DISPLAY_SERVICE);
        if(isScreenOn()){
            startAlarmDialog(mContext);
            return;
        }*/

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
