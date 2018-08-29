package com.pointters.adapter;

import android.content.Context;
import android.location.Location;
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
import com.pointters.model.ServicesModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class ProfileServicesAdapter extends RecyclerView.Adapter<ProfileServicesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ServicesModel> serviceArrayList;
    private String userName;
    private double userLat;
    private double userLng;
    private OnRecyclerViewButtonClickListener listener;

    public ProfileServicesAdapter(Context context, ArrayList<ServicesModel> serviceArrayList) {
        this.context = context;
        this.serviceArrayList = serviceArrayList;
    }
    public void setData(ArrayList<ServicesModel> models) {
        this.serviceArrayList = models;
    }

    public ProfileServicesAdapter(Context context, ArrayList<ServicesModel> serviceArrayList, double lat, double lng, String userName, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.serviceArrayList = serviceArrayList;
        this.userName = userName;
        this.userLat = lat;
        this.userLng = lng;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_service_card, parent, false);
        return new ProfileServicesAdapter.MyViewHolder(itemView);
    }

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

        if (serviceArrayList.get(position).getService() != null) {
            if (serviceArrayList.get(position).getService().getMedia() != null) {
                if (serviceArrayList.get(position).getService().getMedia().getFileName() != null && !serviceArrayList.get(position).getService().getMedia().getFileName().isEmpty()) {
                    String strPic = serviceArrayList.get(position).getService().getMedia().getFileName();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
//                        strPic = "https://s3.amazonaws.com" + strPic;
                    }
                    ImageLoader.getInstance().displayImage(strPic, holder.imgUser, options);
                }
            }

            if (serviceArrayList.get(position).getService().getDescription() != null && !serviceArrayList.get(position).getService().getDescription().isEmpty()) {
                holder.txtServiceDesc.setText(serviceArrayList.get(position).getService().getDescription());
            } else {
                holder.txtServiceDesc.setText("NA");
            }

            if (serviceArrayList.get(position).getService().getPromoted() != null && serviceArrayList.get(position).getService().getPromoted()) {
                holder.txtPromated.setVisibility(View.VISIBLE);
            } else {
                holder.txtPromated.setVisibility(View.GONE);
            }

            if (serviceArrayList.get(position).getService().getPrices() != null) {
                String strSymbol = "$";
                if (serviceArrayList.get(position).getService().getPrices().getCurrencySymbol() != null && !serviceArrayList.get(position).getService().getPrices().getCurrencySymbol().isEmpty()) {
                    strSymbol = serviceArrayList.get(position).getService().getPrices().getCurrencySymbol();
                }
                float valPrice = 0;
                if (serviceArrayList.get(position).getService().getPrices().getPrice() != null && serviceArrayList.get(position).getService().getPrices().getPrice() > 0) {
                    valPrice = serviceArrayList.get(position).getService().getPrices().getPrice();
                }
                holder.txtServicePrice.setText(strSymbol + String.valueOf(valPrice));

                float valPriceDiscount = 0;
                if (serviceArrayList.get(position).getService().getPrices().getPriceWithoutDiscount() != null && serviceArrayList.get(position).getService().getPrices().getPriceWithoutDiscount() > 0) {
                    valPriceDiscount = serviceArrayList.get(position).getService().getPrices().getPriceWithoutDiscount();
                }

                if (valPriceDiscount > 0) {
//                    holder.txtServicePrice.setVisibility(View.VISIBLE);
                    holder.txtServicePrice.setText(strSymbol + String.valueOf(valPriceDiscount));
                } else {
//                    holder.txtServicePrice.setVisibility(View.GONE);
                }

                int valTime = 0;
                if (serviceArrayList.get(position).getService().getPrices().getTime() != null && serviceArrayList.get(position).getService().getPrices().getTime() > 0) {
                    valTime = serviceArrayList.get(position).getService().getPrices().getTime();
                }
                String strUnit = "";
                if (serviceArrayList.get(position).getService().getPrices().getTimeUnitOfMeasure() != null && !serviceArrayList.get(position).getService().getPrices().getTimeUnitOfMeasure().isEmpty()) {
                    strUnit = serviceArrayList.get(position).getService().getPrices().getTimeUnitOfMeasure();
                    if (strUnit.equals("hour")) {
                        strUnit = "hr";
                    }
                }

                if (valTime > 0) {
                    holder.txtPriceDesc.setText("per " + String.valueOf(valTime) + " " + strUnit + "s");
                } else {
                    holder.txtPriceDesc.setText("per " + String.valueOf(valTime) + " " + strUnit);
                }
            }

            if (userName != null && !userName.isEmpty()) {
                holder.txtName.setText(userName);
            } else {
                holder.txtName.setText("NA");
            }

            int valPoint = 0;
            if (serviceArrayList.get(position).getPointValue() != null && serviceArrayList.get(position).getPointValue() > 0) {
                valPoint = serviceArrayList.get(position).getPointValue();
            }
            holder.txtFlag.setText(String.valueOf(valPoint));

            int valOrders = 0;
            if (serviceArrayList.get(position).getNumOrders() != null && serviceArrayList.get(position).getNumOrders() > 0) {
                valOrders = serviceArrayList.get(position).getNumOrders();
            }
            holder.txtTick.setText(String.valueOf(valOrders));

            float valRating = 0.0f;
            if (serviceArrayList.get(position).getAvgRating() != null && serviceArrayList.get(position).getAvgRating() > 0) {
                valRating = serviceArrayList.get(position).getAvgRating();
            }
            holder.txtTime.setText(String.valueOf(valRating) + "%");

            if (serviceArrayList.get(position).getService().getLocation() != null) {
                String strCity = "", strState = "", strPos="", strKm="NA";
                if (serviceArrayList.get(position).getService().getLocation().getCity() != null && !serviceArrayList.get(position).getService().getLocation().getCity().equals(""))
                    strCity = serviceArrayList.get(position).getService().getLocation().getCity();
                if (serviceArrayList.get(position).getService().getLocation().getState() != null && !serviceArrayList.get(position).getService().getLocation().getState().equals(""))
                    strState = serviceArrayList.get(position).getService().getLocation().getState();
                if (serviceArrayList.get(position).getService().getLocation().getGeoJson() != null) {
                    GeoJsonModel geoJson = serviceArrayList.get(position).getService().getLocation().getGeoJson();
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
        private TextView txtServiceDesc,txtServicePrice,txtPriceDesc,txtName,txtDistance,txtAddress,txtFlag,txtTick,txtTime;
        private LinearLayout txtPromated;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;

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
            txtPromated=(LinearLayout)itemView.findViewById(R.id.ll_promoted);
            imgUser=(ImageView)itemView.findViewById(R.id.img_user);
            txtFlag=(TextView)itemView.findViewById(R.id.txt_flag);
            txtTick=(TextView)itemView.findViewById(R.id.txt_tick);
            txtTime=(TextView)itemView.findViewById(R.id.txt_time);
            layoutParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }
}