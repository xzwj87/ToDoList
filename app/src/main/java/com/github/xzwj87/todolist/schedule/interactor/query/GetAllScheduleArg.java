package com.github.xzwj87.todolist.schedule.interactor.query;


public class GetAllScheduleArg extends GetScheduleListArg {

    public GetAllScheduleArg() {
        super();
    }

    public GetAllScheduleArg(String doneFilter) {
        super(doneFilter);
    }

    public GetAllScheduleArg(@SortOrder String sortOrder, String doneFilter) {
        super(sortOrder, doneFilter);
    }

}
