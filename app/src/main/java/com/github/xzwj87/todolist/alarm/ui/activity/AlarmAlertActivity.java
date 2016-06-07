package com.github.xzwj87.todolist.alarm.ui.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.alarm.media.AudioPlayerService;
import com.github.xzwj87.todolist.alarm.shake.IShakeListener;
import com.github.xzwj87.todolist.alarm.shake.ShakeDetectService;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.settings.SharePreferenceHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Handler;

/**
 * Created by JasonWang on 2016/3/2.
 */
public class AlarmAlertActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String LOG_TAG = "AlarmAlertActivity";

    public static String ACTION_NOTIFICATION = "com.github.xzwj87.action.notification";

    /* User Event Definition */
    public static final int EVENT_USER_CLICK_CLOSE = 0x01;
    public static final int EVENT_USER_CLICK_CANCEL = 0x02;
    public static final int EVENT_USER_SHAKE = 0x03;
    public static final int EVENT_ALARM_TIME_UP = 0x04;

    private TextView mAlertTitle = null;
    private TextView mAlarmTime = null;
    private TextView mAlarmTimeWeek = null;
    private ImageView mClose = null;

    private ServiceThread mThread = null;
    private ServiceThread.EventHandler mHandler = null;
    private ShakeDetectService mShakeDetector = null;
    private LocalBroadcastManager mBroadcastMgr = null;

    private long mScheduleId = 0;
    //private long now;
    private long mAlarmDuration = 1000*90;
    private boolean mUserShake = false;
    private boolean mAlarmDone = false;
    private String mScheduleTitle = null;
    private NotificationManager mNotificationMgr = null;

    private SharePreferenceHelper mSharePrefHelper = null;

    @Override
    public void onCreate(Bundle savedSate){
        super.onCreate(savedSate);

        Intent intent = getIntent();
        initViews(intent);

        mShakeDetector = new ShakeDetectService(this);
        mShakeDetector.setShakeListener(new ShakeListener());

        mSharePrefHelper = SharePreferenceHelper.getInstance(this);
        mAlarmDuration = mSharePrefHelper.getAlarmDuration();

        /* start a new thread */
        mThread = new ServiceThread("ServicesThread");
        mThread.start();
        /* send a delayed message to do timer alarm */
        Message msg = mHandler.obtainMessage(EVENT_ALARM_TIME_UP, getEventName(EVENT_ALARM_TIME_UP));
        mHandler.sendMessageDelayed(msg, mAlarmDuration);

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v(LOG_TAG, "onStart()");
        mBroadcastMgr = LocalBroadcastManager.getInstance(this);
        mBroadcastMgr.registerReceiver(mNotificationReceiver,
                new IntentFilter(ACTION_NOTIFICATION));
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "onResume()");
        mBroadcastMgr.unregisterReceiver(mNotificationReceiver);
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
            Log.d(LOG_TAG, "creating thread: " + name);
            this.mThreadName = name;
        }

        @Override
        public void start(){
            Log.d(LOG_TAG, "thread: " + mThreadName + " is started");

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
    }

    private void initViews(Intent intent){
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

        mScheduleId = intent.getLongExtra(ScheduleContract.ScheduleEntry._ID,1);

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
    }

    private void sendNotification(){
        Log.d(LOG_TAG, "sendNotification()");

        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_access_alarms_24dp)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(mScheduleTitle)
                .setCategory(Notification.CATEGORY_ALARM)
                .setAutoCancel(true);

        /*Intent resultIntent = new Intent(this,
                NotifierDetailActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);*/

        int notifyId = 1;
        mNotificationMgr = (NotificationManager)getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationMgr.notify(notifyId, builder.build());
    }


    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateAlarmState();
            sendNotification();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopServices();
                    finish();
                }
            }, 3 * 1000);
        }
    };


    // should run in a different thread
    private void startServices(){
        Log.d(LOG_TAG, "startServices()");

        /* playing music */
        Intent intent = new Intent(getApplicationContext(), AudioPlayerService.class);
        startService(intent);
        /* shake detecting */
        mShakeDetector.start();
    }

    private void stopServices(){
        Log.d(LOG_TAG, "stopServices()");

        /* stop music */
        Intent intent = new Intent(getApplicationContext(),AudioPlayerService.class);
        stopService(intent);
        /* stop shake detecting */
        mShakeDetector.stop();
    }

    private void updateAlarmState(){
        Log.d(LOG_TAG, "updateAlarmState()");

        Uri uri = ContentUris.withAppendedId(ScheduleContract.ScheduleEntry.CONTENT_URI,mScheduleId);
        ContentValues updated = new ContentValues();
        updated.put(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE, ScheduleModel.DONE);
        getContentResolver().update(uri, updated, null, null);
    }

}
