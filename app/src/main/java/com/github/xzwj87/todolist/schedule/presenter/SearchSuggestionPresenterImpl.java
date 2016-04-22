package com.github.xzwj87.todolist.schedule.presenter;

import android.content.Context;
import android.database.Cursor;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.xzwj87.todolist.schedule.data.provider.ScheduleSuggestionProvider;
import com.github.xzwj87.todolist.schedule.interactor.DefaultSubscriber;
import com.github.xzwj87.todolist.schedule.interactor.UseCase;
import com.github.xzwj87.todolist.schedule.interactor.mapper.ScheduleSuggestionModelDataMapper;
import com.github.xzwj87.todolist.schedule.interactor.query.GetAllScheduleSuggestionArg;
import com.github.xzwj87.todolist.schedule.ui.SearchSuggestionView;
import com.github.xzwj87.todolist.schedule.ui.model.ScheduleSuggestionModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class SearchSuggestionPresenterImpl implements SearchSuggestionPresenter {
    private static final String LOG_TAG = SearchSuggestionPresenterImpl.class.getSimpleName();

    private SearchSuggestionView mSearchSuggestionView;
    private UseCase mUseCase;
    private ScheduleSuggestionModelDataMapper mMapper;
    private List<ScheduleSuggestionModel> mSuggestions;
    @Inject @Named("activityContext") Context mContext;

    @Inject
    public SearchSuggestionPresenterImpl(@Named("getAllScheduleSuggestion")UseCase useCase,
                                         ScheduleSuggestionModelDataMapper mapper) {
        mUseCase = useCase;
        mMapper = mapper;
    }

    @Override
    public void setView(@NonNull SearchSuggestionView view) {
        mSearchSuggestionView = view;
    }

    @Override
    public void initialize() {

    }

    @Override @SuppressWarnings("unchecked")
    public void requestSuggestion(String query) {
        mUseCase.unsubscribe();
        mUseCase.init(new GetAllScheduleSuggestionArg(query))
                .execute(new SearchSuggestionSubscriber());
    }

    @Override
    public void saveRecent(String query) {
        Log.v(LOG_TAG, "saveRecent(): mContext = " + mContext);
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                mContext,
                ScheduleSuggestionProvider.AUTHORITY, ScheduleSuggestionProvider.MODE);

        suggestions.saveRecentQuery(query, null);
    }

    @Override
    public void onSuggestionSelected(int position) {
        mSearchSuggestionView.updateSearchText(mSuggestions.get(position).getTitle());
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        mSearchSuggestionView = null;
        mUseCase.unsubscribe();
    }

    private final class SearchSuggestionSubscriber extends DefaultSubscriber<Cursor> {

        @Override public void onCompleted() {}

        @Override public void onError(Throwable e) {}

        @Override public void onNext(Cursor cursor) {
            mSuggestions = mMapper.transformList(cursor);
            Log.v(LOG_TAG, "onNext(): mSuggestions.size() = " + mSuggestions.size());
            mSearchSuggestionView.updateSuggestions(mSuggestions);
        }
    }
}
