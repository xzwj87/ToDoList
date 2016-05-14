package com.github.xzwj87.todolist.schedule.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.schedule.utility.ScheduleUtility;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private static final String LOG_TAG = ScheduleAdapter.class.getSimpleName();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/d");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MMM");
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("d");

    private static OnItemClickListener mListener;

    private DataSource mDataSource;

    public interface DataSource {
        ScheduleModel getItemAtPosition(int position);
        int getItemCount();
    }

    public ScheduleAdapter(DataSource dataSource) {
        mDataSource = dataSource;
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
        ScheduleModel schedule = mDataSource.getItemAtPosition(position);
        Log.v(LOG_TAG, "onBindViewHolder(): position = " + position +
                ", schedule id = " + schedule.getId() + ", title = " + schedule.getTitle());

        holder.mIvColorStrip.setBackgroundColor(
                ScheduleUtility.getScheduleColor(schedule.getType()));

        String startDate = DATE_FORMAT.format(schedule.getScheduleStart());
        holder.mTvScheduleStartDate.setText(startDate);

        String startTime = TIME_FORMAT.format(schedule.getScheduleStart());
        holder.mTvScheduleStartTime.setText(startTime);

        holder.mTvScheduleTitle.setText(schedule.getTitle().replaceAll("\n", " "));
    }

    @Override
    public int getItemCount() {
        return mDataSource.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return mDataSource.getItemAtPosition(position).getId();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ScheduleAdapter.ViewHolder vh);
        void onItemLongClick(int position, ScheduleAdapter.ViewHolder vh);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_schedule_item_start_date) public TextView mTvScheduleStartDate;
        @Bind(R.id.tv_schedule_title) public TextView mTvScheduleTitle;
        @Bind(R.id.tv_schedule_start_time) public TextView mTvScheduleStartTime;
        @Bind(R.id.iv_schedule_item_color_strip) public ImageView mIvColorStrip;

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

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mListener != null){
                        mListener.onItemLongClick(getLayoutPosition(),ViewHolder.this);
                    }
                    return false;
                }
            });
        }

    }

}
