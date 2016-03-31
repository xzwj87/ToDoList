package com.github.xzwj87.todolist.schedule.ui.adapter;


import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleSuggestionModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchSuggestionAdapter extends CursorAdapter {
    private static final String LOG_TAG = SearchSuggestionAdapter.class.getSimpleName();

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DETAIL = "detail";

    private static final String[] COLUMNS = {
            COLUMN_ID,
            COLUMN_TYPE,
            COLUMN_TITLE,
            COLUMN_DETAIL
    };

    private static final int COL_IDX_ID = 0;
    private static final int COL_IDX_TYPE = 1;
    private static final int COL_IDX_TITLE = 2;
    private static final int COL_DETAIL = 3;

    public SearchSuggestionAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void changeCursor(Cursor cursor) {}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.search_suggestion_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

//        Log.v(LOG_TAG, "bindView(): cursor.getColumnNames() = " + Arrays.toString(cursor.getColumnNames()));
        viewHolder.mTitle.setText(cursor.getString(COL_IDX_TITLE));
    }

    public String getSuggestionText(int position) {
        if (position >= 0 && position < getCursor().getCount()) {
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);
            return cursor.getString(COL_IDX_TITLE);
        }
        return null;
    }

    public void swapSuggestions(List<ScheduleSuggestionModel> suggestions) {
        Cursor cursor = null;
        if (suggestions != null && suggestions.size() != 0) {
            cursor = convertSuggestionListToCursor(suggestions);
        }
        swapCursor(cursor);
    }

    private MatrixCursor convertSuggestionListToCursor(List<ScheduleSuggestionModel> suggestions) {
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

    public static class ViewHolder {
        @Bind(R.id.iv_suggestion_item_icon)
        ImageView mIcon;
        @Bind(R.id.tv_suggestion_item_title)
        TextView mTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
