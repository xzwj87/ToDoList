package com.github.xzwj87.todolist.schedule.utility;


import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.provider.ScheduleSuggestionProvider;
import com.github.xzwj87.todolist.schedule.ui.adapter.SearchSuggestionAdapter;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleSuggestionModel;

import java.util.List;

public class ScheduleSuggestionUtility {
    private static final String LOG_TAG = ScheduleSuggestionUtility.class.getSimpleName();

    public static Cursor getRecentSuggestions(Context context, String query, int limit) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(ScheduleSuggestionProvider.AUTHORITY);

        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);

        String selection = " ?";
        String[] selArgs = new String[] { query };

        if (limit > 0) {
            uriBuilder.appendQueryParameter(
                    SearchManager.SUGGEST_PARAMETER_LIMIT, String.valueOf(limit));
        }

        Uri uri = uriBuilder.build();

        return context.getContentResolver().query(uri, null, selection, selArgs, null);
    }

    public static Cursor convertSuggestionListToCursor(List<ScheduleSuggestionModel> suggestions) {
        MatrixCursor cursor = new MatrixCursor(SearchSuggestionAdapter.COLUMNS);
        for (ScheduleSuggestionModel suggestion : suggestions) {
            cursor.addRow(new Object[] {
                    suggestion.getId(),
                    suggestion.getType(),
                    suggestion.getTitle(),
                    suggestion.getDetail()});
        }
        return cursor;
    }
}
