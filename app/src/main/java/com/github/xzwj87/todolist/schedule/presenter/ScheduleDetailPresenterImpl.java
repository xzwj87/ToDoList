package com.github.xzwj87.todolist.schedule.presenter;


import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.interactor.DefaultSubscriber;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.internal.di.PerActivity;
import com.github.xzwj87.todolist.schedule.ui.ScheduleDetailView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.schedule.utility.ScheduleUtility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.inject.Inject;
import javax.inject.Named;

@PerActivity
public class ScheduleDetailPresenterImpl implements ScheduleDetailPresenter {
    private static final String LOG_TAG = ScheduleDetailPresenterImpl.class.getSimpleName();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM dd, yyyy");
    //private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");

    private ScheduleDetailView mScheduleDetailView;
    private UseCase mGetDetailUseCase;
    private UseCase mDeleteUseCase;
    private ScheduleModelDataMapper mMapper;

    @Inject
    public ScheduleDetailPresenterImpl(@Named("getScheduleById")UseCase getDetailUseCase,
                                       @Named("deleteSchedule")UseCase deleteUseCase,
                                       ScheduleModelDataMapper mapper) {
        mGetDetailUseCase = getDetailUseCase;
        mDeleteUseCase = deleteUseCase;
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
        mGetDetailUseCase.unsubscribe();
        mScheduleDetailView = null;
    }

    @Override
    public void onDeleteSchedule(boolean isConfirmed) {
        Log.v(LOG_TAG, "onDeleteSchedule(): isConfirmed = " + isConfirmed);
        if (!isConfirmed) {
            mScheduleDetailView.requestConfirmDelete();
        } else {
            mDeleteUseCase.execute(new DeleteScheduleSubscriber());
        }
    }

    private void updateScheduleToView(ScheduleModel schedule) {
        mScheduleDetailView.updateScheduleTitle(schedule.getTitle());

        // if start date is equal to end date, just display one
        String scheduleStartDate = DATE_FORMAT.format(schedule.getScheduleStart());
        String scheduleEndDate = DATE_FORMAT.format(schedule.getScheduleEnd());
        String scheduleDate;
        if(scheduleStartDate.equals(scheduleEndDate)){
            scheduleDate = scheduleStartDate;
        }else{
            scheduleDate = scheduleStartDate + "-" + scheduleEndDate;
        }
        mScheduleDetailView.updateScheduleDate(scheduleDate);

        String scheduleTime = TIME_FORMAT.format(schedule.getScheduleStart()) + "-" +
                TIME_FORMAT.format(schedule.getScheduleEnd());
        mScheduleDetailView.updateScheduleTime(scheduleTime);

        mScheduleDetailView.updateAlarmTime(TIME_FORMAT.format(schedule.getAlarmTime()));

        mScheduleDetailView.updateScheduleType(
                ScheduleUtility.getScheduleTypeText(schedule.getType()));

        mScheduleDetailView.updateScheduleNote(schedule.getNote());

        mScheduleDetailView.updateSchedulePriority(schedule.getPriority());

    }

    private void loadScheduleDetails() {
        mGetDetailUseCase.execute(new ScheduleDetailsSubscriber());
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

    private final class DeleteScheduleSubscriber extends DefaultSubscriber<Integer> {

        @Override public void onCompleted() {}

        @Override public void onError(Throwable e) {}

        @Override public void onNext(Integer deleted) {
            Log.v(LOG_TAG, "DeleteScheduleSubscriber onNext(): deleted = " + deleted);
            if (mScheduleDetailView != null) {
                mScheduleDetailView.finishView();
            }
        }
    }
}
