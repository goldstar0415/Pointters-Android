package com.pointters.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class PopularCategoriesAdapter extends RecyclerView.Adapter<PopularCategoriesAdapter.PopularCategoryViewHolder> {


    @Override
    public PopularCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapater_popular_category, parent, false);

        return new PopularCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PopularCategoryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class PopularCategoryViewHolder extends RecyclerView.ViewHolder {

        public PopularCategoryViewHolder(View itemView) {
            super(itemView);
        }
    }

}
