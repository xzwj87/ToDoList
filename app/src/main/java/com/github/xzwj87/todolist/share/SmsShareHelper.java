package com.github.xzwj87.todolist.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by JasonWang on 2016/5/5.
 */
public class SmsShareHelper {
    public static final String TAG = "SmsShareHelper";

    private Context mContext;
    private static SmsShareHelper mInstance;

    public SmsShareHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new SmsShareHelper(context);
        }

        return mInstance;
    }

    public SmsShareHelper(Context context){
        this.mContext = context;
    }

    public void sendSms(String schedule){
        String msg = "#Todo List#" + "我刚添加了新的任务，快来加入吧！";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        ((Activity) mContext).startActivityForResult(intent, ShareConstants.SHARE_TO_SMS);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

    }
}
