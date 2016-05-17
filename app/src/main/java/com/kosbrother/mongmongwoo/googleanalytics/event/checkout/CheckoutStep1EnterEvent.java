package com.kosbrother.mongmongwoo.googleanalytics.event.checkout;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class CheckoutStep1EnterEvent implements GAEvent {

    @Override
    public String getCategory() {
        return GACategory.CHECKOUT_STEP_1;
    }

    @Override
    public String getAction() {
        return GAAction.ENTER;
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
