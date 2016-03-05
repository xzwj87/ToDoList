package com.github.xzwj87.todolist.schedule.interactor.mapper;


import android.content.ContentValues;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

public class ScheduleContentValuesDataMapper {
    private static final String LOG_TAG = ScheduleContentValuesDataMapper.class.getSimpleName();

    public ScheduleContentValuesDataMapper() {}

    public ContentValues transform(ScheduleModel scheduleModel) {
        ContentValues scheduleValues = new ContentValues();
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_TITLE, scheduleModel.getTitle());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DETAIL, scheduleModel.getDetail());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_TYPE, scheduleModel.getType());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DATE_START, scheduleModel.getScheduleStart().getTime());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DATE_END,  scheduleModel.getScheduleEnd().getTime());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_SCHEDULE, scheduleModel.getScheduleRepeatType());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME, scheduleModel.getAlarmTime().getTime());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES, scheduleModel.getRepeatAlarmTimes());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL, scheduleModel.getRepeatAlarmInterval());

        Log.v(LOG_TAG, "getSchedule(): scheduleValues = " + scheduleValues);
        return scheduleValues;
    }

}
