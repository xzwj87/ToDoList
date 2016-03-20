package com.github.xzwj87.todolist.schedule.interactor;


import android.net.Uri;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import rx.Observable;
import rx.schedulers.Schedulers;

public class GetScheduleDetail extends QueryUseCase {
    private final String LOG_TAG = GetScheduleDetail.class.getSimpleName();

    private final long mScheduleId;
    private final BriteContentResolver mBriteContentResolver;

    public GetScheduleDetail(long scheduleId) {
        mScheduleId = scheduleId;
        SqlBrite sqlBrite = SqlBrite.create();
        mBriteContentResolver = sqlBrite.wrapContentProvider(
                App.getAppContext().getContentResolver(), Schedulers.io());
    }

    @Override
    protected Observable buildUseCaseObservable() {
        Uri uri = ScheduleContract.ScheduleEntry.buildScheduleUri(mScheduleId);
        Log.v(LOG_TAG, "buildUseCaseObservable(): uri = " + uri);

        return mBriteContentResolver
                .createQuery(
                        uri,
                        ScheduleModelDataMapper.SCHEDULE_COLUMNS, null, null, null, false)
                .map(SqlBrite.Query::run);
    }

}
