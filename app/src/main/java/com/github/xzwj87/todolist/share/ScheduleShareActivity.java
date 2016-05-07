package com.github.xzwj87.todolist.share;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.SocketHandler;

/**
 * Created by JasonWang on 2016/4/30.
 */
public class ScheduleShareActivity extends AppCompatActivity
        implements View.OnClickListener,IWXAPIEventHandler{
    public static final String TAG = "ScheduleShareActivity";

    private PackageManager mPackageMgr;
    private ImageView mShareWithWeibo;
    private ImageView mShareWithWeixinFriend;
    private ImageView mShareWithWeixinCircle;
    private ImageView mShareWithSms;
    private TextView mShareCancel;

    private Bundle mShareData;
    private IWXAPI mWxAPI;
    private int mWxShareFlag;
    private HashMap<String,String> mAppNameMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedState){
        super.onCreate(savedState);

        Intent intent = getIntent();
        mShareData = intent.getExtras();

        registerToWX();

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

        mAppNameMap.put(ShareConstants.WX_PACKAGE_NAME,
                getResources().getString(R.string.weixin));
        mAppNameMap.put(ShareConstants.WEBO_PACKAGE_NAME,
                getResources().getString(R.string.weibo));
    }


    @Override
    public void onClick(View v) {
        if(v.equals(mShareCancel)){
            finish();
        }else if(v.equals(mShareWithWeibo)){

        }else if(v.equals(mShareWithWeixinFriend)) {
            mWxShareFlag = ShareConstants.SHARE_TO_FRIEND;
            if (isInstalled(ShareConstants.WX_PACKAGE_NAME)) {
                shareToWx();
            }
        }else if(v.equals(mShareWithWeixinCircle)){
            mWxShareFlag = ShareConstants.SHARE_TO_CIRCLE;
            if (isInstalled(ShareConstants.WX_PACKAGE_NAME)) {
                shareToWx();
            }
        }else if(v.equals(mShareWithSms)){
            sendSms();
        }
        // hide the UI
        setVisible(false);
    }

    // whether the package is installed
    private boolean isInstalled(String packageName){
        try{
            mPackageMgr = getPackageManager();
            mPackageMgr.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            Log.e(TAG, packageName + "is not found");

            String msg = mAppNameMap.get(packageName) + " " +
                    getResources().getString(R.string.install_info);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            e.printStackTrace();

            return false;
        }
    }

    // when WX call us
    @Override
    public void onReq(BaseReq baseReq) {
    }

    // when we call WX, wx response
    @Override
    public void onResp(BaseResp resp) {
        int resultCode = 0;
        switch(resp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                resultCode = R.string.share_result_sucess;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                resultCode = R.string.share_result_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                resultCode = R.string.share_result_auth_deny;
                break;
            default:
                resultCode = R.string.share_result_unknown;
                break;
        }
        Toast.makeText(this,resultCode,Toast.LENGTH_LONG).show();
        // delay to finish the activity
        finish();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);*/
    }


    public void shareToWx(){
        Log.v(TAG,"shareToWx");

        long  scheduleTime = mShareData.getLong(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME);
        String scheduleTitle = mShareData.getString(ScheduleContract.ScheduleEntry.COLUMN_TITLE);
        String format = "yyyy/MM/dd,HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String msg = dateFormat.format(new Date(scheduleTime)) + ",我在Todo List完成了任务"
                + "#" + scheduleTitle +"#" + ",快来加入吧!";
        WXTextObject textObject = new WXTextObject();
        textObject.text = msg;

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;
        mediaMessage.description = scheduleTitle;

        // send the req to weixin
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = mediaMessage;
        req.scene = mWxShareFlag == 0x00 ? SendMessageToWX.Req.WXSceneSession :
                SendMessageToWX.Req.WXSceneTimeline;

        mWxAPI.sendReq(req);
    }

    // register Tencent WX API
    public void registerToWX(){
        mWxAPI = WXAPIFactory.createWXAPI(this, ShareConstants.WX_APP_ID, true);
        mWxAPI.registerApp(ShareConstants.WX_APP_ID);
    }


    public void sendSms(){

        String scheduleTitle = mShareData.getString(ScheduleContract.ScheduleEntry.COLUMN_TITLE);
        String msg = "#Todo List#" + "我刚添加了新的任务:"  + scheduleTitle +"，快来加入吧！";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra(ShareConstants.SMS_BODY, msg);
        startActivityForResult(intent, ShareConstants.SHARE_TO_SMS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ShareConstants.SHARE_TO_SMS){
            int msgId = 0;
            if(resultCode == RESULT_OK){
                msgId = R.string.share_result_sucess;
            }else if(resultCode == RESULT_CANCELED){
                msgId = R.string.share_result_cancel;
            }
            Toast.makeText(this,msgId,Toast.LENGTH_LONG).show();
        }
    }
}
