package com.github.xzwj87.todolist.alarm.service;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by JasonWang on 2016/3/6.
 */

public class AlarmObserver extends ContentObserver {
    static final String LOG_TAG = "AlarmObserver";

    private Context mContext;
    private Uri mUri;
    private Cursor mCursor;
    private ScheduleModelDataMapper mDataMapper;
    private AlarmService mAlarmService;
    private HashMap<Long,ScheduleModel> mCurrentAlarm = new HashMap<>();

    /**
     * Creates a content observer to observe the state of alarm(add/delete/cancel)
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */

    public AlarmObserver(Context context, Handler handler) {
        super(handler);
        Log.d(LOG_TAG, "creating alarm observer");

        this.mContext = context;
        this.mAlarmService = AlarmService.getInstance(context);
        this.mDataMapper = new ScheduleModelDataMapper();
    }

    @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.d(LOG_TAG, "onChange(): schedule uri = " + uri);

        mUri = uri;

        HashMap<Long,ScheduleModel> preAlarmSchedule = mAlarmService.getAllAlarmSchedule();

        boolean isAdd = false;
        /* now we do add alarm using cursor */
        mCursor = mContext.getContentResolver().query(mUri,null,null,null,null);
        //mCursor.moveToFirst();
        while(mCursor.moveToNext()){
            long id = mCursor.getLong(ScheduleModelDataMapper.COL_SCHEDULE_ID);
            ScheduleModel item = mDataMapper.transform(mCursor);

            @ScheduleModel.AlarmType String alarmType = item.getAlarmType();
            /* no alarm for this schedule */
            if (alarmType.equals(ScheduleModel.ALARM_NONE)) {
                continue;
            }

            /* not included in previous alarm schedule*/
            if(!preAlarmSchedule.containsKey(id)){
                addAlarm(item);
                isAdd = true;
            }
        }

        if(isAdd) return;

        // get current all alarm schedule
        getAllAlarmSchedule();
        /* now compare two alarm list to delete/update */
        Iterator iterator = preAlarmSchedule.entrySet().iterator();
        while(iterator.hasNext()){
            HashMap.Entry pair = (HashMap.Entry)iterator.next();
            ScheduleModel schedule = (ScheduleModel)pair.getValue();
            long id = schedule.getId();
            if(!mCurrentAlarm.containsKey(id)){
                deleteAlarm(schedule);
            }else if(!schedule.equals(mCurrentAlarm.get(id))){
                updateAlarm(schedule);
            }
        }
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    protected void deleteAlarm(ScheduleModel item) {
        Log.d(LOG_TAG,"deleteAlarm(): title = " + item.getTitle());

        mAlarmService.deleteAlarmSchedule(item);
        mAlarmService.cancelAlarm(item);
    }

    protected void updateAlarm(ScheduleModel item) {
        Log.d(LOG_TAG, "updateAlarm(): title = " + item.getTitle());

        /* alarm is done */
        if(ScheduleModel.DONE == item.getDoneStatus()){
            mAlarmService.deleteAlarmSchedule(item);
            return;
        }

        mAlarmService.updateAlarmSchedule(item);
        /* alarm time is changed */
        if(!item.getAlarmTime().equals(
                mAlarmService.getScheduleById(item.getId()).getAlarmTime())) {
            mAlarmService.cancelAlarm(item);
            mAlarmService.setAlarm(item);
        }

    }

    protected void addAlarm(ScheduleModel item) {
        Log.d(LOG_TAG, "addAlarm(): title = " + item.getTitle());

        mAlarmService.addAlarmSchedule(item);
        mAlarmService.setAlarm(item);
    }

    protected void getAllAlarmSchedule(){
        String selection = ScheduleContract.ScheduleEntry.COLUMN_ALARM_TYPE + " != ?"
                + " AND " + ScheduleContract.ScheduleEntry.COLUMN_IS_DONE + " = ?";
        String args[] = {ScheduleModel.ALARM_NONE,ScheduleModel.UNDONE};
        Cursor cursor = mContext.getContentResolver().query(ScheduleContract.ScheduleEntry.CONTENT_URI,
        null,selection,args,null);

        while (!cursor.moveToFirst()){
            if(!cursor.getString(ScheduleModelDataMapper.COL_SCHEDULE_ALARM_TYPE)
                    .equals(ScheduleModel.ALARM_NONE)){
                mCurrentAlarm.put(mCursor.getLong(ScheduleModelDataMapper.COL_SCHEDULE_ID),
                        mDataMapper.transform(cursor));
            }
            cursor.moveToNext();
        }
    }
}