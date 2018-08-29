package com.pointters.adapter;

import android.annotation.SuppressLint;
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

public class FeaturedStyle1Adapter extends RecyclerView.Adapter<FeaturedStyle1Adapter.FeaturedStyleViewHolder> {

    public Context context;
    DisplayImageOptions options;
    public ArrayList<LinkedTreeMap> getSections() {
        return sections;
    }

    ArrayList<LinkedTreeMap> sections;

    public FeaturedStyle1Adapter(Context context, ArrayList<LinkedTreeMap> sections) {
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
    public FeaturedStyleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_featured_style1, parent, false);

        return new FeaturedStyleViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(FeaturedStyleViewHolder holder, int position) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        holder.cardView.getLayoutParams().width = (int) (screenWidth * 0.9);
        LinkedTreeMap section = sections.get(position);
        ImageLoader.getInstance().displayImage(section.get("image").toString(), holder.imgProfile, options);
        holder.txtDescription.setText(section.get("description").toString());
        holder.txtName.setText(section.get("name").toString());
        holder.txtPointValue.setText(section.get("pointValue").toString());
        holder.txtNumOrders.setText(section.get("numOrders").toString());
        holder.txtAvgRating.setText(section.get("avgRating").toString());
        LinkedTreeMap price = (LinkedTreeMap) section.get("price");
        Float pricevalue = Float.parseFloat(price.get("price").toString());
        String currencySymbol = price.get("currencySymbol").toString();
        Float time = Float.parseFloat(price.get("time").toString());
        String timeUnit = price.get("timeUnitOfMeasure").toString();
        holder.txtPrice.setText(String.format("%s%d", currencySymbol, Math.round(pricevalue)));
        holder.txtService.setText(String.format("Per %d %s", Math.round(time), timeUnit));
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public class FeaturedStyleViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgProfile;
        TextView txtDescription, txtName, txtPointValue, txtNumOrders, txtAvgRating, txtPrice, txtService;
        public FeaturedStyleViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            imgProfile = (ImageView) itemView.findViewById(R.id.img_profile);
            txtDescription = (TextView) itemView.findViewById(R.id.txt_description);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtPointValue = (TextView) itemView.findViewById(R.id.txt_point_value);
            txtNumOrders = (TextView) itemView.findViewById(R.id.txt_num_orders);
            txtAvgRating = (TextView) itemView.findViewById(R.id.txt_avg_rating);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
            txtService = (TextView) itemView.findViewById(R.id.txt_service);
        }
    }

}
