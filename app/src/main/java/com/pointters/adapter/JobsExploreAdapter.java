package com.pointters.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.ExploreJobsModel;
import com.pointters.model.GeoJsonModel;
import com.pointters.model.ServicesExploreModel;
import com.pointters.utils.CommonUtils;
import com.pointters.utils.SquareImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;


public class JobsExploreAdapter extends RecyclerView.Adapter<JobsExploreAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ExploreJobsModel> serviceArrayList;
    private String userName;
    private double userLat;
    private double userLng;
    private OnRecyclerViewButtonClickListener listener;
    private OnRecyclerViewItemClickListener listener1;

    public JobsExploreAdapter(Context context, ArrayList<ExploreJobsModel> serviceArrayList, OnRecyclerViewButtonClickListener listener1) {
        this.context = context;
        this.listener = listener1;
        this.serviceArrayList = serviceArrayList;
    }

    public JobsExploreAdapter(Context context, ArrayList<ExploreJobsModel> serviceArrayList, double lat, double lng, String userName, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.serviceArrayList = serviceArrayList;
        this.userName = userName;
        this.userLat = lat;
        this.userLng = lng;
        this.listener = listener;
    }

    public void setListener1(OnRecyclerViewItemClickListener listener1) {
        this.listener1 = listener1;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_jobs, parent, false);
        return new JobsExploreAdapter.MyViewHolder(itemView);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        ExploreJobsModel model = serviceArrayList.get(position);
        if (model != null) {
            if (model.getMedia() != null) {
                if (model.getMedia().get(0).getFileName() != null && !model.getMedia().get(0).getFileName().isEmpty()) {
                    String strPic = model.getMedia().get(0).getFileName();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
//                        strPic = "https://s3.amazonaws.com" + strPic;
                    }
                    ImageLoader.getInstance().displayImage(strPic, holder.imgMedia, options);
                }
            }

            if (model.getDescription() != null && !model.getDescription().isEmpty()) {
                holder.txtjobDescription.setText(model.getDescription());
            } else {
                holder.txtjobDescription.setText("NA");
            }

            if (model.getMinPrice() != null && model.getMaxPrice() != null) {
                String currencyCode = model.getCurrencyCode() != null ? model.getCurrencyCode() : "";
                holder.txtpriceRange.setText(String.format("%s%d - %s%d", currencyCode, model.getMinPrice(), currencyCode, model.getMaxPrice()));
            } else {
                holder.txtpriceRange.setText(" - ");
            }


            if (model.getNumOffers() != null) {
                holder.txtNumOffers.setText(String.valueOf(model.getNumOffers()));
            }else{
                holder.txtNumOffers.setText(String.valueOf(0));
            }

            if (model.getCreatedAt() != null) {
                String createdDate = DateTimeUtils.getTimeAgo(context, DateTimeUtils.formatDate(model.getCreatedAt()), DateTimeStyle.AGO_FULL_STRING);
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


            }

            if (model.getScheduleDate() != null ) {
                long timeInMilliseconds = DateTimeUtils.getDateDiff(model.getScheduleDate(), new Date(), DateTimeUnits.DAYS);
                if (timeInMilliseconds < 0) {
                    holder.txtexpiresDate.setText("Job expired");
                    holder.makeOfferButton.setVisibility(View.GONE);
                    holder.editOfferButton.setVisibility(View.GONE);
                }else{
                    if (timeInMilliseconds > 30) {
                        holder.txtexpiresDate.setText(String.format("Job expires in %d months", timeInMilliseconds / 30));
                    }else if (timeInMilliseconds > 1) {
                        holder.txtexpiresDate.setText(String.format("Job expires in %d days", timeInMilliseconds));
                    }else if (timeInMilliseconds == 1) {
                        holder.txtexpiresDate.setText(String.format("Job expires in %d day", timeInMilliseconds));
                    }else{
                        long hoursdiff = DateTimeUtils.getDateDiff(model.getScheduleDate(), new Date(), DateTimeUnits.HOURS);
                        if (hoursdiff > 1) {
                            holder.txtexpiresDate.setText(String.format("Job expires in %d hours", hoursdiff));
                        }else if (hoursdiff == 1) {
                            holder.txtexpiresDate.setText(String.format("Job expires in %d hour", hoursdiff));
                        }else{
                            long minutesDiff = DateTimeUtils.getDateDiff(model.getScheduleDate(), new Date(), DateTimeUnits.MINUTES);
                            if (minutesDiff > 1) {
                                holder.txtexpiresDate.setText(String.format("Job expires in %d minutes", minutesDiff));
                            }else if (minutesDiff == 1) {
                                holder.txtexpiresDate.setText(String.format("Job expires in %d minute", minutesDiff));
                            }else{
                                long secondsdiff = DateTimeUtils.getDateDiff(model.getScheduleDate(), new Date(), DateTimeUnits.SECONDS);
                                if (secondsdiff > 0) {
                                    holder.txtexpiresDate.setText(String.format("Job expires in %d seconds", secondsdiff));
                                }
                            }
                        }
                    }
                }
            }
            if (model.getOfferSent() != null) {
                holder.editOfferButton.setVisibility(View.VISIBLE);
            }else{
                holder.editOfferButton.setVisibility(View.GONE);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener1 != null) {
                    listener1.onItemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return serviceArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout layoutParent;
        private SquareImageView imgMedia;
        private TextView txtcreatedAt;
        private TextView txtjobDescription, txtNumOffers, txtpriceRange, txtexpiresDate;
        private Button makeOfferButton, editOfferButton;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;


        public MyViewHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            imgMedia=(SquareImageView)itemView.findViewById(R.id.img_media);
            txtcreatedAt = (TextView) itemView.findViewById(R.id.txt_created_date);
            txtjobDescription = (TextView) itemView.findViewById(R.id.txt_description);
            txtNumOffers = (TextView) itemView.findViewById(R.id.txt_order_num);
            txtpriceRange = (TextView) itemView.findViewById(R.id.txt_price_range);
            txtexpiresDate = (TextView) itemView.findViewById(R.id.txt_expires_date);

            makeOfferButton = (Button) itemView.findViewById(R.id.btn_make_offer);
            editOfferButton = (Button) itemView.findViewById(R.id.btn_edit_offer);
            makeOfferButton.setOnClickListener(this);
            editOfferButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listenerRef != null){
                switch (v.getId()){
                    case R.id.btn_make_offer:
                        listenerRef.get().onButtonClick(v, getAdapterPosition());
                        break;
                    case R.id.btn_edit_offer:
                        listenerRef.get().onButtonClick(v, getAdapterPosition());
                        break;
                }
            }
        }
    }
}