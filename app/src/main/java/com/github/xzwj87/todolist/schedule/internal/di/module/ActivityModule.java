package com.github.xzwj87.todolist.schedule.internal.di.module;

import android.app.Activity;

import com.github.xzwj87.todolist.schedule.internal.di.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @PerActivity
    Activity activity() {
        return this.mActivity;
    }
}