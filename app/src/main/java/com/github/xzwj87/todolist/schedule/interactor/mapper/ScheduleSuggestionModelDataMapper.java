package com.github.xzwj87.todolist.schedule.interactor.mapper;

import android.app.SearchManager;
import android.database.Cursor;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.internal.di.PerActivity;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleSuggestionModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@PerActivity
public class ScheduleSuggestionModelDataMapper {
    private static final String LOG_TAG = ScheduleSuggestionModelDataMapper.class.getSimpleName();

    public static final String[] SCHEDULE_SUGGESTION_COLUMNS = {
            "_id",
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
    };

    public static final int COL_SCHEDULE_ID = 0;
    public static final int COL_SCHEDULE_TITLE = 1;
    public static final int COL_SCHEDULE_DETAIL = 2;

    @Inject
    public ScheduleSuggestionModelDataMapper() {}

    public ScheduleSuggestionModel transform(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cannot transform a null value");
        }
        cursor.moveToFirst();

        ScheduleSuggestionModel suggestion = new ScheduleSuggestionModel();
        suggestion.setId(cursor.getLong(cursor.getColumnIndex("_id")));
        suggestion.setTitle(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)));
//        suggestion.setDetail(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2)));

        Log.v(LOG_TAG, "transform(): suggestion = " + suggestion);

        return suggestion;
    }


    public List<ScheduleSuggestionModel> transformList(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cannot transform a null value");
        }

        List<ScheduleSuggestionModel> suggestions = new ArrayList<>();
        while(cursor.moveToNext()) {
            ScheduleSuggestionModel suggestion = new ScheduleSuggestionModel();
            suggestion.setId(cursor.getLong(cursor.getColumnIndex("_id")));
            suggestion.setTitle(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)));
//            suggestion.setDetail(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2)));

            Log.v(LOG_TAG, "transform(): suggestion = " + suggestion);

            suggestions.add(suggestion);
        }

        Log.v(LOG_TAG, "transform(): suggestions.size() = " + suggestions.size());

        return suggestions;
    }
}
