package com.github.xzwj87.todolist.schedule.interactor.update;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.io.IOException;
import java.util.Arrays;

import rx.Observable;

public class MarkScheduleAsDone extends UseCase<MarkScheduleAsDoneArg> {
    private static final String LOG_TAG = MarkScheduleAsDone.class.getSimpleName();

    @Override
    protected Observable buildUseCaseObservable() {
        if (mArg == null) {
            throw new IllegalArgumentException("mArg cannot be null.");
        }

        return Observable.<Integer>create(subscriber -> {
            ContentValues schedule = new ContentValues();
            if (mArg.getMarkAsDone()) {
                schedule.put(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE, ScheduleModel.DONE);
            } else {
                schedule.put(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE, ScheduleModel.UNDONE);
            }

            int updated = 0;
            for (long id : mArg.getIds()) {
                Uri uri = ScheduleContract.ScheduleEntry.buildScheduleUri(id);
                updated += App.getAppContext()
                        .getContentResolver()
                        .update(uri, schedule, null, null);
            }
            Log.v(LOG_TAG, "buildUseCaseObservable(): updatedNum = " + updated);

            if (updated != 0) {
                subscriber.onNext(updated);
            } else {
                subscriber.onError(new IOException(
                        "Update ids = " + Arrays.toString(mArg.getIds()) + "failed."));
            }
            subscriber.onCompleted();
        });
    }
}
