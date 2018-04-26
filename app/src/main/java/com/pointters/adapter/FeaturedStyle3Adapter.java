package com.pointters.adapter;

import android.content.Context;
import android.media.Image;
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
import java.util.LinkedHashMap;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class FeaturedStyle3Adapter extends RecyclerView.Adapter<FeaturedStyle3Adapter.FeaturedStyle3ViewHolder> {

    public Context context;
    ArrayList<LinkedTreeMap> sections;
    DisplayImageOptions options;

    public FeaturedStyle3Adapter(Context context, ArrayList<LinkedTreeMap> sections) {
        this.context = context;
        this.sections = sections;
    }

    @Override
    public FeaturedStyle3ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_featured_style3, parent, false);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        return new FeaturedStyle3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeaturedStyle3ViewHolder holder, int position) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        holder.cardView.getLayoutParams().width = (int) (screenWidth * 0.9);
        LinkedTreeMap section = sections.get(position);
        LinkedTreeMap media = (LinkedTreeMap) section.get("media");
        String imgurl = media.get("fileName").toString();
        ImageLoader.getInstance().displayImage(imgurl, holder.imgBackgroud, options);
        holder.txtDescription.setText(section.get("description").toString());
        holder.txtName.setText(section.get("name").toString());
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public class FeaturedStyle3ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgBackgroud;
        TextView txtDescription, txtName;
        public FeaturedStyle3ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            txtName = (TextView)itemView.findViewById(R.id.txt_name);
            txtDescription = (TextView)itemView.findViewById(R.id.txt_description);
            imgBackgroud = (ImageView)itemView.findViewById(R.id.img_background);
        }
    }

}
