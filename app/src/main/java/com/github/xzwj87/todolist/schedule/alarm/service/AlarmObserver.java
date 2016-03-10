package com.github.xzwj87.todolist.schedule.alarm.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

/**
 * Created by JasonWang on 2016/3/6.
 */
public class AlarmObserver extends ContentObserver {
    static final String LOG_TAG = "AlarmObserver";

    private ContentResolver mContentResolver;
    private Context mContext;
    /**
     * Creates a content observer to observe the state of alarm(add/delete/cancel)
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public AlarmObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange){
        super.onChange(selfChange);

        mContentResolver = mContext.getContentResolver();
        //Cursor cursor = mContentResolver.query()
    }

    @Override
    public boolean deliverSelfNotifications(){
        return true;
    }
}
