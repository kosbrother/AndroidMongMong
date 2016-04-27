package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.api.UrlCenter;

/**
 * Created by kolichung on 3/10/16.
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setToolbar();
        setVersionName();
        setViewByVersionUpToDate();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("關於我們");
    }

    private void setVersionName() {
        TextView versionNameTv = (TextView) findViewById(R.id.version_name_tv);
        versionNameTv.setText(Settings.getVersionName(this));
    }

    private void setViewByVersionUpToDate() {
        View updateBtn = findViewById(R.id.update_btn);
        View upToDateTv = findViewById(R.id.up_to_date_tv);
        if (Settings.isUpToDate(this)) {
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
        Uri uri = Uri.parse(UrlCenter.GOOGLE_PLAY);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
