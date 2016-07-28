package com.kosbrother.mongmongwoo.mynotification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MyNotificationListPresenterTest {

    @Mock
    private MyNotificationListContract.View view;

    @Mock
    private MyNotificationListModel model;

    @Mock
    private List<MyNotification> myNotifications;

    private MyNotificationListPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new MyNotificationListPresenter(view, model);
    }

    @Test
    public void testOnCreate() throws Exception {
        presenter.onCreate();

        verify(view).showLoading();
        verify(model).getMyNotificationList(presenter);
    }

    @Test
    public void testOnMyNotificationClick() throws Exception {
        int adapterPosition = 1;

        presenter.onMyNotificationClick(adapterPosition);

        verify(model).saveReadNotification(adapterPosition);
        verify(view).setMyNotificationRead(adapterPosition);
        verify(view).startMyNotificationDetailActivity(model.getMyNotificationDetail(adapterPosition));
    }

    @Test
    public void testCall_with_data() throws Exception {
        when(myNotifications.size()).thenReturn(0);

        presenter.call(myNotifications);

        verify(view).showMyNotificationListEmpty();
    }

    @Test
    public void testCall_empty_data() throws Exception {
        when(myNotifications.size()).thenReturn(1);

        presenter.call(myNotifications);

        verify(view).showMyNotificationList(model.getDisplayNotificationList());
    }
}