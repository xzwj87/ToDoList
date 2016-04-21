package com.github.xzwj87.todolist.schedule.internal.di.component;

import android.app.Application;
import android.content.Context;

import com.github.xzwj87.todolist.schedule.internal.di.module.AppModule;
import com.github.xzwj87.todolist.schedule.ui.activity.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(BaseActivity baseActivity);

    Context context();
    Application application();
}