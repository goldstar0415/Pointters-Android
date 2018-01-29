package com.pointters.listener;

/**
 * Created by vikas on 7/15/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;



public class RecyclerViewListener implements RecyclerView.OnItemTouchListener {

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    OnItemClickListener itemClickListener;
    GestureDetector mGestureDetector;

    public RecyclerViewListener(Context context, OnItemClickListener listener) {
        itemClickListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }




        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && itemClickListener != null && mGestureDetector.onTouchEvent(e)) {
            itemClickListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
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

