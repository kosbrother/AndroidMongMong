package com.kosbrother.mongmongwoo.googleanalytics.event.navigationdrawer;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class NavigationDrawerClickEvent implements GAEvent {

    private final String label;

    public NavigationDrawerClickEvent(String itemName) {
        label = itemName;
    }

    @Override
    public String getCategory() {
        return GACategory.NAVIGATION_DRAWER;
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
