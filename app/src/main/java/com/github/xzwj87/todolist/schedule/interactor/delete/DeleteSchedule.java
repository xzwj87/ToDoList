package com.github.xzwj87.todolist.schedule.interactor.delete;

import android.net.Uri;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;

import javax.inject.Inject;

import rx.Observable;

public class DeleteSchedule extends UseCase<DeleteScheduleArg> {
    private static final String LOG_TAG = DeleteSchedule.class.getSimpleName();

    @Inject
    public DeleteSchedule(DeleteScheduleArg arg) {
        mArg = arg;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (mArg == null) {
            throw new IllegalArgumentException("mArg cannot be null.");
        }
        Uri uri = ScheduleContract.ScheduleEntry.buildScheduleUri(mArg.getId());

        return Observable.<Integer>create(subscriber -> {
                    int deleted = App.getAppContext()
                            .getContentResolver()
                            .delete(uri, null, null);

                    Log.v(LOG_TAG, "buildUseCaseObservable(): deleted = " + deleted);

                    subscriber.onNext(deleted);
                    subscriber.onCompleted();
                }
        );
    }
}
