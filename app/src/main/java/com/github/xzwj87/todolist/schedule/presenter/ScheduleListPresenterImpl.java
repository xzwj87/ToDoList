package com.github.xzwj87.todolist.schedule.presenter;


import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.interactor.DefaultSubscriber;
import com.github.xzwj87.todolist.schedule.interactor.QueryUseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.ui.ScheduleListView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

public class ScheduleListPresenterImpl implements ScheduleListPresenter {
    private static final String LOG_TAG = ScheduleListPresenterImpl.class.getSimpleName();

    private ScheduleListView mScheduleListView;
    private QueryUseCase mUseCase;
    private ScheduleModelDataMapper mMapper;
    private Cursor mCursor;

    public ScheduleListPresenterImpl(QueryUseCase useCase, ScheduleModelDataMapper mapper) {
        mUseCase = useCase;
        mMapper = mapper;
    }

    @Override
    public void setView(@NonNull ScheduleListView view) {
        mScheduleListView = view;
    }

    @Override
    public void initialize() {
        loadScheduleList();
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        mScheduleListView = null;
        mUseCase.unsubscribe();
    }

    @Override
    public ScheduleModel getScheduleAtPosition(int position) {
        if (mCursor == null) {
            throw new RuntimeException("Schedule list not loaded yet.");
        }

        mCursor.moveToPosition(position);
        ScheduleModel scheduleModel = mMapper.transform(mCursor);
        Log.v(LOG_TAG, "getScheduleAtPosition(): position = " + position +
                ", scheduleModel = " + scheduleModel);
        return scheduleModel;
    }

    @Override
    public int getScheduleItemCount() {
        int count = mCursor == null ? 0: mCursor.getCount();
        Log.v(LOG_TAG, "getScheduleItemCount(): count = " + count);
        return count;
    }

    private void loadScheduleList() {
        mUseCase.execute(new ScheduleListSubscriber());
    }

    private final class ScheduleListSubscriber extends DefaultSubscriber<Cursor> {

        @Override public void onCompleted() {}

        @Override public void onError(Throwable e) {}

        @Override public void onNext(Cursor cursor) {
            mCursor = cursor;
            Log.v(LOG_TAG, "onNext(): cursor size = " + cursor.getCount());
            mScheduleListView.renderScheduleList();
        }
    }
}
