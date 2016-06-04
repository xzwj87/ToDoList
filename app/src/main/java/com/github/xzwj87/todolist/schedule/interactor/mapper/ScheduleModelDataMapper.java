package com.github.xzwj87.todolist.schedule.interactor.mapper;

import android.database.Cursor;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.internal.di.PerActivity;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.util.Date;

import javax.inject.Inject;

@PerActivity
public class ScheduleModelDataMapper {
    private static final String LOG_TAG = ScheduleModelDataMapper.class.getSimpleName();

    public static final String[] SCHEDULE_COLUMNS = {
            ScheduleContract.ScheduleEntry._ID,
            ScheduleContract.ScheduleEntry.COLUMN_TITLE,
            ScheduleContract.ScheduleEntry.COLUMN_NOTE,
            ScheduleContract.ScheduleEntry.COLUMN_TYPE,
            ScheduleContract.ScheduleEntry.COLUMN_DATE_START,
            ScheduleContract.ScheduleEntry.COLUMN_DATE_END,
            ScheduleContract.ScheduleEntry.COLUMN_REPEAT_SCHEDULE,
            ScheduleContract.ScheduleEntry.COLUMN_ALARM_TYPE,
            ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME,
            ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES,
            ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL,
            ScheduleContract.ScheduleEntry.COLUMN_IS_DONE,
            ScheduleContract.ScheduleEntry.COLUMN_PRIORITY
    };

    public static final int COL_SCHEDULE_ID = 0;
    public static final int COL_SCHEDULE_TITLE = 1;
    public static final int COL_SCHEDULE_NOTE = 2;
    public static final int COL_SCHEDULE_TYPE = 3;
    public static final int COL_SCHEDULE_DATE_START = 4;
    public static final int COL_SCHEDULE_DATE_END = 5;
    public static final int COL_SCHEDULE_REPEAT_SCHEDULE = 6;
    public static final int COL_SCHEDULE_ALARM_TYPE = 7;
    public static final int COL_SCHEDULE_ALARM_TIME = 8;
    public static final int COL_SCHEDULE_REPEAT_ALARM_TIMES = 9;
    public static final int COL_SCHEDULE_REPEAT_ALARM_INTERVAL = 10;
    public static final int COL_SCHEDULE_IS_DONE = 11;
    public static final int COL_SCHEDULE_PRIORITY = 12;

    @Inject
    public ScheduleModelDataMapper() {}

    @SuppressWarnings("ResourceType")
    public ScheduleModel transform(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cannot transform a null value");
        }

        ScheduleModel scheduleModel = new ScheduleModel();
        scheduleModel.setId(cursor.getLong(COL_SCHEDULE_ID));
        scheduleModel.setTitle(cursor.getString(COL_SCHEDULE_TITLE));
        scheduleModel.setNote(cursor.getString(COL_SCHEDULE_NOTE));
        scheduleModel.setType(cursor.getString(COL_SCHEDULE_TYPE));

        Date start = new Date(cursor.getLong(COL_SCHEDULE_DATE_START));
        scheduleModel.setScheduleStart(start);

        Date end = new Date(cursor.getLong(COL_SCHEDULE_DATE_END));
        scheduleModel.setScheduleEnd(end);

        scheduleModel.setScheduleRepeatType(cursor.getString(COL_SCHEDULE_REPEAT_SCHEDULE));

        scheduleModel.setAlarmType(cursor.getString(COL_SCHEDULE_ALARM_TYPE));

        Date alarm = new Date(cursor.getLong(COL_SCHEDULE_ALARM_TIME));
        scheduleModel.setAlarmTime(alarm);

        scheduleModel.setRepeatAlarmTimes(cursor.getInt(COL_SCHEDULE_REPEAT_ALARM_TIMES));
        scheduleModel.setRepeatAlarmInterval(cursor.getInt(COL_SCHEDULE_REPEAT_ALARM_INTERVAL));

        scheduleModel.setDoneStatus(cursor.getString(COL_SCHEDULE_IS_DONE));

        scheduleModel.setPriority(cursor.getInt(COL_SCHEDULE_PRIORITY));

        Log.v(LOG_TAG, "transform(): scheduleModel = " + scheduleModel);

        return scheduleModel;
    }


}
