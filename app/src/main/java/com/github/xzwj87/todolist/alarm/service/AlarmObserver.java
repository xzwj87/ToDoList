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

/**
 * Created by JasonWang on 2016/3/6.
 */

public class AlarmObserver extends ContentObserver {
    static final String LOG_TAG = "AlarmObserver";

    private Context mContext;
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

        HashMap<Long,ScheduleModel> preAlarmSchedule = mAlarmService.getAllAlarmSchedule();
        HashMap<Long,ScheduleModel> curAlarmSchedule = getAllAlarmSchedule();

        /* now we do add alarm using cursor */
        mCursor = mContext.getContentResolver().query(uri,null,null,null,null);
        //mCursor.moveToFirst();
        while(mCursor.moveToNext()){
            ScheduleModel item = mDataMapper.transform(mCursor);
            long id = item.getId();
            @ScheduleModel.AlarmType String alarmType = item.getAlarmType();

            long alarmTime = item.getAlarmTime().getTime();
            /* no alarm or illegal time for this schedule */
            if (alarmType.equals(ScheduleModel.ALARM_NONE) ||
                    alarmTime <= System.currentTimeMillis()) {
                continue;
            }

            /* not included in previous alarm schedule*/
            if(!preAlarmSchedule.containsKey(id) && curAlarmSchedule.containsKey(id)){
                addAlarm(item);
            }else if(preAlarmSchedule.containsKey(id) && !curAlarmSchedule.containsKey(id)){
                deleteAlarm(item);
            }else if(preAlarmSchedule.containsKey(id) && curAlarmSchedule.containsKey(id)){
                updateAlarm(preAlarmSchedule.get(id),curAlarmSchedule.get(id));
            }
        }
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    private void deleteAlarm(ScheduleModel item) {
        Log.d(LOG_TAG,"deleteAlarm(): title = " + item.getTitle());

        mAlarmService.deleteAlarmSchedule(item);
        mAlarmService.cancelAlarm(item);
    }

    private void updateAlarm(ScheduleModel pre,ScheduleModel cur) {
        Log.d(LOG_TAG, "updateAlarm(): title = " + pre.getTitle());

        /* alarm status is changed */
        if(pre.getDoneStatus().equals(ScheduleModel.UNDONE)
                && cur.getDoneStatus().equals(ScheduleModel.DONE)){
            mAlarmService.deleteAlarmSchedule(pre);
            return;
        }

        mAlarmService.updateAlarmSchedule(cur);
        /* alarm time is changed */
        if(!pre.getAlarmTime().equals(cur.getAlarmTime())
                || !pre.getScheduleStart().equals(cur.getScheduleStart())) {
            mAlarmService.cancelAlarm(pre);
            mAlarmService.setAlarm(cur);
        }
    }

    private void addAlarm(ScheduleModel item) {
        Log.d(LOG_TAG, "addAlarm(): title = " + item.getTitle());

        mAlarmService.addAlarmSchedule(item);
        mAlarmService.setAlarm(item);
    }

    protected HashMap<Long,ScheduleModel> getAllAlarmSchedule(){
        String selection = ScheduleContract.ScheduleEntry.COLUMN_ALARM_TYPE + " != ?";
        String args[] = {ScheduleModel.ALARM_NONE};
        Cursor cursor = mContext.getContentResolver().query(ScheduleContract.ScheduleEntry.CONTENT_URI,
        null,selection,args,null);

        HashMap<Long,ScheduleModel> current = new HashMap<>();
        while (cursor.moveToNext()){
            ScheduleModel schedule = mDataMapper.transform(cursor);
            current.put(schedule.getId(), schedule);
        }
        cursor.close();

        return current;
    }
}