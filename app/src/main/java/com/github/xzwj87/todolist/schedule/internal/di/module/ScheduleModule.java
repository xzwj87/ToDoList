package com.github.xzwj87.todolist.schedule.internal.di.module;

import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.delete.DeleteSchedule;
import com.github.xzwj87.todolist.schedule.interactor.delete.DeleteScheduleArg;
import com.github.xzwj87.todolist.schedule.interactor.insert.AddSchedule;
import com.github.xzwj87.todolist.schedule.interactor.query.GetAllSchedule;
import com.github.xzwj87.todolist.schedule.interactor.query.GetAllScheduleSuggestion;
import com.github.xzwj87.todolist.schedule.interactor.query.GetScheduleById;
import com.github.xzwj87.todolist.schedule.interactor.query.GetScheduleListByType;
import com.github.xzwj87.todolist.schedule.interactor.query.SearchSchedule;
import com.github.xzwj87.todolist.schedule.interactor.update.MarkScheduleAsDone;
import com.github.xzwj87.todolist.schedule.interactor.update.UpdateSchedule;
import com.github.xzwj87.todolist.schedule.internal.di.PerActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ScheduleModule {
    private long mScheduleId = -1;

    public ScheduleModule() {}

    public ScheduleModule(long scheduleId) {
        mScheduleId = scheduleId;
    }

    @Provides @PerActivity @Named("getScheduleById")
    UseCase provideGetScheduleByIdUseCase() {
        return new GetScheduleById(mScheduleId);
    }

    @Provides @PerActivity @Named("deleteSchedule")
    UseCase provideDeleteScheduleUseCase() {
        return new DeleteSchedule(new DeleteScheduleArg(mScheduleId));
    }

    @Provides @PerActivity @Named("updateSchedule")
    UseCase provideUpdateScheduleUseCase() {
        return new UpdateSchedule();
    }

    @Provides @PerActivity @Named("addSchedule")
    UseCase provideAddScheduleUseCase() {
        return new AddSchedule();
    }

    @Provides @PerActivity @Named("getAllScheduleSuggestion")
    UseCase provideGetAllScheduleSuggestion() {
        return new GetAllScheduleSuggestion();
    }

    @Provides @PerActivity @Named("markScheduleAsDone")
    UseCase provideMarkScheduleAsDone() {
        return new MarkScheduleAsDone();
    }

    @Provides @PerActivity @Named("getAllSchedule")
    UseCase provideGetAllSchedule() {
        return new GetAllSchedule();
    }

    @Provides @PerActivity @Named("getScheduleListByType")
    UseCase provideGetScheduleListByType() {
        return new GetScheduleListByType();
    }

    @Provides @PerActivity @Named("searchSchedule")
    UseCase provideSearchSchedule() {
        return new SearchSchedule();
    }
}
