package com.github.xzwj87.todolist.schedule.shake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by JasonWang on 2016/3/28.
 */
public class ShakeDetectService  implements SensorEventListener{
    public static final String TAG = "ShakeDetectService";

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.6F;
    private static final int SHAKE_SLOP_TIME = 500; // 500ms interval to detect
    /* after 3s,reset the shake count */
    private static final int SHAKE_COUNT_RESET_TIME = 3*1000;

    private SensorManager mSensorMgr;
    private Sensor mAccelerometer;
    private long mShakeTimestamp;
    private int mShakeCount;
    /* onShake callback listener */
    private IShakeListener mListener;


    public ShakeDetectService(Context context){
        Log.d(TAG,"creating ShakeDetectService");

        mSensorMgr = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mShakeTimestamp = System.currentTimeMillis();
        mShakeCount = 0;
        mListener = null;
    }

    public void setShakeListener(IShakeListener listener){
        this.mListener = listener;
    }

    public void start(){
        mAccelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(mAccelerometer != null) {
            mSensorMgr.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void stop(){
        Log.d(TAG,"onDestroy()");

        mSensorMgr.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor,int which){
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(mListener == null){
            Log.e(TAG, "listener should be implemented");
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gx = x/SensorManager.GRAVITY_EARTH;
        float gy = y/SensorManager.GRAVITY_EARTH;
        float gz = z/SensorManager.GRAVITY_EARTH;

        /* gForce will be equal to 1 if there is no movement */
        float gForce = (float)Math.sqrt(gx*gx + gy*gy + gz*gz);

        if(gForce > SHAKE_THRESHOLD_GRAVITY){
            Log.d(TAG, "onSensorChanged(): event " + event.getClass().getSimpleName());

            final long now = System.currentTimeMillis();

            /* ignore event too close to each other */
            if(mShakeTimestamp + SHAKE_SLOP_TIME > now){
                return;
            }
            /* reset the shake count */
            if(mShakeTimestamp + SHAKE_COUNT_RESET_TIME < now){
                mShakeCount = 0;
            }

            mShakeTimestamp = now;
            mListener.onShake(++mShakeCount);
        }
    }
}
