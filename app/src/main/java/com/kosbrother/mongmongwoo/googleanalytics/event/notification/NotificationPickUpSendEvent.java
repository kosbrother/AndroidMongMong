package com.kosbrother.mongmongwoo.googleanalytics.event.notification;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class NotificationPickUpSendEvent implements GAEvent {

    private final String label;

    public NotificationPickUpSendEvent(String orderId) {
        label = orderId;
    }

    @Override
    public String getCategory() {
        return GACategory.NOTIFICATION_PICK_UP;
    }

    @Override
    public String getAction() {
        return GAAction.SEND;
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
