package com.github.xzwj87.todolist.schedule.interactor.query;


import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import rx.Observable;
import rx.schedulers.Schedulers;

public class GetScheduleListByType extends UseCase<GetScheduleListByTypeArg> {
    private final String LOG_TAG = GetScheduleListByType.class.getSimpleName();

    private final BriteContentResolver mBriteContentResolver;

    public GetScheduleListByType(GetScheduleListByTypeArg arg) {
        SqlBrite sqlBrite = SqlBrite.create();
        mBriteContentResolver = sqlBrite.wrapContentProvider(
                App.getAppContext().getContentResolver(), Schedulers.io());

        mArg = arg;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return mBriteContentResolver
                .createQuery(
                        ScheduleContract.ScheduleEntry.CONTENT_URI,
                        ScheduleModelDataMapper.SCHEDULE_COLUMNS,
                        ScheduleContract.ScheduleEntry.COLUMN_TYPE + " = ?",
                        new String[]{mArg.getTypeFilter()},
                        mArg.getSortOrder(),
                        true)
                .map(SqlBrite.Query::run);
    }

}
