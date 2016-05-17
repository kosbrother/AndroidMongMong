package com.kosbrother.mongmongwoo.googleanalytics.event.product;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class ProductViewEvent implements GAEvent {

    private final String label;

    public ProductViewEvent(String productName) {
        label = productName;
    }

    @Override
    public String getCategory() {
        return GACategory.PRODUCT;
    }

    @Override
    public String getAction() {
        return GAAction.VIEW;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public long getValue() {
        return 0;
    }
}
