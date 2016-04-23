package com.github.xzwj87.todolist.schedule.interactor.query;


import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Arrays;

import rx.Observable;
import rx.schedulers.Schedulers;

public class GetScheduleListByType extends UseCase<GetScheduleListByTypeArg> {
    private final String LOG_TAG = GetScheduleListByType.class.getSimpleName();

    private final BriteContentResolver mBriteContentResolver;

    public GetScheduleListByType(GetScheduleListByTypeArg arg) {
        this();
        mArg = arg;
    }

    public GetScheduleListByType() {
        SqlBrite sqlBrite = SqlBrite.create();
        mBriteContentResolver = sqlBrite.wrapContentProvider(
                App.getAppContext().getContentResolver(), Schedulers.io());
    }

    @Override
    protected Observable buildUseCaseObservable() {
        String selection = ScheduleContract.ScheduleEntry.COLUMN_TYPE + " = ?";
        String[] selectionArgs = new String[]{mArg.getTypeFilter()};

        if (mArg.getDoneFilter()!= null) {
            selection += " AND " + ScheduleContract.ScheduleEntry.COLUMN_IS_DONE + " = ?";
            selectionArgs = new String[]{mArg.getTypeFilter(), mArg.getDoneFilter()};
        }

        Log.v(LOG_TAG, "buildUseCaseObservable(): DoneFilter = " + mArg.getDoneFilter() +
                ", selection = " + selection +
                ", selectionArgs = " + Arrays.toString(selectionArgs));

        return mBriteContentResolver
                .createQuery(
                        ScheduleContract.ScheduleEntry.CONTENT_URI,
                        ScheduleModelDataMapper.SCHEDULE_COLUMNS,
                        selection,
                        selectionArgs,
                        mArg.getSortOrder(),
                        true)
                .map(SqlBrite.Query::run);
    }

}
