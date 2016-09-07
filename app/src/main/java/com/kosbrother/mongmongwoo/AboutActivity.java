package com.kosbrother.mongmongwoo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.utils.MongMongWooUtil;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setToolbar();
        setVersionName();
        setViewByVersionUpToDate();
    }

    private void setVersionName() {
        TextView versionNameTv = (TextView) findViewById(R.id.version_name_tv);
        versionNameTv.setText(BuildConfig.VERSION_NAME);
    }

    private void setViewByVersionUpToDate() {
        View updateBtn = findViewById(R.id.update_btn);
        View upToDateTv = findViewById(R.id.up_to_date_tv);
        if (Settings.isUpToDate()) {
            upToDateTv.setVisibility(View.VISIBLE);
            updateBtn.setVisibility(View.GONE);
        } else {
            upToDateTv.setVisibility(View.GONE);
            updateBtn.setVisibility(View.VISIBLE);
            updateBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        MongMongWooUtil.startToGooglePlayPage(this);
    }

}
