package com.github.xzwj87.todolist.schedule.internal.di.module;

import android.app.Activity;
import android.content.Context;

import com.github.xzwj87.todolist.schedule.internal.di.PerActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides @PerActivity
    Activity provideActivity() {
        return this.mActivity;
    }

    @Provides @PerActivity @Named("activityContext")
    Context provideActivityContext() {
        return this.mActivity;
    }
}