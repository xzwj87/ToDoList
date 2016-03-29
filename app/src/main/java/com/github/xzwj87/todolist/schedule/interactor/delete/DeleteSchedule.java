package com.github.xzwj87.todolist.schedule.interactor.delete;

import android.net.Uri;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;

import rx.Observable;

public class DeleteSchedule extends UseCase<DeleteScheduleArg> {
    private static final String LOG_TAG = DeleteSchedule.class.getSimpleName();

    @Override
    protected Observable buildUseCaseObservable() {
        return Observable.<Integer>create(subscriber -> {
                    long id = mArg.getId();
                    Uri uri = ScheduleContract.ScheduleEntry.buildScheduleUri(id);

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
