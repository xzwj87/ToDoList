package com.github.xzwj87.todolist.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;

/**
 * Created by JasonWang on 2016/4/30.
 */
public class ScheduleShareActivity extends AppCompatActivity
        implements View.OnClickListener{
    public static final String TAG = "ScheduleShareActivity";

    private ImageView mShareWithWeibo;
    private ImageView mShareWithFriend;
    private ImageView mShareWithCircle;
    private ImageView mShareWithSms;
    private Button mShareCancel;

    @Override
    public void onCreate(Bundle savedState){
        super.onCreate(savedState);

        Intent intent = getIntent();
        String scheduleTile = intent.getStringExtra(ScheduleContract.ScheduleEntry.COLUMN_TITLE);

        // need to call before setContentView()
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_schedule_share);

        Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        //setTitle(getResources().getString(R.string.share_to))
        mShareWithWeibo = (ImageView)findViewById(R.id.share_with_weibo);
        mShareWithFriend = (ImageView)findViewById(R.id.share_with_weixin_circle);
        mShareWithCircle = (ImageView)findViewById(R.id.share_with_weixin_friend);
        mShareWithSms = (ImageView)findViewById(R.id.share_with_sms);
        mShareCancel = (Button)findViewById(R.id.share_cancel);

        mShareWithWeibo.setOnClickListener(this);
        mShareWithFriend.setOnClickListener(this);
        mShareWithCircle.setOnClickListener(this);
        mShareWithSms.setOnClickListener(this);
        mShareCancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == mShareWithWeibo.getId()){

        }else if(id == mShareWithFriend.getId()){

        }else if(id == mShareWithCircle.getId()){

        }else if(id == mShareWithSms.getId()){

        }

    }
}
