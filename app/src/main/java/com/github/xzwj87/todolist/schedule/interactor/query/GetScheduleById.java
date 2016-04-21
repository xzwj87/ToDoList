package com.github.xzwj87.todolist.schedule.interactor.query;

import android.net.Uri;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class GetScheduleById extends UseCase<Long> {
    private final String LOG_TAG = GetScheduleById.class.getSimpleName();

    private final BriteContentResolver mBriteContentResolver;

    @Inject
    public GetScheduleById(long scheduleId) {
        SqlBrite sqlBrite = SqlBrite.create();
        mBriteContentResolver = sqlBrite.wrapContentProvider(
                App.getAppContext().getContentResolver(), Schedulers.io());

        mArg = scheduleId;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        Uri uri = ScheduleContract.ScheduleEntry.buildScheduleUri(mArg);
        Log.v(LOG_TAG, "buildUseCaseObservable(): uri = " + uri);

        return mBriteContentResolver
                .createQuery(
                        uri,
                        ScheduleModelDataMapper.SCHEDULE_COLUMNS,
                        null,
                        null,
                        null,
                        false)
                .map(SqlBrite.Query::run);
    }
}
