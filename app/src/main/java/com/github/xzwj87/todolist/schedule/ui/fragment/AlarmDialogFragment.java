package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.data.entity.ScheduleEntity;

/**
 * Created by JasonWang on 2016/3/2.
 */
public class AlarmDialogFragment extends DialogFragment {
    static final String LOG_TAG = "AlarmDialogFragment";

    private final Window win = getActivity().getWindow();
    //private ScheduleEntity mAlarmEntity;

    @Override
    public void onCreate(Bundle savedSate){
        super.onCreate(savedSate);

        // keep screen on while alarm is started
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        getDialog().setContentView(R.layout.fragment_alarm_dialog);
    }

    @Override
    public void onStart(){
    }
}
