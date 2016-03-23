package com.github.xzwj87.todolist.schedule.interactor;

import android.support.annotation.StringDef;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import rx.Observable;
import rx.schedulers.Schedulers;


public class SearchSchedule extends QueryUseCase {
    private final String LOG_TAG = GetScheduleList.class.getSimpleName();

    public static final String SORT_BY_START_DATE_ASC =
            ScheduleContract.ScheduleEntry.COLUMN_DATE_START + " ASC";
    public static final String SORT_BY_START_DATE_DESC =
            ScheduleContract.ScheduleEntry.COLUMN_DATE_START + " DESC";

    @StringDef({SORT_BY_START_DATE_ASC, SORT_BY_START_DATE_DESC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortOrder {}

    private final BriteContentResolver mBriteContentResolver;
    @SortOrder private final String mSortOrder;
    private String mQuery;

    public SearchSchedule(String query) {
        this(SORT_BY_START_DATE_ASC, query);
    }

    public SearchSchedule(@SortOrder String sortOrder, String query) {
        SqlBrite sqlBrite = SqlBrite.create();
        mBriteContentResolver = sqlBrite.wrapContentProvider(
                App.getAppContext().getContentResolver(), Schedulers.io());

        mSortOrder = sortOrder;
        mQuery = query;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        String selection = ScheduleContract.ScheduleEntry.COLUMN_TITLE + " like ? OR " +
                ScheduleContract.ScheduleEntry.COLUMN_NOTE + " like ?";
        String[] selectionArgs = new String[]{ "%" + mQuery + "%", "%" + mQuery + "%"};
        return mBriteContentResolver
                .createQuery(
                        ScheduleContract.ScheduleEntry.CONTENT_URI,
                        ScheduleModelDataMapper.SCHEDULE_COLUMNS,
                        selection,
                        selectionArgs,
                        mSortOrder,
                        true)
                .map(SqlBrite.Query::run);
    }
}
