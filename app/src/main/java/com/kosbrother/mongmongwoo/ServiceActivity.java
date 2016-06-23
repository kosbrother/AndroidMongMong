package com.kosbrother.mongmongwoo;

import android.os.Bundle;
import android.view.View;

import com.kosbrother.mongmongwoo.utils.CustomerServiceUtil;

public class ServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        setToolbar();
    }

    public void onStartToLineServiceClick(View view) {
        CustomerServiceUtil.startToLineService(this);
    }
}