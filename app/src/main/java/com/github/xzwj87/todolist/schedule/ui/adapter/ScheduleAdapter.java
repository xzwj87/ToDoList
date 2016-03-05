package com.github.xzwj87.todolist.schedule.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private static final String LOG_TAG = ScheduleAdapter.class.getSimpleName();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM d, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");

    private static OnItemClickListener mListener;

    private DataSource mDataSourse;

    public interface DataSource {
        ScheduleModel getItemAtPosition(int position);
        int getItemCount();
    }

    public ScheduleAdapter(DataSource dataSource) {
        mDataSourse = dataSource;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_schedule, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduleModel schedule = mDataSourse.getItemAtPosition(position);
        Log.v(LOG_TAG, "onBindViewHolder(): position = " + position + ", schedule = " + schedule);


        holder.mTvScheduleTitle.setText(schedule.getTitle());

        String startTime = TIME_FORMAT.format(schedule.getScheduleStart());
        holder.mTvScheduleStartTime.setText(startTime);

        String endTime = TIME_FORMAT.format(schedule.getScheduleEnd());
        holder.mTvScheduleEndTime.setText(endTime);
    }

    @Override
    public int getItemCount() {
        return mDataSourse.getItemCount();
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ScheduleAdapter.ViewHolder vh);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_schedule_title) public TextView mTvScheduleTitle;
        @Bind(R.id.tv_schedule_start_time) public TextView mTvScheduleStartTime;
        @Bind(R.id.tv_schedule_end_time) public TextView mTvScheduleEndTime;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(getLayoutPosition(), ViewHolder.this);
                    }
                }
            });
        }

    }

}
