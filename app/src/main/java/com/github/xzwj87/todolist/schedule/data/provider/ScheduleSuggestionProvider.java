package com.github.xzwj87.todolist.schedule.data.provider;


import android.content.SearchRecentSuggestionsProvider;

public class ScheduleSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.github.xzwj87.todolist.ScheduleSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public ScheduleSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
