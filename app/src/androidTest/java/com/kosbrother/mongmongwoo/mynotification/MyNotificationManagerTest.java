package com.kosbrother.mongmongwoo.mynotification;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
public class MyNotificationManagerTest {

    @Test
    public void testGetNewNotificationNumber() throws Exception {
        List<MyNotification> myNotifications = new ArrayList<>();
        myNotifications.add(createMyNotification(1));
        myNotifications.add(createMyNotification(2));

        Set<String> myReadNotifications = new HashSet<>();
        myReadNotifications.add("1");

        int expected = 1;
        int actual = MyNotificationManager.getInstance(InstrumentationRegistry.getContext(), 0).
                getNumberOfNewNotification(myNotifications, myReadNotifications);
        Assert.assertEquals(expected, actual);
    }

    @NonNull
    private MyNotification createMyNotification(int id) {
        MyNotification myNotification = new MyNotification();
        myNotification.setId(id);
        return myNotification;
    }

}