package com.github.xzwj87.todolist.schedule.interactor.mapper;


import android.database.Cursor;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.util.Date;

public class ScheduleModelDataMapper {
    private static final String LOG_TAG = ScheduleModelDataMapper.class.getSimpleName();

    public static final String[] SCHEDULE_COLUMNS = {
            ScheduleContract.ScheduleEntry._ID,
            ScheduleContract.ScheduleEntry.COLUMN_TITLE,
            ScheduleContract.ScheduleEntry.COLUMN_DETAIL,
            ScheduleContract.ScheduleEntry.COLUMN_TYPE,
            ScheduleContract.ScheduleEntry.COLUMN_DATE_START,
            ScheduleContract.ScheduleEntry.COLUMN_DATE_END,
            ScheduleContract.ScheduleEntry.COLUMN_REPEAT_SCHEDULE,
            ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME,
            ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES,
            ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL
    };

    public static final int COL_SCHEDULE_ID = 0;
    public static final int COL_SCHEDULE_TITLE = 1;
    public static final int COL_SCHEDULE_DETAIL = 2;
    public static final int COL_SCHEDULE_TYPE = 3;
    public static final int COL_SCHEDULE_DATE_START = 4;
    public static final int COL_SCHEDULE_DATE_END = 5;
    public static final int COL_SCHEDULE_REPEAT_SCHEDULE = 6;
    public static final int COL_SCHEDULE_ALARM_TIME = 7;
    public static final int COL_SCHEDULE_REPEAT_ALARM_TIMES = 8;
    public static final int COL_SCHEDULE_REPEAT_ALARM_INTERVAL = 9;


    public ScheduleModelDataMapper() {}

    @SuppressWarnings("ResourceType")
    public ScheduleModel transform(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cannot transform a null value");
        }

        ScheduleModel scheduleModel = new ScheduleModel();
        scheduleModel.setId(cursor.getLong(COL_SCHEDULE_ID));
        scheduleModel.setTitle(cursor.getString(COL_SCHEDULE_TITLE));
        scheduleModel.setDetail(cursor.getString(COL_SCHEDULE_DETAIL));
        scheduleModel.setType(cursor.getString(COL_SCHEDULE_TYPE));

        Date start = new Date(cursor.getLong(COL_SCHEDULE_DATE_START));
        scheduleModel.setScheduleStart(start);

        Date end = new Date(cursor.getLong(COL_SCHEDULE_DATE_END));
        scheduleModel.setScheduleEnd(end);

        scheduleModel.setScheduleRepeatType(cursor.getString(COL_SCHEDULE_REPEAT_SCHEDULE));

        Date alarm = new Date(cursor.getLong(COL_SCHEDULE_ALARM_TIME));
        scheduleModel.setAlarmTime(alarm);

        scheduleModel.setRepeatAlarmTimes(cursor.getInt(COL_SCHEDULE_REPEAT_ALARM_TIMES));
        scheduleModel.setRepeatAlarmInterval(cursor.getInt(COL_SCHEDULE_REPEAT_ALARM_INTERVAL));

        Log.v(LOG_TAG, "transform(): scheduleModel = " + scheduleModel);

        return scheduleModel;
    }


}
