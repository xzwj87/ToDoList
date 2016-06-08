package com.github.xzwj87.todolist.schedule.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.internal.di.HasComponent;
import com.github.xzwj87.todolist.schedule.internal.di.component.DaggerScheduleComponent;
import com.github.xzwj87.todolist.schedule.internal.di.component.ScheduleComponent;
import com.github.xzwj87.todolist.schedule.internal.di.module.ScheduleModule;
import com.github.xzwj87.todolist.schedule.presenter.AddSchedulePresenter;
import com.github.xzwj87.todolist.schedule.presenter.AddSchedulePresenterImpl;
import com.github.xzwj87.todolist.schedule.presenter.EditSchedulePresenterImpl;
import com.github.xzwj87.todolist.schedule.ui.AddScheduleView;
import com.github.xzwj87.todolist.schedule.ui.fragment.AlarmTypePickerDialogFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleGridFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleListFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.SchedulePriorityPickerDialogFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleTypePickerDialogFragment;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.github.xzwj87.todolist.schedule.utility.ScheduleUtility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddScheduleActivity extends BaseActivity
        implements AddScheduleView, DatePickerDialog.OnDateSetListener,
        HasComponent<ScheduleComponent> {
    private static final String LOG_TAG = AddScheduleActivity.class.getSimpleName();

    public static final String SCHEDULE_ID = "id";
    public static final String PARENT_TAG = "parent_tag";

    private static final String START_DATE_PICK_DLG_TAG = "start_date_pick_dlg";
    private static final String END_DATE_PICK_DLG_TAG = "end_date_pick_dlg";
    private static final String START_TIME_PICK_DLG_TAG = "start_date_pick_dlg";
    private static final String END_TIME_PICK_DLG_TAG = "end_date_pick_dlg";
    private static final String ALARM_TYPE_PICK_DLG_TAG = "alarm_time_pick_dlg";
    private static final String SCHEDULE_TYPE_PICK_DLG_TAG = "schedule_type_pick_dlg";
    private static final String SCHEDULE_PRIORITY_PICK_DLG_TAG = "schedule_priority_pick_dlg";

    private AddSchedulePresenter mPresenter;
    @Inject AddSchedulePresenterImpl mAddSchedulePresenterImpl;
    @Inject EditSchedulePresenterImpl mEditSchedulePresenterImpl;

    private boolean mIsEditMode = false;
    private long mScheduleId;
    private String mParentTag;
    private ScheduleComponent mScheduleComponent;

    @Bind(R.id.edit_schedule_title) EditText mEditScheduleTitle;
    @Bind(R.id.btn_schedule_date_start) Button mBtnScheduleDateStart;
    @Bind(R.id.btn_schedule_time_start) Button mBtnScheduleTimeStart;
    @Bind(R.id.btn_schedule_date_end) Button mBtnScheduleDateEnd;
    @Bind(R.id.btn_schedule_time_end) Button mBtnScheduleTimeEnd;
    @Bind(R.id.btn_alarm_time) Button mBtnAlarmTime;
    @Bind(R.id.btn_schedule_type) Button mBtnScheduleType;
    @Bind(R.id.edit_schedule_note) EditText mEditScheduleNote;
    @Bind(R.id.btn_priority) Button mBtnPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24dp);

        if (savedInstanceState == null) {
            mScheduleId = getIntent().getLongExtra(SCHEDULE_ID, -1);
            mParentTag = getIntent().getStringExtra(PARENT_TAG);
            Log.v(LOG_TAG, "onCreate(): mScheduleId = " + mScheduleId);
            if (mScheduleId != -1) {
                mIsEditMode = true;
            } else {
                mIsEditMode = false;
            }
        }

        initializeInjector();
        initializeView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    private void initializeInjector() {
        mScheduleComponent = DaggerScheduleComponent.builder()
                .appComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .scheduleModule(new ScheduleModule(mScheduleId))
                .build();
        mScheduleComponent.inject(this);
    }

    private void initializeView() {

        if (mIsEditMode) {
            mPresenter = mEditSchedulePresenterImpl;
        } else {
            mPresenter = mAddSchedulePresenterImpl;
        }
        mPresenter.setView(this);
        mPresenter.initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_schedule, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                mPresenter.onTitleSet(mEditScheduleTitle.getText().toString());
                mPresenter.onNoteSet(mEditScheduleNote.getText().toString());
                mPresenter.onSave();
                // finish();
                return true;
            case android.R.id.home:
                Intent intent = null;
                Log.v(LOG_TAG,"parent tag = " + mParentTag);
                intent = new Intent(this,ScheduleListActivity.class);
                NavUtils.navigateUpTo(this, intent);
                /*if(mParentTag.equals(ScheduleListFragment.LOG_TAG)){
                    intent = new Intent(this,ScheduleListActivity.class);
                    NavUtils.navigateUpTo(this, intent);
                    return true;
                }else if(mParentTag.equals(ScheduleGridFragment.LOG_TAG)){
                    Log.v(LOG_TAG,"parent tag = " + mParentTag);
                    intent = new Intent(this,ScheduleGridActivity.class);
                    // when this is empty item,here navigateUpTo fail to work
                    NavUtils.navigateUpTo(this, intent);
                    //startActivity(intent);
                    return true;
                }*/
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public ScheduleComponent getComponent() {
        return mScheduleComponent;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        // NOTE: monthOfYear starts from 0
        Log.v(LOG_TAG, "onDateSet(): tag =" + view.getTag() + ", year = " + year +
                ", monthOfYear = " + monthOfYear + ", dayOfMonth = " + dayOfMonth);

        switch (view.getTag()) {
            case START_DATE_PICK_DLG_TAG:
                mPresenter.onStartDateSet(year, monthOfYear, dayOfMonth);
                break;
            case END_DATE_PICK_DLG_TAG:
                mPresenter.onEndDateSet(year, monthOfYear, dayOfMonth);
                break;
        }
    }

    @Override
    public void showPickStartDateDlg(int year, int monthOfYear, int dayOfMonth) {
        DatePickerDialog dpd = DatePickerDialog.newInstance(this, year, monthOfYear, dayOfMonth);
        dpd.show(getFragmentManager(), START_DATE_PICK_DLG_TAG);
    }

    @Override
    public void showPickEndDateDlg(int year, int monthOfYear, int dayOfMonth) {
        DatePickerDialog dpd = DatePickerDialog.newInstance(this, year, monthOfYear, dayOfMonth);
        dpd.show(getFragmentManager(), END_DATE_PICK_DLG_TAG);
    }

    @Override
    public void showPickStartTimeDlg(int hourOfDay, int minute, int second) {
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute,
                                          int second) {
                        mPresenter.onStartTimeSet(hourOfDay, minute, second);
                    }
                },
                hourOfDay,
                minute,
                true
        );

        tpd.show(getFragmentManager(), START_TIME_PICK_DLG_TAG);
    }

    @Override
    public void showPickEndTimeDlg(int hourOfDay, int minute, int second) {
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute,
                                          int second) {
                        mPresenter.onEndTimeSet(hourOfDay, minute, second);
                    }
                },
                hourOfDay,
                minute,
                true
        );

        tpd.show(getFragmentManager(), END_TIME_PICK_DLG_TAG);
    }

    @Override
    public void showPickAlarmTypeDlg(@ScheduleModel.AlarmType String alarmType) {
        AlarmTypePickerDialogFragment fragment = AlarmTypePickerDialogFragment.newInstance(
                new AlarmTypePickerDialogFragment.OnPickAlertTypeListener() {
                    @Override
                    public void onAlertTimePicked(@ScheduleModel.AlarmType String alarmType) {
                        mPresenter.onAlarmTypeSet(alarmType);
                    }
                }, alarmType);
        fragment.show(getSupportFragmentManager(), SCHEDULE_TYPE_PICK_DLG_TAG);
    }

    @Override
    public void showPickScheduleTypeDlg(@ScheduleModel.ScheduleType String scheduleType) {
        ScheduleTypePickerDialogFragment fragment = ScheduleTypePickerDialogFragment.newInstance(
                new ScheduleTypePickerDialogFragment.OnScheduleTypeSetListener() {
                    @Override
                    public void onScheduleTypeSet(@ScheduleModel.ScheduleType String scheduleType) {
                        mPresenter.onScheduleTypeSet(scheduleType);
                    }
                }, scheduleType);
        fragment.show(getSupportFragmentManager(), SCHEDULE_TYPE_PICK_DLG_TAG);
    }

    @Override
    public void showMessageDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {})
                .show();
    }

    @Override
    public void showErrorIndicationOnStartTime(boolean isError) {
        int color;
        if (isError) {
            color = ContextCompat.getColor(this, R.color.colorTextErrorIndication);
        } else {
            color = ContextCompat.getColor(this, R.color.colorText);
        }
        mBtnScheduleDateStart.setTextColor(color);
        mBtnScheduleTimeStart.setTextColor(color);
    }

    @Override
    public void ShowPickPriorityDlg(int priority) {
        SchedulePriorityPickerDialogFragment fragment = SchedulePriorityPickerDialogFragment.newInstance(
                priority, new SchedulePriorityPickerDialogFragment.onPriorityChangedListener() {
                    @Override
                    public void onPriorityChanged(int priority) {
                        mPresenter.onPrioritySet(priority);
                    }
                });
        fragment.show(getSupportFragmentManager(), SCHEDULE_PRIORITY_PICK_DLG_TAG);
    }

    @Override
    public void finishView() {
        finish();
    }

    @Override
    public void updateStartDateDisplay(String startDate) {
        mBtnScheduleDateStart.setText(startDate);
    }

    @Override
    public void updateEndDateDisplay(String endDate) {
        mBtnScheduleDateEnd.setText(endDate);
    }

    @Override
    public void updateStartTimeDisplay(String startTime) {
        mBtnScheduleTimeStart.setText(startTime);
    }

    @Override
    public void updateAlarmTypeDisplay(String alarmTypeText) {
        mBtnAlarmTime.setText(alarmTypeText);
    }

    @Override
    public void updateScheduleTypeDisplay(String scheduleTypeText) {
        mBtnScheduleType.setText(scheduleTypeText);
    }

    @Override
    public void updateSchedulePriority(int priority) {
        mBtnPriority.setText(ScheduleUtility.getSchedulePriorityText(priority));
    }

    @Override
    public void updateEndTimeDisplay(String endTime) {
        mBtnScheduleTimeEnd.setText(endTime);
    }

    @Override
    public void updateScheduleTitle(String title) {
        mEditScheduleTitle.setText(title);
    }

    @Override
    public void updateScheduleNote(String note) {
        mEditScheduleNote.setText(note);
    }

    @Override
    public String getScheduleTitle() {
        return mEditScheduleTitle.getText().toString();
    }

    @OnClick(R.id.btn_schedule_date_start)
    public void pickStartDate(View view) {
        mPresenter.setStartDate();
    }

    @OnClick(R.id.btn_schedule_date_end)
    public void pickEndDate(View view) {
        mPresenter.setEndDate();
    }

    @OnClick(R.id.btn_schedule_time_start)
    public void pickStartTime(View view) {
        mPresenter.setStartTime();
    }

    @OnClick(R.id.btn_schedule_time_end)
    public void pickEndTime(View view) {
        mPresenter.setEndTime();
    }

    @OnClick(R.id.btn_alarm_time)
    public void pickAlarmType(View view) {
        mPresenter.setAlarmType();
    }

    @OnClick(R.id.btn_schedule_type)
    public void pickScheduleType(View view) {
        mPresenter.setScheduleType();
    }

    @OnClick(R.id.btn_priority)
    public void pickSchedulePriority(){
        mPresenter.setPriority();
    }

}
