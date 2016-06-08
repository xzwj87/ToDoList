package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.interactor.query.GetAllScheduleArg;
import com.github.xzwj87.todolist.schedule.interactor.query.GetScheduleListByTypeArg;
import com.github.xzwj87.todolist.schedule.interactor.query.SearchScheduleArg;
import com.github.xzwj87.todolist.schedule.internal.di.component.ScheduleComponent;
import com.github.xzwj87.todolist.schedule.observer.ScheduleDataObserver;
import com.github.xzwj87.todolist.schedule.presenter.ScheduleListPresenterImpl;
import com.github.xzwj87.todolist.schedule.ui.ScheduleListView;
import com.github.xzwj87.todolist.schedule.ui.activity.AddScheduleActivity;
import com.github.xzwj87.todolist.schedule.ui.adapter.ScheduleAdapter;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.share.ScheduleShareActivity;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScheduleListFragment extends BaseFragment implements
        ScheduleAdapter.DataSource,ScheduleDataObserver.DataSetChanged,
        ScheduleListView {
    public static final String LOG_TAG = ScheduleListFragment.class.getSimpleName();
    public static final String SCHEDULE_TYPE_DONE = "done";

    private static final String SCHEDULE_TYPE = "schedule_type";
    private static final String QUERY = "query";

    private String mScheduleType;
    private String mObserverType = "ListFragment";
    private Callbacks mCallbacks = sDummyCallbacks;
    private ScheduleAdapter mScheduleAdapter;
    private ScheduleDataObserver mScheduleObserver = null;

    ScheduleListPresenterImpl mScheduleListPresenter;

    private boolean mIsSearchMode = false;
    private String mQuery;
    private boolean mSwipeMarkAsDone = true;
    private int mLastRemovedPosition = -1;
    private boolean mMarkedDone = true;

    @Inject @Named("markScheduleAsDone") UseCase mMarkScheduleAsDone;
    @Inject @Named("getAllSchedule") UseCase mGetAllSchedule;
    @Inject @Named("getScheduleListByType") UseCase mGetScheduleListByType;
    @Inject @Named("searchSchedule") UseCase mSearchSchedule;
    @Inject @Named("deleteSchedule") UseCase mDeleteSchedule;
    @Inject ScheduleModelDataMapper mMapper;

    @Bind(R.id.rv_schedule_list) RecyclerView mRvScheduleList;

    @Override
    public void onDataSetChanged() {
        Log.v(LOG_TAG, "onDataSetChanged()");
        mCallbacks.onDataChanged(mScheduleObserver.getScheduleCategoryNumber());
        // refresh the list
        loadScheduleListData();
    }

    public interface Callbacks {
        void onItemSelected(long id, ScheduleAdapter.ViewHolder vh);
        void onDataChanged(ScheduleDataObserver.ScheduleCategoryNumber sn);
    }

    private static Callbacks sDummyCallbacks = null;

    public ScheduleListFragment() {
    }

    public static ScheduleListFragment newInstanceByType(String scheduleType) {
        ScheduleListFragment fragment = new ScheduleListFragment();

        Bundle args = new Bundle();
        args.putString(SCHEDULE_TYPE, scheduleType);
        fragment.setArguments(args);

        return fragment;
    }

    public static ScheduleListFragment newInstanceByQuery(String query) {
        ScheduleListFragment fragment = new ScheduleListFragment();

        Bundle args = new Bundle();
        args.putString(QUERY, query);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_list, container, false);
        ButterKnife.bind(this, rootView);

        mSwipeMarkAsDone = true;
        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.containsKey(SCHEDULE_TYPE)) {
                mScheduleType = arguments.getString(SCHEDULE_TYPE);
                Log.v(LOG_TAG, "onCreateView(): mScheduleType = " + mScheduleType);
                if (mScheduleType != null && mScheduleType.equals(SCHEDULE_TYPE_DONE)) {
                    mSwipeMarkAsDone = false;
                }
                Log.v(LOG_TAG, "onCreateView(): mScheduleType = " + mScheduleType +
                        ", mSwipeMarkAsDone = " + mSwipeMarkAsDone);
            } else  if (arguments.containsKey(QUERY)) {
                mIsSearchMode = true;
                mQuery = arguments.getString(QUERY);
                Log.v(LOG_TAG, "onCreateView(): mQuery = " + mQuery);
            }

        }

        mObserverType = mObserverType + "_" + mScheduleType;
        registerDataObserver();

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
        Log.v(LOG_TAG, "onAttach()");
        try {
            mCallbacks = (Callbacks) context;
        }catch (IllegalStateException e){
            Log.e(LOG_TAG,"Activity must implement fragment's callbacks.");
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(LOG_TAG, "onDetach(): " + this.getTag());
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume(): " + this.getTag());
        mScheduleListPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause()");
        mScheduleListPresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy()");
        unregisterDataObserver();
        mScheduleListPresenter.destroy();
    }

    @Override
    public ScheduleModel getItemAtPosition(int position) {
        try {
            ScheduleModel model = mScheduleListPresenter.getScheduleAtPosition(position);
            return model;
        }catch (RuntimeException e){
            Log.v(LOG_TAG,"getItemAtPosition(): fail to get the item");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return mScheduleListPresenter.getScheduleItemCount();
    }

    @Override
    public void renderScheduleList() {
        Log.v(LOG_TAG, "renderScheduleList(): mLastRemovedPosition = " + mLastRemovedPosition);
        if (mLastRemovedPosition != -1) {
            mScheduleAdapter.notifyItemRemoved(mLastRemovedPosition);
            mLastRemovedPosition = -1;
        } else {
            mScheduleAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void requestConfirmDelete(long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete_schedule_confirm_message)
               .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               })
               .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       mScheduleListPresenter.onDeleteSchedule(id,true);
                       dialog.dismiss();
                   }
               })
                .show();
    }

    @SuppressWarnings("unchecked")
    private void initialize() {
        getComponent(ScheduleComponent.class).inject(this);

        UseCase getListUseCase;
        if (mIsSearchMode) {
            getListUseCase = mSearchSchedule.init(new SearchScheduleArg(mQuery));
        } else {
            if (mScheduleType != null) {
                if (mScheduleType.equals(SCHEDULE_TYPE_DONE)) {
                    getListUseCase = mGetAllSchedule.init(
                            new GetAllScheduleArg(ScheduleModel.DONE));
                } else {
                    getListUseCase = mGetScheduleListByType.init(
                            new GetScheduleListByTypeArg(mScheduleType, ScheduleModel.UNDONE));
                }
            } else {
                getListUseCase = mGetAllSchedule.init(new GetAllScheduleArg(ScheduleModel.UNDONE));
            }
        }

        mScheduleListPresenter = new ScheduleListPresenterImpl(
                getListUseCase, mMarkScheduleAsDone,mDeleteSchedule, mMapper);
        mScheduleListPresenter.setView(this);

        setupRecyclerView();

        mCallbacks.onDataChanged(mScheduleObserver.getScheduleCategoryNumber());

        loadScheduleListData();
    }

    private void loadScheduleListData() {
        mScheduleListPresenter.initialize();
    }

    private void registerDataObserver(){
        Log.v(LOG_TAG,"registerDataObserver()");
        // schedule data observer
        mScheduleObserver = ScheduleDataObserver.getInstance(mObserverType);
        mScheduleObserver.registerObserver(mObserverType);
        mScheduleObserver.registerDataChangedCb(this);
    }

    private void unregisterDataObserver(){
        mScheduleObserver.unregisterDataChangedCb(this);
        mScheduleObserver.unregisterObserver(mObserverType);
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

            @Override
            public void onItemLongClick(int position, ScheduleAdapter.ViewHolder vh) {
                final long id = mScheduleListPresenter.getScheduleAtPosition(position).getId();
                Log.v(LOG_TAG, "onItemLongClick(): position = " + position + ", id = " + id);

                createDialog(id,position);
            }
        });
        mRvScheduleList.setAdapter(mScheduleAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvScheduleList.setLayoutManager(layoutManager);

        mRvScheduleList.setItemAnimator(new DefaultItemAnimator());
        mRvScheduleList.setHasFixedSize(true);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public boolean isItemViewSwipeEnabled() {
                        return !mIsSearchMode;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Log.v(LOG_TAG, "onSwiped(): position = " + position +
                                 ", direction = " + direction);
                        long id = mScheduleAdapter.getItemId(position);
                        mScheduleListPresenter.markAsDone(new long[]{id}, mSwipeMarkAsDone);
                        mLastRemovedPosition = position;
                        showSnackBarNotification(id, mSwipeMarkAsDone);
                    }
                });
        itemTouchHelper.attachToRecyclerView(mRvScheduleList);
    }

    private void showSnackBarNotification(long id, boolean undoMarkAsDone) {
        String message = undoMarkAsDone ?
                getString(R.string.marked_done) : getString(R.string.marked_undone);
        Snackbar snackbar = Snackbar.make(mRvScheduleList, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), v -> {
            mScheduleListPresenter.markAsDone(new long[]{id}, !undoMarkAsDone);
        });
        snackbar.show();
    }

    private void createDialog(long id,int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        int choiceListId = R.array.dialog_choice_list;
        if(SCHEDULE_TYPE_DONE.equals(mScheduleType)){
            choiceListId = R.array.schedule_done_dialog_choice_list;
            mMarkedDone = false;
        }
        builder.setItems(choiceListId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(LOG_TAG,"onClick(): " + which);
                switch(which){
                    // share
                    case 0:
                        Intent intent = new Intent(getContext(), ScheduleShareActivity.class);
                        ScheduleModel scheduleModel = mScheduleListPresenter.getScheduleAtPosition(position);
                        intent.putExtra(ScheduleContract.ScheduleEntry.COLUMN_TITLE,
                                scheduleModel.getTitle());
                        intent.putExtra(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME,
                                scheduleModel.getAlarmTime().getTime());
                        boolean scheduleDoneStatus = false;
                        if(scheduleModel.getDoneStatus().equals(ScheduleModel.DONE)){
                            scheduleDoneStatus = true;
                        }
                        intent.putExtra(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE,scheduleDoneStatus);
                        startActivity(intent);
                        break;
                    // edit
                    case 1:
                        Intent editIntent = new Intent(getContext(), AddScheduleActivity.class);
                        editIntent.putExtra(AddScheduleActivity.SCHEDULE_ID,id);
                        editIntent.putExtra(AddScheduleActivity.PARENT_TAG,LOG_TAG);
                        startActivity(editIntent);
                        break;
                    // delete
                    case 2:
                        mScheduleListPresenter.onDeleteSchedule(id,false);
                        break;
                    // marked as done
                    case 3:
                        mScheduleListPresenter.markAsDone(new long[]{id},mMarkedDone);
                        break;
                    default:
                        //dialog.dismiss();
                        break;
                }
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();
    }
}
