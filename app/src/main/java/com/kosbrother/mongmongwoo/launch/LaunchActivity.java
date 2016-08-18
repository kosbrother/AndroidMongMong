package com.kosbrother.mongmongwoo.launch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.facebook.applinks.AppLinkData;
import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.viewpagerindicator.CirclePageIndicator;

public class LaunchActivity extends FragmentActivity implements AppLinkData.CompletionHandler {

    private static final int NUM_PAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // From facebook deep link
        AppLinkData appLinkData = AppLinkData.createFromActivity(this);
        if (appLinkUriValid(appLinkData)) {
            intentToUriThenFinish(appLinkData.getTargetUri());
            return;
        }

        if (isNewUser()) {
            AppLinkData.fetchDeferredAppLinkData(this, this);
        } else {
            startMainActivityThenFinish();
        }
    }

    @Override
    public void onBackPressed() {
        startMainActivityThenFinish();
    }

    public void onStartButtonClick(View view) {
        startMainActivityThenFinish();
    }

    @Override
    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
        if (appLinkUriValid(appLinkData)) {
            intentToUriThenFinish(appLinkData.getTargetUri());
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLaunchContentView();
                }
            });
        }
    }

    private boolean appLinkUriValid(AppLinkData appLinkData) {
        return appLinkData != null && appLinkData.getTargetUri().toString().startsWith("android-app");
    }

    private void setLaunchContentView() {
        setContentView(R.layout.activity_launch);
        View mRootView = findViewById(R.id.root_rl);
        mRootView.setVisibility(View.INVISIBLE);

        ViewPager mPager = (ViewPager) findViewById(R.id.launch_pager);
        mPager.setOffscreenPageLimit(2);
        PagerAdapter launchPagerAdapter = new LaunchPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(launchPagerAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        mRootView.setVisibility(View.VISIBLE);
    }

    private void intentToUriThenFinish(Uri targetUri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(targetUri);
        startActivity(intent);
        finish();
    }

    private void startMainActivityThenFinish() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean isNewUser() {
        return Settings.getVersionName().isEmpty();
    }

    private class LaunchPagerAdapter extends FragmentPagerAdapter {

        public LaunchPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return LaunchPageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
