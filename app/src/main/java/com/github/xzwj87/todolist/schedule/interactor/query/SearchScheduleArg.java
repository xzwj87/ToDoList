package com.github.xzwj87.todolist.schedule.interactor.query;


public class SearchScheduleArg extends GetScheduleListArg {
    private String mQuery;

    public SearchScheduleArg(String query) {
        super();
        mQuery = query;
    }

    public SearchScheduleArg(String query, @SortOrder String sortOrder) {
        super(sortOrder);
        mQuery = query;
    }

    public String getQuery() {
        return mQuery;
    }

}
