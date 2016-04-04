package com.github.xzwj87.todolist.schedule.interactor.query;


public class GetAllScheduleArg extends GetScheduleListArg {

    public GetAllScheduleArg() {
        super();
    }

    public GetAllScheduleArg(@SortOrder String sortOrder) {
        super(sortOrder);
    }

    public GetAllScheduleArg(@SortOrder String sortOrder, String doneFilter) {
        super(sortOrder, doneFilter);
    }

}
