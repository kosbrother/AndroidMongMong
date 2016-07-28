package com.kosbrother.mongmongwoo.mynotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;

import com.kosbrother.mongmongwoo.Settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyNotificationManager {

    private static final String PREFS_MY_READ_NOTIFICATIONS = "PREFS_MY_READ_NOTIFICATIONS";
    private String keyOfMyReadNotifications = String.valueOf(Settings.getSavedUser().getUserId());

    private static MyNotificationManager instance;
    private final SharedPreferences sharedPreferences;

    private List<MyNotification> myNotificationsTmp = new ArrayList<>();

    private MyNotificationManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_MY_READ_NOTIFICATIONS, Context.MODE_PRIVATE);
    }

    public static MyNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyNotificationManager(context);
        }
        return instance;
    }

    public int getNumberOfNewNotifications() {
        Set<String> myReadNotifications = getMyReadNotifications();
        if (myReadNotifications.size() == 0) {
            return myNotificationsTmp.size();
        }
        return getNumberOfNewNotification(myNotificationsTmp, myReadNotifications);
    }

    public void saveReadNotification(int position) {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.addAll(getMyReadNotifications());
        tmpSet.add(String.valueOf(myNotificationsTmp.get(position).getId()));

        sharedPreferences
                .edit()
                .putStringSet(keyOfMyReadNotifications, tmpSet)
                .apply();
    }

    public List<MyNotification> getDisplayNotificationList() {
        Set<String> myReadNotifications = getMyReadNotifications();
        if (myReadNotifications.size() == 0) {
            return myNotificationsTmp;
        }

        for (String id : myReadNotifications) {
            for (MyNotification myNotification : myNotificationsTmp) {
                if (myNotification.getId() == Integer.parseInt(id)) {
                    myNotification.setNew(false);
                    break;
                }
            }
        }
        return myNotificationsTmp;
    }

    @VisibleForTesting
    protected int getNumberOfNewNotification(List<MyNotification> myNotifications, Set<String> myReadNotifications) {
        for (String id : myReadNotifications) {
            for (int i = 0; i < myNotifications.size(); i++) {
                MyNotification myNotification = myNotifications.get(i);
                if (myNotification.getId() == Integer.parseInt(id)) {
                    myNotifications.remove(myNotification);
                    break;
                }
            }
        }
        return myNotifications.size();
    }

    private Set<String> getMyReadNotifications() {
        return sharedPreferences.getStringSet(keyOfMyReadNotifications, new HashSet<String>());
    }

    public void setMyNotificationsTmp(List<MyNotification> myNotificationsTmp) {
        this.myNotificationsTmp = myNotificationsTmp;
    }

    public MyNotification.NotificationDetail getMyNotificationDetail(int index) {
        return myNotificationsTmp.get(index).getNotificationDetail();
    }
}
