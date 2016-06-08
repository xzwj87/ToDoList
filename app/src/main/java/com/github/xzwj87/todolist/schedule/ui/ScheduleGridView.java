package com.github.xzwj87.todolist.schedule.ui;

import com.github.xzwj87.todolist.schedule.observer.ScheduleDataObserver;

/**
 * Created by JasonWang on 2016/5/15.
 */
public interface ScheduleGridView{

    void renderScheduleList();

    void requestConfirmDelete(long id,boolean isConfirmed);
    //void onDataSetChanged();
}
