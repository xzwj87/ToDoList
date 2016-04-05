package com.github.xzwj87.todolist.schedule.interactor.update;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;

import java.io.IOException;

import rx.Observable;

public class UpdateSchedule extends UseCase<UpdateScheduleArg> {
    private static final String LOG_TAG = UpdateSchedule.class.getSimpleName();

    @Override
    protected Observable buildUseCaseObservable() {
        if (mArg == null) {
            throw new IllegalArgumentException("mArg cannot be null.");
        }

        long id = mArg.getId();
        Uri uri = ScheduleContract.ScheduleEntry.buildScheduleUri(id);
        ContentValues schedule = mArg.getSchedule();

        return Observable.<Integer>create(subscriber -> {
                    int updated = App.getAppContext()
                            .getContentResolver()
                            .update(uri, schedule, null, null);

                    Log.v(LOG_TAG, "buildUseCaseObservable(): updatedNum = " + updated);

                    if (updated != 0) {
                        subscriber.onNext(updated);
                    } else {
                        subscriber.onError(new IOException("Update id = " + id + "failed."));
                    }
                    subscriber.onCompleted();
                }
        );
    }
}
