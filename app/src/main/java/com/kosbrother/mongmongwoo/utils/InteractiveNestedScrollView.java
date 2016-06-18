package com.kosbrother.mongmongwoo.utils;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

public class InteractiveNestedScrollView extends NestedScrollView {
    OnBottomReachedListener mListener = new OnBottomReachedListener() {
        @Override
        public void onBottomReached() {

        }

        @Override
        public void onBottomLeft() {

        }
    };
    private boolean bottomReached = false;

    public InteractiveNestedScrollView(Context context, AttributeSet attrs,
                                       int defStyle) {
        super(context, attrs, defStyle);
    }

    public InteractiveNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InteractiveNestedScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = getChildAt(getChildCount() - 1);
        int diff = (view.getBottom() - (getHeight() + getScrollY()));

        if (diff <= 0) {
            bottomReached = true;
            mListener.onBottomReached();
        } else if (bottomReached && oldt > t) {
            bottomReached = false;
            mListener.onBottomLeft();
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnBottomReachedListener(
            OnBottomReachedListener onBottomReachedListener) {
        mListener = onBottomReachedListener;
    }

    public interface OnBottomReachedListener {
        void onBottomReached();

        void onBottomLeft();
    }

}
