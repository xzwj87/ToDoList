package com.github.xzwj87.todolist.schedule.alarm.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.entity.ScheduleEntity;
import com.github.xzwj87.todolist.schedule.data.entity.ScheduleEntity.ScheduleRepeatType;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleProvider;

import java.util.Date;

/**
 * Created by JasonWang on 2016/3/6.
 */
public class AlarmObserver extends ContentObserver {
    static final String LOG_TAG = "AlarmObserver";

    private ContentResolver mContentResolver;
    private Context mContext;
    private Uri mUri;
    private Cursor mCursor;
    private AlarmService mAlarmService;
    /**
     * Creates a content observer to observe the state of alarm(add/delete/cancel)
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public AlarmObserver(Context context,Handler handler) {
        super(handler);

        Log.d(LOG_TAG, "creating alarm observer");
        this.mContext = context;
        this.mAlarmService = new AlarmService(context);
    }

    @Override
    public void onChange(boolean selfChange){
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange,Uri uri){
        Log.d(LOG_TAG, "schedule :" + uri.toString() + " is selfChange: " +  selfChange);
        this.mUri = uri;
        mContentResolver = mContext.getContentResolver();

        Cursor cursor = mContentResolver.query(mUri,null,null,null,null);
        cursor.moveToFirst();
        while(cursor != null) {
            String title = cursor.getString(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_TITLE));
            /* update alarm state */
            updateAlarmState(title);
            cursor.moveToNext();
        }
    }

    @Override
    public boolean deliverSelfNotifications(){
        return true;
    }

    protected void updateAlarmState(String title){
        Log.d(LOG_TAG,"updateAlarmState");
        if(title == null) return;
        /* update an alarm */
        if(mAlarmService.hasAlarm(title)){

        /* add a new alarm */
        }else{

        }
    }

    public ScheduleEntity getScheduleFromCursor(Cursor cursor){
        ScheduleEntity entity = new ScheduleEntity();
        entity.setTitle(cursor.getString(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_TITLE)));
        entity.setAlarmTime(new Date(cursor.getLong(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME))));
        entity.setRepeatAlarmInterval(cursor.getInt(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL)));
        entity.setDetail(cursor.getString(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_DETAIL)));
        entity.setRepeatAlarmTimes(cursor.getInt(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES)));
        String scheduleType = cursor.getString(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_TYPE));
        if(scheduleType == ScheduleEntity.SCHEDULE_TYPE_DEFAULT)
            entity.setType(ScheduleEntity.SCHEDULE_TYPE_DEFAULT);
        else if(scheduleType == ScheduleEntity.SCHEDULE_TYPE_DATE){
            entity.setType(ScheduleEntity.SCHEDULE_TYPE_DATE);
        }else if(scheduleType == ScheduleEntity.SCHEDULE_TYPE_MEETING){
            entity.setType(ScheduleEntity.SCHEDULE_TYPE_MEETING);
        }else if(scheduleType == ScheduleEntity.SCHEDULE_TYPE_ENTERTAINMENT){
            entity.setType(ScheduleEntity.SCHEDULE_TYPE_ENTERTAINMENT);
        }
        //entity.setType(cursor.getString(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_TYPE)));

        return entity;
    }
}