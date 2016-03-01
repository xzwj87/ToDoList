package com.github.xzwj87.todolist.schedule.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

public class Schedule {

    public static final int SCHEDULE_TYPE_DEFAULT = 0;
    public static final int SCHEDULE_TYPE_MEETING = 1;
    public static final int SCHEDULE_TYPE_DATE = 2;
    public static final int SCHEDULE_TYPE_ENTERTAINMENT = 3;

    @IntDef({SCHEDULE_TYPE_DEFAULT, SCHEDULE_TYPE_MEETING, SCHEDULE_TYPE_DATE,
            SCHEDULE_TYPE_ENTERTAINMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScheduleType {}


    public static final int SCHEDULE_REPEAT_NONE = 0;
    public static final int SCHEDULE_REPEAT_EVERY_DAY = 1;
    public static final int SCHEDULE_REPEAT_EVERY_WEEK = 2;
    public static final int SCHEDULE_REPEAT_EVERY_MONTH = 3;
    public static final int SCHEDULE_REPEAT_EVERY_YEAR = 4;

    @IntDef({SCHEDULE_REPEAT_NONE, SCHEDULE_REPEAT_EVERY_DAY, SCHEDULE_REPEAT_EVERY_WEEK,
            SCHEDULE_REPEAT_EVERY_MONTH, SCHEDULE_REPEAT_EVERY_YEAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScheduleRepeatType {}

    private String mTitle;
    private String mDetail;
    @ScheduleType private int mType;

    private Date mScheduleFrom;
    private Date mScheduleTo;
    @ScheduleRepeatType private int mScheduleRepeatType;

    private Date mAlarmTime;
    private int mRepeatAlarmTimes;
    private int mRepeatAlarmInterval;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String mDetail) {
        this.mDetail = mDetail;
    }

    @ScheduleType
    public int getType() {
        return mType;
    }

    public void setType(@ScheduleType int mType) {
        this.mType = mType;
    }

    public Date getScheduleFrom() {
        return mScheduleFrom;
    }

    public void setScheduleFrom(Date mScheduleFrom) {
        this.mScheduleFrom = mScheduleFrom;
    }

    public Date getScheduleTo() {
        return mScheduleTo;
    }

    public void setScheduleTo(Date mScheduleTo) {
        this.mScheduleTo = mScheduleTo;
    }

    @ScheduleRepeatType
    public int getScheduleRepeatType() {
        return mScheduleRepeatType;
    }

    public void setScheduleRepeatType(@ScheduleRepeatType int mScheduleRepeatType) {
        this.mScheduleRepeatType = mScheduleRepeatType;
    }

    public Date getAlarmTime() {
        return mAlarmTime;
    }

    public void setAlarmTime(Date mAlarmTime) {
        this.mAlarmTime = mAlarmTime;
    }

    public int getRepeatAlarmTimes() {
        return mRepeatAlarmTimes;
    }

    public void setRepeatAlarmTimes(int mRepeatAlarmTimes) {
        this.mRepeatAlarmTimes = mRepeatAlarmTimes;
    }

    public int getRepeatAlarmInterval() {
        return mRepeatAlarmInterval;
    }

    public void setRepeatAlarmInterval(int mRepeatAlarmInterval) {
        this.mRepeatAlarmInterval = mRepeatAlarmInterval;
    }

}
