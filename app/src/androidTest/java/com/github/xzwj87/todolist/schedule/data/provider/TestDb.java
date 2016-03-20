package com.github.xzwj87.todolist.schedule.data.provider;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(ScheduleDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(ScheduleContract.ScheduleEntry.TABLE_NAME);

        mContext.deleteDatabase(ScheduleDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new ScheduleDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: The database has not been created correctly.", cursor.moveToFirst());

        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while(cursor.moveToNext());

        assertTrue("Error: The database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        cursor = db.rawQuery("PRAGMA table_info(" + ScheduleContract.ScheduleEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: Unable to query for table information.", cursor.moveToFirst());

        final HashSet<String> scheduleColumnHashSet = new HashSet<String>();
        scheduleColumnHashSet.add(ScheduleContract.ScheduleEntry._ID);
        scheduleColumnHashSet.add(ScheduleContract.ScheduleEntry.COLUMN_TITLE);
        scheduleColumnHashSet.add(ScheduleContract.ScheduleEntry.COLUMN_NOTE);
        scheduleColumnHashSet.add(ScheduleContract.ScheduleEntry.COLUMN_TYPE);
        scheduleColumnHashSet.add(ScheduleContract.ScheduleEntry.COLUMN_DATE_START);
        scheduleColumnHashSet.add(ScheduleContract.ScheduleEntry.COLUMN_DATE_END);
        scheduleColumnHashSet.add(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_SCHEDULE);
        scheduleColumnHashSet.add(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME);
        scheduleColumnHashSet.add(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES);
        scheduleColumnHashSet.add(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL);


        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            Log.v(LOG_TAG, "testCreateDb(): columnName = " + columnName);
            scheduleColumnHashSet.remove(columnName);
        } while(cursor.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                scheduleColumnHashSet.isEmpty());

        cursor.close();
        db.close();
    }

    public void testScheduleTable() {
        ScheduleDbHelper dbHelper = new ScheduleDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues scheduleValues = TestUtility.createScheduleValues();

        long scheduleRowId = db.insert(
                ScheduleContract.ScheduleEntry.TABLE_NAME, null, scheduleValues);
        assertTrue(scheduleRowId != -1);

        Cursor scheduleCursor = db.query(
                ScheduleContract.ScheduleEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No Records returned from query", scheduleCursor.moveToFirst());

        TestUtility.validateCurrentRecord("testScheduleTable()(): Failed to validate",
                scheduleCursor, scheduleValues);

        assertFalse("Error: More than one record returned", scheduleCursor.moveToNext());

        scheduleCursor.close();
        dbHelper.close();
    }

}
