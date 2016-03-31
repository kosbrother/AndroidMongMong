package com.jasonko.mongmongwoo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by kolichung on 3/10/16.
 */
public class ServiceActivity extends AppCompatActivity {

    LinearLayout addLineButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Activity Service");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("客服中心");

        addLineButtonLayout = (LinearLayout) findViewById(R.id.add_line_button_layout);
        addLineButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://line.me/ti/p/%40kya5456n";
                Intent intentGood = new Intent(Intent.ACTION_VIEW);
                intentGood.setData(Uri.parse(url));
                startActivity(intentGood);
            }
        });
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