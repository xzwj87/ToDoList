package com.github.xzwj87.todolist.schedule.interactor.query;


public class GetAllScheduleSuggestionArg {
    private static final int LIMIT = 10;

    private final String mQuery;
    private final int mLimit;

    public GetAllScheduleSuggestionArg(String query) {
        this(query, LIMIT);
    }

    public GetAllScheduleSuggestionArg(String query, int limit) {
        mQuery = query;
        mLimit = limit;
    }

    public String getQuery() {
        return mQuery;
    }

    public int getLimit() {
        return mLimit;
    }

}
