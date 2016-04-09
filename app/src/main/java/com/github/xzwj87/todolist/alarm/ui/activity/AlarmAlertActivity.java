package com.github.xzwj87.todolist.alarm.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.alarm.service.AlarmCommandsInterface;
import com.github.xzwj87.todolist.alarm.media.AudioPlayerService;
import com.github.xzwj87.todolist.alarm.shake.IShakeListener;
import com.github.xzwj87.todolist.alarm.shake.ShakeDetectService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.os.Handler;

/**
 * Created by JasonWang on 2016/3/2.
 */
public class AlarmAlertActivity extends Activity implements View.OnClickListener {
    public static final String LOG_TAG = "AlarmAlertActivity";

    /* User Event Definition */
    public static final int EVENT_USER_CLICK_OK = 0x01;
    public static final int EVENT_USER_CLICK_CANCEL = 0x02;
    public static final int EVENT_USER_SHAKE = 0x03;
    public static final int EVENT_USER_ALARM_TIME_UP = 0x04;

    private TextView mAlertTitle;
    private TextView mEventTitle;
    private TextView mEventTime;
    private Button mOk;
    private Button mCancel;

    private ServiceThread mThread;
    private ServiceThread.EventHandler mHandler;
    private ShakeDetectService mShakeDetector;

    //private long now;
    private long mAlarmDuration = 1000*90;

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
        mEventTime = (TextView)findViewById(R.id.alarm_time);
        mEventTitle = (TextView)findViewById(R.id.event);

        mAlertTitle.setText(R.string.alarm_alert_title);

        String formats = "HH:mm EEEE";
        Date date = new Date(intent.getLongExtra(AlarmCommandsInterface.ALARM_START_TIME,0));
        SimpleDateFormat sdf = new SimpleDateFormat(formats);
        mEventTime.setText(sdf.format(date));

        /* if there are notes, need to display also */
        String event = intent.getStringExtra(AlarmCommandsInterface.ALARM_TITLE);
        mEventTitle.setText(event);

        mOk = (Button)findViewById(R.id.ok);
        mCancel = (Button)findViewById(R.id.cancel);
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG,"onResume()");

        mShakeDetector = new ShakeDetectService(this);
        mShakeDetector.setShakeListener(new ShakeListener());

        /* start a new thread */
        mThread = new ServiceThread("ServicesThread");
        mThread.start();
        /* send a delayed message to do timer alarm */
        Message msg = mHandler.obtainMessage(EVENT_USER_ALARM_TIME_UP, getEventName(EVENT_USER_ALARM_TIME_UP));
        mHandler.sendMessageDelayed(msg, mAlarmDuration);
    }
    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG,"onClick(); button = " + getClass().getName() + "is clicked");
        if (v.equals(mOk)) {
            /* update the alarm state to database */
            updateAlarmState(EVENT_USER_CLICK_OK);
            /* send message */
            Message msg = mHandler.obtainMessage(EVENT_USER_CLICK_OK, getEventName(EVENT_USER_CLICK_OK));
            mHandler.sendMessage(msg);
        }else{
            updateAlarmState(EVENT_USER_CLICK_CANCEL);
            /* send message */
            Message msg = mHandler.obtainMessage(EVENT_USER_CLICK_CANCEL,getEventName(EVENT_USER_CLICK_CANCEL));
            mHandler.sendMessage(msg);
        }
    }

    protected void updateAlarmState(int event){
        Log.d(LOG_TAG, "updateAlarmState()");

    }

    // should run in a different thread
    public void startServices(){
        Log.d(LOG_TAG, "startServices()");

        /* playing music */
        Intent intent = new Intent(this, AudioPlayerService.class);
        startService(intent);
        /* shake detecting */
        mShakeDetector.start();
    }

    public void stopServices(){
        Log.d(LOG_TAG,"stopServices()");

        /* stop music */
        Intent intent = new Intent(this,AudioPlayerService.class);
        stopService(intent);
        /* stop shake detecting */
        mShakeDetector.stop();
    }

    public String getEventName(int event){
        switch (event){
            case EVENT_USER_CLICK_OK:
                return "USER_CLICK_OK";
            case EVENT_USER_CLICK_CANCEL:
                return "USER_CLICK_CANCEL";
            case EVENT_USER_SHAKE:
                return "USER_SHAKE";
            case EVENT_USER_ALARM_TIME_UP:
                return "USER_ALARM_TIME_UP";
            default:
                return null;
        }
    }

    public class ShakeListener implements IShakeListener{

        @Override
        public void onShake() {
            Log.d(ShakeListener.class.getSimpleName(),"onShake()");

            /* send message */
            Message msg = mHandler.obtainMessage(EVENT_USER_SHAKE, getEventName(EVENT_USER_SHAKE));
            mHandler.sendMessage(msg);
        }
    }

    /* another thread to start services */
    public class ServiceThread extends Thread{
        private static final String LOG_TAG = "ServiceThread";

        private Thread mThread;
        private String mThreadName;

        public ServiceThread(String name){
            Log.d(LOG_TAG,"creating thread: " + name);
            this.mThreadName = name;
        }

        @Override
        public void start(){
            Log.d(LOG_TAG,"thread: " + mThreadName + " is started");

            if(mThread == null){
                mThread = new Thread(this,mThreadName);
                mThread.start();
                startServices();

                mHandler = new EventHandler();
            }
        }

        @Override
        public void run(){
            Log.d(LOG_TAG, "thread: " + mThreadName + " is running");

            Looper.prepare();

            //mHandler = new EventHandler();

            Looper.loop();

        }

        protected class EventHandler extends Handler{

            @Override
            public void handleMessage(Message message){
                Log.d(LOG_TAG,"message: " + message.obj + " is handled");

                switch (message.what){
                    case EVENT_USER_CLICK_OK:
                    case EVENT_USER_CLICK_CANCEL:
                    case EVENT_USER_SHAKE:
                    case EVENT_USER_ALARM_TIME_UP:
                        stopServices();
                        finish();
                        break;
                }
            }
        }
    }

}
