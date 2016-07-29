package com.kosbrother.mongmongwoo.mynotification;

import java.util.List;

public interface MyNotificationListContract {

    interface View {
        void showLoading();

        void showMyNotificationList(List<MyNotification> myNotificationList);

        void showMyNotificationListEmpty();

        void setMyNotificationRead(int adapterPosition);

        void startMyNotificationDetailActivity(MyNotification.NotificationDetail notificationDetail);

        void startMainActivityThenFinish();

        void superOnBackPressed();
    }

    interface Presenter {
        void onCreate();

        void onMyNotificationClick(int adapterPosition);

        void onBackPressed();
    }
}
