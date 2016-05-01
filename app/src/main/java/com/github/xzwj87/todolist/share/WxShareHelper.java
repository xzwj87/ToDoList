package com.github.xzwj87.todolist.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

/**
 * Created by JasonWang on 2016/5/1.
 */
public class WxShareHelper implements IWXAPIEventHandler{
    public static final String TAG = "WxShareHelper";

    private IWXAPI mWxAPI;

    private String mScheduleTitle;
    private long mScheduleTime;
    private int mWayToShare;
    private Context mContext;
    private Bundle mShareData;

    public WxShareHelper(Context context, Bundle data){
        mContext = context;
        mShareData = data;
    }

    public void share(){

        //registerToWX();

        mScheduleTitle = mShareData.getString(ScheduleContract.ScheduleEntry.COLUMN_TITLE);
        mScheduleTime = mShareData.getLong(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME,
                System.currentTimeMillis());
        mWayToShare = mShareData.getInt(ShareConstants.WX_SHARE_FLAG, ShareConstants.SHARE_TO_CIRCLE);

        shareToWx();
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
                resultCode = R.string.wx_share_result_sucess;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                resultCode = R.string.wx_share_result_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                resultCode = R.string.wx_share_result_auth_deny;
                break;
            default:
                resultCode = R.string.wx_share_result_default;
                break;
        }

        Toast.makeText(mContext,resultCode,Toast.LENGTH_LONG).show();
    }


    public void shareToWx(){
        Log.v(TAG,"shareToWx");

        String format = "yyyy/MM/dd,HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String msg = dateFormat.format(new Date(mScheduleTime)) + ",我在Todo List完成了任务"
                + "#" + mScheduleTitle +"#" + ",快来加入吧!";
        WXTextObject textObject = new WXTextObject();
        textObject.text = msg;

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;
        mediaMessage.description = mScheduleTitle;


        // send the req to weixin
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = mediaMessage;
        req.scene = mWayToShare == 0x00 ? SendMessageToWX.Req.WXSceneSession :
                SendMessageToWX.Req.WXSceneTimeline;

        mWxAPI.sendReq(req);
    }

    // register Tencent WX API
    public void registerToWX(){
        mWxAPI = WXAPIFactory.createWXAPI(mContext, ShareConstants.WX_APP_ID, true);
        mWxAPI.registerApp(ShareConstants.WX_APP_ID);
    }
}
