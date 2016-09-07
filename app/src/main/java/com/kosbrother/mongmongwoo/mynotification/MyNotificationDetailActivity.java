package com.kosbrother.mongmongwoo.mynotification;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.databinding.ActivityMyNotificationDetailBinding;

public class MyNotificationDetailActivity extends BaseActivity {

    public static final String EXTRA_SERIALIZABLE_NOTIFICATION_DETAIL =
            "EXTRA_SERIALIZABLE_NOTIFICATION_DETAIL";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyNotification.NotificationDetail notificationDetail =
                (MyNotification.NotificationDetail) getIntent().getSerializableExtra(EXTRA_SERIALIZABLE_NOTIFICATION_DETAIL);

        ActivityMyNotificationDetailBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_my_notification_detail);
        binding.setNotificationDetail(notificationDetail);
        setToolbar();
    }
}
