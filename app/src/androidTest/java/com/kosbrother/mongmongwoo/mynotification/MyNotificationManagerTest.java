package com.kosbrother.mongmongwoo.mynotification;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MyNotificationManagerTest {

    private MyNotificationManager manager;

    @Before
    public void setUp() throws Exception {
        manager = MyNotificationManager.getInstance(InstrumentationRegistry.getTargetContext(), 0);
    }

    @After
    public void tearDown() throws Exception {
        manager.clearMySavedNotifications();
    }

    @Test
    public void testSaveNewMyNotifications() throws Exception {
        List<MyNotification> myNotifications = new ArrayList<>();
        myNotifications.add(createNewNotification(1));
        manager.saveNewMyNotifications(myNotifications);

        List<MyNotification> newMyNotifications = new ArrayList<>();
        newMyNotifications.add(createNewNotification(1));
        newMyNotifications.add(createNewNotification(2));
        manager.saveNewMyNotifications(newMyNotifications);

        int expected = newMyNotifications.size();
        int actual = manager.getDisplayNotifications().size();
        assertEquals(expected, actual);
    }

    @Test
    public void testSaveReadNotificationThenGetNumberOfNewNotifications() throws Exception {
        List<MyNotification> newMyNotifications = new ArrayList<>();
        newMyNotifications.add(createNewNotification(1));
        newMyNotifications.add(createNewNotification(2));
        manager.saveNewMyNotifications(newMyNotifications);

        manager.saveReadNotification(1);

        int expected = 1;
        int actual = manager.getNumberOfNewNotifications();
        assertEquals(expected, actual);
    }

    @NonNull
    private MyNotification createNewNotification(int id) {
        MyNotification myNotification = new MyNotification();
        myNotification.setId(id);
        myNotification.setNew(true);
        return myNotification;
    }

}