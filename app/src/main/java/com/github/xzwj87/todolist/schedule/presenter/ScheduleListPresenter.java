package com.github.xzwj87.todolist.schedule.presenter;

import android.support.annotation.NonNull;

import com.github.xzwj87.todolist.schedule.ui.ScheduleListView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

public interface ScheduleListPresenter extends Presenter {

    void setView(@NonNull ScheduleListView view);

    void initialize();

    ScheduleModel getScheduleAtPosition(int position);

    int getScheduleItemCount();

    void markAsDone(long[] ids, boolean markAsDone);

    void undoLastMarkAsDone();

    void onDeleteSchedule(long id, boolean isConfirmed);

}
