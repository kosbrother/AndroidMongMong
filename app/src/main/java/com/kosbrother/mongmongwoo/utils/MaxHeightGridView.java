package com.kosbrother.mongmongwoo.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import com.kosbrother.mongmongwoo.api.DensityApi;

public class MaxHeightGridView extends GridView {

    public MaxHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                (int) DensityApi.convertDpToPixel(200, getContext()), MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
