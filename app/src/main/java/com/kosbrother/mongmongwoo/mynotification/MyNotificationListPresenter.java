package com.kosbrother.mongmongwoo.mynotification;

import java.util.List;

import rx.functions.Action1;

public class MyNotificationListPresenter implements MyNotificationListContract.Presenter
        , Action1<List<MyNotification>> {

    private MyNotificationListContract.View view;
    private MyNotificationListModel model;

    public MyNotificationListPresenter(MyNotificationListContract.View view, MyNotificationListModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onCreate() {
        view.showLoading();
        model.getMyNotificationList(this);
    }

    @Override
    public void onMyNotificationClick(int adapterPosition) {
        model.saveReadNotification(adapterPosition);
        view.setMyNotificationRead(adapterPosition);
        view.startMyNotificationDetailActivity(model.getMyNotificationDetail(adapterPosition));
    }

    @Override
    public void call(List<MyNotification> myNotifications) {
        model.saveMyNotificationList(myNotifications);
        if (myNotifications.size() == 0) {
            view.showMyNotificationListEmpty();
        } else {
            view.showMyNotificationList(model.getDisplayNotificationList());
        }
    }
}
