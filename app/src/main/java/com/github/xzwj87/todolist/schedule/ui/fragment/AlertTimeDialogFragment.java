package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.github.xzwj87.todolist.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AlertTimeDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String LOG_TAG = AlertTimeDialogFragment.class.getSimpleName();

    private OnPickAlertTimeListener mListener;
    @AlertTimeType private int mAlertTimeType = ALERT_NONE;

    public static final int ALERT_NONE = 0;
    public static final int ALERT_10_MINUTES_BEFORE = 1;
    public static final int ALERT_30_MINUTES_BEFORE = 2;
    public static final int ALERT_1_HOUR_BEFORE = 3;
    public static final int ALERT_CUSTOM = 4;

    @IntDef({ALERT_NONE, ALERT_10_MINUTES_BEFORE, ALERT_30_MINUTES_BEFORE, ALERT_1_HOUR_BEFORE,
            ALERT_CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlertTimeType {}

    public interface OnPickAlertTimeListener {
        public void onAlertTimePicked(@AlertTimeType int alertTimeType);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setSingleChoiceItems(R.array.alert_time_type, mAlertTimeType, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.v(LOG_TAG, "onClick(): which = " + which);
        mListener.onAlertTimePicked(which);
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

    public void setSelected(@AlertTimeType int alertTimeType) {
        mAlertTimeType = alertTimeType;
    }
}
