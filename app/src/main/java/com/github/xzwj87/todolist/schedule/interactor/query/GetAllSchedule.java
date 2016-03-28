package com.github.xzwj87.todolist.schedule.interactor.query;


import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import rx.Observable;
import rx.schedulers.Schedulers;

public class GetAllSchedule extends UseCase<GetAllScheduleArg> {
    private final String LOG_TAG = GetScheduleListByType.class.getSimpleName();

    private final BriteContentResolver mBriteContentResolver;

    public GetAllSchedule() {
        this(new GetAllScheduleArg());
    }

    public GetAllSchedule(GetAllScheduleArg arg) {
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
                        null,
                        null,
                        mArg.getSortOrder(),
                        true)
                .map(SqlBrite.Query::run);
    }

}
