package com.kosbrother.mongmongwoo.googleanalytics.event.notification;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class NotificationMyMessageSendEvent implements GAEvent {

    private final String label;

    public NotificationMyMessageSendEvent(String contentTitle) {
        label = contentTitle;
    }

    @Override
    public String getCategory() {
        return GACategory.NOTIFICATION_MY_MESSAGE;
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
