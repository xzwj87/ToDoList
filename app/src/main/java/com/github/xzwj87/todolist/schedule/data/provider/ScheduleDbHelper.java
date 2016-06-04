package com.github.xzwj87.todolist.schedule.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.xzwj87.todolist.schedule.data.provider.ScheduleContract.ScheduleEntry;

public class ScheduleDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;

    static final String DATABASE_NAME = "schedule.db";

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " (" +
                ScheduleEntry._ID + " INTEGER PRIMARY KEY," +
                ScheduleEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_NOTE + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_DATE_START + " INTEGER NOT NULL, " +
                ScheduleEntry.COLUMN_DATE_END + " INTEGER NOT NULL, " +
                ScheduleEntry.COLUMN_REPEAT_SCHEDULE + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_ALARM_TYPE + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_ALARM_TIME + " INTEGER, " +
                ScheduleEntry.COLUMN_REPEAT_ALARM_TIMES + " INTEGER, " +
                ScheduleEntry.COLUMN_REPEAT_ALARM_INTERVAL + " INTEGER, " +
                ScheduleEntry.COLUMN_IS_DONE + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_PRIORITY + " INTEGER NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleEntry.TABLE_NAME);
        onCreate(db);
    }
}
