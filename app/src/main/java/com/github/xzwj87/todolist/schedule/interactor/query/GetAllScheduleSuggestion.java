package com.github.xzwj87.todolist.schedule.interactor.query;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.github.xzwj87.todolist.app.App;
import com.github.xzwj87.todolist.schedule.data.provider.ScheduleSuggestionProvider;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleModelDataMapper;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import rx.Observable;
import rx.schedulers.Schedulers;

public class GetAllScheduleSuggestion extends UseCase<GetAllScheduleSuggestionArg> {
    private final String LOG_TAG = GetAllScheduleSuggestion.class.getSimpleName();

    private final BriteContentResolver mBriteContentResolver;

    public GetAllScheduleSuggestion() {
        SqlBrite sqlBrite = SqlBrite.create();
        mBriteContentResolver = sqlBrite.wrapContentProvider(
                App.getAppContext().getContentResolver(), Schedulers.io());
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (mArg == null) {
            throw new IllegalArgumentException("mArg cannot be null.");
        }

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(ScheduleSuggestionProvider.AUTHORITY);

        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);

        String selection = " ?";
        String[] selArgs = new String[] { mArg.getQuery() };

        if (mArg.getLimit() > 0) {
            uriBuilder.appendQueryParameter(
                    SearchManager.SUGGEST_PARAMETER_LIMIT, String.valueOf(mArg.getLimit()));
        }

        Uri uri = uriBuilder.build();
        Log.v(LOG_TAG, "buildUseCaseObservable(): uri = " + uri);

        return mBriteContentResolver
                .createQuery(
                        uri,
                        ScheduleModelDataMapper.SCHEDULE_COLUMNS,
                        selection,
                        selArgs,
                        null,
                        false)
                .map(SqlBrite.Query::run);
    }
}
