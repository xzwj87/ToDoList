package com.github.xzwj87.todolist.schedule.interactor.insert;


import android.content.ContentValues;

public class AddScheduleArg {
    private final ContentValues mSchedule;

    public AddScheduleArg(ContentValues schedule) {
        mSchedule = schedule;
    }

    public ContentValues getSchedule() {
        return mSchedule;
    }
}
