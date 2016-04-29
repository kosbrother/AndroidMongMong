package com.kosbrother.mongmongwoo.adpters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kosbrother.mongmongwoo.fragments.ProductImageFragment;

import java.util.ArrayList;

public class ProductImageFragmentPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<String> urls;

    public ProductImageFragmentPagerAdapter(
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
        return ProductImageFragment.newInstance(urls.get(position), position);
    }
}
