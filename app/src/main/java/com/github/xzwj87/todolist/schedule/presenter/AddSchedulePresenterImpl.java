package com.github.xzwj87.todolist.schedule.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.interactor.DefaultSubscriber;
import com.github.xzwj87.todolist.schedule.interactor.InsertUseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleContentValuesDataMapper;
import com.github.xzwj87.todolist.schedule.ui.AddScheduleView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.schedule.utility.ScheduleUtility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddSchedulePresenterImpl implements AddSchedulePresenter {
    private static final String LOG_TAG = AddSchedulePresenterImpl.class.getSimpleName();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM d, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");

    private static final long MILLISECONDS_IN_10_MINUTES = 10 * 60 * 1000;
    private static final long MILLISECONDS_IN_30_MINUTES = MILLISECONDS_IN_10_MINUTES * 3;
    private static final long MILLISECONDS_IN_1_HOUR = MILLISECONDS_IN_30_MINUTES * 2;

    private InsertUseCase mUseCase;
    private ScheduleContentValuesDataMapper mMapper;
    private AddScheduleView mAddScheduleView;

    private ScheduleModel mSchedule;

    public AddSchedulePresenterImpl(InsertUseCase useCase,
                                    ScheduleContentValuesDataMapper mapper) {
        mUseCase = useCase;
        mMapper = mapper;
    }

    @Override
    public void setView(@NonNull AddScheduleView view) {
        mAddScheduleView = view;
    }

    @Override
    public void initialize() {
        mSchedule = ScheduleModel.createDefaultSchedule();
        Log.v(LOG_TAG, "initialize(): mSchedule = " + mSchedule);

        mAddScheduleView.updateStartDateDisplay(DATE_FORMAT.format(mSchedule.getScheduleStart()));
        mAddScheduleView.updateEndDateDisplay(DATE_FORMAT.format(mSchedule.getScheduleEnd()));

        mAddScheduleView.updateStartTimeDisplay(TIME_FORMAT.format(mSchedule.getScheduleStart()));
        mAddScheduleView.updateEndTimeDisplay(TIME_FORMAT.format(mSchedule.getScheduleEnd()));

        mAddScheduleView.updateAlarmTypeDisplay(
                ScheduleUtility.getAlarmTypeText(mSchedule.getAlarmType()));

        mAddScheduleView.updateScheduleTypeDisplay(
                ScheduleUtility.getScheduleTypeText(mSchedule.getType()));
    }

    @Override
    public void setStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mSchedule.getScheduleStart());
        mAddScheduleView.showPickStartDateDlg(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setEndDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mSchedule.getScheduleEnd());
        mAddScheduleView.showPickEndDateDlg(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mSchedule.getScheduleStart());
        mAddScheduleView.showPickStartTimeDlg(calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    @Override
    public void setEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mSchedule.getScheduleEnd());
        mAddScheduleView.showPickEndTimeDlg(calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    @Override
    public void setAlarmType() {
        mAddScheduleView.showPickAlarmTypeDlg(mSchedule.getAlarmType());
    }

    @Override
    public void setScheduleType() {
        mAddScheduleView.showPickScheduleTypeDlg(mSchedule.getType());
    }

    @Override
    public void onTitleSet(String title) {
        mSchedule.setTitle(title);
    }

    @Override
    public void onStartDateSet(int year, int monthOfYear, int dayOfMonth) {
        Date startDate = updateDateFromBase(mSchedule.getScheduleStart(),
                year, monthOfYear, dayOfMonth);
        mSchedule.setScheduleStart(startDate);
        mAddScheduleView.updateStartDateDisplay(DATE_FORMAT.format(mSchedule.getScheduleStart()));
    }

    @Override
    public void onStartTimeSet(int hourOfDay, int minute, int second) {
        Date startTime = updateTimeFromBase(mSchedule.getScheduleStart(),
                hourOfDay, minute, second);
        mSchedule.setScheduleStart(startTime);
        mAddScheduleView.updateStartTimeDisplay(TIME_FORMAT.format(mSchedule.getScheduleStart()));
    }

    @Override
    public void onEndDateSet(int year, int monthOfYear, int dayOfMonth) {
        Date endDate = updateDateFromBase(mSchedule.getScheduleEnd(),
                year, monthOfYear, dayOfMonth);
        mSchedule.setScheduleEnd(endDate);
        mAddScheduleView.updateEndDateDisplay(DATE_FORMAT.format(mSchedule.getScheduleEnd()));
    }

    @Override
    public void onEndTimeSet(int hourOfDay, int minute, int second) {
        Date endTime = updateTimeFromBase(mSchedule.getScheduleEnd(),
                hourOfDay, minute, second);
        mSchedule.setScheduleEnd(endTime);
        mAddScheduleView.updateEndTimeDisplay(TIME_FORMAT.format(mSchedule.getScheduleEnd()));
    }

    @Override
    public void onAlarmTypeSet(@ScheduleModel.AlarmType String alarmType) {
        Log.v(LOG_TAG, "onAlarmTypeSet(): alarmType = " + alarmType);
        mSchedule.setAlarmType(alarmType);

        Date alarmTime = getAlarmTimeByType(mSchedule.getScheduleStart(), alarmType);
        mSchedule.setAlarmTime(alarmTime);

        mAddScheduleView.updateAlarmTypeDisplay(
                ScheduleUtility.getAlarmTypeText(mSchedule.getAlarmType()));
    }

    @Override
    public void onScheduleTypeSet(@ScheduleModel.AlarmType String scheduleType) {
        Log.v(LOG_TAG, "onScheduleTypeSet(): scheduleType = " + scheduleType);
        mSchedule.setType(scheduleType);
        mAddScheduleView.updateScheduleTypeDisplay(
                ScheduleUtility.getScheduleTypeText(mSchedule.getType()));
    }

    @Override
    public void onNoteSet(String note) {
        mSchedule.setNote(note);
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        mAddScheduleView = null;
        mUseCase.unsubscribe();
    }

    @Override
    public void onSave() {
        mUseCase.execute(mMapper.transform(mSchedule), new AddScheduleSubscriber());
    }

    private final class AddScheduleSubscriber extends DefaultSubscriber<Long> {

        @Override public void onCompleted() {}

        @Override public void onError(Throwable e) {}

        @Override public void onNext(Long id) {
            Log.v(LOG_TAG, "onNext(): id = " + id);
        }
    }

    private Date getAlarmTimeByType(Date schedule, @ScheduleModel.AlarmType String alarmType) {
        switch (alarmType) {
            case ScheduleModel.ALARM_10_MINUTES_BEFORE:
                return new Date(schedule.getTime() - MILLISECONDS_IN_10_MINUTES);
            case ScheduleModel.ALARM_30_MINUTES_BEFORE:
                return new Date(schedule.getTime() - MILLISECONDS_IN_30_MINUTES);
            case ScheduleModel.ALARM_1_HOUR_BEFORE:
                return new Date(schedule.getTime() - MILLISECONDS_IN_1_HOUR);
            default:
                return new Date(schedule.getTime());
        }
    }

    private Date updateDateFromBase(Date base, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        return calendar.getTime();
    }

    private Date updateTimeFromBase(Date base, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }

}
