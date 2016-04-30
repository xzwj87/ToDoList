package com.github.xzwj87.todolist.share;


import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.*;
/**
 * Created by JasonWang on 2016/4/30.
 */
public class ShareHelper {

    public static final String TAG = "ShareHelper";
    private static ShareHelper mInstance;

    private Context mContext;
    private PackageManager mPackageMgr;

    private static final String WX_PACKAGE_NAME = "com.tencent.mm";
    private static final String WX_APP_ID = "wxb6f7522430205227";
    private IWXAPI WX_API;



    public static ShareHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new ShareHelper(context);
        }

        return mInstance;
    }

    public ShareHelper(Context context){
        this.mContext = context;
        this.mPackageMgr = context.getPackageManager();
    }


    // register Tencent WX API
    private void registerToWX(){
        WX_API = WXAPIFactory.createWXAPI(mContext,WX_APP_ID,true);
        WX_API.registerApp(WX_APP_ID);
    }



    // whether the package is installed
    private boolean isInstalled(String packageName){
        try{
            mPackageMgr.getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            Log.e(TAG,packageName + "is not found");
            Toast.makeText(mContext,packageName + "is not installed!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();

            return false;
        }
    }
}

