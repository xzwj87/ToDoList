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
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
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

/**
 * Created by JasonWang on 2016/4/30.
 */
public class ScheduleShareActivity extends AppCompatActivity
        implements View.OnClickListener,IWXAPIEventHandler,IWeiboHandler.Response{
    public static final String TAG = "ScheduleShareActivity";

    private PackageManager mPackageMgr = null;
    private ImageView mShareWithWeibo = null;
    private ImageView mShareWithWeixinFriend = null;
    private ImageView mShareWithWeixinCircle = null;
    private ImageView mShareWithSms = null;
    private TextView mShareCancel = null;

    private Bundle mShareData = null;
    private String mShareMessage = null;
    private IWXAPI mWxAPI = null;
    private int mWxShareFlag = 0x00;
    private IWeiboShareAPI mWbAPI = null;
    private HashMap<String,String> mAppNameMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedState){
        super.onCreate(savedState);
        setContentView(R.layout.activity_schedule_share);

        mShareData = getIntent().getExtras();

        registerToWX();
        registerToWB();
        mAppNameMap.put(ShareConstants.WX_PACKAGE_NAME,
                getResources().getString(R.string.weixin));
        mAppNameMap.put(ShareConstants.WEBO_PACKAGE_NAME,
                getResources().getString(R.string.weibo));

        initViews();

        composeShareMessage();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(mShareCancel)){
            finish();
        }else if(v.equals(mShareWithWeibo)){
            ShareToWB();
        }else if(v.equals(mShareWithWeixinFriend)) {
            mWxShareFlag = ShareConstants.SHARE_TO_FRIEND;
            if (isInstalled(ShareConstants.WX_PACKAGE_NAME)) {
                shareToWX();
            }
        }else if(v.equals(mShareWithWeixinCircle)){
            mWxShareFlag = ShareConstants.SHARE_TO_CIRCLE;
            if (isInstalled(ShareConstants.WX_PACKAGE_NAME)) {
                shareToWX();
            }
        }else if(v.equals(mShareWithSms)){
            shareToSMS();
        }
        // hide the UI
        setVisible(false);
    }

    private void initViews(){

        Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        this.overridePendingTransition(R.anim.enter_from_bottom, R.anim.fade_out);

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

    private void shareToWX(){
        Log.v(TAG, "shareToWX");

        String scheduleTitle = mShareData.getString(ScheduleContract.ScheduleEntry.COLUMN_TITLE);
        WXTextObject textObject = new WXTextObject();
        textObject.text = mShareMessage;

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
    private void registerToWX(){
        mWxAPI = WXAPIFactory.createWXAPI(this, ShareConstants.WX_APP_ID, true);
        mWxAPI.registerApp(ShareConstants.WX_APP_ID);
    }

    private void registerToWB(){
        // register to WB
        mWbAPI = WeiboShareSDK.createWeiboAPI(this, ShareConstants.WB_APP_KEY);
        mWbAPI.registerApp();
    }

    private void shareToSMS(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra(ShareConstants.SMS_BODY, mShareMessage);
        startActivityForResult(intent, ShareConstants.SHARE_TO_SMS);
    }

    private void ShareToWB(){
        WeiboMultiMessage wbMsg = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.text = mShareMessage;
        wbMsg.textObject = textObject;

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = wbMsg;
        mWbAPI.sendRequest(this, request);
    }

    protected void composeShareMessage(){
        long  scheduleTime = mShareData.getLong(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME);
        String scheduleTitle = mShareData.getString(ScheduleContract.ScheduleEntry.COLUMN_TITLE);
        String format = "yyyy/MM/dd,HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        boolean isScheduleDone = mShareData.getBoolean(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE);

        mShareMessage = "#" + getResources().getString(R.string.app_name) + "#" +
                dateFormat.format(new Date(scheduleTime)) +
                (isScheduleDone ? getResources().getString(R.string.start_schedule) :
                getResources().getString(R.string.finish_schedule)) +
                scheduleTitle + getResources().getString(R.string.come_on);
    }

    // for SMS share result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ShareConstants.SHARE_TO_SMS){
            int msgId = 0;
            if(resultCode == RESULT_OK){
                msgId = R.string.share_result_success;
            }else if(resultCode == RESULT_CANCELED){
                msgId = R.string.share_result_cancel;
            }
            Toast.makeText(this,msgId,Toast.LENGTH_LONG).show();
        }
    }

    // when WX call us
    @Override
    public void onReq(BaseReq baseReq) {
    }

    // response from WeiXin
    @Override
    public void onResp(BaseResp resp) {
        int resultCode = 0;
        switch(resp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                resultCode = R.string.share_result_success;
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

    @Override
    protected  void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        mWbAPI.handleWeiboResponse(intent,this);
    }

    // response from Weibo
    @Override
    public void onResponse(BaseResponse baseResponse) {
        int result = 0;
        switch (baseResponse.errCode){
            case WBConstants.ErrorCode.ERR_OK:
                result = R.string.share_result_success;
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                result = R.string.share_result_cancel;
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                result = R.string.share_result_auth_deny;
                break;
            default:
                result = R.string.share_result_unknown;
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        finish();
    }
}