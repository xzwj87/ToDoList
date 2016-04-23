package com.github.xzwj87.todolist.schedule.interactor.query;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import rx.Observable;
import rx.schedulers.Schedulers;

public class SearchSchedule extends UseCase<SearchScheduleArg> {
    private final String LOG_TAG = SearchSchedule.class.getSimpleName();

    private final BriteContentResolver mBriteContentResolver;

    public SearchSchedule(SearchScheduleArg arg) {
        this();
        mArg = arg;
    }

    public SearchSchedule() {
        SqlBrite sqlBrite = SqlBrite.create();
        mBriteContentResolver = sqlBrite.wrapContentProvider(
                App.getAppContext().getContentResolver(), Schedulers.io());
    }

    @Override
    protected Observable buildUseCaseObservable() {
        String selection = ScheduleContract.ScheduleEntry.COLUMN_TITLE + " like ? OR " +
                ScheduleContract.ScheduleEntry.COLUMN_NOTE + " like ?";
        String[] selectionArgs = new String[]{
                "%" + mArg.getQuery() + "%",
                "%" + mArg.getQuery() + "%"};

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
