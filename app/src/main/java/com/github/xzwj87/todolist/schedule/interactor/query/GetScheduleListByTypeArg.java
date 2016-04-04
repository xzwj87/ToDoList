package com.github.xzwj87.todolist.schedule.interactor.query;


public class GetScheduleListByTypeArg extends GetScheduleListArg {
    private String mTypeFilter;

    public GetScheduleListByTypeArg(String typeFilter) {
        super();
        mTypeFilter = typeFilter;
    }

    public GetScheduleListByTypeArg(String typeFilter, String doneFilter) {
        super(doneFilter);
        mTypeFilter = typeFilter;
    }

    public GetScheduleListByTypeArg(String typeFilter, @SortOrder String sortOrder,
                                    String doneFilter) {
        super(doneFilter, sortOrder);
        mTypeFilter = typeFilter;
    }

    public String getTypeFilter() {
        return mTypeFilter;
    }

}
