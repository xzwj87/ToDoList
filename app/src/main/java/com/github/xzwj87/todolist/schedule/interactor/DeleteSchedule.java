package com.github.xzwj87.todolist.schedule.interactor;


import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;

import rx.Observable;

public class DeleteSchedule extends DeleteUseCase {
    private static final String LOG_TAG = DeleteSchedule.class.getSimpleName();

    @Override
    protected Observable buildUseCaseObservable(long id) {
        Observable<Integer> observable = Observable.create(subscriber -> {
                    int deleted = App.getAppContext()
                            .getContentResolver()
                            .delete(ScheduleContract.ScheduleEntry.buildScheduleUri(id),
                                    null, null);
                    Log.v(LOG_TAG, "buildUseCaseObservable(): deleted = " + deleted);
                    subscriber.onNext(deleted);
                    subscriber.onCompleted();
                }
        );

        return observable;
    }
}
