package com.kosbrother.mongmongwoo.googleanalytics.event.error;

import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class ErrorEvent implements GAEvent{
    private final String errorTitle;
    private final String message;

    public ErrorEvent(String errorTitle, String message) {
        this.errorTitle = errorTitle;
        this.message = message;
    }

    @Override
    public String getCategory() {
        return GACategory.ERROR;
    }

    @Override
    public String getAction() {
        return errorTitle;
    }

    @Override
    public String getLabel() {
        return message;
    }

    @Override
    public long getValue() {
        return 0;
    }
}
