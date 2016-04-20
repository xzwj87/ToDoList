package com.github.xzwj87.todolist.schedule.ui.activity;

import android.support.v7.app.AppCompatActivity;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.internal.di.component.AppComponent;
import com.github.xzwj87.todolist.schedule.internal.di.module.ActivityModule;

public class BaseActivity extends AppCompatActivity {

    protected AppComponent getApplicationComponent() {
        return ((App)getApplication()).getAppComponent();
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

}
