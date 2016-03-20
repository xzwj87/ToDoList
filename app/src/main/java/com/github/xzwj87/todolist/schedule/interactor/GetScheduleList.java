package com.github.xzwj87.todolist.schedule.interactor;


import android.support.annotation.StringDef;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import rx.Observable;
import rx.schedulers.Schedulers;

public class GetScheduleList extends QueryUseCase {
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
    private String mScheduleType;

    public GetScheduleList(@SortOrder String sortOrder) {
        SqlBrite sqlBrite = SqlBrite.create();
        mBriteContentResolver = sqlBrite.wrapContentProvider(
                App.getAppContext().getContentResolver(), Schedulers.io());

        mSortOrder = sortOrder;
    }

    public GetScheduleList(@SortOrder String sortOrder, String scheduleType) {
        SqlBrite sqlBrite = SqlBrite.create();
        mBriteContentResolver = sqlBrite.wrapContentProvider(
                App.getAppContext().getContentResolver(), Schedulers.io());

        mSortOrder = sortOrder;
        mScheduleType = scheduleType;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (mScheduleType == null)
            return mBriteContentResolver
                    .createQuery(
                            ScheduleContract.ScheduleEntry.CONTENT_URI,
                            ScheduleModelDataMapper.SCHEDULE_COLUMNS, null, null, mSortOrder, true)
                    .map(SqlBrite.Query::run);
        else
            return mBriteContentResolver
                    .createQuery(
                            ScheduleContract.ScheduleEntry.CONTENT_URI,
                            ScheduleModelDataMapper.SCHEDULE_COLUMNS,
                            ScheduleContract.ScheduleEntry.COLUMN_TYPE + " = ?",
                            new String[]{mScheduleType},
                            mSortOrder,
                            true)
                    .map(SqlBrite.Query::run);
    }
}
