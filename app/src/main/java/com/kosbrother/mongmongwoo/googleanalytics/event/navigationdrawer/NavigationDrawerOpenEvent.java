package com.kosbrother.mongmongwoo.googleanalytics.event.navigationdrawer;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class NavigationDrawerOpenEvent implements GAEvent {

    @Override
    public String getCategory() {
        return GACategory.NAVIGATION_DRAWER;
    }

    @Override
    public String getAction() {
        return GAAction.OPEN;
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
