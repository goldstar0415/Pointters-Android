package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;

import java.util.ArrayList;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class PopularCategoriesAdapter extends RecyclerView.Adapter<PopularCategoriesAdapter.PopularCategoryViewHolder> {

    public Context context;
    DisplayImageOptions options;
    public ArrayList<LinkedTreeMap> getSections() {
        return sections;
    }

    ArrayList<LinkedTreeMap> sections;
    public PopularCategoriesAdapter(Context context, ArrayList<LinkedTreeMap> sections) {
        this.context = context;
        this.sections = sections;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public PopularCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_popular_category, parent, false);

        return new PopularCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PopularCategoryViewHolder holder, int position) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        holder.cardView.getLayoutParams().width = (int) (screenWidth * 0.5);
        LinkedTreeMap section = sections.get(position);
        ImageLoader.getInstance().displayImage(section.get("image").toString(), holder.imgBackgroud,options);
        holder.title.setText(section.get("title").toString());
        holder.subtitle.setText(section.get("subTitle").toString());

    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public class PopularCategoryViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imgBackgroud;
        TextView title, subtitle;
        public PopularCategoryViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            imgBackgroud = (ImageView) itemView.findViewById(R.id.img_background);
            title = (TextView) itemView.findViewById(R.id.txt_category);
            subtitle = (TextView)itemView.findViewById(R.id.txt_category_numbers);
        }
    }

}
