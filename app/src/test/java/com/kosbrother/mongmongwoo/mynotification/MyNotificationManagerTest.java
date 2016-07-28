package com.kosbrother.mongmongwoo.mynotification;

import android.content.Context;
import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyNotificationManagerTest {

    @Test
    public void testGetNewNotificationNumber() throws Exception {
        List<MyNotification> myNotifications = new ArrayList<>();
        myNotifications.add(createMyNotification(1));
        myNotifications.add(createMyNotification(2));

        Set<String> myReadNotifications = new HashSet<>();
        myReadNotifications.add("1");

        int expected = 1;
        int actual = MyNotificationManager.getInstance(Mockito.mock(Context.class)).
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