package com.github.xzwj87.todolist.schedule.ui.activity;

import android.content.ContentValues;
import android.net.Uri;
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
import com.github.xzwj87.todolist.schedule.provider.ScheduleContract;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddScheduleActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
    private static final String LOG_TAG = AddScheduleActivity.class.getSimpleName();

    private static final String TAG_START_DATE_PICK_DLG = "start_date_pick_dlg";
    private static final String TAG_END_DATE_PICK_DLG = "end_date_pick_dlg";
    private static final String TAG_START_TIME_PICK_DLG = "start_date_pick_dlg";
    private static final String TAG_END_TIME_PICK_DLG = "end_date_pick_dlg";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM d, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");

    private Calendar mScheduleStart;
    private Calendar mScheduleEnd;

    @Bind(R.id.edit_schedule_title) EditText mEditScheduleTitle;
    @Bind(R.id.btn_schedule_date_start) Button mBtnScheduleDateStart;
    @Bind(R.id.btn_schedule_time_start) Button mBtnScheduleTimeStart;
    @Bind(R.id.btn_schedule_date_end) Button mBtnScheduleDateEnd;
    @Bind(R.id.btn_schedule_time_end) Button mBtnScheduleTimeEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24dp);

        initViews();
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
                ContentValues schedule = getSchedule();
                Uri scheduleInsertUri = this.getContentResolver()
                        .insert(ScheduleContract.ScheduleEntry.CONTENT_URI, schedule);
                Log.v(LOG_TAG, "onOptionsItemSelected(): Saved to " + scheduleInsertUri);
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
        Calendar calendar;
        Button button;
        switch (view.getTag()) {
            case TAG_START_DATE_PICK_DLG: {
                calendar = mScheduleStart;
                button = mBtnScheduleDateStart;
                break;
            }
            case TAG_END_DATE_PICK_DLG: {
                calendar = mScheduleEnd;
                button = mBtnScheduleDateEnd;
                break;
            }
            default: {
                calendar = mScheduleStart;
                button = mBtnScheduleDateStart;
                break;
            }

        }

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        button.setText(DATE_FORMAT.format(calendar.getTime()));
    }

    @OnClick(R.id.btn_schedule_date_start)
    public void pickStartDate(View view) {
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                mScheduleStart.get(Calendar.YEAR),
                mScheduleStart.get(Calendar.MONTH),
                mScheduleStart.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show(getFragmentManager(), TAG_START_DATE_PICK_DLG);
    }

    @OnClick(R.id.btn_schedule_date_end)
    public void pickEndDate(View view) {
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                mScheduleEnd.get(Calendar.YEAR),
                mScheduleEnd.get(Calendar.MONTH),
                mScheduleEnd.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show(getFragmentManager(), TAG_END_DATE_PICK_DLG);
    }

    @OnClick(R.id.btn_schedule_time_start)
    public void pickStartTime(View view) {
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute,
                                          int second) {
                        mScheduleStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mScheduleStart.set(Calendar.MINUTE, minute);
                        mScheduleStart.set(Calendar.SECOND, second);
                        mBtnScheduleTimeStart.setText(TIME_FORMAT.format(mScheduleStart.getTime()));
                    }
                },
                mScheduleStart.get(Calendar.HOUR_OF_DAY),
                mScheduleStart.get(Calendar.MINUTE),
                true
        );

        tpd.show(getFragmentManager(), TAG_START_TIME_PICK_DLG);
    }

    @OnClick(R.id.btn_schedule_time_end)
    public void pickEndTime(View view) {
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute,
                                          int second) {
                        mScheduleEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mScheduleEnd.set(Calendar.MINUTE, minute);
                        mScheduleEnd.set(Calendar.SECOND, second);
                        mBtnScheduleTimeEnd.setText(TIME_FORMAT.format(mScheduleEnd.getTime()));
                    }
                },
                mScheduleEnd.get(Calendar.HOUR_OF_DAY),
                mScheduleEnd.get(Calendar.MINUTE),
                true
        );

        tpd.show(getFragmentManager(), TAG_END_TIME_PICK_DLG);
    }

    private void initCalendars() {
        mScheduleStart = Calendar.getInstance();
        int minute = mScheduleStart.get(Calendar.MINUTE);
        mScheduleStart.set(Calendar.MINUTE, minute + 10);

        mScheduleEnd = Calendar.getInstance();
        int hourOfDay = mScheduleEnd.get(Calendar.HOUR_OF_DAY);
        mScheduleEnd.set(Calendar.HOUR_OF_DAY, hourOfDay + 1);
        mScheduleEnd.set(Calendar.MINUTE, minute + 10);

        Log.v(LOG_TAG, "initCalendars(): mScheduleStart = " + mScheduleStart.getTime() +
                ", mScheduleEnd = " + mScheduleEnd.getTime());

    }

    private void initViews() {
        initCalendars();

        String startDate = DATE_FORMAT.format(mScheduleStart.getTime());
        String startTime = TIME_FORMAT.format(mScheduleStart.getTime());
        String endDate = DATE_FORMAT.format(mScheduleEnd.getTime());
        String endTime = TIME_FORMAT.format(mScheduleEnd.getTime());

        Log.v(LOG_TAG, "initViews(): startDate = " + startDate + ", startTime = " + startTime +
                ", endDate = " + endDate + ", endTime = " + endTime);

        mBtnScheduleDateStart.setText(startDate);
        mBtnScheduleTimeStart.setText(startTime);
        mBtnScheduleDateEnd.setText(endDate);
        mBtnScheduleTimeEnd.setText(endTime);
    }

    private ContentValues getSchedule() {
        ContentValues scheduleValues = new ContentValues();
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_TITLE, mEditScheduleTitle.getText().toString());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DETAIL, "None");
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_TYPE, "None");
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DATE_START, mScheduleStart.getTimeInMillis());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DATE_END, mScheduleEnd.getTimeInMillis());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_SCHEDULE, 1);
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME, mScheduleStart.getTimeInMillis());
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES, 1);
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL, 10);

        return scheduleValues;
    }

}
