package com.github.xzwj87.todolist.schedule.presenter;


import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.interactor.DefaultSubscriber;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.ui.ScheduleDetailView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.schedule.utility.ScheduleUtility;

import java.text.SimpleDateFormat;

public class ScheduleDetailPresenterImpl implements ScheduleDetailPresenter {
    private static final String LOG_TAG = ScheduleDetailPresenterImpl.class.getSimpleName();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM d, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");

    private ScheduleDetailView mScheduleDetailView;
    private UseCase mUseCase;
    private ScheduleModelDataMapper mMapper;

    public ScheduleDetailPresenterImpl(UseCase useCase, ScheduleModelDataMapper mapper) {
        mUseCase = useCase;
        mMapper = mapper;
    }

    @Override
    public void setView(@NonNull ScheduleDetailView view) {
        mScheduleDetailView = view;
    }

    @Override
    public void initialize() {
        loadScheduleDetails();
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        Log.v(LOG_TAG, "destroy()");
        mUseCase.unsubscribe();
        mScheduleDetailView = null;
    }

    private void updateScheduleToView(ScheduleModel schedule) {
        mScheduleDetailView.updateScheduleTitle(schedule.getTitle());

        String scheduleDate = DATE_FORMAT.format(schedule.getScheduleStart()) + "-" +
                DATE_FORMAT.format(schedule.getScheduleEnd());
        mScheduleDetailView.updateScheduleDate(scheduleDate);

        String scheduleTime = TIME_FORMAT.format(schedule.getScheduleStart()) + "-" +
                TIME_FORMAT.format(schedule.getScheduleEnd());
        mScheduleDetailView.updateScheduleTime(scheduleTime);

        mScheduleDetailView.updateAlarmTime(TIME_FORMAT.format(schedule.getAlarmTime()));

        mScheduleDetailView.updateScheduleType(
                ScheduleUtility.getScheduleTypeText(schedule.getType()));

        mScheduleDetailView.updateScheduleNote(schedule.getNote());

    }

    private void loadScheduleDetails() {
        mUseCase.execute(new ScheduleDetailsSubscriber());
    }

    private final class ScheduleDetailsSubscriber extends DefaultSubscriber<Cursor> {

        @Override public void onCompleted() {}

        @Override public void onError(Throwable e) {}

        @Override public void onNext(Cursor cursor) {
            cursor.moveToFirst();
            ScheduleModel scheduleModel = mMapper.transform(cursor);
            Log.v(LOG_TAG, "onNext(): scheduleModel = " + scheduleModel);
            cursor.close();
            updateScheduleToView(scheduleModel);
        }
    }
}
