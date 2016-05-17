package com.kosbrother.mongmongwoo.googleanalytics.event.checkout;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class CheckoutStep2ClickEvent implements GAEvent {

    private final String label;

    public CheckoutStep2ClickEvent(String label) {
        this.label = label;
    }

    @Override
    public String getCategory() {
        return GACategory.CHECKOUT_STEP_2;
    }

    @Override
    public String getAction() {
        return GAAction.CLICK;
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
