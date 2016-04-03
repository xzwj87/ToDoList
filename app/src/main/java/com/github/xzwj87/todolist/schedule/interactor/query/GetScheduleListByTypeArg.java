package com.github.xzwj87.todolist.schedule.interactor.query;


public class GetScheduleListByTypeArg extends GetScheduleListArg {
    private String mTypeFilter;

    public GetScheduleListByTypeArg(String typeFilter) {
        super();
        mTypeFilter = typeFilter;
    }

    public GetScheduleListByTypeArg(String typeFilter, @SortOrder String sortOrder) {
        super(sortOrder);
        mTypeFilter = typeFilter;
    }

    public GetScheduleListByTypeArg(String typeFilter, @SortOrder String sortOrder,
                                    String doneFilter) {
        super(sortOrder, doneFilter);
        mTypeFilter = typeFilter;
    }

    public String getTypeFilter() {
        return mTypeFilter;
    }

}
