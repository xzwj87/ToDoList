package com.github.xzwj87.todolist.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.github.xzwj87.todolist.R;

/**
 * Created by JasonWang on 2016/4/26.
 */
public class SharePreferenceHelper {
    public static final String TAG = "SharePreferenceHelper";

    private static SharePreferenceHelper mInstance;
    private SharedPreferences mSharePref;
    private Context mContext;
    private Resources mResources;

    private String mAlarmRingtoneKey;
    private String mAlarmVolumeKey;
    private String mAlarmDurationKey;


    public static SharePreferenceHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new SharePreferenceHelper(context);
        }

        return mInstance;
    }

    public SharePreferenceHelper(Context context){
        this.mContext = context;
        this.mResources = mContext.getResources();
        this.mSharePref = PreferenceManager.getDefaultSharedPreferences(context);

        mAlarmRingtoneKey = mResources.getString(R.string.settings_alarm_ringtone_key);
        mAlarmVolumeKey = mResources.getString(R.string.settings_alarm_volume_key);
        mAlarmDurationKey = mResources.getString(R.string.settings_alarm_duration_key);
    }

    public Uri getAlarmRingtone(){
        Uri uri = Uri.parse(mSharePref.getString(mAlarmRingtoneKey,
                Settings.System.DEFAULT_RINGTONE_URI.toString()));
        return uri;
    }


    public int getAlarmVolume(){
        String defaultVal = mResources.getString(R.string.settings_alarm_volume_default);
        String volume = mSharePref.getString(mAlarmVolumeKey, defaultVal);

        return Integer.parseInt(volume);
    }

    public int getAlarmDuration(){
        String defaultVal = mResources.getString(R.string.settings_alarm_duration_default);
        String duration = mSharePref.getString(mAlarmDurationKey,defaultVal);

        return Integer.parseInt(duration)*1000;
    }
}
