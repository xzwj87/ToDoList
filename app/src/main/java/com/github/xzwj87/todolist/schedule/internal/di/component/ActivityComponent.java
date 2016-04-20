package com.github.xzwj87.todolist.schedule.internal.di.component;

import android.app.Activity;

import com.github.xzwj87.todolist.schedule.internal.di.PerActivity;
import com.github.xzwj87.todolist.schedule.internal.di.module.ActivityModule;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    Activity activity();
}