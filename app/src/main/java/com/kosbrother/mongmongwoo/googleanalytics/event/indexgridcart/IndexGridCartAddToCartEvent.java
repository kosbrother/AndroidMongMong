package com.kosbrother.mongmongwoo.googleanalytics.event.indexgridcart;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class IndexGridCartAddToCartEvent implements GAEvent {

    private final String label;

    public IndexGridCartAddToCartEvent(String productName) {
        label = productName;
    }

    @Override
    public String getCategory() {
        return GACategory.INDEX_GRID_CART;
    }

    @Override
    public String getAction() {
        return GAAction.ADD_TO_CART;
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
