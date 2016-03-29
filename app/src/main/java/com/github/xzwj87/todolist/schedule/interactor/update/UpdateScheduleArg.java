package com.github.xzwj87.todolist.schedule.interactor.update;

import android.content.ContentValues;

public class UpdateScheduleArg {
    private final long mId;
    private final ContentValues mSchedule;

    public UpdateScheduleArg(long id, ContentValues schedule) {
        mId = id;
        mSchedule = schedule;
    }

    public long getId() {
        return mId;
    }

    public ContentValues getSchedule() {
        return mSchedule;
    }
}
