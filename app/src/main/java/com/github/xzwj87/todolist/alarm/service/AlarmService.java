package com.github.xzwj87.todolist.alarm.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by JasonWang on 2016/3/6.
 */

public class AlarmService implements AlarmCommandsInterface{
    public static final String LOG_TAG = "AlarmService";

    private AlarmManager mAlarmMgr;
    private Context mContext;
    private volatile static AlarmService mInstance;
    // an observer to observe the state of alarm(add/delete/cancel)
    private HashMap<Long,ScheduleModel> mAlarmSchedule = new HashMap<>();
    private ScheduleModelDataMapper mDataMapper;

    public AlarmService(){
        // empty constructor
    }

    public static AlarmService getInstance(Context context){
        if(mInstance == null){
            synchronized (AlarmService.class) {
                if(mInstance == null)
                    mInstance = new AlarmService(context);
            }
        }

        return mInstance;
    }


    public AlarmService(Context context){
        this.mContext = context;
        mAlarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mDataMapper = new ScheduleModelDataMapper();
    }

    /* device boot or reboot complete, init all alarms */
    public void initAlarms(){
        Log.d(LOG_TAG, "initAlarms()");

        String selection = ScheduleContract.ScheduleEntry.COLUMN_DATE_START +" > ?" +
                " AND " + ScheduleContract.ScheduleEntry.COLUMN_TYPE + " != ?" +
                " AND " + ScheduleContract.ScheduleEntry.COLUMN_IS_DONE + " = ?";
        String args[] = {String.valueOf(System.currentTimeMillis()),ScheduleModel.ALARM_NONE,
                ScheduleModel.UNDONE};
        Cursor cursor = mContext.getContentResolver().query(ScheduleContract.ScheduleEntry.CONTENT_URI,
                null,selection,args,null);

        while(cursor.moveToNext()){
            ScheduleModel item = mDataMapper.transform(cursor);
            mAlarmSchedule.put(item.getId(),item);
            setAlarm(item);
        }
    }

    @Override
    public void addAlarmSchedule(ScheduleModel item){
        Log.v(LOG_TAG,"addAlarmSchedule()");

        mAlarmSchedule.put(item.getId(), item);
    }

    @Override
    public void updateAlarmSchedule(ScheduleModel item){
        Log.v(LOG_TAG, "updateScheduleItem()");

        Iterator iterator = mAlarmSchedule.entrySet().iterator();
        HashMap.Entry entry;
        while(iterator.hasNext()) {
            entry = (HashMap.Entry)iterator.next();
            if(Long.getLong(entry.getKey().toString()) == item.getId()){
                entry.setValue(item);
                break;
            }
        }
    }

    @Override
    public void deleteAlarmSchedule(ScheduleModel item){
        Log.v(LOG_TAG,"deleteAlarmSchedule()");

        mAlarmSchedule.remove(item.getId());
    }

    @Override
    public boolean hasAlarm(long id){
        if(mAlarmSchedule.get(id) != null){
            return true;
        }
        return false;
    }

    public ScheduleModel getScheduleById(long id){
        return mAlarmSchedule.get(id);
    }

    public HashMap<Long,ScheduleModel> getAllAlarmSchedule(){
        Log.v(LOG_TAG,"getAllAlarmSchedule()");

        return mAlarmSchedule;
    }

    @Override
    public void setAlarm(ScheduleModel item){
        Log.d(LOG_TAG, "setAlarm(): title = " + item.getTitle());

       @ScheduleModel.ScheduleRepeatType String repeatType = item.getScheduleRepeatType();
        if(repeatType.equals(ScheduleModel.SCHEDULE_REPEAT_NONE)){
            //ACTION_ONE_TIME_ALARM;
            setOneTimeAlarm(item);
        }else{
            setRepeatAlarm(item);
        }
    }

    @Override
    public void setOneTimeAlarm(ScheduleModel item) {
        Log.d(LOG_TAG, "setOneTimeAlarm(): title = " + item.getTitle()
        + " date = " + item.getScheduleStart());

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(ACTION_ONE_TIME_ALARM);
        // put extra information to the intent
        alarmIntent.putExtra(ALARM_TITLE, item.getTitle());
        alarmIntent.putExtra(ALARM_START_TIME,item.getScheduleStart().getTime());
        // a dummy data to keep consistent extra
        int repeatAlarmInterval = item.getRepeatAlarmInterval();
        alarmIntent.putExtra(ALARM_REPEAT_INTERVAL,repeatAlarmInterval);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,(int)item.getId(),alarmIntent,0);
        try {
            mAlarmMgr.set(AlarmManager.RTC_WAKEUP, item.getScheduleStart().getTime(), pendingIntent);
        }catch (Exception e){
            Log.e(LOG_TAG,"setOneTimeAlarm(): fail to set alarm");
        }
    }

    @Override
    public void setRepeatAlarm(ScheduleModel item) {
        Log.e(LOG_TAG, "setRepeatAlarm(): " + item.getTitle());

        Intent alarmIntent = new Intent(mContext,AlarmReceiver.class);
        alarmIntent.setAction(ACTION_REPEAT_ALARM);
        // put extra information to intent
        alarmIntent.putExtra(ALARM_TITLE, item.getTitle());
        alarmIntent.putExtra(ALARM_START_TIME,item.getScheduleStart());
        alarmIntent.putExtra(ALARM_REPEAT_INTERVAL, item.getRepeatAlarmInterval());

        PendingIntent pi = PendingIntent.getBroadcast(mContext,(int)item.getId(),alarmIntent,0);
        try{
            mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, item.getScheduleStart().getTime(), item.getRepeatAlarmInterval(), pi);
        }catch (Exception e){
            Log.e(LOG_TAG,"setRepeatAlarm(): fail to set repeat alarm");
        }
    }

    @Override
    public void cancelAlarm(ScheduleModel item) {
        Log.v(LOG_TAG, "cancelAlarm(): title = " + item.getTitle());

        @ScheduleModel.ScheduleRepeatType String type = item.getScheduleRepeatType();
        String alarmType;
        if(type == ScheduleModel.SCHEDULE_REPEAT_NONE){
            alarmType = ACTION_ONE_TIME_ALARM;
        }else{ alarmType = ACTION_REPEAT_ALARM; }

        Intent cancelIntent = new Intent(mContext,AlarmReceiver.class);
        cancelIntent.setAction(alarmType);
        // put extra data to intent
        cancelIntent.putExtra(ALARM_TITLE, item.getTitle());
        cancelIntent.putExtra(ALARM_START_TIME,item.getScheduleStart().getTime());
        cancelIntent.putExtra(ALARM_REPEAT_INTERVAL,item.getRepeatAlarmInterval());

        PendingIntent sender = PendingIntent.getBroadcast(mContext,(int)item.getId(),cancelIntent,0);
        try {
            mAlarmMgr.cancel(sender);
        }catch (Exception e){
            Log.e(LOG_TAG,"cancelAlarm(): fail to cancel the alarm");
        }
    }
}
