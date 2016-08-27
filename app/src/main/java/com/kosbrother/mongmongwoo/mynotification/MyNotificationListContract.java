package com.kosbrother.mongmongwoo.mynotification;

import android.net.Uri;

import java.util.List;

public interface MyNotificationListContract {

    interface View {
        void showLoading();

        void showMyNotificationList(List<MyNotification> myNotificationList);

        void showMyNotificationListEmpty();

        void setMyNotificationRead(int adapterPosition);

        void startMyNotificationDetailActivity(MyNotification.NotificationDetail notificationDetail);

        void startMainActivityThenFinish();

        void startIndexActivity(Uri appIndexUri);

        void superOnBackPressed();
    }

    interface Presenter {
        void onCreate();

        void onMyNotificationClick(int adapterPosition);

        void onBackPressed();
    }
}
