package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.SellJobsModel;
import com.pointters.utils.SquareImageView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by mac on 12/5/17.
 */

public class SellJobsAdapter extends RecyclerView.Adapter<SellJobsAdapter.MyViewHolder> {
    private Context context;
    private List<SellJobsModel> sellJobsList;

    public SellJobsAdapter(Context context, List<SellJobsModel> sellJobsList) {
        this.context = context;
        this.sellJobsList = sellJobsList;
    }

    @Override
    public SellJobsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_jobs, parent, false);
        return new SellJobsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SellJobsAdapter.MyViewHolder holder, int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

//        if (position == 0) {
//            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
//            holder.layoutParent.setLayoutParams(holder.layoutParams);
//        }
//        if (sellJobsList != null && sellJobsList.size() > 0) {
//            if (sellJobsList.get(position).getRequestOffers() != null) {
//                if (sellJobsList.get(position).getRequestOffers().getRequest() != null) {
//                    if (sellJobsList.get(position).getRequestOffers().getRequest().getMedia() != null) {
//                        if (sellJobsList.get(position).getRequestOffers().getRequest().getMedia().getFileName() != null && !sellJobsList.get(position).getRequestOffers().getRequest().getMedia().getFileName().isEmpty()) {
//                            String strPic = sellJobsList.get(position).getRequestOffers().getRequest().getMedia().getFileName();
//                            if (!strPic.contains("https://s3.amazonaws.com")) {
//                                strPic = "https://s3.amazonaws.com" + strPic;
//                            }
//                            ImageLoader.getInstance().displayImage(strPic, holder.imgProfile, options);
//                        }
//                    }
//
//                    if (sellJobsList.get(position).getRequestOffers().getRequest().getDescription() != null && !sellJobsList.get(position).getRequestOffers().getRequest().getDescription().isEmpty())
//                        holder.txtJobsDesc.setText(sellJobsList.get(position).getRequestOffers().getRequest().getDescription());
//                    else
//                        holder.txtJobsDesc.setText("NA");
//
//                    if (sellJobsList.get(position).getRequestOffers().getRequest().getCreatedAt() != null) {
//                        TimeZone tz = TimeZone.getTimeZone("UTC");
//                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//                        df.setTimeZone(tz);
//
//                        SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy  hh:mm a");
//                        try {
//                            holder.txtCreateddate.setText("Posted on " + String.valueOf(fmtOut.format(df.parse(String.valueOf(sellJobsList.get(position).getRequestOffers().getRequest().getCreatedAt())))));
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    } else
//                        holder.txtCreateddate.setText("NA");
//                }
//
//                if (sellJobsList.get(position).getRequestOffers().getRequester() != null) {
//                    if (sellJobsList.get(position).getRequestOffers().getRequester().getLow() != null && sellJobsList.get(position).getRequestOffers().getRequester().getHigh() != null) {
//                        holder.txtPriceRange.setText("Price range: $" + sellJobsList.get(position).getRequestOffers().getRequester().getLow() + "-" + sellJobsList.get(position).getRequestOffers().getRequester().getHigh());
//                    } else
//                        holder.txtPriceRange.setText("Price range: NA");
//                }
//
//                if (sellJobsList.get(position).getRequestOffers().getCreatedAt() != null) {
//                    TimeZone tz = TimeZone.getTimeZone("UTC");
//                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//                    df.setTimeZone(tz);
//
//                    long offer_time = 0;
//                    try {
//                        offer_time = df.parse(sellJobsList.get(position).getRequestOffers().getCreatedAt()).getTime();
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    String strDuration = getDateDuration(offer_time);
//
//                    holder.txtOfferDuration.setText("You sent an offer " + strDuration + " ago");
//                } else
//                    holder.txtOfferDuration.setText("");
//
//
//                if (sellJobsList.get(position).getRequestOffers().getNumOffers() != null && sellJobsList.get(position).getRequestOffers().getNumOffers() > 0) {
//                    holder.txtSellerCnt.setText(sellJobsList.get(position).getRequestOffers().getNumOffers() + " other sellers sent offers");
//                } else
//                    holder.txtSellerCnt.setText("0 other sellers sent offers");
//
//                if (sellJobsList.get(position).getRequestOffers().getExpiresIn() != null) {
//                    if (sellJobsList.get(position).getRequestOffers().getExpiresIn() < 0) {
//                        holder.txtValidity.setText("Expired");
//                    } else {
//                        holder.txtValidity.setText("Expires in " + sellJobsList.get(position).getRequestOffers().getExpiresIn() + " days");
//                    }
//                }
//                else
//                    holder.txtValidity.setText("Expires in NA days");
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return sellJobsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutParent;
        private SquareImageView imgMedia;
        private TextView txtcreatedAt;
        private TextView txtjobDescription, txtNumOffers, txtpriceRange, txtexpiresDate;
        private Button makeOfferButton, editOfferButton;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;


        public MyViewHolder(View itemView) {
            super(itemView);
//            listenerRef = new WeakReference<>(listener);

            imgMedia=(SquareImageView)itemView.findViewById(R.id.img_media);
            txtcreatedAt = (TextView) itemView.findViewById(R.id.txt_created_date);
            txtjobDescription = (TextView) itemView.findViewById(R.id.txt_description);
            txtNumOffers = (TextView) itemView.findViewById(R.id.txt_order_num);
            txtpriceRange = (TextView) itemView.findViewById(R.id.txt_price_range);
            txtexpiresDate = (TextView) itemView.findViewById(R.id.txt_expires_date);

            makeOfferButton = (Button) itemView.findViewById(R.id.btn_make_offer);
            editOfferButton = (Button) itemView.findViewById(R.id.btn_edit_offer);

            layoutParent=(LinearLayout)itemView.findViewById(R.id.move_to_service_detail);
//            layoutParent.setOnClickListener(this);
        }

    }

    private String getDateDuration(long originTime) {
        long unit_sec = 1000;
        long unit_min = 60 * unit_sec;
        long unit_hour = 60 * unit_min;
        long unit_day = 24 * unit_hour;
        long unit_week = 7 * unit_day;
        long unit_month = 30 * unit_day;
        long unit_year = 365 * unit_day;

        long curTime = System.currentTimeMillis();
        long duration = curTime - originTime;

        long years = (long) Math.floor(duration/unit_year);
        long months = (long) Math.floor(duration/unit_month);
        long weeks = (long) Math.floor(duration/unit_week);
        long days = (long) Math.floor(duration/unit_day);
        long hours = (long) Math.floor(duration/unit_hour);
        long mins = (long) Math.floor(duration/unit_min);
        long secs = (long) Math.floor(duration/unit_sec);

        if (years > 0)       { return years + "Y"; }
        else if (months > 0) { return months + "M"; }
        else if (weeks > 0)  { return weeks + "W"; }
        else if (days > 0)   { return days + "d"; }
        else if (hours > 0)  { return hours + "hr"; }
        else if (mins > 0)   { return mins + "m"; }
        else                 { return secs + "s"; }
    }
}