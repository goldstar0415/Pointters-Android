package com.pointters.adapter;

import android.content.Context;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.GeoJsonModel;
import com.pointters.model.ServiceDetailModel;
import com.pointters.utils.AndroidUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by prashantkumar on 15/11/17.
 */

public class RelatedServiceAdapter extends RecyclerView.Adapter<RelatedServiceAdapter.MyViewHolder> {

    private Context context;
    private List<ServiceDetailModel> serviceArrayList;
    private Double userLat;
    private Double userLng;
    private OnRecyclerViewButtonClickListener listener;

    public RelatedServiceAdapter(Context context, List<ServiceDetailModel> serviceArrayList, Double lat, Double lng, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.serviceArrayList = serviceArrayList;
        this.userLat = lat;
        this.userLng = lng;
        this.listener = listener;
    }

    @Override
    public RelatedServiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_service_card, parent, false);
        return new RelatedServiceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RelatedServiceAdapter.MyViewHolder holder, int position) {
//        if (position == 0) {
//            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp), (int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
//            holder.moveToServiceScreeen.setLayoutParams(holder.layoutParams);
//        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        if (serviceArrayList.get(position).getSeller() != null) {
            if (serviceArrayList.get(position).getSeller().getFirstName() != null && !serviceArrayList.get(position).getSeller().getFirstName().isEmpty()) {
                holder.txtName.setText(serviceArrayList.get(position).getSeller().getFirstName() + " " + serviceArrayList.get(position).getSeller().getLastName());
            } else {
                holder.txtName.setText("NA");
            }
        }

        if (serviceArrayList.get(position).getService() != null) {
            if (serviceArrayList.get(position).getService().getMedia() != null && serviceArrayList.get(position).getService().getMedia().size() > 0) {
                if (serviceArrayList.get(position).getService().getMedia().get(0).getFileName() != null && !serviceArrayList.get(position).getService().getMedia().get(0).getFileName().isEmpty()) {
                    String strPic = serviceArrayList.get(position).getService().getMedia().get(0).getFileName();
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

            if (serviceArrayList.get(position).getService().getPrices() != null && !serviceArrayList.get(position).getService().getPrices().isEmpty()) {
                String strSymbol = "$";
                if (serviceArrayList.get(position).getService().getPrices().get(0).getCurrencySymbol() != null && !serviceArrayList.get(position).getService().getPrices().get(0).getCurrencySymbol().isEmpty()) {
                    strSymbol = serviceArrayList.get(position).getService().getPrices().get(0).getCurrencySymbol();
                }
                int valPrice = 0;
                if (serviceArrayList.get(position).getService().getPrices().get(0).getPrice() != null && serviceArrayList.get(position).getService().getPrices().get(0).getPrice() > 0) {
                    valPrice = serviceArrayList.get(position).getService().getPrices().get(0).getPrice();
                }
                holder.txtServicePrice.setText(strSymbol + String.valueOf(valPrice));

                int valDiscount = 0;
                if (serviceArrayList.get(position).getService().getPrices().get(0).getPriceWithoutDiscount() != null && serviceArrayList.get(position).getService().getPrices().get(0).getPriceWithoutDiscount() > 0) {
                    valDiscount = serviceArrayList.get(position).getService().getPrices().get(0).getPriceWithoutDiscount();
                }
//                if (valDiscount > 0) {
//                    holder.txtServiceActualPrice.setVisibility(View.VISIBLE);
//                    holder.txtServiceActualPrice.setText(strSymbol + String.valueOf(valDiscount));
//                } else {
//                    holder.txtServiceActualPrice.setVisibility(View.GONE);
//                }

                int valTime = 0;
                if (serviceArrayList.get(position).getService().getPrices().get(0).getTime() != null && serviceArrayList.get(position).getService().getPrices().get(0).getTime() > 0) {
                    valTime = serviceArrayList.get(position).getService().getPrices().get(0).getTime();
                }
                String strUnit = "";
                if (serviceArrayList.get(position).getService().getPrices().get(0).getTimeUnitOfMeasure() != null && !serviceArrayList.get(position).getService().getPrices().get(0).getTimeUnitOfMeasure().isEmpty()) {
                    strUnit = serviceArrayList.get(position).getService().getPrices().get(0).getTimeUnitOfMeasure();
                }

                if (strUnit.equals("hour")) {
                    strUnit = "hr";
                }

                if (valTime > 0) {
                    holder.txtPriceDesc.setText("per " + String.valueOf(valTime) + " " + strUnit + "s");
                } else {
                    holder.txtPriceDesc.setText("per " + String.valueOf(valTime) + " " + strUnit);
                }
            }

            if (serviceArrayList.get(position).getService().isPromoted()) {
                holder.llpromoted.setVisibility(View.VISIBLE);
            } else {
                holder.llpromoted.setVisibility(View.GONE);
            }

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

            int valPoint = 0;
            if (serviceArrayList.get(position).getService().getPointValue() != null && serviceArrayList.get(position).getService().getPointValue() > 0) {
                valPoint = serviceArrayList.get(position).getService().getPointValue();
            }
            holder.txtFlag.setText(String.valueOf(valPoint));

            int valOrders = 0;
            if (serviceArrayList.get(position).getService().getNumOrders() != null && serviceArrayList.get(position).getService().getNumOrders() > 0) {
                valOrders = serviceArrayList.get(position).getService().getNumOrders();
            }
            holder.txtTick.setText(String.valueOf(valOrders));

            float valRating = 0.0f;
            if (serviceArrayList.get(position).getService().getAvgRating() != null && serviceArrayList.get(position).getService().getAvgRating() > 0) {
                valRating = serviceArrayList.get(position).getService().getAvgRating();
            }
            holder.txtTime.setText(String.valueOf(valRating) + "%");
        }
    }

    @Override
    public int getItemCount() {
        return serviceArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout moveToServiceScreeen;
        private FrameLayout.LayoutParams layoutParams;
        private ImageView imgUser;
        private TextView txtServiceDesc,txtServicePrice,txtServiceActualPrice,txtPriceDesc,txtName,txtDistance,txtAddress,txtPromated,txtFlag,txtTick,txtTime;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;
        private LinearLayout llpromoted;
        public MyViewHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            moveToServiceScreeen = (LinearLayout) itemView.findViewById(R.id.move_to_service_detail);
            layoutParams = (FrameLayout.LayoutParams) moveToServiceScreeen.getLayoutParams();
            txtServiceDesc=(TextView)itemView.findViewById(R.id.txt_service_desc);
            txtServicePrice=(TextView)itemView.findViewById(R.id.txt_service_price);
            txtServiceActualPrice=(TextView)itemView.findViewById(R.id.txt_service_actual_price);
            txtPriceDesc=(TextView)itemView.findViewById(R.id.txt_price_desc);
            txtName=(TextView)itemView.findViewById(R.id.txt_name);
            txtDistance=(TextView)itemView.findViewById(R.id.txt_distance);
            txtAddress=(TextView)itemView.findViewById(R.id.txt_address);
            txtPromated=(TextView)itemView.findViewById(R.id.txt_service_type);
            imgUser=(ImageView)itemView.findViewById(R.id.img_user);
            txtFlag=(TextView)itemView.findViewById(R.id.txt_flag);
            txtTick=(TextView)itemView.findViewById(R.id.txt_tick);
            txtTime=(TextView)itemView.findViewById(R.id.txt_time);
            llpromoted = (LinearLayout) itemView.findViewById(R.id.ll_promoted);
            txtName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }
}
