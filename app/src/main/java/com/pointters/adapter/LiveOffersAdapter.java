package com.pointters.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.GeoJsonModel;
import com.pointters.model.LiveOfferModel;
import com.pointters.model.ServicesModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class LiveOffersAdapter extends RecyclerView.Adapter<LiveOffersAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<LiveOfferModel> serviceArrayList;
    private String userName;
    private double userLat;
    private double userLng;
    private OnRecyclerViewButtonClickListener listener;

    public LiveOffersAdapter(Context context, ArrayList<LiveOfferModel> serviceArrayList, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.serviceArrayList = serviceArrayList;
    }

    public LiveOffersAdapter(Context context, ArrayList<LiveOfferModel> serviceArrayList, double lat, double lng, String userName, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.serviceArrayList = serviceArrayList;
        this.userName = userName;
        this.userLat = lat;
        this.userLng = lng;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_live_offers, parent, false);
        return new LiveOffersAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
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

        LiveOfferModel model = serviceArrayList.get(position);
        if (model == null) {
            return;
        }
        if (model.getMedia() != null) {
            if (model.getMedia().getFileName() != null && !model.getMedia().getFileName().isEmpty()) {
                String strPic = model.getMedia().getFileName();
                if (!strPic.contains("https://s3.amazonaws.com")) {
//                    strPic = "https://s3.amazonaws.com" + strPic;
                }
                ImageLoader.getInstance().displayImage(strPic, holder.imgUser, options);
            }

        }
        if (model.getDescription() != null && !model.getDescription().isEmpty()) {
            holder.txtServiceDesc.setText(model.getDescription());
        } else {
            holder.txtServiceDesc.setText("NA");
        }

        if (model.getSeller() != null && !model.getSeller().getFirstName().isEmpty() && !model.getSeller().getLastName().isEmpty()) {

            holder.txtUserName.setText(String.valueOf(position + 1) + ". " + model.getSeller().getFirstName() + " " + model.getSeller().getLastName());
        } else {
            holder.txtUserName.setText(String.valueOf(position + 1) + ".");
        }

        if (model.getPrices() != null) {
            String currency = "";
            currency = model.getPrices().getCurrencySymbol() != null ? model.getPrices().getCurrencySymbol(): "";
            holder.txtPrice.setText(currency + "" + model.getPrices().getPrice());
        } else {
            holder.txtPrice.setText("NA");
        }

    }

    @Override
    public int getItemCount() {
        return serviceArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private
        ConstraintLayout layoutParent;
        private ImageView imgUser;
        private TextView txtServiceDesc,txtCount, txtPrice, txtUserName;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;

        public MyViewHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            layoutParent=(ConstraintLayout)itemView.findViewById(R.id.move_to_service_detail);
            txtServiceDesc=(TextView)itemView.findViewById(R.id.txt_service_desc);
            txtUserName=(TextView)itemView.findViewById(R.id.txt_username);
            txtPrice=(TextView)itemView.findViewById(R.id.txt_price);
            txtCount=(TextView)itemView.findViewById(R.id.txt_count);
            imgUser=(ImageView)itemView.findViewById(R.id.img_user);
            layoutParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listenerRef != null)
                listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }
}