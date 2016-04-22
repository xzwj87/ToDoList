package com.github.xzwj87.todolist.schedule.ui;

import com.github.xzwj87.todolist.schedule.ui.model.ScheduleSuggestionModel;

import java.util.List;

public interface SearchSuggestionView {

    void updateSuggestions(List<ScheduleSuggestionModel> suggestions);

    void updateSearchText(String query);

}
