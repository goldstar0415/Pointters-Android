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

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.JobRequesterModel;
import com.pointters.model.Media;
import com.pointters.model.RequestOffersModel;
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

    private OnRecyclerViewItemClickListener listener;
    public SellJobsAdapter(Context context, List<SellJobsModel> sellJobsList) {
        this.context = context;
        this.sellJobsList = sellJobsList;
    }

    public void setListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
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

        SellJobsModel model = sellJobsList.get(position);
        if (model.getRequestOffers() != null) {
            RequestOffersModel requestOffersModel = model.getRequestOffers();
            if(requestOffersModel.getRequest() != null){
                if (requestOffersModel.getRequest().getMedia() != null) {
                    Media media = requestOffersModel.getRequest().getMedia();
                    if (media.getFileName() != null) {
                        ImageLoader.getInstance().displayImage(media.getFileName(), holder.imgMedia, options);
                    }
                }
                if (requestOffersModel.getRequest().getDescription() != null) {
                    holder.txtjobDescription.setText(requestOffersModel.getRequest().getDescription());
                }else{
                    holder.txtjobDescription.setText("NA");
                }

            }
            if (requestOffersModel.getRequester() != null) {
                JobRequesterModel requesterModel = requestOffersModel.getRequester();
            }
            if (requestOffersModel.getExpiresIn() > 0) {
                holder.txtexpiresDate.setText(String.format("Expires in %d days", requestOffersModel.getExpiresIn()));
                holder.editOfferButton.setVisibility(View.VISIBLE);
                holder.makeOfferButton.setVisibility(View.VISIBLE);
            }else{
                holder.txtexpiresDate.setText("Job Expired");
                holder.editOfferButton.setVisibility(View.GONE);
                holder.makeOfferButton.setVisibility(View.GONE);
            }
            if (requestOffersModel.getNumOffers() > 0) {
                holder.txtNumOffers.setText(String.format("%d", requestOffersModel.getNumOffers()));
            }else{
                holder.txtNumOffers.setText("0");
            }

            if (requestOffersModel.getCreatedAt() != null) {
                String createdDate = DateTimeUtils.getTimeAgo(context, DateTimeUtils.formatDate(requestOffersModel.getCreatedAt()), DateTimeStyle.AGO_FULL_STRING);
                String agoString = createdDate.replace(" ago", "");
                if (agoString.contains("hours")) {
                    agoString = agoString.replace("hours", "h");
                }else if (agoString.contains("hour")) {
                    agoString = agoString.replace("hour", "h");
                }else if (agoString.contains("months")) {
                    agoString = agoString.replace("months", "M");
                }else if (agoString.contains("month")) {
                    agoString = agoString.replace("month", "M");
                }else if (agoString.contains("days")) {
                    agoString = agoString.replace("days", "d");
                }else if (agoString.contains("day")) {
                    agoString = agoString.replace("day", "d");
                }else if (agoString.contains("seconds")) {
                    agoString = agoString.replace("seconds", "s");
                }else if (agoString.contains("second")) {
                    agoString = agoString.replace("second", "s");
                }else if (agoString.contains("minutes")) {
                    agoString = agoString.replace("minutes", "m");
                }else if (agoString.contains("minute")) {
                    agoString = agoString.replace("minute", "m");
                }
                holder.txtcreatedAt.setText(agoString);
            }else{
                holder.txtcreatedAt.setText("NA");
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
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