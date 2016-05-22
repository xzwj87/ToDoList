package com.github.xzwj87.todolist.schedule.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.schedule.utility.ScheduleUtility;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by JasonWang on 2016/5/14.
 */

public class ScheduleGridAdapter extends BaseAdapter {
    public static final String LOG_TAG = "ScheduleGridAdapter";

    private DataSource mDataSource = null;
    private static OnItemClickListener mListener;
    private static LayoutInflater mInflater = null;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM dd");
    //private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");


    public ScheduleGridAdapter(DataSource dataSource){
        Log.v(LOG_TAG,"creating grid adapter");
        mDataSource = dataSource;
    }

    public interface DataSource{
        ScheduleModel getItemAtPosition(int position);
        int getItemCount();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        mListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, GridViewHolder vh);
        void onItemLongClick(int position, GridViewHolder vh);
    }

    @Override
    public int getCount() {
        return mDataSource.getItemCount();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.getItemAtPosition(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataSource.getItemAtPosition(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if(mInflater == null) {
            mInflater = LayoutInflater.from(context);
        }
        View rootView = mInflater.inflate(R.layout.item_schedule_grid,null);

        GridViewHolder vh = new GridViewHolder(rootView,position);

        ScheduleModel scheduleModel = mDataSource.getItemAtPosition(position);
        vh.mTvScheduleTitle.setText(scheduleModel.getTitle());
        vh.mTvScheduleTitle.setTextColor(ScheduleUtility.getScheduleColor(
                scheduleModel.getType()));

        String scheduleDate = DATE_FORMAT.format(scheduleModel.getScheduleStart());
        String startTime = TIME_FORMAT.format(scheduleModel.getScheduleStart());
        String endTime = TIME_FORMAT.format(scheduleModel.getScheduleEnd());
        String scheduleTime = startTime + " - " + endTime;

        vh.mTvSchedulePeriod.setText(scheduleDate);
        vh.mTvScheduleTime.setText(scheduleTime);
        vh.mTvScheduleAlarmTime.setText(TIME_FORMAT.format(
                scheduleModel.getAlarmTime()));
        vh.mTvScheduleType.setText(ScheduleUtility.getScheduleTypeText(
                scheduleModel.getType()));

        return rootView;
    }

    public class GridViewHolder{
        @Bind(R.id.grid_schedule_title) public TextView mTvScheduleTitle;

        @Bind(R.id.grid_schedule_period_icon) public ImageView mIvSchedulePeriod;
        @Bind(R.id.grid_schedule_period) public TextView mTvSchedulePeriod;
        @Bind(R.id.grid_schedule_time) public TextView mTvScheduleTime;

        @Bind(R.id.grid_alarm_time_icon) public ImageView mIvScheduleAlarmTime;
        @Bind(R.id.grid_alarm_time) public TextView mTvScheduleAlarmTime;

        @Bind(R.id.grid_schedule_type_icon) public ImageView mIvScheduleType;
        @Bind(R.id.grid_schedule_type) public TextView mTvScheduleType;



        public GridViewHolder(View view,int position){
            ButterKnife.bind(this,view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position,GridViewHolder.this);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.onItemLongClick(position,GridViewHolder.this);
                    return false;
                }
            });
        }
    }
}
