package com.github.xzwj87.todolist.schedule.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.insert.AddSchedule;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleContentValuesDataMapper;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.github.xzwj87.todolist.schedule.interactor.query.GetScheduleById;
import com.github.xzwj87.todolist.schedule.interactor.update.UpdateSchedule;
import com.github.xzwj87.todolist.schedule.presenter.AddSchedulePresenter;
import com.github.xzwj87.todolist.schedule.presenter.AddSchedulePresenterImpl;
import com.github.xzwj87.todolist.schedule.presenter.EditSchedulePresenterImpl;
import com.github.xzwj87.todolist.schedule.ui.AddScheduleView;
import com.github.xzwj87.todolist.schedule.ui.fragment.AlarmTypePickerDialogFragment;
import com.github.xzwj87.todolist.schedule.ui.fragment.ScheduleTypePickerDialogFragment;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddScheduleActivity extends AppCompatActivity
        implements AddScheduleView, DatePickerDialog.OnDateSetListener {
    private static final String LOG_TAG = AddScheduleActivity.class.getSimpleName();

    public static final String SCHEDULE_ID = "id";

    private static final String START_DATE_PICK_DLG_TAG = "start_date_pick_dlg";
    private static final String END_DATE_PICK_DLG_TAG = "end_date_pick_dlg";
    private static final String START_TIME_PICK_DLG_TAG = "start_date_pick_dlg";
    private static final String END_TIME_PICK_DLG_TAG = "end_date_pick_dlg";
    private static final String ALARM_TYPE_PICK_DLG_TAG = "alarm_time_pick_dlg";
    private static final String SCHEDULE_TYPE_PICK_DLG_TAG = "schedule_type_pick_dlg";

    private AddSchedulePresenter mPresenter;
    private boolean mIsEditMode = false;
    private long mScheduleId;

    @Bind(R.id.edit_schedule_title) EditText mEditScheduleTitle;
    @Bind(R.id.btn_schedule_date_start) Button mBtnScheduleDateStart;
    @Bind(R.id.btn_schedule_time_start) Button mBtnScheduleTimeStart;
    @Bind(R.id.btn_schedule_date_end) Button mBtnScheduleDateEnd;
    @Bind(R.id.btn_schedule_time_end) Button mBtnScheduleTimeEnd;
    @Bind(R.id.btn_alarm_time) Button mBtnAlarmTime;
    @Bind(R.id.btn_schedule_type) Button mBtnScheduleType;
    @Bind(R.id.edit_schedule_note) EditText mEditScheduleNote;

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
            Log.v(LOG_TAG, "onCreate(): mScheduleId = " + mScheduleId);
            if (mScheduleId != -1) {
                mIsEditMode = true;
            } else {
                mIsEditMode = false;
            }
        }

        initialize();
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

    private void initialize() {
        if (mIsEditMode) {
            UseCase queryUseCase = new GetScheduleById(mScheduleId);
            UseCase updateUseCase = new UpdateSchedule();
            ScheduleContentValuesDataMapper contentValueMapper = new ScheduleContentValuesDataMapper();
            ScheduleModelDataMapper modelMapper = new ScheduleModelDataMapper();
            mPresenter = new EditSchedulePresenterImpl(updateUseCase, queryUseCase,
                    contentValueMapper, modelMapper);
        } else {
            UseCase addSchedule = new AddSchedule();
            ScheduleContentValuesDataMapper mapper = new ScheduleContentValuesDataMapper();
            mPresenter = new AddSchedulePresenterImpl(addSchedule, mapper);
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
                finish();
                return true;
            default:
                break;
        }

        return false;
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

    @Override
    public Context getViewContext() {
        return this;
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

}
