package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScheduleDetailFragment extends Fragment {
    private static final String LOG_TAG = ScheduleDetailFragment.class.getSimpleName();

    public static final String ARG_ITEM_ID = "item_id";
    public static final String DETAIL_URI = "URI";

    private Uri mUri;

    @Bind(R.id.tv_schedule_detail) TextView mTvScheduleDetail;

    public ScheduleDetailFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_detail, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
            Log.v(LOG_TAG, "onCreateView(): mUri = " + mUri);
            mTvScheduleDetail.setText(mUri.toString());
        }

        return rootView;
    }
}
