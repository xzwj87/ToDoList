package com.github.xzwj87.todolist.schedule.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.xzwj87.todolist.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchSuggestionAdapter extends CursorAdapter {
    private static final String LOG_TAG = SearchSuggestionAdapter.class.getSimpleName();

    public SearchSuggestionAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

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
        Log.v(LOG_TAG, "bindView(): cursor = " + cursor.getCount());
        Log.v(LOG_TAG, "bindView(): cursor = " + cursor);

        ViewHolder viewHolder = (ViewHolder) view.getTag();

//        Log.v(LOG_TAG, "bindView(): cursor.getColumnNames() = " + Arrays.toString(cursor.getColumnNames()));
        viewHolder.mTitle.setText(cursor.getString(cursor.getColumnIndex("suggest_text_1")));
    }

    public static class ViewHolder {
        @Bind(R.id.iv_suggestion_item_icon) ImageView mIcon;
        @Bind(R.id.tv_suggestion_item_title) TextView mTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
