package com.github.xzwj87.todolist.schedule.presenter;

import android.support.annotation.NonNull;

import com.github.xzwj87.todolist.schedule.observer.ScheduleDataObserver;
import com.github.xzwj87.todolist.schedule.ui.ScheduleGridView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

/**
 * Created by JasonWang on 2016/5/15.
 */
public interface ScheduleGridPresenter extends Presenter{
    void setView(@NonNull ScheduleGridView view);

    void initialize();

    ScheduleModel getScheduleAtPosition(int position);

    int getScheduleItemCount();

    void markAsDone(long[] ids, boolean markAsDone);

    void onDeleteSchedule(long id, boolean isConfirmed);
}
