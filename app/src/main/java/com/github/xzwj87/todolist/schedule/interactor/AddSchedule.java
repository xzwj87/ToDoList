package com.github.xzwj87.todolist.schedule.interactor;


import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;

import rx.Observable;

public class AddSchedule extends InsertUseCase {
    private static final String LOG_TAG = AddSchedule.class.getSimpleName();

    @Override
    protected Observable buildUseCaseObservable(ContentValues schedule) {
        Observable<Long> observable = Observable.create(subscriber -> {
                    Uri uri = App.getAppContext()
                            .getContentResolver()
                            .insert(ScheduleContract.ScheduleEntry.CONTENT_URI, schedule);
                    Log.v(LOG_TAG, "buildUseCaseObservable(): uri = " + uri);
                    Long id = ScheduleContract.ScheduleEntry.getScheduleIdFromUri(uri);
                    subscriber.onNext(id);
                    subscriber.onCompleted();
                }
        );

        return observable;
    }

}
