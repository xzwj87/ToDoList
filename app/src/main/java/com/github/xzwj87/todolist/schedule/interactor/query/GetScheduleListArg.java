package com.github.xzwj87.todolist.schedule.interactor.query;

import android.support.annotation.StringDef;

import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class GetScheduleListArg {

    public static final String SORT_BY_START_DATE_ASC =
            ScheduleContract.ScheduleEntry.COLUMN_DATE_START + " ASC";
    public static final String SORT_BY_START_DATE_DESC =
            ScheduleContract.ScheduleEntry.COLUMN_DATE_START + " DESC";
    @StringDef({SORT_BY_START_DATE_ASC, SORT_BY_START_DATE_DESC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortOrder {}

    @SortOrder protected String mSortOrder;
    protected String mDoneFilter;

    public GetScheduleListArg() {
        this(null, SORT_BY_START_DATE_ASC);
    }

    public GetScheduleListArg(String doneFilter) {
        mDoneFilter = doneFilter;
    }

    public GetScheduleListArg(String doneFilter, @SortOrder String sortOrder) {
        mSortOrder = sortOrder;
        mDoneFilter = doneFilter;
    }

    public @SortOrder String getSortOrder() {
        return mSortOrder;
    }

    public String getDoneFilter() {
        return mDoneFilter;
    }
}
