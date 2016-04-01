package com.github.xzwj87.todolist.schedule.presenter;


import android.support.annotation.NonNull;

import com.github.xzwj87.todolist.schedule.ui.SearchSuggestionView;

public interface SearchSuggestionPresenter extends Presenter {

    void setView(@NonNull SearchSuggestionView view);

    void initialize();

    void requestSuggestion(String query);

    void saveRecent(String query);

    void onSuggestionSelected(int position);

}
