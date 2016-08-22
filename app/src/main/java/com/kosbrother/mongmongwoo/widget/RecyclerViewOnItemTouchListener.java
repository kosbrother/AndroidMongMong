package com.kosbrother.mongmongwoo.widget;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.kosbrother.mongmongwoo.model.Spec;

import rx.functions.Action1;

public class RecyclerViewOnItemTouchListener implements RecyclerView.OnItemTouchListener {

    private final GestureDetector mGestureDetector;
    private Action1<Integer> onItemClickAction;

    public RecyclerViewOnItemTouchListener(Context context, Action1<Integer> onItemClickAction) {
        this.onItemClickAction = onItemClickAction;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        int position = rv.getChildAdapterPosition(child);
        if (child != null && position != -1 && mGestureDetector.onTouchEvent(e)) {
            onItemClickAction.call(position);
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}
