package com.github.xzwj87.todolist.alarm.ui.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.alarm.media.AudioPlayerService;
import com.github.xzwj87.todolist.alarm.shake.IShakeListener;
import com.github.xzwj87.todolist.alarm.shake.ShakeDetectService;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Handler;

/**
 * Created by JasonWang on 2016/3/2.
 */
public class AlarmAlertActivity extends Activity implements View.OnClickListener {
    public static final String LOG_TAG = "AlarmAlertActivity";

    /* User Event Definition */
    public static final int EVENT_USER_CLICK_CLOSE = 0x01;
    public static final int EVENT_USER_CLICK_CANCEL = 0x02;
    public static final int EVENT_USER_SHAKE = 0x03;
    public static final int EVENT_ALARM_TIME_UP = 0x04;

    private TextView mAlertTitle;
    private TextView mAlarmTime;
    private TextView mAlarmTimeWeek;
    private ImageView mClose;

    private ServiceThread mThread;
    private ServiceThread.EventHandler mHandler;
    private ShakeDetectService mShakeDetector;

    private long mScheduleId;
    //private long now;
    private long mAlarmDuration = 1000*90;
    private boolean mUserShake = false;
    private boolean mAlarmDone = false;
    private String mScheduleTitle;
    private NotificationManager mNotificationMgr;

    private SharedPreferences mSharePref;

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

        mAlertTitle = (TextView)findViewById(R.id.alarm_title);
        mAlarmTime = (TextView)findViewById(R.id.alarm_time);
        mAlarmTimeWeek = (TextView)findViewById(R.id.alarm_time_week);
        mClose = (ImageView)findViewById(R.id.alert_close);

        mScheduleId = intent.getLongExtra(ScheduleContract.ScheduleEntry._ID,-1);

        String formats = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(formats);
        mAlarmTime.setText(sdf.format(new Date()));

        formats = "EEEE";
        sdf = new SimpleDateFormat(formats);
        mAlarmTimeWeek.setText(sdf.format(new Date()));

        /* if there are notes, need to display also */
        mScheduleTitle = intent.getStringExtra(ScheduleContract.ScheduleEntry.COLUMN_TITLE);
        mAlertTitle.setText(mScheduleTitle);

        mClose.setOnClickListener(this);

        mShakeDetector = new ShakeDetectService(this);
        mShakeDetector.setShakeListener(new ShakeListener());

        mSharePref = PreferenceManager.getDefaultSharedPreferences(this);
        String alarmDuration = mSharePref.getString(getResources().getString(R.string.setting_alarm_duration_key),
                "90");
        mAlarmDuration = Integer.valueOf(alarmDuration)*1000;
        /* start a new thread */
        mThread = new ServiceThread("ServicesThread");
        mThread.start();
        /* send a delayed message to do timer alarm */
        Message msg = mHandler.obtainMessage(EVENT_ALARM_TIME_UP, getEventName(EVENT_ALARM_TIME_UP));
        mHandler.sendMessageDelayed(msg, mAlarmDuration);

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG, "onResume()");
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG, "onClick(); button = " + getClass().getName() + "is clicked");
        if (v.equals(mClose)) {
            /* send message */
            Message msg = mHandler.obtainMessage(EVENT_USER_CLICK_CLOSE, getEventName(EVENT_USER_CLICK_CLOSE));
            mHandler.sendMessage(msg);
        }
    }

    public String getEventName(int event){
        switch (event){
            case EVENT_USER_CLICK_CLOSE:
                return "USER_CLICK_CLOSE";
            case EVENT_USER_CLICK_CANCEL:
                return "USER_CLICK_CANCEL";
            case EVENT_USER_SHAKE:
                return "USER_SHAKE";
            case EVENT_ALARM_TIME_UP:
                return "ALARM_TIME_UP";
            default:
                return null;
        }
    }

    public class ShakeListener implements IShakeListener{

        @Override
        public void onShake() {
            Log.d(ShakeListener.class.getSimpleName(),"onShake()");

            /* send message */
            if(!mUserShake) {
                Message msg = mHandler.obtainMessage(EVENT_USER_SHAKE, getEventName(EVENT_USER_SHAKE));
                mHandler.sendMessage(msg);
                mUserShake = true;
            }
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
                Log.d(LOG_TAG, "message: " + message.obj + " is handled");

                stopServices();
                switch (message.what){
                    case EVENT_USER_CLICK_CLOSE:
                    case EVENT_USER_SHAKE:
                        if(!mAlarmDone){
                            mAlarmDone = true;
                            updateAlarmState();
                        }
                        break;
                    case EVENT_ALARM_TIME_UP:
                        if(!mAlarmDone) {
                            sendNotification();
                            updateAlarmState();
                        }
                        break;
                }
                /* before done, need to remove all the pending messages */
                if(mHandler.hasMessages(EVENT_ALARM_TIME_UP)){
                    mHandler.removeMessages(EVENT_ALARM_TIME_UP);
                }
                finish();
            }
        }

        protected void updateAlarmState(){
            Log.d(LOG_TAG, "updateAlarmState()");

            Uri uri = ContentUris.withAppendedId(ScheduleContract.ScheduleEntry.CONTENT_URI,mScheduleId);
            ContentValues updated = new ContentValues();
            updated.put(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE,ScheduleModel.DONE);
            getContentResolver().update(uri, updated, null, null);
        }

        protected void sendNotification(){
            Log.d(LOG_TAG, "sendNotification()");

            NotificationCompat.Builder builder = new
                    NotificationCompat.Builder(getBaseContext());
            builder.setSmallIcon(R.drawable.ic_access_alarms_24dp)
                   .setContentTitle(getResources().getString(R.string.app_name))
                   .setContentText(mScheduleTitle)
                   .setCategory(Notification.CATEGORY_ALARM)
                   .setAutoCancel(true);

            Intent resultIntent = new Intent(getBaseContext(),
                    NotifierDetailActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            int notifyId = 1;
            mNotificationMgr = (NotificationManager)getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationMgr.notify(notifyId, builder.build());
        }


        // should run in a different thread
        public void startServices(){
            Log.d(LOG_TAG, "startServices()");

        /* playing music */
            Intent intent = new Intent(getApplicationContext(), AudioPlayerService.class);
            startService(intent);

        /* shake detecting */
            mShakeDetector.start();
        }

        public void stopServices(){
            Log.d(LOG_TAG, "stopServices()");

        /* stop music */
            Intent intent = new Intent(getApplicationContext(),AudioPlayerService.class);
            stopService(intent);
        /* stop shake detecting */
            mShakeDetector.stop();
        }

    }

}
