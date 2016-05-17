package com.kosbrother.mongmongwoo.googleanalytics.event.cart;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class CartClickEvent implements GAEvent {

    @Override
    public String getCategory() {
        return GACategory.CART;
    }

    @Override
    public String getAction() {
        return GAAction.CLICK;
    }

    @Override
    public String getLabel() {
        return "";
    }

    @Override
    public long getValue() {
        return 0;
    }
}
