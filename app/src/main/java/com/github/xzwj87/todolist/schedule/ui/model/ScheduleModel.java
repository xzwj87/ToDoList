package com.github.xzwj87.todolist.schedule.ui.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

public class ScheduleModel {

    public static final String SCHEDULE_TYPE_DEFAULT = "default";
    public static final String SCHEDULE_TYPE_MEETING = "meeting";
    public static final String SCHEDULE_TYPE_DATE = "date";
    public static final String SCHEDULE_TYPE_ENTERTAINMENT = "entertainment";

    @StringDef({SCHEDULE_TYPE_DEFAULT, SCHEDULE_TYPE_MEETING, SCHEDULE_TYPE_DATE,
            SCHEDULE_TYPE_ENTERTAINMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScheduleType {}


    public static final String SCHEDULE_REPEAT_NONE = "none";
    public static final String SCHEDULE_REPEAT_EVERY_DAY = "every_day";
    public static final String SCHEDULE_REPEAT_EVERY_WEEK = "every_week";
    public static final String SCHEDULE_REPEAT_EVERY_MONTH = "every_month";
    public static final String SCHEDULE_REPEAT_EVERY_YEAR = "every_year";

    @StringDef({SCHEDULE_REPEAT_NONE, SCHEDULE_REPEAT_EVERY_DAY, SCHEDULE_REPEAT_EVERY_WEEK,
            SCHEDULE_REPEAT_EVERY_MONTH, SCHEDULE_REPEAT_EVERY_YEAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScheduleRepeatType {}

    private long mId;

    private String mTitle;
    private String mDetail;
    @ScheduleType private String mType;

    private Date mScheduleStart;
    private Date mScheduleEnd;
    @ScheduleRepeatType private String mScheduleRepeatType;

    private Date mAlarmTime;
    private int mRepeatAlarmTimes;
    private int mRepeatAlarmInterval;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String detail) {
        this.mDetail = detail;
    }

    @ScheduleType
    public String getType() {
        return mType;
    }

    public void setType(@ScheduleType String type) {
        this.mType = type;
    }

    public Date getScheduleStart() {
        return mScheduleStart;
    }

    public void setScheduleStart(Date scheduleFrom) {
        this.mScheduleStart = scheduleFrom;
    }

    public Date getScheduleEnd() {
        return mScheduleEnd;
    }

    public void setScheduleEnd(Date scheduleEnd) {
        this.mScheduleEnd = scheduleEnd;
    }

    @ScheduleRepeatType
    public String getScheduleRepeatType() {
        return mScheduleRepeatType;
    }

    public void setScheduleRepeatType(@ScheduleRepeatType String scheduleRepeatType) {
        this.mScheduleRepeatType = scheduleRepeatType;
    }

    public Date getAlarmTime() {
        return mAlarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.mAlarmTime = alarmTime;
    }

    public int getRepeatAlarmTimes() {
        return mRepeatAlarmTimes;
    }

    public void setRepeatAlarmTimes(int repeatAlarmTimes) {
        this.mRepeatAlarmTimes = repeatAlarmTimes;
    }

    public int getRepeatAlarmInterval() {
        return mRepeatAlarmInterval;
    }

    public void setRepeatAlarmInterval(int repeatAlarmInterval) {
        this.mRepeatAlarmInterval = repeatAlarmInterval;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("***** ScheduleModel Details *****\n");
        stringBuilder.append("id = " + mId + "\n");
        stringBuilder.append("title = " + mTitle + "\n");
        stringBuilder.append("start = " + mScheduleStart + "\n");
        stringBuilder.append("end = " + mScheduleEnd + "\n");
        stringBuilder.append("alarm = " + mAlarmTime + "\n");

        return stringBuilder.toString();
    }
}
