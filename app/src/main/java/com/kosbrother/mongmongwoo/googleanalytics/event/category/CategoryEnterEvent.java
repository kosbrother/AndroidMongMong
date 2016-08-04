package com.kosbrother.mongmongwoo.googleanalytics.event.category;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class CategoryEnterEvent implements GAEvent {
    private String categoryName;

    public CategoryEnterEvent(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String getCategory() {
        return GACategory.CATEGORY;
    }

    @Override
    public String getAction() {
        return GAAction.ENTER;
    }

    @Override
    public String getLabel() {
        return categoryName;
    }

    @Override
    public long getValue() {
        return 0;
    }
}
