package com.github.xzwj87.todolist.schedule.internal.di.component;

import com.github.xzwj87.todolist.schedule.internal.di.PerActivity;
import com.github.xzwj87.todolist.schedule.internal.di.module.ActivityModule;
import com.github.xzwj87.todolist.schedule.internal.di.module.ScheduleModule;
import com.github.xzwj87.todolist.schedule.ui.activity.AddScheduleActivity;
import com.github.xzwj87.todolist.schedule.ui.activity.ScheduleListActivity;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleDetailFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleGridFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleListFragment;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class,
        modules = {ActivityModule.class, ScheduleModule.class})
public interface ScheduleComponent extends ActivityComponent {
    void inject(ScheduleDetailFragment scheduleDetailFragment);
    void inject(AddScheduleActivity addScheduleActivity);
    void inject(ScheduleListActivity scheduleListActivity);
    void inject(ScheduleListFragment scheduleListFragment);
    void inject(ScheduleGridFragment scheduleGridFragment);
}
