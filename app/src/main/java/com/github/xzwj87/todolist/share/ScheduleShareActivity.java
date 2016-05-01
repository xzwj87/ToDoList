package com.github.xzwj87.todolist.share;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.xzwj87.todolist.R;

/**
 * Created by JasonWang on 2016/4/30.
 */
public class ScheduleShareActivity extends AppCompatActivity
        implements View.OnClickListener{
    public static final String TAG = "ScheduleShareActivity";

    private PackageManager mPackageMgr;
    private ImageView mShareWithWeibo;
    private ImageView mShareWithWeixinFriend;
    private ImageView mShareWithWeixinCircle;
    private ImageView mShareWithSms;
    private TextView mShareCancel;

    private WxShareHelper mWxShare;
    private Bundle mShareData;

    @Override
    public void onCreate(Bundle savedState){
        super.onCreate(savedState);

        Intent intent = getIntent();
        mShareData = intent.getExtras();

        mWxShare = new WxShareHelper(this,mShareData);
        mWxShare.registerToWX();

        setContentView(R.layout.activity_schedule_share);

        Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        mShareWithWeibo = (ImageView)findViewById(R.id.share_with_weibo);
        mShareWithWeixinFriend = (ImageView)findViewById(R.id.share_with_weixin_friend);
        mShareWithWeixinCircle = (ImageView)findViewById(R.id.share_with_weixin_circle);
        mShareWithSms = (ImageView)findViewById(R.id.share_with_sms);
        mShareCancel = (TextView)findViewById(R.id.share_cancel);

        mShareWithWeibo.setOnClickListener(this);
        mShareWithWeixinFriend.setOnClickListener(this);
        mShareWithWeixinCircle.setOnClickListener(this);
        mShareWithSms.setOnClickListener(this);
        mShareCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.equals(mShareCancel)){
            finish();
        }else if(v.equals(mShareWithWeibo)){

        }else if(v.equals(mShareWithWeixinFriend)) {
            if (!isInstalled(ShareConstants.WX_PACKAGE_NAME)) {
                return;
            }
            mShareData.putInt(ShareConstants.WX_SHARE_FLAG, ShareConstants.SHARE_TO_FRIEND);
            mWxShare.share();
        }else if(v.equals(mShareWithWeixinCircle)){
            if (!isInstalled(ShareConstants.WX_PACKAGE_NAME)) {
                return;
            }
            mShareData.putInt(ShareConstants.WX_SHARE_FLAG, ShareConstants.SHARE_TO_CIRCLE);
            mWxShare.share();
        }else if(v.equals(mShareWithSms)){

        }
        finish();
    }

    // whether the package is installed
    private boolean isInstalled(String packageName){
        try{
            mPackageMgr = getPackageManager();
            mPackageMgr.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            Log.e(TAG, packageName + "is not found");
            Toast.makeText(this, packageName + "is not installed!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();

            return false;
        }
    }
}
