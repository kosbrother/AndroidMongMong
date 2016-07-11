package com.kosbrother.mongmongwoo.googleanalytics.event.search;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class SearchEnterEvent implements GAEvent {
    private final String productName;

    public SearchEnterEvent(String productName) {
        this.productName = productName;
    }

    @Override
    public String getCategory() {
        return GACategory.SEARCH;
    }

    @Override
    public String getAction() {
        return GAAction.ENTER;
    }

    @Override
    public String getLabel() {
        return productName;
    }

    @Override
    public long getValue() {
        return 0;
    }
}
