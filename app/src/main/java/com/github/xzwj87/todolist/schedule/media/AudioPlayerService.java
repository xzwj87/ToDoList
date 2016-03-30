package com.github.xzwj87.todolist.schedule.media;


import com.github.xzwj87.todolist.R;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by JasonWang on 2016/3/25.
 */

public class AudioPlayerService extends Service{
    private static final String TAG = "AudioPlayerService";

    private MediaPlayer mPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.over_the_horizon);
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int resId){
        play();
        return super.onStartCommand(intent,flags,resId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        stop();
    }

    protected void play(){
        Log.d(TAG,"play()");
        mPlayer.start();
        mPlayer.setLooping(true);
    }

    protected void stop(){
        Log.d(TAG,"stop()");

        if(mPlayer.isPlaying()){
            mPlayer.stop();
        }
    }
}
