package com.kosbrother.mongmongwoo.utils;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

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
    private boolean showQuickBar = false;
    private boolean switchNewQuickBar = false;

    private View popularTitleView;
    private View newItemTitleView;

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
        int popularTitleY = 0;
        getDeepChildOffset(popularTitleView.getParent(), popularTitleView, popularTitleY);
        int newItemTitleY = 0;
        getDeepChildOffset(newItemTitleView.getParent(), newItemTitleView, newItemTitleY);

        int showPopularBarY = popularTitleY + popularTitleView.getHeight();
        int showNewItemBarTop = newItemTitleY + newItemTitleView.getHeight();

        if (t > showPopularBarY && !showQuickBar) {
            showQuickBar = true;
            mListener.onShowQuickBar();
        } else if (t < popularTitleY && showQuickBar) {
            showQuickBar = false;
            mListener.onHideQuickBar();
        } else if (t > showNewItemBarTop && !switchNewQuickBar) {
            switchNewQuickBar = true;
            mListener.onSwitchNewQuickBar();
        } else if (t < showNewItemBarTop && switchNewQuickBar) {
            switchNewQuickBar = false;
            mListener.onSwitchPopularQuickBar();
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void getDeepChildOffset(final ViewParent parent, final View child, int accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset += child.getTop();
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
