package com.github.xzwj87.todolist.schedule.interactor.insert;

import android.net.Uri;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;

import rx.Observable;

public class AddSchedule extends UseCase<AddScheduleArg> {
    private static final String LOG_TAG = AddSchedule.class.getSimpleName();

    @Override
    protected Observable buildUseCaseObservable() {
        if (mArg == null) {
            throw new IllegalArgumentException("mArg cannot be null.");
        }
        return Observable.<Long>create(subscriber -> {
                    Uri uri = App.getAppContext()
                            .getContentResolver()
                            .insert(ScheduleContract.ScheduleEntry.CONTENT_URI,
                                    mArg.getSchedule());
                    Log.v(LOG_TAG, "buildUseCaseObservable(): uri = " + uri);
                    Long id = ScheduleContract.ScheduleEntry.getScheduleIdFromUri(uri);
                    subscriber.onNext(id);
                    subscriber.onCompleted();
                }
        );
    }
}
