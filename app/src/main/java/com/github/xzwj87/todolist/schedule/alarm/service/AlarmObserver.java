package com.github.xzwj87.todolist.schedule.alarm.service;

import android.content.ContentResolver;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.entity.ScheduleEntity;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleProvider;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by JasonWang on 2016/3/6.
 * 再次启动时，需要检测数据库中是否有尚未添加的Alarm
 */
public class AlarmObserver extends ContentObserver {
    static final String LOG_TAG = "AlarmObserver";

    private ContentResolver mContentResolver;
    private Context mContext;
    private Uri mUri;
    private Cursor mCursor;
    private AlarmService mAlarmService;
    private UriMatcher mUriMatcher = ScheduleProvider.buildUriMatcher();
    /**
     * Creates a content observer to observe the state of alarm(add/delete/cancel)
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public AlarmObserver(Context context,Handler handler) {
        super(handler);

        Log.d(LOG_TAG, "creating alarm observer");
        this.mContext = context;
        this.mAlarmService = AlarmService.getInstance(context);
    }

    @Override
    public void onChange(boolean selfChange){
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange,Uri uri){
        Log.d(LOG_TAG, "onChange(): schedule :" + uri.toString() + " is selfChange: " + selfChange);
        mUri = uri;
        int match = mUriMatcher.match(uri);
        mContentResolver = mContext.getContentResolver();
        mCursor = mContentResolver.query(mUri, null, null, null, null);
        mCursor.moveToFirst();

        if(match == ScheduleProvider.SCHEDULE){
            deleteAlarm();
        }else if(match == ScheduleProvider.SCHEDULE_WITH_ID) {
            long id = ScheduleContract.ScheduleEntry.getScheduleIdFromUri(mUri);
            if (mAlarmService.hasAlarm(id)) {
                updateAlarm();
            } else {
                addAlarm();
            }
        }
    }

    @Override
    public boolean deliverSelfNotifications(){
        return true;
    }

    protected void deleteAlarm(){
        if(mCursor == null) return;

        while(mCursor != null) {
            long id = mCursor.getColumnIndex(ScheduleContract.ScheduleEntry._ID);
            //Uri uri = ScheduleContract.ScheduleEntry.buildScheduleUri(id);
            HashMap<Long, ScheduleEntity> alarmSchedule = AlarmService.AlarmSchedule;
            if (alarmSchedule.get(id) != null) {
                mAlarmService.deleteScheduleEntity(id, null);
                mAlarmService.cancelAlarm(alarmSchedule.get(id));
                Log.d(LOG_TAG, "deleteAlarm(): title = " + alarmSchedule.get(id).getTitle());
            }

            mCursor.moveToNext();
        }
    }

    protected void updateAlarm(){
        if(mCursor == null) return;

        String title = mCursor.getString(mCursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_TITLE));
        Log.d(LOG_TAG, "updateAlarm(): title = " + title);

        long id = ScheduleContract.ScheduleEntry.getScheduleIdFromUri(mUri);
        ScheduleEntity entityCurrent = getScheduleFromCursor(mCursor);
        ScheduleEntity entityBefore = AlarmService.AlarmSchedule.get(id);

        mAlarmService.updateScheduleEntity(id,entityCurrent);

        String alarmTypeCurrent = entityCurrent.getScheduleRepeatType() == ScheduleEntity.SCHEDULE_REPEAT_NONE ?
                AlarmService.ACTION_ONE_TIME_ALARM : AlarmService.ACTION_REPEAT_ALARM;
        String alarmTypeBefore = entityBefore.getScheduleRepeatType() == ScheduleEntity.SCHEDULE_REPEAT_NONE ?
                AlarmService.ACTION_ONE_TIME_ALARM : AlarmService.ACTION_REPEAT_ALARM;

        if(alarmTypeCurrent == alarmTypeBefore && entityCurrent.getAlarmTime() == entityBefore.getAlarmTime() &&
                entityCurrent.getAlarmState() == entityBefore.getAlarmState() &&
                entityCurrent.getRepeatAlarmInterval() == entityBefore.getRepeatAlarmInterval() &&
                entityCurrent.getScheduleRepeatType() == entityBefore.getScheduleRepeatType()){
            return;
        }

        mAlarmService.cancelAlarm(entityBefore);
        mAlarmService.setAlarm(entityCurrent);

    }

    protected void addAlarm(){
        String title = mCursor.getString(mCursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_TITLE));
        Log.d(LOG_TAG,"addAlarm(): title = " + title);

        long id = ScheduleContract.ScheduleEntry.getScheduleIdFromUri(mUri);
        ScheduleEntity entity = getScheduleFromCursor(mCursor);
        mAlarmService.addScheduleEntity(id,entity);
        mAlarmService.setAlarm(entity);
    }

    public ScheduleEntity getScheduleFromCursor(Cursor cursor){
        ScheduleEntity entity = new ScheduleEntity();
        entity.setTitle(cursor.getString(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_TITLE)));
        entity.setAlarmTime(new Date(cursor.getLong(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME))));
        entity.setRepeatAlarmInterval(cursor.getInt(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL)));
        entity.setScheduleStart(new Date(cursor.getLong(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_DATE_START))));
        entity.setDetail(cursor.getString(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_NOTE)));
        entity.setRepeatAlarmTimes(cursor.getInt(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES)));
        entity.setAlarmState((cursor.getInt(cursor.getColumnIndex(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE)) > 0 ? true : false));

        entity.setType(ScheduleEntity.SCHEDULE_TYPE_DEFAULT);
        entity.setScheduleRepeatType(ScheduleEntity.SCHEDULE_REPEAT_NONE);

        return entity;
    }
}