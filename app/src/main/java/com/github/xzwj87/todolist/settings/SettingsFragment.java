package com.github.xzwj87.todolist.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.github.xzwj87.todolist.R;

/**
 * Created by JasonWang on 2016/4/22.
 */
public class SettingsFragment extends PreferenceFragment{
    public static final String LOG_TAG = "SettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");

        addPreferencesFromResource(R.xml.settings);
    }
}
