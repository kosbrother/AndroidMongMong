package com.kosbrother.mongmongwoo.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import rx.functions.Action1;

public class RecyclerViewEndlessScrollListener extends RecyclerView.OnScrollListener {
    private static final int VISIBLE_THRESHOLD = 6;

    private LinearLayoutManager mLayoutManager;
    private Action1<Integer> onLoadMoreAction;

    private int previousTotal = 0;
    private boolean loading = true;
    private int currentPage = 1;

    public RecyclerViewEndlessScrollListener(LinearLayoutManager mLayoutManager, Action1<Integer> onLoadMoreAction) {
        this.mLayoutManager = mLayoutManager;
        this.onLoadMoreAction = onLoadMoreAction;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <=
                (firstVisibleItem + VISIBLE_THRESHOLD)) {
            loading = true;
            currentPage++;
            onLoadMoreAction.call(currentPage);
        }
    }
}
