package com.kosbrother.mongmongwoo.mynotification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;

import java.util.List;

public class MyNotificationListActivity extends BaseActivity implements
        MyNotificationAdapter.OnMyNotificationClickListener, MyNotificationListContract.View {

    private MyNotificationAdapter adapter;
    private MyNotificationListPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int userId = Settings.getSavedUser().getUserId();
        MyNotificationListModel model = new MyNotificationListModel(
                DataManager.getInstance(),
                MyNotificationManager.getInstance(getApplicationContext(), userId));
        presenter = new MyNotificationListPresenter(this, model);
        presenter.onCreate();
    }

    @Override
    public void showLoading() {
        setContentView(R.layout.loading_no_toolbar);
    }

    @Override
    public void showMyNotificationList(List<MyNotification> myNotificationList) {
        setContentView(R.layout.activity_my_notification_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_notification_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new MyNotificationAdapter(myNotificationList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showMyNotificationListEmpty() {
        setContentView(R.layout.activity_my_notification_list_empty);
    }

    @Override
    public void onMyNotificationClick(int adapterPosition) {
        presenter.onMyNotificationClick(adapterPosition);
    }

    @Override
    public void setMyNotificationRead(int adapterPosition) {
        adapter.setMyNotificationRead(adapterPosition);
    }

    @Override
    public void startMyNotificationDetailActivity(MyNotification.NotificationDetail notificationDetail) {
        Intent intent = new Intent(this, MyNotificationDetailActivity.class);
        intent.putExtra(MyNotificationDetailActivity.EXTRA_SERIALIZABLE_NOTIFICATION_DETAIL,
                notificationDetail);
        startActivity(intent);
    }


}
