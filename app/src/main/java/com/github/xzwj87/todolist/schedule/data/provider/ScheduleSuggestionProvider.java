package com.github.xzwj87.todolist.schedule.data.provider;


import android.content.ContentValues;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class ScheduleSuggestionProvider extends SearchRecentSuggestionsProvider {
    private static final String LOG_TAG = ScheduleSuggestionProvider.class.getSimpleName();

    public final static String AUTHORITY = "com.github.xzwj87.todolist.ScheduleSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public ScheduleSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        Log.v(LOG_TAG, "query(): uri = " + uri + ", selection = " + selection +
//                ", selectionArgs = " + selectionArgs[0] + ", sortOrder = " + sortOrder);
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

}
