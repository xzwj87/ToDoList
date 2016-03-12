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

public class ScheduleTypePickerDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener {
    private static final String LOG_TAG = ScheduleTypePickerDialogFragment.class.getSimpleName();

    private static List<String> sScheduleTypeArray = null;
    private static List<String> sScheduleTypeTextArray = null;

    private OnScheduleTypeSetListener mListener;
    private int mSelectedIdx = 0;
    @ScheduleModel.ScheduleType private String mScheduleType;

    public interface OnScheduleTypeSetListener {
        void onScheduleTypeSet(@ScheduleModel.ScheduleType String scheduleType);
    }

    public ScheduleTypePickerDialogFragment() {
        if (sScheduleTypeArray == null || sScheduleTypeTextArray == null) {
            buildScheduleTypeLists();
        }
    }

    public static ScheduleTypePickerDialogFragment newInstance(
            OnScheduleTypeSetListener listener, @ScheduleModel.ScheduleType String scheduleType) {
        ScheduleTypePickerDialogFragment fragment = new ScheduleTypePickerDialogFragment();
        fragment.setOnPickScheduleTypeListener(listener);
        fragment.setInitialScheduleType(scheduleType);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String[] items = sScheduleTypeTextArray.toArray(new String[sScheduleTypeTextArray.size()]);
        builder.setSingleChoiceItems(items, mSelectedIdx, this);

        return builder.create();
    }


    @Override @SuppressWarnings("ResourceType")
    public void onClick(DialogInterface dialog, int which) {
        Log.v(LOG_TAG, "onClick(): which = " + which);
        mScheduleType = sScheduleTypeArray.get(which);

        mListener.onScheduleTypeSet(mScheduleType);
        dismiss();
    }

    public void setInitialScheduleType(@ScheduleModel.ScheduleType String scheduleType) {
        mScheduleType = scheduleType;
        mSelectedIdx = sScheduleTypeArray.indexOf(mScheduleType);
    }

    public void setOnPickScheduleTypeListener(OnScheduleTypeSetListener listener) {
        mListener = listener;
    }

    private void buildScheduleTypeLists() {
        sScheduleTypeArray = new ArrayList<>();
        sScheduleTypeArray.add(ScheduleModel.SCHEDULE_TYPE_DEFAULT);
        sScheduleTypeArray.add(ScheduleModel.SCHEDULE_TYPE_MEETING);
        sScheduleTypeArray.add(ScheduleModel.SCHEDULE_TYPE_ENTERTAINMENT);
        sScheduleTypeArray.add(ScheduleModel.SCHEDULE_TYPE_DATE);

        sScheduleTypeTextArray = new ArrayList<>();
        Context context = App.getAppContext();
        sScheduleTypeTextArray.add(context.getString(R.string.schedule_type_default));
        sScheduleTypeTextArray.add(context.getString(R.string.schedule_type_meeting));
        sScheduleTypeTextArray.add(context.getString(R.string.schedule_type_entertainment));
        sScheduleTypeTextArray.add(context.getString(R.string.schedule_type_date));
    }
}
