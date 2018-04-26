package com.pointters.utils;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;

import java.util.List;

/**
 * Created by jkc on 3/8/18.
 */

public class AutoSpanGridLayoutManager extends GridLayoutManager {
    private static final String TAG = AutoSpanGridLayoutManager.class.getSimpleName();
    private SparseIntArray itemsSpanSize;
    private float columnWidth;

    public interface AutoSpanAdapter {
        List<? extends AutoSpannable> getItems();
    }

    public AutoSpanGridLayoutManager(Context context, int spanCount, int viewPadding) {
        super(context, spanCount);
        init(context, spanCount, viewPadding);
    }

    private void init(Context context, final int spanCount, int viewPadding) {
        itemsSpanSize = new SparseIntArray();
        columnWidth = calculateColumnWidth(context, spanCount, viewPadding);

        setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return itemsSpanSize.get(position);
            }
        });
    }

    private float calculateColumnWidth(Context context, int spanCount, int viewPadding) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.widthPixels - viewPadding*2) / spanCount;
    }


    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);
        calculateItemsSpan(newAdapter);
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        calculateItemsSpan(recyclerView.getAdapter(), positionStart, positionStart+itemCount);
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        super.onItemsChanged(recyclerView);
        Log.d(TAG, "on Items changed");
        calculateItemsSpan(recyclerView.getAdapter());
    }

    @Override
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        calculateItemsSpan(recyclerView.getAdapter());
    }

    @Override
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount);
        Log.d(TAG, "on Items updated");
        calculateItemsSpan(recyclerView.getAdapter(), positionStart, positionStart+itemCount);
    }

    @Override
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount, payload);
        Log.d(TAG, "on Items updated");
        calculateItemsSpan(recyclerView.getAdapter(), positionStart, positionStart + itemCount);
    }

    @Override
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        super.onItemsMoved(recyclerView, from, to, itemCount);
        // todo
    }

    private void calculateItemsSpan(RecyclerView.Adapter a) {
        calculateItemsSpan(a, 0, a.getItemCount());
    }

    private void calculateItemsSpan(RecyclerView.Adapter a, int start, int end) {
        try {
            AutoSpanAdapter adapter = (AutoSpanAdapter) a;
            for (int i = start; i < end; i++) {
                AutoSpannable autoMeasurable = adapter.getItems().get(i);
                calculateItemSpan(i, autoMeasurable.autoSpan());
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("RecyclerView.Adapter must implement " + AutoSpanAdapter.class.getSimpleName());
        } catch (ArrayIndexOutOfBoundsException e1) {
            Log.e(TAG, "Error calculating span for start: "+start+ " - end: "+end);
        }
    }

    private void calculateItemSpan(int position, float viewWidth) {
        double neededSpan = Math.ceil(viewWidth / columnWidth);
        int spanSize = (int) Math.min(getSpanCount(), Math.max(1, neededSpan));
        saveItemSpan(position, spanSize);
    }

    private void saveItemSpan(int position, int spanSize) {
        itemsSpanSize.put(position, spanSize);
    }
}
