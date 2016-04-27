package com.kosbrother.mongmongwoo.adpters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kosbrother.mongmongwoo.fragments.ZoomImageFragment;

import java.util.ArrayList;

public class ZoomImageFragmentPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<String> urls;

    public ZoomImageFragmentPagerAdapter(
            FragmentManager fm,
            ArrayList<String> urls) {
        super(fm);
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Fragment getItem(int position) {
        return ZoomImageFragment.newInstance(urls.get(position));
    }
}
