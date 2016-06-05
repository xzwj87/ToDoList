package com.github.xzwj87.todolist.schedule.utility;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

public class ScheduleUtility {

    private static String[] sPriorityText = null;
    public static String getAlarmTypeText(@ScheduleModel.AlarmType String alarmType) {
        Context context = App.getAppContext();

        switch(alarmType) {
            case ScheduleModel.ALARM_NONE:
                return context.getString(R.string.alarm_type_no_alarm);
            case ScheduleModel.ALARM_10_MINUTES_BEFORE:
                return context.getString(R.string.alarm_type_10_minutes_before);
            case ScheduleModel.ALARM_30_MINUTES_BEFORE:
                return context.getString(R.string.alarm_type_30_minutes_before);
            case ScheduleModel.ALARM_1_HOUR_BEFORE:
                return context.getString(R.string.alarm_type_1_hour_before);
            case ScheduleModel.ALARM_CUSTOM:
                return context.getString(R.string.alarm_type_custom);
            default:
                return context.getString(R.string.alarm_type_custom);
        }
    }

    public static String getScheduleTypeText(@ScheduleModel.ScheduleType String scheduleType) {
        Context context = App.getAppContext();

        switch(scheduleType) {
            case ScheduleModel.SCHEDULE_TYPE_DEFAULT:
                return context.getString(R.string.schedule_type_default);
            case ScheduleModel.SCHEDULE_TYPE_MEETING:
                return context.getString(R.string.schedule_type_meeting);
            case ScheduleModel.SCHEDULE_TYPE_ENTERTAINMENT:
                return context.getString(R.string.schedule_type_entertainment);
            case ScheduleModel.SCHEDULE_TYPE_DATE:
                return context.getString(R.string.schedule_type_date);
            default:
                return context.getString(R.string.schedule_type_default);
        }
    }

    public static int getScheduleColor(@ScheduleModel.ScheduleType String scheduleType) {
        Context context = App.getAppContext();
        switch (scheduleType) {
            case ScheduleModel.SCHEDULE_TYPE_DEFAULT:
                return ContextCompat.getColor(context, R.color.colorDefault);
            case ScheduleModel.SCHEDULE_TYPE_MEETING:
                return ContextCompat.getColor(context, R.color.colorMeeting);
            case ScheduleModel.SCHEDULE_TYPE_ENTERTAINMENT:
                return ContextCompat.getColor(context, R.color.colorEntertainment);
            case ScheduleModel.SCHEDULE_TYPE_DATE:
                return ContextCompat.getColor(context, R.color.colorDate);
            default:
                return ContextCompat.getColor(context, R.color.colorDefault);
        }
    }

    public static String getSchedulePriorityText(@ScheduleModel.Priority int prority){
        Context context = App.getAppContext();

        if(sPriorityText == null) {
            sPriorityText = context.getResources().getStringArray(R.array.schedule_priority_picker_choice_list);
        }

        if(sPriorityText != null){
            return sPriorityText[prority];
        }

        return null;
    }

}
