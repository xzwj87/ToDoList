package com.github.xzwj87.todolist.schedule.alarm.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.ui.fragment.AlarmDialogFragment;

/**
 * Created by JasonWang on 2016/3/2.
 */
public class AlarmReceiver extends BroadcastReceiver{
    protected static final String LOG_TAG = "AlarmReceiver";

    private DisplayManager mDisplayMgr;
    private PowerManager pm;
    private Context mContext = null;
    private AlarmDialogFragment mAlarmDialog;

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
        Log.v(LOG_TAG, "onReceive(): action = " + intent.getAction().toString());
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
        Toast.makeText(App.getAppContext(),"Something you need to do!!!",Toast.LENGTH_LONG).show();
        startAlarmDialog(mContext);
        wl.release();
    }

    private void startAlarmDialog(Context context){
        Log.v(LOG_TAG,"startAlarmDialog()");
    }
}
