package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.interactor.query.GetAllScheduleArg;
import com.github.xzwj87.todolist.schedule.internal.di.component.ScheduleComponent;
import com.github.xzwj87.todolist.schedule.observer.ScheduleDataObserver;
import com.github.xzwj87.todolist.schedule.presenter.ScheduleGridPresenterImpl;
import com.github.xzwj87.todolist.schedule.ui.ScheduleGridView;
import com.github.xzwj87.todolist.schedule.ui.activity.AddScheduleActivity;
import com.github.xzwj87.todolist.schedule.ui.adapter.ScheduleGridAdapter;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.share.ScheduleShareActivity;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JasonWang on 2016/5/14.
 */
public class ScheduleGridFragment extends BaseFragment implements
        ScheduleGridAdapter.DataSource,ScheduleDataObserver.DataSetChanged,
        ScheduleGridView{
    public static final String LOG_TAG = "ScheduleGridFragment";
    public static final String SCHEDULE_TYPE_DONE = "done";

    private static final String SCHEDULE_TYPE = "schedule_type";

    private String mScheduleType = null;
    private String mObserverType = "GridFragment";
    private ScheduleGridPresenterImpl mScheduleGridPresenter;
    private ScheduleGridAdapter mScheduleGridAdapter;
    private GridCallBacks mCallBacks;
    private ScheduleDataObserver mScheduleObserver;
    private boolean mMarkedDone = true;

    @Inject @Named("markScheduleAsDone") UseCase mMarkedAsDone;
    @Inject @Named("getAllSchedule") UseCase mGetAllSchedule;
    @Inject @Named("getScheduleListByType") UseCase mGetScheduleListByType;
    @Inject @Named("deleteSchedule") UseCase mDeleteSchedule;
    @Inject ScheduleModelDataMapper mDataMapper;

    @Bind(R.id.schedule_grid_view) GridView mScheduleGridView;

    public interface GridCallBacks{
        void onGridItemSelected(long id, ScheduleGridAdapter.GridViewHolder vh);
    }

    public ScheduleGridFragment(){
    }

    public static ScheduleGridFragment getInstanceByType(String scheduleType){
        ScheduleGridFragment gridFragment = new ScheduleGridFragment();

        Bundle args = new Bundle();
        args.putString(SCHEDULE_TYPE, scheduleType);
        gridFragment.setArguments(args);

        return gridFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Log.v(LOG_TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_schedule_grid,container,false);
        ButterKnife.bind(this, rootView);

        Bundle args = getArguments();
        if(args != null){
            if(args.containsKey(SCHEDULE_TYPE)){
                mScheduleType = args.getString(SCHEDULE_TYPE);
                Log.v(LOG_TAG,"onCreateView(): scheduleType = " + mScheduleType);
            }
        }

        mObserverType = mObserverType + "_" + mScheduleType;
        registerDataObserver();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.v(LOG_TAG, "onActivityCreated()");

        initialize();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.v(LOG_TAG, "onAttach()");

        if(!(context instanceof GridCallBacks)){
            Log.e(LOG_TAG,"Activity has to implement GridCallbacks");
            throw new IllegalStateException("Grid Callbacks is not implemented");
        }

        mCallBacks = (GridCallBacks)context;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterDataObserver();
        mScheduleGridPresenter.destroy();
    }

    @Override
    public ScheduleModel getItemAtPosition(int position) {
        return mScheduleGridPresenter.getScheduleAtPosition(position);
    }

    @Override
    public int getItemCount() {
        return mScheduleGridPresenter.getScheduleItemCount();
    }

    @Override
    public void renderScheduleList() {
        mScheduleGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void requestConfirmDelete(long id,boolean isConfirmed) {
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
                       mScheduleGridPresenter.onDeleteSchedule(id,true);
                       dialog.dismiss();
                   }
               }).show();
    }

    @Override
    public void onDataSetChanged() {
        Log.v(LOG_TAG, "onDataSetChanged()");
        loadScheduleData();
    }

    private void initialize(){
        getComponent(ScheduleComponent.class).inject(this);

        UseCase getScheduleListCase;
        if(mScheduleType != null){
            if(mScheduleType.equals(SCHEDULE_TYPE_DONE)){
                getScheduleListCase = mGetAllSchedule.init(
                        new GetAllScheduleArg(ScheduleModel.DONE));
            }else{
                getScheduleListCase = mGetAllSchedule.init(
                        new GetAllScheduleArg(mScheduleType,ScheduleModel.UNDONE));
            }
        }else{
            getScheduleListCase = mGetAllSchedule.init(
                    new GetAllScheduleArg(ScheduleModel.UNDONE));
        }

        mScheduleGridPresenter = new ScheduleGridPresenterImpl(getScheduleListCase,
                mMarkedAsDone,mDeleteSchedule,mDataMapper);
        mScheduleGridPresenter.setView(this);

        initGridView();

        loadScheduleData();
    }

    private void registerDataObserver(){
        // data set observer
        mScheduleObserver = ScheduleDataObserver.getInstance(mObserverType);
        mScheduleObserver.registerObserver(mObserverType);
        mScheduleObserver.registerDataChangedCb(this);
    }

    private void unregisterDataObserver(){
        mScheduleObserver.unregisterDataChangedCb(this);
        mScheduleObserver.unregisterObserver(mObserverType);
    }

    private void loadScheduleData(){
        Log.v(LOG_TAG, "loadScheduleData()");
        mScheduleGridPresenter.initialize();
        mScheduleGridView.invalidateViews();
        mScheduleGridAdapter.notifyDataSetChanged();
    }

    private void initGridView(){
        mScheduleGridAdapter = new ScheduleGridAdapter(this);
        mScheduleGridAdapter.setOnItemClickListener(new ScheduleGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ScheduleGridAdapter.GridViewHolder vh) {
                long id = getItemAtPosition(position).getId();
                Log.v(LOG_TAG, "onItemClick(): position = " + position + ",id = " + id);
                mCallBacks.onGridItemSelected(id, vh);
            }

            @Override
            public void onItemLongClick(int position, ScheduleGridAdapter.GridViewHolder vh) {
                ScheduleModel scheduleModel = getItemAtPosition(position);
                long id = scheduleModel.getId();
                Log.v(LOG_TAG, "onItemLongClick(): position = " + position + ", id = " + id);
                createDialog(scheduleModel, position);
            }
        });

        mScheduleGridView.invalidateViews();
        mScheduleGridAdapter.notifyDataSetChanged();
        mScheduleGridView.setAdapter(mScheduleGridAdapter);
    }

    private void createDialog(ScheduleModel model,int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        int choiceListId = R.array.dialog_choice_list;
        if(SCHEDULE_TYPE_DONE.equals(mScheduleType)){
            choiceListId = R.array.schedule_done_dialog_choice_list;
            mMarkedDone = false;
        }

        builder.setItems(choiceListId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // share
                    case 0:
                        Intent shareIntent = new Intent(getContext(), ScheduleShareActivity.class);
                        shareIntent.putExtra(ScheduleContract.ScheduleEntry.COLUMN_TITLE,
                                model.getTitle());
                        shareIntent.putExtra(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME,
                                model.getAlarmTime().getTime());
                        boolean scheduleDoneStatus = false;
                        if(model.getDoneStatus().equals(ScheduleModel.DONE)){
                            scheduleDoneStatus = true;
                        }
                        shareIntent.putExtra(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE, scheduleDoneStatus);
                        startActivity(shareIntent);
                        break;
                    // edit
                    case 1:
                        Intent intent = new Intent(getContext(), AddScheduleActivity.class);
                        intent.putExtra(AddScheduleActivity.SCHEDULE_ID,model.getId());
                        intent.putExtra(AddScheduleActivity.PARENT_TAG,LOG_TAG);
                        startActivity(intent);
                        break;
                    // delete
                    case 2:
                        mScheduleGridPresenter.onDeleteSchedule(model.getId(),false);
                        break;
                    // marked as done
                    case 3:
                        mScheduleGridPresenter.markAsDone(new long[]{model.getId()},mMarkedDone);
                        break;
                    default:
                        //dialog.dismiss();
                        break;
                }
                dialog.dismiss();
            }
        });
        // add animation to dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();
    }
}
