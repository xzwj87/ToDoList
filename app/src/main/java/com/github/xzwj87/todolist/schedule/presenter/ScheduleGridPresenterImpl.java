package com.github.xzwj87.todolist.schedule.presenter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.interactor.DefaultSubscriber;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.delete.DeleteScheduleArg;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.observer.ScheduleDataObserver;
import com.github.xzwj87.todolist.schedule.interactor.update.MarkScheduleAsDoneArg;
import com.github.xzwj87.todolist.schedule.ui.ScheduleGridView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

/**
 * Created by JasonWang on 2016/5/15.
 */
public class ScheduleGridPresenterImpl implements ScheduleGridPresenter {
    public static final String LOG_TAG = ScheduleGridPresenterImpl.class.getSimpleName();

    private UseCase mGetListUseCase = null;
    private UseCase mMarkedAsDoneUseCase = null;
    private UseCase mDeleteScheduleUseCase = null;
    private ScheduleModelDataMapper mDataMapper = null;
    private Cursor mCursor = null;
    private ScheduleGridView mScheduleGridView = null;

    public ScheduleGridPresenterImpl(UseCase getList, UseCase markedAsDone,
                                     UseCase deleteSchedule, ScheduleModelDataMapper mapper){
        mGetListUseCase = getList;
        mMarkedAsDoneUseCase = markedAsDone;
        mDeleteScheduleUseCase = deleteSchedule;
        mDataMapper = mapper;
    }

    @Override
    public void setView(@NonNull ScheduleGridView view) {
        mScheduleGridView = view;
    }

    @Override
    public void initialize() {
        loadScheduleData();
    }

    @Override
    public ScheduleModel getScheduleAtPosition(int position) {
        if(mCursor == null){
            Log.e(LOG_TAG,"cursor is null for data is not loaded yet");
            return null;
        }

        mCursor.moveToPosition(position);
        ScheduleModel model = mDataMapper.transform(mCursor);
        Log.v(LOG_TAG, "getScheduleAtPosition: position = " + position);
        return model;
    }

    @Override
    public int getScheduleItemCount() {
        if(mCursor == null) return 0;

        return mCursor.getCount();
    }

    @Override
    public void markAsDone(long[] ids, boolean markAsDone) {
        mMarkedAsDoneUseCase.init(new MarkScheduleAsDoneArg(ids,markAsDone))
                            .execute(new MarkedAsDoneSubscriber());
    }

    @Override
    public void onDeleteSchedule(long id, boolean isConfirmed) {
        Log.v(LOG_TAG, "onDeleteSchedule(): isConfirmed = " + isConfirmed);
        if(isConfirmed) {
            mDeleteScheduleUseCase.init(new DeleteScheduleArg(id))
                    .execute(new DeleteScheduleSubscriber());
        }else{
            mScheduleGridView.requestConfirmDelete(id,isConfirmed);
        }
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        mGetListUseCase.unsubscribe();
        mGetListUseCase = null;
    }

    private void loadScheduleData(){
        Log.v(LOG_TAG,"loadScheduleData()");
        mGetListUseCase.execute(new GetScheduleListSubscriber());
    }


    private final class GetScheduleListSubscriber extends DefaultSubscriber<Cursor>{
        @Override
        public void onCompleted(){}

        @Override
        public void onError(Throwable throwable){}

        @Override
        public void onNext(Cursor cursor){
            mCursor = cursor;
            Log.v(LOG_TAG, "onNext(): cursor count = " + cursor.getCount());
            mScheduleGridView.renderScheduleList();
        }
    }

    private final class MarkedAsDoneSubscriber extends DefaultSubscriber<Integer>{
        @Override
        public void onCompleted(){}

        @Override
        public void onError(Throwable throwable){}

        @Override
        public void onNext(Integer updated){
            Log.v(LOG_TAG,"onNext(): updated = " + updated);
        }
    }

    private final class DeleteScheduleSubscriber extends DefaultSubscriber<Integer>{
        @Override
        public void onCompleted(){}

        @Override
        public void onError(Throwable throwable){}

        @Override
        public void onNext(Integer deleted){
            Log.v(LOG_TAG,"onNext(): deleted = " + deleted);
        }
    }
}
