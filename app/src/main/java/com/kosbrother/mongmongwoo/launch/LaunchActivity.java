package com.kosbrother.mongmongwoo.launch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.utils.InitUtil;
import com.viewpagerindicator.CirclePageIndicator;

public class LaunchActivity extends FragmentActivity {

    private static final int NUM_PAGES = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitUtil.initApp(getApplicationContext(), getApplication());

        if (isNewUser()) {
            setContentView(R.layout.activity_launch);
            View mRootView = findViewById(R.id.root_rl);
            mRootView.setVisibility(View.INVISIBLE);

            ViewPager mPager = (ViewPager) findViewById(R.id.launch_pager);
            mPager.setOffscreenPageLimit(3);
            PagerAdapter launchPagerAdapter = new LaunchPagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(launchPagerAdapter);
            CirclePageIndicator pageControl = (CirclePageIndicator) findViewById(R.id.page_control);
            pageControl.setViewPager(mPager);
            mRootView.setVisibility(View.VISIBLE);
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
