package com.github.xzwj87.todolist.schedule.data.provider;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract.ScheduleEntry;

public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                ScheduleEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                ScheduleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete",
                0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                ScheduleProvider.class.getName());

        try {
            ProviderInfo info = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: ScheduleProvider registered with authority: " + info.authority +
                            " instead of authority: " + ScheduleContract.CONTENT_AUTHORITY,
                    info.authority, ScheduleContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: ScheduleProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://com.github.xzwj87.todolist/schedule/
        String type = mContext.getContentResolver().getType(ScheduleEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.github.xzwj87.todolist/schedule
        Log.v(LOG_TAG, "testGetType(): CONTENT_URI type = " + type);
        assertEquals("Error: the ScheduleEntry CONTENT_URI should return ScheduleEntry.CONTENT_TYPE",
                ScheduleEntry.CONTENT_TYPE, type);

        long id = 9527L;
        // content://com.github.xzwj87.todolist/schedule/9527
        type = mContext.getContentResolver().getType(ScheduleEntry.buildScheduleUri(id));
        Log.v(LOG_TAG, "testGetType(): CONTENT_URI with ID type = " + type);
        // vnd.android.cursor.item/com.github.xzwj87.todolist/schedule
        assertEquals("Error: the ScheduleEntry CONTENT_URI with ID should return ScheduleEntry.CONTENT_ITEM_TYPE",
                ScheduleEntry.CONTENT_ITEM_TYPE, type);
    }

    public Uri testInsertReadProvider() {
        ContentValues scheduleValues = TestUtility.createScheduleValues();

        TestUtility.TestContentObserver tco = TestUtility.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ScheduleEntry.CONTENT_URI, true, tco);

        Uri scheduleInsertUri = mContext.getContentResolver()
                .insert(ScheduleEntry.CONTENT_URI, scheduleValues);
        Log.v(LOG_TAG, "testInsertReadProvider(): " + scheduleInsertUri);
        assertTrue(scheduleInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor scheduleCursor = mContext.getContentResolver().query(
                ScheduleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtility.validateCursor("testInsertReadProvider(): Error validating ScheduleEntry insert.",
                scheduleCursor, scheduleValues);

        scheduleCursor = mContext.getContentResolver().query(
                ScheduleEntry.buildScheduleUri(
                        ScheduleEntry.getScheduleIdFromUri(scheduleInsertUri)),
                null,
                null,
                null,
                null
        );
        TestUtility.validateCursor("testInsertReadProvider(): Error validating ScheduleEntry with ID.",
                scheduleCursor, scheduleValues);

        return scheduleInsertUri;
    }

    public void testUpdateRecordsWithId() {
        // Insert record
        ContentValues[] bulkInsertContentValues = createBulkInsertScheduleValues();

        TestUtility.TestContentObserver tco = TestUtility.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ScheduleEntry.CONTENT_URI, true, tco);

        Uri scheduleInsertUri = mContext.getContentResolver()
                .insert(ScheduleEntry.CONTENT_URI, bulkInsertContentValues[0]);
        Log.v(LOG_TAG, "testUpdateRecords(): " + scheduleInsertUri);
        assertTrue(scheduleInsertUri != null);

        tco.waitForNotificationOrFail();

        Cursor scheduleCursor = mContext.getContentResolver().query(
                scheduleInsertUri,
                null,
                null,
                null,
                null
        );
        TestUtility.validateCursor("testUpdateRecords(): Error validating ScheduleEntry with ID.",
                scheduleCursor, bulkInsertContentValues[0]);

        mContext.getContentResolver().unregisterContentObserver(tco);

        // Update record
        mContext.getContentResolver().registerContentObserver(scheduleInsertUri, true, tco);
        int rowsUpdated = mContext.getContentResolver()
                .update(scheduleInsertUri, bulkInsertContentValues[1], null, null);
        Log.v(LOG_TAG, "testUpdateRecords(): rowsUpdated = " + rowsUpdated);

        tco.waitForNotificationOrFail();

        Cursor updatedScheduleCursor = mContext.getContentResolver().query(
                scheduleInsertUri,
                null,
                null,
                null,
                null
        );
        TestUtility.validateCursor("testUpdateRecords(): Error validating ScheduleEntry with ID.",
                updatedScheduleCursor, bulkInsertContentValues[1]);

        mContext.getContentResolver().unregisterContentObserver(tco);
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtility.TestContentObserver tco = TestUtility.getTestContentObserver();
        mContext.getContentResolver()
                .registerContentObserver(ScheduleEntry.CONTENT_URI, true, tco);

        deleteAllRecordsFromProvider();
        tco.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(tco);
    }

    public void testDeleteRecordsWithId() {
        Uri uri = testInsertReadProvider();

        TestUtility.TestContentObserver tco = TestUtility.getTestContentObserver();
        mContext.getContentResolver()
                .registerContentObserver(uri, true, tco);

        int rowsDeleted = mContext.getContentResolver().delete(uri, null, null);
        Log.v(LOG_TAG, "testDeleteRecordsWithId(): rowsDeleted = " + rowsDeleted);

        tco.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(tco);
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertScheduleValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues scheduleValues = new ContentValues();
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_TITLE, "Watch movie " + i);
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_NOTE, "Watch movie at " + i);
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_TYPE, "Movie");
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DATE_START, 1700 + i);
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DATE_END, 1900 - i);
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_SCHEDULE, i);
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TYPE, "custom");
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_ALARM_TIME, 1640 + i);
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES, 20 + i);
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL, 40 + i);
            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_IS_DONE, 0);
            returnContentValues[i] = scheduleValues;
        }
        return returnContentValues;
    }

    public void testBulkInsert() {
        ContentValues[] bulkInsertContentValues = createBulkInsertScheduleValues();

        TestUtility.TestContentObserver tco = TestUtility.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ScheduleEntry.CONTENT_URI, true, tco);

        int insertCount = mContext.getContentResolver().bulkInsert(ScheduleEntry.CONTENT_URI, bulkInsertContentValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(
                ScheduleEntry.CONTENT_URI,
                null,
                null,
                null,
                ScheduleEntry.COLUMN_DATE_START + " ASC"
        );

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtility.validateCurrentRecord(
                    "testBulkInsert(). Error validating ScheduleEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
