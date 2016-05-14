package com.github.xzwj87.todolist.schedule.presenter;


import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.xzwj87.todolist.schedule.interactor.DefaultSubscriber;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.delete.DeleteSchedule;
import com.github.xzwj87.todolist.schedule.interactor.delete.DeleteScheduleArg;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.interactor.update.MarkScheduleAsDone;
import com.github.xzwj87.todolist.schedule.interactor.update.MarkScheduleAsDoneArg;
import com.github.xzwj87.todolist.schedule.ui.ScheduleListView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

public class ScheduleListPresenterImpl implements ScheduleListPresenter {
    private static final String LOG_TAG = ScheduleListPresenterImpl.class.getSimpleName();

    private ScheduleListView mScheduleListView;
    private UseCase mGetList;
    private UseCase mMarkAsDone;
    private UseCase mDeleteUseCase;
    private ScheduleModelDataMapper mMapper;
    private Cursor mCursor;
    private long[] mLastMarkedIds;
    private boolean mLastMarkAsDone;

    public ScheduleListPresenterImpl(UseCase getList, UseCase markAsDone,
                                     UseCase deleteSchedule, ScheduleModelDataMapper mapper) {

        mGetList = getList;
        mMarkAsDone = markAsDone;
        mDeleteUseCase = deleteSchedule;
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
        mGetList.unsubscribe();
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

    @Override @SuppressWarnings("unchecked")
    public void markAsDone(long[] ids, boolean markAsDone) {
        mLastMarkedIds = ids;
        mLastMarkAsDone = markAsDone;
        mMarkAsDone.init(new MarkScheduleAsDoneArg(ids, markAsDone))
                .execute(new MarkAsDoneScheduleSubscriber());
    }

    @Override
    public void undoLastMarkAsDone() {

    }

    @Override
    public void onDeleteSchedule(long id,boolean isConfirmed) {
        Log.v(LOG_TAG, "onDeleteSchedule(): isConfirmed = " + isConfirmed);
        if (!isConfirmed) {
            mScheduleListView.requestConfirmDelete(id);
        } else {
            mDeleteUseCase.init(new DeleteScheduleArg(id))
                          .execute(new DeleteScheduleSubscriber());
        }
    }

    private void loadScheduleList() {
        mGetList.execute(new ScheduleListSubscriber());
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

    private final class MarkAsDoneScheduleSubscriber extends DefaultSubscriber<Integer> {

        @Override public void onCompleted() {}

        @Override public void onError(Throwable e) {}

        @Override public void onNext(Integer updated) {
            Log.v(LOG_TAG, "onNext(): updated = " + updated);
            // mScheduleListView.renderScheduleList();
        }
    }

    private final class DeleteScheduleSubscriber extends DefaultSubscriber<Integer> {

        @Override public void onCompleted() {}

        @Override public void onError(Throwable e) {}

        @Override public void onNext(Integer deleted) {
            Log.v(LOG_TAG, "DeleteScheduleSubscriber onNext(): deleted = " + deleted);
            // refresh the schedule list
            mScheduleListView.renderScheduleList();
        }
    }
}
