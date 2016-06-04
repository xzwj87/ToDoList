package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.util.ArrayList;

/**
 * Created by JasonWang on 2016/6/2.
 */
public class SchedulePriorityPickerDialogFragment extends DialogFragment
            implements DialogInterface.OnClickListener {
    public static final String TAG = SchedulePriorityPickerDialogFragment.class.getSimpleName();

    private int mSelectedItemId = 2; // default: important/nonUrgent
    private onPriorityChangedListener mListener = null;

    public interface onPriorityChangedListener {
        void onPriorityChanged(@ScheduleModel.Priority int priority);
    }

    public SchedulePriorityPickerDialogFragment(){
    }

    public static SchedulePriorityPickerDialogFragment newInstance(
            @ScheduleModel.Priority int priority, onPriorityChangedListener listener) {
        SchedulePriorityPickerDialogFragment dialogFragment = new SchedulePriorityPickerDialogFragment();
        dialogFragment.setSchedulePriorityChangedListener(listener);
        dialogFragment.setSchedulePriority(priority);

        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedState) {
        Log.v(TAG,"onCreateDialog()");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setSingleChoiceItems(R.array.schedule_priority_picker_choice_list,
                mSelectedItemId, this);

        return builder.create();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.i(TAG, "onClick(): selected item = " + which);
        mListener.onPriorityChanged(which);
        dismiss();
    }

    private void setSchedulePriorityChangedListener(onPriorityChangedListener listener){
        mListener = listener;
    }

    private void setSchedulePriority(int priority){
        mSelectedItemId = priority;
    }
}
