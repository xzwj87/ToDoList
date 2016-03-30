package com.github.xzwj87.todolist.schedule.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.alarm.service.AlarmCommandsInterface;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by JasonWang on 2016/3/2.
 */
public class AlarmAlertActivity extends Activity implements View.OnClickListener {
    public static final String LOG_TAG = "AlarmAlertActivity";

    private TextView mAlertTitle;
    private TextView mEventTitle;
    private Button mOk;
    private Button mCancel;

    //private ScheduleEntity mAlarmEntity;

    @Override
    public void onCreate(Bundle savedSate){
        super.onCreate(savedSate);

        Intent intent = getIntent();
        Window win = getWindow();
        // keep screen on while alarm is started
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        setContentView(R.layout.activity_alarm_alert);

        mAlertTitle = (TextView)findViewById(R.id.alert);
        mEventTitle = (TextView)findViewById(R.id.event);

        mAlertTitle.setText(R.string.alarm_alert_title);
        Date date = Calendar.getInstance().getTime();
        String event =  date.toString() + intent.getStringExtra(AlarmCommandsInterface.ALARM_TITLE);
        mEventTitle.setText(event);

        mOk = (Button)findViewById(R.id.ok);
        mCancel = (Button)findViewById(R.id.cancel);
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG,"onClick(); button = " + getClass().getName() + "is clicked");
        if (v.equals(mOk)) {
            // update the alarm state to database
            updateAlarmState();
            finish();
        }else{
            // nothing happends
            finish();
        }
    }

    protected void updateAlarmState(){
        Log.d(LOG_TAG,"updateAlarmState()");

    }
}
