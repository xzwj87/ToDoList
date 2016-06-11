package com.github.xzwj87.todolist.schedule.observer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract;
import com.github.xzwj87.todolist.schedule.interactor.DefaultSubscriber;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.query.GetAllSchedule;
import com.github.xzwj87.todolist.schedule.interactor.query.GetAllScheduleArg;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleListFragment;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import rx.Observable;

/**
 * Created by JasonWang on 2016/5/15.
 */

public class ScheduleDataObserver extends ContentObserver {
    public static final String TAG = "ScheduleDataObserver";

    private static HashSet<DataSetChanged> mCallbacks = new HashSet<>();
    private static HashMap<String,ContentObserver> mObservers = new HashMap<>();
    private static ScheduleCategoryNumber mScheduleNumber = null;
    private Context mContext = App.getAppContext();
    private String mObserverType = null;
    private UseCase mGetScheduleList = null;
    private Cursor mCursor = null;

    public static ScheduleDataObserver getInstance(String type){
        return new ScheduleDataObserver(type,new Handler());
    }

    public ScheduleDataObserver(String type,Handler handler){
        super(handler);
        mObserverType = type;
        mContext = App.getAppContext();
        mScheduleNumber = new ScheduleCategoryNumber();
        updateScheduleCategoryNumber();
    }

    @Override
    public void onChange(boolean selfChange){
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange,Uri uri){
        Log.v(TAG, "onChange: uri = " + uri);

        updateScheduleCategoryNumber();

        Iterator iterator = mCallbacks.iterator();
        while(iterator.hasNext()){
            DataSetChanged cb = (DataSetChanged)iterator.next();
            cb.onDataSetChanged();
        }
    }

    public interface DataSetChanged{
        void onDataSetChanged();
    }

    public void registerDataChangedCb(DataSetChanged cb){
        Log.v(TAG,"registerDataChangedCb(): callback = " + cb.getClass().getSimpleName());
        mCallbacks.add(cb);
    }

    public void unregisterDataChangedCb(DataSetChanged cb){
        Log.v(TAG,"unregisterDataChangedCb(): callback = " + cb.getClass().getSimpleName());
        mCallbacks.remove(cb);
    }

    public void registerObserver(String type){
        if(!mObservers.containsKey(type)) {
            ScheduleDataObserver observer = getInstance(type);
            mObservers.put(type,observer);
            mContext.getContentResolver().registerContentObserver(ScheduleContract.ScheduleEntry.CONTENT_URI,
                    true,observer);
        }
        Log.v(TAG,"registerObserver(): type = " + type + ",size = " + mObservers.size());
    }

    public void unregisterObserver(String type){
        if(mObservers.containsKey(type)){
            ScheduleDataObserver observer = (ScheduleDataObserver)mObservers.get(type);
            mContext.getContentResolver().unregisterContentObserver(observer);
            mObservers.remove(type);
        }
        Log.v(TAG,"unregisterObserver(): type = " + type + ",size = " + mObservers.size());
    }

    public class ScheduleCategoryNumber{
        private long mUndoneTotal;
        private long mDoneTotal;

        public void setUndoneTotal(long undone){
            mUndoneTotal = undone;
        }

        public void setDoneTotal(long done){
            mDoneTotal = done;
        }

        public long getUndoneTotal(){
            return mUndoneTotal;
        }

        public long getDoneTotal(){
            return mDoneTotal;
        }
    }

    public ScheduleCategoryNumber getScheduleCategoryNumber(){
        return mScheduleNumber;
    }

    private void updateScheduleCategoryNumber(){
        String selection = ScheduleContract.ScheduleEntry.COLUMN_IS_DONE + " = ?";
        String args[] = {ScheduleModel.UNDONE};

        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(ScheduleContract.ScheduleEntry.CONTENT_URI,
                null,selection,args,null);

        mScheduleNumber.setUndoneTotal(cursor.getCount());

        selection = ScheduleContract.ScheduleEntry.COLUMN_IS_DONE + " = ?";
        args = new String[]{ScheduleModel.DONE};
        cursor = resolver.query(ScheduleContract.ScheduleEntry.CONTENT_URI,
                null, selection, args, null);
        mScheduleNumber.setDoneTotal(cursor.getCount());
        cursor.close();

        Log.v(TAG, "updateScheduleCategoryNumber():done = " + mScheduleNumber.getDoneTotal() +
                    ",undone = " + mScheduleNumber.getUndoneTotal());

    }
}