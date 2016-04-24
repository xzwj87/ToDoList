package com.github.xzwj87.todolist.alarm.shake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by NingXu/JasonWang on 2016/3/28.
 */
public class ShakeDetectService  implements SensorEventListener{
    public static final String TAG = "ShakeDetectService";

    private static final float SHAKE_THRESHOLD_SPEED = 3800;
    private static final int SHAKE_SLOP_TIME = 50; // 50ms interval to detect

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.5F;
    private static final int SHAKE_SLOP_TIME_MS = 400;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private float lastX,lastY,lastZ;

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
        mListener = null;
    }

    public void setShakeListener(IShakeListener listener){
        this.mListener = listener;
    }

    public void start(){
        Log.d(TAG,"start()");
        mAccelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(mAccelerometer != null) {
            mSensorMgr.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void stop(){
        Log.d(TAG,"stop()");

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

        /*final long now = System.currentTimeMillis();
        final long interval = now - mShakeTimestamp;

        if(interval < SHAKE_SLOP_TIME) return;

        long diff = now - mShakeTimestamp;
        mShakeTimestamp = now;*/

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        float gx = x/SensorManager.GRAVITY_EARTH;
        float gy = y/SensorManager.GRAVITY_EARTH;
        float gz = z/SensorManager.GRAVITY_EARTH;

        //float speed = Math.abs(deltaX + deltaY + deltaZ)/diff * 10000;
        double gForce = Math.sqrt(gx*gx + gy*gy + gz*gz);

        if(gForce > SHAKE_THRESHOLD_GRAVITY){
            final long now = System.currentTimeMillis();

            if(mShakeTimestamp + SHAKE_SLOP_TIME_MS > now){
                return;
            }

            if(mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now){
                mShakeCount = 0;
            }

            mShakeTimestamp = now;
            mShakeCount++;

            mListener.onShake();
        }
        /*if(speed > SHAKE_THRESHOLD_SPEED){
            Log.d(TAG, "onSensorChanged(): speed =  " + speed);

            mListener.onShake();
        }

        lastX = x;
        lastY = y;
        lastZ = z;*/
    }
}
