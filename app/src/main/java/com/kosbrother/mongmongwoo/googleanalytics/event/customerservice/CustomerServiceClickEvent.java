package com.kosbrother.mongmongwoo.googleanalytics.event.customerservice;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class CustomerServiceClickEvent implements GAEvent {
    private final String label;

    public CustomerServiceClickEvent(String label) {
        this.label = label;
    }

    @Override
    public String getCategory() {
        return GACategory.CUSTOMER_SERVICE;
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
