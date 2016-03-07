package com.github.xzwj87.todolist.schedule.interactor;


import android.content.ContentValues;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;

import java.io.IOException;

import rx.Observable;

public class UpdateSchedule extends UpdateUseCase {
    private static final String LOG_TAG = UpdateSchedule.class.getSimpleName();

    @Override
    protected Observable buildUseCaseObservable(long id, ContentValues schedule) {
        Observable<Integer> observable = Observable.create(subscriber -> {
                    int updated = App.getAppContext()
                            .getContentResolver()
                            .update(ScheduleContract.ScheduleEntry.buildScheduleUri(id),
                                    schedule, null, null);
                    Log.v(LOG_TAG, "buildUseCaseObservable(): updatedNum = " + updated);
                    if (updated != 0) {
                        subscriber.onNext(updated);
                    } else {
                        subscriber.onError(new IOException("Update id = " + id + "failed."));
                    }
                    subscriber.onCompleted();
                }
        );

        return observable;
    }
}
