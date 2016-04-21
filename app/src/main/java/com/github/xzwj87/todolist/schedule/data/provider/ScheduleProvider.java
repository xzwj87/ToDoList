package com.github.xzwj87.todolist.schedule.data.provider;


import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.github.xzwj87.todolist.alarm.service.AlarmObserver;

public class ScheduleProvider extends ContentProvider {
    public static final String LOG_TAG = ScheduleProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ScheduleDbHelper mOpenHelper;
    private AlarmObserver mAlarmObserver;

    public static final int SCHEDULE = 100;
    public static final int SCHEDULE_WITH_ID = 101;

    private static final String sScheduleIdSelection =
            ScheduleContract.ScheduleEntry.TABLE_NAME +
                    "." + ScheduleContract.ScheduleEntry._ID + " = ? ";

    @Override
    public boolean onCreate() {
        mOpenHelper = new ScheduleDbHelper(getContext());
        registerContentObserver();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SCHEDULE_WITH_ID:
                return ScheduleContract.ScheduleEntry.CONTENT_ITEM_TYPE;
            case SCHEDULE:
                return ScheduleContract.ScheduleEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "schedule/*"
            case SCHEDULE_WITH_ID: {
                long id = ScheduleContract.ScheduleEntry.getScheduleIdFromUri(uri);
                selectionArgs = new String[]{ String.valueOf(id) };
                selection = sScheduleIdSelection;
                Log.v(LOG_TAG, "query(): SCHEDULE_WITH_ID, id = " + id);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScheduleContract.ScheduleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "schedule"
            case SCHEDULE: {
                Log.v(LOG_TAG, "query(): SCHEDULE");
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScheduleContract.ScheduleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SCHEDULE: {
                long id = db.insert(ScheduleContract.ScheduleEntry.TABLE_NAME, null, values);
                if ( id > 0 )
                    returnUri = ScheduleContract.ScheduleEntry.buildScheduleUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) selection = "1";
        switch (match) {
            case SCHEDULE:
                rowsDeleted = db.delete(
                        ScheduleContract.ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SCHEDULE_WITH_ID:
                long id = ScheduleContract.ScheduleEntry.getScheduleIdFromUri(uri);
                selectionArgs = new String[]{ String.valueOf(id) };
                selection = sScheduleIdSelection;
                Log.v(LOG_TAG, "delete(): SCHEDULE_WITH_ID, id = " + id);
                rowsDeleted = db.delete(
                        ScheduleContract.ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        Uri returnUri = uri;

        switch (match) {
            case SCHEDULE:
                rowsUpdated = db.update(ScheduleContract.ScheduleEntry.TABLE_NAME, values,
                        selection, selectionArgs);

                break;
            case SCHEDULE_WITH_ID:
                long id = ScheduleContract.ScheduleEntry.getScheduleIdFromUri(uri);
                selectionArgs = new String[]{ String.valueOf(id) };
                selection = sScheduleIdSelection;
                Log.v(LOG_TAG, "update(): SCHEDULE_WITH_ID, id = " + id);
                rowsUpdated = db.update(ScheduleContract.ScheduleEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(returnUri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCHEDULE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(ScheduleContract.ScheduleEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScheduleContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ScheduleContract.PATH_SCHEDULE, SCHEDULE);
        matcher.addURI(authority, ScheduleContract.PATH_SCHEDULE + "/#", SCHEDULE_WITH_ID);

        return matcher;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        unregisterContentObserver();
        super.shutdown();
    }

    public void registerContentObserver(){
        Uri uri = ScheduleContract.ScheduleEntry.CONTENT_URI;
        mAlarmObserver = new AlarmObserver(getContext(), new Handler());
        getContext().getContentResolver().registerContentObserver(uri, true, mAlarmObserver);
    }

    public void unregisterContentObserver(){
        getContext().getContentResolver().unregisterContentObserver(mAlarmObserver);
    }
}

