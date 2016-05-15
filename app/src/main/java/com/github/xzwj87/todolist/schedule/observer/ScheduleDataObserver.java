package com.github.xzwj87.todolist.schedule.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by JasonWang on 2016/5/15.
 */

public class ScheduleDataObserver extends ContentObserver {
    public static final String TAG = "ScheduleObserver";

    private ArrayList<DataSetChanged> mCallbacks;

    public ScheduleDataObserver(Context context,Handler handler){
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange){
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange,Uri uri){
        Log.v(TAG, "onChange: uri = " + uri);

        Iterator iterator = mCallbacks.iterator();
        while(iterator.hasNext()){
            DataSetChanged cb = (DataSetChanged)iterator.next();
            cb.onDataSetChanged();
        }
    }

    public interface DataSetChanged{
        void onDataSetChanged();
    }

    public void registerDataChangedCb(DataSetChanged cb){
        Log.v(TAG,"registerDataChagnedcb(): callback = " + cb.getClass().getSimpleName());
        mCallbacks.add(cb);
    }

    public void unregisterDataChangedCb(DataSetChanged cb){
        Log.v(TAG,"unregisterDataChangedCb(): callback = " + cb.getClass().getSimpleName());
        mCallbacks.remove(cb);
    }
}