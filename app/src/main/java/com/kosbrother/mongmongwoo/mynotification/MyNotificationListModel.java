package com.kosbrother.mongmongwoo.mynotification;

import android.net.Uri;

import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationMyMessageOpenedEvent;

import java.util.List;

import rx.functions.Action1;

public class MyNotificationListModel {
    private DataManager dataManager;
    private MyNotificationManager myNotificationManager;
    private boolean fromNotification;
    private String notificationTitle;

    public MyNotificationListModel(DataManager dataManager,
                                   MyNotificationManager myNotificationManager,
                                   boolean fromNotification,
                                   String notificationTitle) {
        this.dataManager = dataManager;
        this.myNotificationManager = myNotificationManager;
        this.fromNotification = fromNotification;
        this.notificationTitle = notificationTitle;
    }

    public void getMyNotificationList(Action1<List<MyNotification>> action1) {
        dataManager.getMyNotificationList(Settings.getSavedUser().getUserId(), action1);
    }

    public void saveMyNotificationList(List<MyNotification> myNotificationList) {
        myNotificationManager.saveNewMyNotifications(myNotificationList);
    }

    public MyNotification.NotificationDetail getMyNotificationDetail(int index) {
        return myNotificationManager.getMyNotificationDetail(index);
    }

    public void saveReadNotification(int adapterPosition) {
        myNotificationManager.saveReadNotification(adapterPosition);
    }

    public List<MyNotification> getDisplayNotificationList() {
        return myNotificationManager.getDisplayNotifications();
    }

    public boolean isFromNotification() {
        return fromNotification;
    }

    public void sendNotificationMyMessageOpenedEvent() {
        GAManager.sendEvent(new NotificationMyMessageOpenedEvent(notificationTitle));
    }

    public Uri getAppIndexUri(int adapterPosition) {
        String appIndexUrl = myNotificationManager.getDisplayNotifications().get(adapterPosition).getAppIndexUrl();
        if (appIndexUrl == null || appIndexUrl.isEmpty()) {
            return null;
        }
        return Uri.parse(appIndexUrl);
    }
}
