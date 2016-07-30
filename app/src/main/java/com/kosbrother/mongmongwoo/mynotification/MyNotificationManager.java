package com.kosbrother.mongmongwoo.mynotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyNotificationManager {

    private static final String PREFS_MY_READ_NOTIFICATIONS = "PREFS_MY_READ_NOTIFICATIONS";
    private static String keyOfMyReadNotifications;

    private static MyNotificationManager instance;
    private final SharedPreferences sharedPreferences;

    private MyNotificationManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_MY_READ_NOTIFICATIONS, Context.MODE_PRIVATE);
    }

    public static MyNotificationManager getInstance(Context context, int userId) {
        if (instance == null) {
            instance = new MyNotificationManager(context);
        }
        keyOfMyReadNotifications = String.valueOf(userId);
        return instance;
    }

    public void saveReadNotification(int position) {
        List<MyNotification> myReadNotifications = getMySavedNotifications();
        myReadNotifications.get(position).setNew(false);

        sharedPreferences.edit()
                .putString(keyOfMyReadNotifications, new Gson().toJson(myReadNotifications))
                .apply();
    }

    public int getNumberOfNewNotifications() {
        int numberOfNewNotifications = 0;
        List<MyNotification> myReadNotifications = getMySavedNotifications();
        for (MyNotification myNotification : myReadNotifications) {
            if (myNotification.isNew()) {
                numberOfNewNotifications++;
            }
        }
        return numberOfNewNotifications;
    }

    public List<MyNotification> getDisplayNotifications() {
        return getMySavedNotifications();
    }

    private List<MyNotification> getMySavedNotifications() {
        String jsonString = sharedPreferences.getString(keyOfMyReadNotifications, new Gson().toJson(new ArrayList<MyNotification>()));
        Type typeToken = new TypeToken<List<MyNotification>>() {
        }.getType();
        return new Gson().fromJson(jsonString, typeToken);
    }

    public void saveNewMyNotifications(List<MyNotification> newMyNotifications) {
        List<MyNotification> mySavedNotifications = getMySavedNotifications();
        int sizeOfNewNotifications = newMyNotifications.size();
        int sizeOfSavedNotifications = mySavedNotifications.size();
        for (int i = 0; i < sizeOfNewNotifications - sizeOfSavedNotifications; i++) {
            mySavedNotifications.add(i, newMyNotifications.get(i));
        }

        sharedPreferences.edit()
                .putString(keyOfMyReadNotifications, new Gson().toJson(mySavedNotifications))
                .apply();
    }

    public MyNotification.NotificationDetail getMyNotificationDetail(int index) {
        return getMySavedNotifications().get(index).getNotificationDetail();
    }

    @VisibleForTesting
    protected void clearMySavedNotifications() {
        sharedPreferences.edit()
                .clear()
                .apply();
    }
}
