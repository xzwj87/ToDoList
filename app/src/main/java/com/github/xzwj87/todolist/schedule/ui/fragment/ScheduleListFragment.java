package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.interactor.GetScheduleList;
import com.github.xzwj87.todolist.schedule.interactor.QueryUseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.presenter.ScheduleListPresenter;
import com.github.xzwj87.todolist.schedule.presenter.ScheduleListPresenterImpl;
import com.github.xzwj87.todolist.schedule.ui.ScheduleListView;
import com.github.xzwj87.todolist.schedule.ui.adapter.ScheduleAdapter;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScheduleListFragment extends Fragment implements
        ScheduleAdapter.DataSource, ScheduleListView {
    private static final String LOG_TAG = ScheduleListFragment.class.getSimpleName();

    private Callbacks mCallbacks = sDummyCallbacks;
    private ScheduleAdapter mScheduleAdapter;
    private ScheduleListPresenter mScheduleListPresenter;

    @Bind(R.id.rv_schedule_list) RecyclerView mRvScheduleList;

    public interface Callbacks {
        void onItemSelected(long id, ScheduleAdapter.ViewHolder vh);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(long id, ScheduleAdapter.ViewHolder vh) { }
    };

    public ScheduleListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_list, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScheduleListPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScheduleListPresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScheduleListPresenter.destroy();
    }

    @Override
    public ScheduleModel getItemAtPosition(int position) {
        return mScheduleListPresenter.getScheduleAtPosition(position);
    }

    @Override
    public int getItemCount() {
        return mScheduleListPresenter.getScheduleItemCount();
    }

    @Override
    public void renderScheduleList() {
        Log.v(LOG_TAG, "renderScheduleList()");
        mScheduleAdapter.notifyDataSetChanged();
    }

    private void initialize() {
        QueryUseCase useCase = new GetScheduleList(GetScheduleList.SORT_BY_START_DATE_ASC);
        ScheduleModelDataMapper mapper = new ScheduleModelDataMapper();
        mScheduleListPresenter = new ScheduleListPresenterImpl(useCase, mapper);
        mScheduleListPresenter.setView(this);

        setupRecyclerView();

        loadScheduleListData();
    }

    private void loadScheduleListData() {
        mScheduleListPresenter.initialize();
    }

    private void setupRecyclerView() {
        mScheduleAdapter = new ScheduleAdapter(this);
        mScheduleAdapter.setOnItemClickListener(new ScheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ScheduleAdapter.ViewHolder vh) {
                long id = mScheduleListPresenter.getScheduleAtPosition(position).getId();
                Log.v(LOG_TAG, "onItemClick(): position = " + position + ", id = " + id);

                mCallbacks.onItemSelected(id, vh);
            }
        });
        mRvScheduleList.setAdapter(mScheduleAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvScheduleList.setLayoutManager(layoutManager);

        mRvScheduleList.setHasFixedSize(true);
    }


}
