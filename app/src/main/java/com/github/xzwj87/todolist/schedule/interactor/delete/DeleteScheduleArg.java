package com.github.xzwj87.todolist.schedule.interactor.delete;

public class DeleteScheduleArg {
    private final long mId;

    public DeleteScheduleArg(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }
}
