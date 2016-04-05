package com.github.xzwj87.todolist.schedule.interactor.update;


import android.content.ContentValues;

public class MarkScheduleAsDoneArg {

    private final long[] mIds;
    // private final ContentValues[] mSchedules;
    private final boolean mMarkAsDone;

    public MarkScheduleAsDoneArg(long[] ids, boolean markAsDone) {
        mIds = ids;
        // mSchedules = schedules;
        mMarkAsDone = markAsDone;
    }

    public long[] getIds() {
        return mIds;
    }

    public boolean getMarkAsDone() {
        return mMarkAsDone;
    }


//    public ContentValues[] getSchedules() {
//        return mSchedules;
//    }
}
