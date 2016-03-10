package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.util.Date;

public class AlertTimeDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String LOG_TAG = AlertTimeDialogFragment.class.getSimpleName();

    private OnPickAlertTimeListener mListener;
    private int mSelectedIdx = 0;
    @ScheduleModel.AlarmType private String mAlarmType = ScheduleModel.ALARM_10_MINUTES_BEFORE;
    private Date mAlarmTime;


    public interface OnPickAlertTimeListener {
        void onAlertTimePicked(@ScheduleModel.AlarmType String alarmType, Date alarmTime);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setSingleChoiceItems(R.array.alert_time_type, mSelectedIdx, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.v(LOG_TAG, "onClick(): which = " + which);

        dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPickAlertTimeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPickAlertTimeListener");
        }
    }

    public void setInitialAlarm(@ScheduleModel.AlarmType String alarmType, Date alarmTime) {
        mAlarmType = alarmType;
        mAlarmTime = alarmTime;

        switch (mAlarmType) {
            case ScheduleModel.ALARM_NONE:
                mSelectedIdx = 0;
                break;
            case ScheduleModel.ALARM_10_MINUTES_BEFORE:
                mSelectedIdx = 1;
                break;
            case ScheduleModel.ALARM_30_MINUTES_BEFORE:
                mSelectedIdx = 2;
                break;
            case ScheduleModel.ALARM_CUSTOM_BEFORE:
                mSelectedIdx = 3;
                break;
        }
    }
}
