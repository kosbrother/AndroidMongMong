package com.kosbrother.mongmongwoo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidpagecontrol.PageControl;
import com.kosbrother.mongmongwoo.adpters.ZoomImageFragmentPagerAdapter;

import java.util.ArrayList;

public class ZoomImageActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String STRING_ARRAY_LIST_EXTRA_URL = "STRING_ARRAY_LIST_EXTRA_URL";
    public static final String INT_EXTRA_INDEX = "INT_EXTRA_INDEX";

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        ArrayList<String> urls = getIntent().getStringArrayListExtra(STRING_ARRAY_LIST_EXTRA_URL);
        int index = getIntent().getIntExtra(INT_EXTRA_INDEX, 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.image_vp);
        viewPager.setAdapter(new ZoomImageFragmentPagerAdapter(
                getSupportFragmentManager(), urls));
        viewPager.setCurrentItem(index);

        PageControl pageControl = (PageControl) findViewById(R.id.page_control);
        pageControl.setViewPager(viewPager);

        View container = findViewById(R.id.activity_container);
        container.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
