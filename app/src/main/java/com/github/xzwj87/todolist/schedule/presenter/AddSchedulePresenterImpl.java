package com.github.xzwj87.todolist.schedule.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.interactor.DefaultSubscriber;
import com.github.xzwj87.todolist.schedule.interactor.InsertUseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleContentValuesDataMapper;
import com.github.xzwj87.todolist.schedule.ui.AddScheduleView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddSchedulePresenterImpl implements AddSchedulePresenter {
    private static final String LOG_TAG = AddSchedulePresenterImpl.class.getSimpleName();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM d, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");

    private InsertUseCase mUseCase;
    private ScheduleContentValuesDataMapper mMapper;
    private AddScheduleView mAddScheduleView;
    private Calendar mScheduleStart;
    private Calendar mScheduleEnd;

    public AddSchedulePresenterImpl(InsertUseCase useCase,
                                    ScheduleContentValuesDataMapper mapper) {
        mUseCase = useCase;
        mMapper = mapper;
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
        mUseCase.unsubscribe();
    }

    @Override
    public void onSave() {
        mUseCase.execute(mMapper.transform(getSchedule()), new AddScheduleSubscriber());
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

    private ScheduleModel getSchedule() {
        ScheduleModel schedule = new ScheduleModel();

        schedule.setTitle(mAddScheduleView.getScheduleTitle());
        schedule.setDetail("None");
        schedule.setType(ScheduleModel.SCHEDULE_TYPE_DEFAULT);
        schedule.setScheduleStart(mScheduleStart.getTime());
        schedule.setScheduleEnd(mScheduleEnd.getTime());
        schedule.setScheduleRepeatType(ScheduleModel.SCHEDULE_REPEAT_EVERY_DAY);
        schedule.setAlarmTime(mScheduleStart.getTime());
        schedule.setRepeatAlarmTimes(1);
        schedule.setRepeatAlarmInterval(10);

        return schedule;
    }

    private final class AddScheduleSubscriber extends DefaultSubscriber<Long> {

        @Override public void onCompleted() {}

        @Override public void onError(Throwable e) {}

        @Override public void onNext(Long id) {
            Log.v(LOG_TAG, "onNext(): id = " + id);
        }
    }
}
