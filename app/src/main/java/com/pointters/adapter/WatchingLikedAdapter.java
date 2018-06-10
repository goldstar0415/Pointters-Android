package com.pointters.adapter;

import android.content.Context;
import android.location.Location;
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
import com.pointters.model.GeoJsonModel;
import com.pointters.model.ServicesModel;

import java.util.ArrayList;

/**
 * Created by mac on 12/19/17.
 */

public class WatchingLikedAdapter extends RecyclerView.Adapter<WatchingLikedAdapter.MyViewHolder> {
    private Context context;
    private double userLat;
    private double userLng;
    private ArrayList<ServicesModel> watchingLikedArrayList;


    public WatchingLikedAdapter(Context context, ArrayList<ServicesModel> watchingLikedArrayList, double lat, double lng) {
        this.context = context;
        this.userLat = lat;
        this.userLng = lng;
        this.watchingLikedArrayList = watchingLikedArrayList;
    }

    @Override
    public WatchingLikedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_service_card, parent, false);
        return new WatchingLikedAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WatchingLikedAdapter.MyViewHolder holder, final int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
//
//        if (position == 0) {
//            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
//            holder.layoutParent.setLayoutParams(holder.layoutParams);
//        }
        if (watchingLikedArrayList != null && watchingLikedArrayList.size() > 0) {
            if (watchingLikedArrayList.get(position).getService() != null) {
                if (watchingLikedArrayList.get(position).getService().getMedia() != null) {
                    if (watchingLikedArrayList.get(position).getService().getMedia().getFileName() != null && !watchingLikedArrayList.get(position).getService().getMedia().getFileName().isEmpty()) {
                        String strPic = watchingLikedArrayList.get(position).getService().getMedia().getFileName();
                        if (!strPic.contains("https://s3.amazonaws.com")) {
//                            strPic = "https://s3.amazonaws.com" + strPic;
                        }
                        ImageLoader.getInstance().displayImage(strPic, holder.imgService, options);
                    }
                }

                if (watchingLikedArrayList.get(position).getService().getDescription() != null && !watchingLikedArrayList.get(position).getService().getDescription().isEmpty()) {
                    holder.txtServiceDesc.setText(watchingLikedArrayList.get(position).getService().getDescription());
                } else {
                    holder.txtServiceDesc.setText("NA");
                }

                if (watchingLikedArrayList.get(position).getService().getPromoted() != null) {
                    if (watchingLikedArrayList.get(position).getService().getPromoted())
                        holder.llPromoted.setVisibility(View.VISIBLE);
                    else
                        holder.llPromoted.setVisibility(View.INVISIBLE);
                } else {
                    holder.llPromoted.setVisibility(View.INVISIBLE);
                }

                if (watchingLikedArrayList.get(position).getService().getPrices() != null) {
                    String strSymbol = "$";
                    if (watchingLikedArrayList.get(position).getService().getPrices().getCurrencySymbol() != null) {
                        strSymbol = watchingLikedArrayList.get(position).getService().getPrices().getCurrencySymbol();
                    }

                    if (watchingLikedArrayList.get(position).getService().getPrices().getPrice() != null)
                        holder.txtServicePrice.setText(strSymbol + String.valueOf(watchingLikedArrayList.get(position).getService().getPrices().getPrice()));
                    else
                        holder.txtServicePrice.setText(strSymbol + "0");

//                    if (watchingLikedArrayList.get(position).getService().getPrices().getPriceWithoutDiscount() != null)
//                        holder.txtServiceActualPrice.setText(strSymbol + String.valueOf(watchingLikedArrayList.get(position).getService().getPrices().getPriceWithoutDiscount()));
//                    else
//                        holder.txtServiceActualPrice.setText("");
//
                    if (watchingLikedArrayList.get(position).getService().getPrices().getTime() != null && !watchingLikedArrayList.get(position).getService().getPrices().getTimeUnitOfMeasure().isEmpty()) {
                        String timeunit = watchingLikedArrayList.get(position).getService().getPrices().getTimeUnitOfMeasure();
                        timeunit = timeunit.equals("hour") ? "hr": timeunit;
                        if (watchingLikedArrayList.get(position).getService().getPrices().getTime() > 1) {
                            holder.txtPriceDesc.setText("per " + String.valueOf(watchingLikedArrayList.get(position).getService().getPrices().getTime()) + " " + timeunit + "s");
                        } else {
                            holder.txtPriceDesc.setText("per " + String.valueOf(watchingLikedArrayList.get(position).getService().getPrices().getTime()) + " " + timeunit);
                        }
                    } else
                        holder.txtPriceDesc.setText("");
                }

                if (watchingLikedArrayList.get(position).getService().getLocation() != null) {
                    String strCity = "", strState = "", strPos="", strKm="NA";
                    if (watchingLikedArrayList.get(position).getService().getLocation().getCity() != null && !watchingLikedArrayList.get(position).getService().getLocation().getCity().equals(""))
                        strCity = watchingLikedArrayList.get(position).getService().getLocation().getCity();
                    if (watchingLikedArrayList.get(position).getService().getLocation().getState() != null && !watchingLikedArrayList.get(position).getService().getLocation().getState().equals(""))
                        strState = watchingLikedArrayList.get(position).getService().getLocation().getState();
                    if (watchingLikedArrayList.get(position).getService().getLocation().getGeoJson() != null) {
                        GeoJsonModel geoJson = watchingLikedArrayList.get(position).getService().getLocation().getGeoJson();
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

                    if (strCity.equals("")) {
                        if (!strState.equals("")) {
                            strPos = strKm + " " + strState;
                        } else {
                            strPos = strKm;
                        }
                    } else {
                        strPos = strKm + " " + strCity + ", " + strState;
                    }

                    holder.txtDistance.setText(strPos);
                    holder.txtAddress.setText("");
                }
            }

            if (watchingLikedArrayList.get(position).getUser()!=null) {
                if (watchingLikedArrayList.get(position).getUser().getFirstName() != null && !watchingLikedArrayList.get(position).getUser().getFirstName().isEmpty())
                    holder.txtName.setText(watchingLikedArrayList.get(position).getUser().getFirstName() + " " + watchingLikedArrayList.get(position).getUser().getLastName());
                else
                    holder.txtName.setText("");
            }

            if (!String.valueOf(watchingLikedArrayList.get(position).getPointValue()).isEmpty())
                holder.txtFlag.setText(String.valueOf(watchingLikedArrayList.get(position).getPointValue()));
            else
                holder.txtFlag.setText("NA");

            if (!String.valueOf(watchingLikedArrayList.get(position).getNumOrders()).isEmpty())
                holder.txtTick.setText(String.valueOf(watchingLikedArrayList.get(position).getNumOrders()));
            else
                holder.txtTick.setText("NA");

            if (!String.valueOf(watchingLikedArrayList.get(position).getAvgRating()).isEmpty())
                holder.txtTime.setText(String.format("%.1f", watchingLikedArrayList.get(position).getAvgRating()) + "%");
            else
                holder.txtTime.setText("NA");
        }
    }

    @Override
    public int getItemCount() {
        return watchingLikedArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgService;
        private TextView txtServiceDesc,txtServicePrice,txtPriceDesc,txtName,txtDistance,txtAddress,txtPromoted,txtFlag,txtTick,txtTime;
        LinearLayout llPromoted;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtServiceDesc=(TextView)itemView.findViewById(R.id.txt_service_desc);
            txtServicePrice=(TextView)itemView.findViewById(R.id.txt_service_price);
            txtPriceDesc=(TextView)itemView.findViewById(R.id.txt_price_desc);
            txtName=(TextView)itemView.findViewById(R.id.txt_name);
            txtDistance=(TextView)itemView.findViewById(R.id.txt_distance);
            txtAddress=(TextView)itemView.findViewById(R.id.txt_address);
            txtPromoted=(TextView)itemView.findViewById(R.id.txt_service_type);
            imgService=(ImageView)itemView.findViewById(R.id.img_user);
            txtFlag=(TextView)itemView.findViewById(R.id.txt_flag);
            txtTick=(TextView)itemView.findViewById(R.id.txt_tick);
            txtTime=(TextView)itemView.findViewById(R.id.txt_time);
            llPromoted = (LinearLayout) itemView.findViewById(R.id.ll_promoted);
        }
    }
}
