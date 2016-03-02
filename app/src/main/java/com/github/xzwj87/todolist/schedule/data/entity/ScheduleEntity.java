package com.github.xzwj87.todolist.schedule.data.entity;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

public class ScheduleEntity {

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

    private String mTitle;
    private String mDetail;
    @ScheduleType private String mType;

    private Date mScheduleFrom;
    private Date mScheduleTo;
    @ScheduleRepeatType private String mScheduleRepeatType;

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
    public String getType() {
        return mType;
    }

    public void setType(@ScheduleType String mType) {
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
    public String getScheduleRepeatType() {
        return mScheduleRepeatType;
    }

    public void setScheduleRepeatType(@ScheduleRepeatType String mScheduleRepeatType) {
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
