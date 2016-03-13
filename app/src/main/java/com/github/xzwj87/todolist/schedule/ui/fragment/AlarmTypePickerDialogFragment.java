package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.util.ArrayList;
import java.util.List;

public class AlarmTypePickerDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener {
    private static final String LOG_TAG = AlarmTypePickerDialogFragment.class.getSimpleName();

    private static List<String> sAlarmTypeArray = null;
    private static List<String> sAlarmTypeTextArray = null;

    private OnPickAlertTypeListener mListener;
    private int mSelectedIdx = 0;
    @ScheduleModel.AlarmType private String mAlarmType;

    public interface OnPickAlertTypeListener {
        void onAlertTimePicked(@ScheduleModel.AlarmType String alarmType);
    }

    public AlarmTypePickerDialogFragment() {
        if (sAlarmTypeArray == null || sAlarmTypeTextArray == null) {
            buildAlarmTypeLists();
        }
    }

    public static AlarmTypePickerDialogFragment newInstance(
            OnPickAlertTypeListener listener, @ScheduleModel.AlarmType String alarmType) {
        AlarmTypePickerDialogFragment fragment = new AlarmTypePickerDialogFragment();
        fragment.setOnPickAlertTypeListener(listener);
        fragment.setInitialAlarmType(alarmType);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String[] items = sAlarmTypeTextArray.toArray(new String[sAlarmTypeTextArray.size()]);
        builder.setSingleChoiceItems(items, mSelectedIdx, this);

        return builder.create();
    }

    @Override @SuppressWarnings("ResourceType")
    public void onClick(DialogInterface dialog, int which) {
        Log.v(LOG_TAG, "onClick(): which = " + which);
        mAlarmType = sAlarmTypeArray.get(which);
        mListener.onAlertTimePicked(mAlarmType);
        dismiss();
    }

    public void setInitialAlarmType(@ScheduleModel.AlarmType String alarmType) {
        mAlarmType = alarmType;
        mSelectedIdx = sAlarmTypeArray.indexOf(mAlarmType);
    }

    public void setOnPickAlertTypeListener(OnPickAlertTypeListener listener) {
        mListener = listener;
    }

    private void buildAlarmTypeLists() {
        sAlarmTypeArray = new ArrayList<>();
        sAlarmTypeArray.add(ScheduleModel.ALARM_NONE);
        sAlarmTypeArray.add(ScheduleModel.ALARM_10_MINUTES_BEFORE);
        sAlarmTypeArray.add(ScheduleModel.ALARM_30_MINUTES_BEFORE);
        sAlarmTypeArray.add(ScheduleModel.ALARM_1_HOUR_BEFORE);
        // sAlarmTypeArray.add(ScheduleModel.ALARM_CUSTOM);

        sAlarmTypeTextArray = new ArrayList<>();
        Context context = App.getAppContext();
        sAlarmTypeTextArray.add(context.getString(R.string.alarm_type_no_alarm));
        sAlarmTypeTextArray.add(context.getString(R.string.alarm_type_10_minutes_before));
        sAlarmTypeTextArray.add(context.getString(R.string.alarm_type_30_minutes_before));
        sAlarmTypeTextArray.add(context.getString(R.string.alarm_type_1_hour_before));
        // sAlarmTypeTextArray.add(context.getString(R.string.alarm_type_custom));
    }

}
