package com.pointters.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.support.v7.widget.LinearLayoutManager;
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
import com.pointters.model.Prices;
import com.pointters.model.ServicesExploreModel;
import com.pointters.model.ServicesModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class ServicesExploreAdapter extends RecyclerView.Adapter<ServicesExploreAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ServicesExploreModel> serviceArrayList;
    private String userName;
    private double userLat;
    private double userLng;
    private OnRecyclerViewButtonClickListener listener;

    public ServicesExploreAdapter(Context context, ArrayList<ServicesExploreModel> serviceArrayList, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.serviceArrayList = serviceArrayList;
    }

    public ServicesExploreAdapter(Context context, ArrayList<ServicesExploreModel> serviceArrayList, double lat, double lng, String userName, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.serviceArrayList = serviceArrayList;
        this.userName = userName;
        this.userLat = lat;
        this.userLng = lng;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_service, parent, false);
        return new ServicesExploreAdapter.MyViewHolder(itemView);
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

        if (serviceArrayList.get(position) != null) {
            ServicesExploreModel model = serviceArrayList.get(position);
            if (model.getMedia() != null) {
                if (model.getMedia().getFileName() != null && !model.getMedia().getFileName().isEmpty()) {
                    String strPic = model.getMedia().getFileName();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
//                        strPic = "https://s3.amazonaws.com" + strPic;
                    }
                    ImageLoader.getInstance().displayImage(strPic, holder.imgUser, options);
                }
            }

            if (model.getDescription() != null && !model.getDescription().isEmpty()) {
                holder.txtServiceDesc.setText(model.getDescription());
            } else {
                holder.txtServiceDesc.setText("NA");
            }

            if (model.isPromoted()) {
                holder.llPromoted.setVisibility(View.VISIBLE);
            } else {
                holder.llPromoted.setVisibility(View.INVISIBLE);
            }

            if (model.getPrices() != null) {
                String strSymbol = "$";
                Prices prices = model.getPrices();
                if (prices.getCurrencySymbol() != null && !prices.getCurrencySymbol().isEmpty()) {
                    strSymbol = prices.getCurrencySymbol();
                }
                int valPrice = 0;
                if (prices.getPrice() != null && prices.getPrice() > 0) {
                    valPrice = serviceArrayList.get(position).getPrices().getPrice();
                }
                holder.txtServicePrice.setText(strSymbol + String.valueOf(valPrice));

                int valPriceDiscount = 0;
                if (prices.getPriceWithoutDiscount() != null && prices.getPriceWithoutDiscount() > 0) {
                    valPriceDiscount = prices.getPriceWithoutDiscount();
                }

                if (valPriceDiscount > 0) {
//                    holder.txtServicePrice.setVisibility(View.VISIBLE);
                    holder.txtServicePrice.setText(strSymbol + String.valueOf(valPriceDiscount));
                } else {
//                    holder.txtServicePrice.setVisibility(View.GONE);
                }

                int valTime = 0;
                if (prices.getTime() != null && prices.getTime() > 0) {
                    valTime = prices.getTime();
                }
                String strUnit = "";
                if (prices.getTimeUnitOfMeasure() != null && !prices.getTimeUnitOfMeasure().isEmpty()) {
                    strUnit = prices.getTimeUnitOfMeasure();
                    if (strUnit.equals("hour")) {
                        strUnit = "hr";
                    }
                }

                if (valTime > 1) {
                    holder.txtPriceDesc.setText("per " + String.valueOf(valTime) + " " + strUnit + "s");
                } else {
                    holder.txtPriceDesc.setText("per " + String.valueOf(valTime) + " " + strUnit);
                }
            }

            if (model.getSeller() != null) {
                if (model.getSeller().getFirstName() != null){
                    holder.txtName.setText(model.getSeller().getFirstName() + " " + model.getSeller().getLastName());
                }else{
                    holder.txtName.setText("NA");
                }
            } else {
                holder.txtName.setText("NA");
            }

            int valPoint = 0;
            if (model.getPointValue() != null && model.getPointValue() > 0) {
                valPoint = model.getPointValue();
            }
            holder.txtFlag.setText(String.valueOf(valPoint));

            int valOrders = 0;
            if (model.getNumOrders() != null && model.getNumOrders() > 0) {
                valOrders = model.getNumOrders();
            }
            holder.txtTick.setText(String.valueOf(valOrders));

            float valRating = 0.0f;
            if (model.getAvgRating() != null && model.getAvgRating() > 0) {
                valRating = model.getAvgRating();
            }
            holder.txtTime.setText(String.valueOf(valRating) + "%");

            if (model.getLocation().size() > 0) {
                String strCity = "", strState = "", strPos="", strKm="NA";
                if (model.getLocation().get(0).getCity() != null && !model.getLocation().get(0).getCity().equals(""))
                    strCity = model.getLocation().get(0).getCity();
                if (model.getLocation().get(0).getState() != null && !model.getLocation().get(0).getState().equals(""))
                    strState = model.getLocation().get(0).getState();
                if (model.getLocation().get(0).getGeoJson() != null) {
                    GeoJsonModel geoJson = model.getLocation().get(0).getGeoJson();
                    if (geoJson.getCoordinates() != null && geoJson.getCoordinates().size() > 0) {
                        Location servicePos = new Location("");
                        servicePos.setLatitude(geoJson.getCoordinates().get(1));
                        servicePos.setLongitude(geoJson.getCoordinates().get(0));

                        Location userPos = new Location("");
                        userPos.setLatitude(userLat);
                        userPos.setLongitude(userLng);

                        strKm = String.format("%.02f", userPos.distanceTo(servicePos)/1000) + "km";
                    }
                }
                holder.txtDistance.setText(strKm);

                if (strCity.equals("")) {
                    if (!strState.equals("")) {
                        strPos = strState;
                    }
                } else {
                    strPos = strCity + ", " + strState;
                }

                holder.txtAddress.setText(strPos);
            }else{
                holder.txtAddress.setText("Online");
            }
        }
    }

    @Override
    public int getItemCount() {
        return serviceArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout layoutParent;
        private ImageView imgUser;
        private TextView txtServiceDesc,txtServicePrice,txtPriceDesc,txtName,txtDistance,txtAddress,txtPromated,txtFlag,txtTick,txtTime;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;
        LinearLayout llPromoted;
        public MyViewHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            layoutParent=(LinearLayout)itemView.findViewById(R.id.move_to_service_detail);
            txtServiceDesc=(TextView)itemView.findViewById(R.id.txt_service_desc);
            txtServicePrice=(TextView)itemView.findViewById(R.id.txt_service_price);
            txtPriceDesc=(TextView)itemView.findViewById(R.id.txt_price_desc);
            txtName=(TextView)itemView.findViewById(R.id.txt_name);
            txtAddress=(TextView)itemView.findViewById(R.id.txt_address);
            txtDistance=(TextView)itemView.findViewById(R.id.txt_distance);
            txtPromated=(TextView)itemView.findViewById(R.id.txt_service_type);
            llPromoted=(LinearLayout) itemView.findViewById(R.id.ll_promoted);
            imgUser=(ImageView)itemView.findViewById(R.id.img_user);
            txtFlag=(TextView)itemView.findViewById(R.id.txt_flag);
            txtTick=(TextView)itemView.findViewById(R.id.txt_tick);
            txtTime=(TextView)itemView.findViewById(R.id.txt_time);
            layoutParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listenerRef != null)
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }
}