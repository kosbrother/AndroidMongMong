package com.kosbrother.mongmongwoo.googleanalytics.event.search;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class SearchSubmitQueryEvent implements GAEvent {

    private final String query;

    public SearchSubmitQueryEvent(String query) {
        this.query = query;
    }

    @Override
    public String getCategory() {
        return GACategory.SEARCH;
    }

    @Override
    public String getAction() {
        return GAAction.SUBMIT;
    }

    @Override
    public String getLabel() {
        return query;
    }

    @Override
    public long getValue() {
        return 0;
    }
}
