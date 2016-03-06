package com.github.xzwj87.todolist.schedule.service.alarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.github.xzwj87.todolist.schedule.ui.fragment.AlarmDialogFragment;

import java.util.HashMap;

/**
 * Created by JasonWang on 2016/3/2.
 */
public class AlarmReceiver extends BroadcastReceiver{
    protected static final String LOG_TAG = "AlarmReceiver";

    private DisplayManager mDisplayMgr;
    private PowerManager pm;
    private PowerManager.WakeLock mWakeLock = null;
    private Context mContext = null;
    private AlarmDialogFragment mAlarmDialog;

    public AlarmReceiver() {
    }

    public AlarmReceiver(Context context) {
        this.mContext = context;
    }

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
        Log.v(LOG_TAG, "onReceive(): action = " + intent.getAction().toString());

        if(isScreenOn()){
            startAlarmDialog(mContext);
            return;
        }
        pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,AlarmReceiver.class.getName());
        mWakeLock.acquire();
        // now present a alarm dialog to user
        Toast.makeText(context,"Something you need to do!!!",Toast.LENGTH_LONG).show();
        startAlarmDialog(mContext);
        mWakeLock.release();
    }

    private void startAlarmDialog(Context context){
        Log.v(LOG_TAG,"startAlarmDialog()");
    }
}
