package com.github.xzwj87.todolist.schedule.presenter;


import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.interactor.DefaultSubscriber;
import com.github.xzwj87.todolist.schedule.interactor.QueryUseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.ui.ScheduleDetailView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

public class ScheduleDetailPresenterImpl implements ScheduleDetailPresenter {
    private static final String LOG_TAG = ScheduleDetailPresenterImpl.class.getSimpleName();

    private static final int INVALID_ID = -1;

    private ScheduleDetailView mScheduleDetailView;
    private int mScheduleId = INVALID_ID;
    private QueryUseCase mUseCase;
    private ScheduleModelDataMapper mMapper;

    public ScheduleDetailPresenterImpl(QueryUseCase useCase, ScheduleModelDataMapper mapper) {
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
            mScheduleDetailView.renderSchedule(scheduleModel);
        }
    }
}
