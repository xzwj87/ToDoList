package com.github.xzwj87.todolist.schedule.presenter;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.ui.AddScheduleView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddSchedulePresenterImpl implements AddSchedulePresenter {
    private static final String LOG_TAG = AddSchedulePresenterImpl.class.getSimpleName();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM d, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");

    private AddScheduleView mAddScheduleView;
    private Calendar mScheduleStart;
    private Calendar mScheduleEnd;

    public AddSchedulePresenterImpl() {

    }

    @Override
    public void setView(@NonNull AddScheduleView view) {
        mAddScheduleView = view;

        initCalendarsWithCurrentTime();

        mAddScheduleView.updateStartDateDisplay(DATE_FORMAT.format(mScheduleStart.getTime()));
        mAddScheduleView.updateEndDateDisplay(DATE_FORMAT.format(mScheduleEnd.getTime()));
        mAddScheduleView.updateStartTimeDisplay(TIME_FORMAT.format(mScheduleStart.getTime()));
        mAddScheduleView.updateEndTimeDisplay(TIME_FORMAT.format(mScheduleEnd.getTime()));
    }

    @Override
    public void setStartDate() {
        mAddScheduleView.showPickStartDateDlg(mScheduleStart.get(Calendar.YEAR),
                mScheduleStart.get(Calendar.MONTH), mScheduleStart.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setEndDate() {
        mAddScheduleView.showPickEndDateDlg(mScheduleStart.get(Calendar.YEAR),
                mScheduleStart.get(Calendar.MONTH), mScheduleStart.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setStartTime() {
        mAddScheduleView.showPickStartTimeDlg(mScheduleStart.get(Calendar.HOUR_OF_DAY),
                mScheduleStart.get(Calendar.MINUTE), mScheduleStart.get(Calendar.SECOND));
    }

    @Override
    public void setEndTime() {
        mAddScheduleView.showPickEndTimeDlg(mScheduleStart.get(Calendar.HOUR_OF_DAY),
                mScheduleStart.get(Calendar.MINUTE), mScheduleStart.get(Calendar.SECOND));
    }

    @Override
    public void onStartDateSet(int year, int monthOfYear, int dayOfMonth) {
        mScheduleStart.set(Calendar.YEAR, year);
        mScheduleStart.set(Calendar.MONTH, monthOfYear);
        mScheduleStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mAddScheduleView.updateStartDateDisplay(DATE_FORMAT.format(mScheduleStart.getTime()));
    }

    @Override
    public void onEndDateSet(int year, int monthOfYear, int dayOfMonth) {
        mScheduleEnd.set(Calendar.YEAR, year);
        mScheduleEnd.set(Calendar.MONTH, monthOfYear);
        mScheduleEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mAddScheduleView.updateEndDateDisplay(DATE_FORMAT.format(mScheduleEnd.getTime()));
    }

    @Override
    public void onStartTimeSet(int hourOfDay, int minute, int second) {
        mScheduleStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mScheduleStart.set(Calendar.MINUTE, minute);
        mScheduleStart.set(Calendar.SECOND, second);
        mAddScheduleView.updateStartTimeDisplay(TIME_FORMAT.format(mScheduleStart.getTime()));
    }

    @Override
    public void onEndTimeSet(int hourOfDay, int minute, int second) {
        mScheduleEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mScheduleEnd.set(Calendar.MINUTE, minute);
        mScheduleEnd.set(Calendar.SECOND, second);
        mAddScheduleView.updateEndTimeDisplay(TIME_FORMAT.format(mScheduleEnd.getTime()));
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        mAddScheduleView = null;
    }

    @Override
    public void onSave() {
        ContentValues schedule = getSchedule();
        Uri scheduleInsertUri = mAddScheduleView.getViewContext().getContentResolver()
                .insert(ScheduleContract.ScheduleEntry.CONTENT_URI, schedule);
        Log.v(LOG_TAG, "onSave(): Saved to " + scheduleInsertUri);
    }

    private void initCalendarsWithCurrentTime() {
        mScheduleStart = Calendar.getInstance();
        int minute = mScheduleStart.get(Calendar.MINUTE);
        mScheduleStart.set(Calendar.MINUTE, minute + 10);

        mScheduleEnd = Calendar.getInstance();
        int hourOfDay = mScheduleEnd.get(Calendar.HOUR_OF_DAY);
        mScheduleEnd.set(Calendar.HOUR_OF_DAY, hourOfDay + 1);
        mScheduleEnd.set(Calendar.MINUTE, minute + 10);

        Log.v(LOG_TAG, "initCalendarsWithCurrentTime(): mScheduleStart = "
                + mScheduleStart.getTime() + ", mScheduleEnd = " + mScheduleEnd.getTime());
    }

    private ContentValues getSchedule() {
        Log.v(LOG_TAG, "getSchedule(): Start date = " + mScheduleStart.getTime() + ", end date = " + mScheduleEnd.getTime());
        ContentValues scheduleValues = new ContentValues();
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_TITLE, mAddScheduleView.getScheduleTitle());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DETAIL, "None");
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_TYPE, "None");
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DATE_START, mScheduleStart.getTimeInMillis());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DATE_END, mScheduleEnd.getTimeInMillis());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_SCHEDULE, 1);
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME, mScheduleStart.getTimeInMillis());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES, 1);
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL, 10);

        Log.v(LOG_TAG, "getSchedule(): scheduleValues = " + scheduleValues);
        return scheduleValues;
    }
}
