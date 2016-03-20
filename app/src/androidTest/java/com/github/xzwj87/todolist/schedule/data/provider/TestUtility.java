package com.github.xzwj87.todolist.schedule.data.provider;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import android.util.Log;

import com.github.xzwj87.todolist.utility.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtility extends AndroidTestCase {
    private static final String LOG_TAG = TestUtility.class.getSimpleName();

    static ContentValues createScheduleValues() {
        ContentValues scheduleValues = new ContentValues();

        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_TITLE, "Watch movie");
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_NOTE, "Watch movie at five");
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_TYPE, "Movie");
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DATE_START, 1700);
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DATE_END, 1900);
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_SCHEDULE, 1);
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TYPE, "custom");
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME, 1640);
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES, 1);
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL, 10);
        scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE, 0);

        return scheduleValues;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor record, ContentValues expected) {
        Set<Map.Entry<String, Object>> valueSet = expected.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = record.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();

            Log.v(LOG_TAG, "validateCurrentRecord(): columnName = " + columnName + ", record = " +
                    record.getString(idx)  + ", expected = " + expectedValue);

            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, record.getString(idx));
        }
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

}

