package com.kosbrother.mongmongwoo.mynotification;

import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;

import java.util.List;

import rx.functions.Action1;

public class MyNotificationListModel {
    private DataManager dataManager;
    private MyNotificationManager myNotificationManager;

    public MyNotificationListModel(DataManager dataManager, MyNotificationManager myNotificationManager) {
        this.dataManager = dataManager;
        this.myNotificationManager = myNotificationManager;
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
}
