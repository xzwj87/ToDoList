package com.github.xzwj87.todolist.schedule.data.repository;

import com.github.xzwj87.todolist.schedule.data.entity.ScheduleEntity;

import rx.Observable;

public interface ScheduleRepository {

    Observable<ScheduleEntity> schedule(final int scheduleId);

}
