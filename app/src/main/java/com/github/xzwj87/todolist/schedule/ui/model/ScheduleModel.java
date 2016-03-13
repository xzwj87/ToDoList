package com.github.xzwj87.todolist.schedule.ui.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Date;

public class ScheduleModel {

    public static final String SCHEDULE_TYPE_DEFAULT = "default";
    public static final String SCHEDULE_TYPE_MEETING = "meeting";
    public static final String SCHEDULE_TYPE_ENTERTAINMENT = "entertainment";
    public static final String SCHEDULE_TYPE_DATE = "date";

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

    public static final String ALARM_NONE = "none";
    public static final String ALARM_10_MINUTES_BEFORE = "10_minutes_before";
    public static final String ALARM_30_MINUTES_BEFORE = "30_minutes_before";
    public static final String ALARM_1_HOUR_BEFORE = "1_hour_before";
    public static final String ALARM_CUSTOM = "custom";

    @StringDef({ALARM_NONE, ALARM_10_MINUTES_BEFORE, ALARM_30_MINUTES_BEFORE, ALARM_1_HOUR_BEFORE,
            ALARM_CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlarmType {}

    private static final long MILLISECONDS_IN_10_MINUTES = 10 * 60 * 1000;

    private long mId;

    private String mTitle;
    private String mNote;
    @ScheduleType private String mType;

    private Date mScheduleStart;
    private Date mScheduleEnd;
    @ScheduleRepeatType private String mScheduleRepeatType;

    @AlarmType
    private String mAlarmType;
    private Date mAlarmTime;
    private int mRepeatAlarmTimes;
    private int mRepeatAlarmInterval;

    public static ScheduleModel createDefaultSchedule() {
        ScheduleModel schedule = new ScheduleModel();
        schedule.setTitle("");
        schedule.setNote("");
        schedule.setType(SCHEDULE_TYPE_DEFAULT);

        Calendar current = Calendar.getInstance();
        int minute = current.get(Calendar.MINUTE);
        int hour = current.get(Calendar.HOUR_OF_DAY);

        current.set(Calendar.MINUTE, minute + 10);
        schedule.setScheduleStart(current.getTime());

        current.set(Calendar.HOUR_OF_DAY, hour + 1);
        schedule.setScheduleEnd(current.getTime());

        schedule.setScheduleRepeatType(SCHEDULE_REPEAT_NONE);

        schedule.setAlarmType(ALARM_10_MINUTES_BEFORE);
        schedule.setAlarmTime(
                new Date(schedule.getScheduleStart().getTime() - MILLISECONDS_IN_10_MINUTES));

        schedule.setRepeatAlarmTimes(0);
        schedule.setRepeatAlarmInterval(0);

        return schedule;
    }

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

    public String getNote() {
        return mNote;
    }

    public void setNote(String detail) {
        this.mNote = detail;
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

    @AlarmType
    public String getAlarmType() {
        return mAlarmType;
    }

    public void setAlarmType(@AlarmType String alarmType) {
        mAlarmType = alarmType;
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
        stringBuilder.append("type = " + mType + "\n");
        stringBuilder.append("start = " + mScheduleStart + "\n");
        stringBuilder.append("end = " + mScheduleEnd + "\n");
        stringBuilder.append("alarm type = " + mAlarmType + "\n");
        stringBuilder.append("alarm = " + mAlarmTime + "\n");

        return stringBuilder.toString();
    }
}
