package com.github.xzwj87.todolist.schedule.ui.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ScheduleSuggestionModel {

    public static final String SUGGESTION_TYPE_RECENT = "recent";
    public static final String SUGGESTION_TYPE_CUSTOM = "custom";
    @StringDef({SUGGESTION_TYPE_RECENT, SUGGESTION_TYPE_CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SuggestionType {}

    private long mId;
    @SuggestionType private String mType = SUGGESTION_TYPE_RECENT;
    private String mTitle;
    private String mDetail;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String detail) {
        mDetail = detail;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("***** ScheduleSuggestionModel Details *****\n");
        stringBuilder.append("id = " + mId + "\n");
        stringBuilder.append("type = " + mType + "\n");
        stringBuilder.append("title = " + mTitle + "\n");
        stringBuilder.append("detail = " + mDetail + "\n");

        return stringBuilder.toString();
    }

}
