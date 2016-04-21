package com.github.xzwj87.todolist.app;


import android.app.Application;
import android.content.Context;

import com.github.xzwj87.todolist.schedule.internal.di.component.AppComponent;
import com.github.xzwj87.todolist.schedule.internal.di.component.DaggerAppComponent;
import com.github.xzwj87.todolist.schedule.internal.di.module.AppModule;

public class App extends Application {

    private AppComponent mAppComponent;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static Context getAppContext() {
        return mContext;
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}