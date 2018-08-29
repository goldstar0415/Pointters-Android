package com.pointters.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;

import java.util.ArrayList;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class FeaturedStyle2Adapter extends RecyclerView.Adapter<FeaturedStyle2Adapter.FeaturedStyle2ViewHolder> {

    public Context context;
    public FeaturedStyle2Adapter(Context context, ArrayList<LinkedTreeMap> sections) {
        this.context = context;
        this.sections = sections;
    }
    DisplayImageOptions options;
    public ArrayList<LinkedTreeMap> getSections() {
        return sections;
    }

    ArrayList<LinkedTreeMap> sections;

    @Override
    public FeaturedStyle2ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_featured_style2, parent, false);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        return new FeaturedStyle2ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(FeaturedStyle2ViewHolder holder, int position) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        holder.cardView.getLayoutParams().width = (int) (screenWidth * 0.4);
        LinkedTreeMap section = sections.get(position);
        holder.txtName.setText(section.get("name").toString());
        holder.txtDescriptio.setText(section.get("description").toString());
        LinkedTreeMap price = (LinkedTreeMap) section.get("price");
        Float pricevalue = Float.parseFloat(price.get("price").toString());
        String currencySymbol = price.get("currencySymbol").toString();
        Float time = Float.parseFloat(price.get("time").toString());
        String timeUnit = price.get("timeUnitOfMeasure").toString();
        holder.txtPrice.setText(String.format("%s%d per %d %s", currencySymbol, Math.round(pricevalue), Math.round(time), timeUnit));
        LinkedTreeMap media = (LinkedTreeMap) section.get("media");
        ImageLoader.getInstance().displayImage(media.get("fileName").toString(), holder.imgProfile, options);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public class FeaturedStyle2ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RoundedImageView imgProfile;
        TextView txtName, txtDescriptio, txtPrice;
        public FeaturedStyle2ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            imgProfile = (RoundedImageView) itemView.findViewById(R.id.img_profile_round);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtDescriptio = (TextView)itemView.findViewById(R.id.txt_description);
            txtPrice = (TextView)itemView.findViewById(R.id.txt_price);
        }
    }

}
