package com.kosbrother.mongmongwoo.utils;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.kosbrother.mongmongwoo.api.DensityApi;

public class InteractiveNestedScrollView extends NestedScrollView {
    OnBottomReachedListener mListener = new OnBottomReachedListener() {
        @Override
        public void onShowQuickBar() {

        }

        @Override
        public void onHideQuickBar() {

        }

        @Override
        public void onSwitchNewQuickBar() {

        }

        @Override
        public void onSwitchPopularQuickBar() {

        }

    };
    private float quickBarHeight = 0;
    private boolean showQuickBar = false;
    private boolean switchPopularQuickBar = false;

    private View popularTitleView;
    private View newItemTitleView;

    public InteractiveNestedScrollView(Context context, AttributeSet attrs,
                                       int defStyle) {
        super(context, attrs, defStyle);
        quickBarHeight = DensityApi.convertDpToPixel(50, context);
    }

    public InteractiveNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        quickBarHeight = DensityApi.convertDpToPixel(50, context);
    }

    public InteractiveNestedScrollView(Context context) {
        super(context);
        quickBarHeight = DensityApi.convertDpToPixel(50, context);
    }

    public void setOnBottomReachedListener(
            OnBottomReachedListener onBottomReachedListener) {
        mListener = onBottomReachedListener;
    }

    public void setPopularTitleView(View popularTitleView) {
        this.popularTitleView = popularTitleView;
    }

    public void setNewItemTitleView(View newItemTitleView) {
        this.newItemTitleView = newItemTitleView;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        Point popularItemPoint = new Point();
        getDeepChildOffset(popularTitleView.getParent(), popularTitleView, popularItemPoint);
        Point newItemPoint = new Point();
        getDeepChildOffset(newItemTitleView.getParent(), newItemTitleView, newItemPoint);

        if (t > newItemPoint.y && !showQuickBar) {
            showQuickBar = true;
            mListener.onShowQuickBar();
        } else if (t < newItemPoint.y && showQuickBar) {
            showQuickBar = false;
            mListener.onHideQuickBar();
        } else if (t + quickBarHeight > popularItemPoint.y && !switchPopularQuickBar) {
            switchPopularQuickBar = true;
            mListener.onSwitchPopularQuickBar();
        } else if (t + quickBarHeight < popularItemPoint.y && switchPopularQuickBar) {
            switchPopularQuickBar = false;
            mListener.onSwitchNewQuickBar();
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void getDeepChildOffset(final ViewParent parent, final View child, Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(this)) {
            return;
        }
        getDeepChildOffset(parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    public interface OnBottomReachedListener {
        void onShowQuickBar();

        void onHideQuickBar();

        void onSwitchNewQuickBar();

        void onSwitchPopularQuickBar();
    }

}
