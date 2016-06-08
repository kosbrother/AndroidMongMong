package com.kosbrother.mongmongwoo.googleanalytics.event.exception;

import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class ExceptionEvent implements GAEvent {
    private final String exceptionTitle;
    private final String message;

    public ExceptionEvent(String exceptionTitle, String message) {
        this.exceptionTitle = exceptionTitle;
        this.message = message;
    }

    @Override
    public String getCategory() {
        return GACategory.EXCEPTION;
    }

    @Override
    public String getAction() {
        return exceptionTitle;
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
