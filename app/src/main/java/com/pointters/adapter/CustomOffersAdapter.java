package com.pointters.adapter;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
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
import com.pointters.model.GeoJsonModel;
import com.pointters.model.ReceivedOfferModel;
import com.pointters.utils.GPSTracker;
import com.pointters.utils.SquareImageView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by prashantkumar on 26/9/17.
 */

public class CustomOffersAdapter extends RecyclerView.Adapter<CustomOffersAdapter.MyViewHolder> {
    private Context context;
    private double userLat;
    private double userLng;
    private List<ReceivedOfferModel> receivedOffersList;
    private OnRecyclerViewButtonClickListener listener;

    public CustomOffersAdapter(Context context, List<ReceivedOfferModel> receivedOffersList, double lat, double lng, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.userLat = lat;
        this.userLng = lng;
        this.receivedOffersList = receivedOffersList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_custom_offers, parent, false);
        return new CustomOffersAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
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
        if (receivedOffersList != null && receivedOffersList.size() > 0) {
            if (receivedOffersList.get(position).getMedia() != null) {
                if (receivedOffersList.get(position).getMedia().getFileName() != null && !receivedOffersList.get(position).getMedia().getFileName().isEmpty()) {
                    String strPic = receivedOffersList.get(position).getMedia().getFileName();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
//                        strPic = "https://s3.amazonaws.com" + strPic;
                    }
                    ImageLoader.getInstance().displayImage(strPic, holder.imgBuyer, options);
                }
            }

            if (receivedOffersList.get(position).getSeller()!=null) {
                if (receivedOffersList.get(position).getSeller().getFirstName() != null && !receivedOffersList.get(position).getSeller().getFirstName().isEmpty())
                    holder.txtBuyerName.setText(receivedOffersList.get(position).getSeller().getFirstName());
                else
                    holder.txtBuyerName.setText("");

//                if (receivedOffersList.get(position).getSeller().getPhone() != null && !receivedOffersList.get(position).getSeller().getPhone().isEmpty()) {
//                    holder.btnCall.setVisibility(View.VISIBLE);
//                } else {
//                    holder.btnCall.setVisibility(View.INVISIBLE);
//                }
            }

            if (receivedOffersList.get(position).getServiceDescription() != null && !receivedOffersList.get(position).getServiceDescription().isEmpty() ) {
                holder.txtServiceDesc.setText(receivedOffersList.get(position).getServiceDescription());
            }
            else {
                holder.txtServiceDesc.setText("");
            }

            if (receivedOffersList.get(position).getPrice() != null)
                holder.txtPrice.setText("$"+String.valueOf(receivedOffersList.get(position).getPrice()));
            else
                holder.txtPrice.setText("");

            if (receivedOffersList.get(position).getWorkDuration() != null && !receivedOffersList.get(position).getWorkDurationUom().isEmpty()) {
                if (receivedOffersList.get(position).getWorkDuration() > 1) {
                    holder.txtPriceDesc.setText("for " + String.valueOf(receivedOffersList.get(position).getWorkDuration()) + " " + receivedOffersList.get(position).getWorkDurationUom() + "s");
                } else {
                    holder.txtPriceDesc.setText("for " + String.valueOf(receivedOffersList.get(position).getWorkDuration()) + " " + receivedOffersList.get(position).getWorkDurationUom());
                }
            } else
                holder.txtPriceDesc.setText("");

            if (receivedOffersList.get(position).getCreatedAt() != null && !receivedOffersList.get(position).getCreatedAt().isEmpty()) {
                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(tz);

                SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy  hh:mm a");
                try {
                    holder.txtCreateddate.setText("Posted on "+ String.valueOf(fmtOut.format(df.parse(receivedOffersList.get(position).getCreatedAt()))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else
                holder.txtCreateddate.setText("");

            if (receivedOffersList.get(position).getLocation() != null) {
                String strCity = "", strState = "", strPos="", strKm="NA";
                if (receivedOffersList.get(position).getLocation().getCity() != null && !receivedOffersList.get(position).getLocation().getCity().equals(""))
                    strCity = receivedOffersList.get(position).getLocation().getCity();
                if (receivedOffersList.get(position).getLocation().getState() != null && !receivedOffersList.get(position).getLocation().getState().equals(""))
                    strState = receivedOffersList.get(position).getLocation().getState();
                if (receivedOffersList.get(position).getLocation().getGeoJson() != null) {
                    GeoJsonModel geoJson = receivedOffersList.get(position).getLocation().getGeoJson();
                    if (geoJson.getCoordinates() != null && geoJson.getCoordinates().size() > 0) {
                        Location servicePos = new Location("");
                        servicePos.setLatitude(geoJson.getCoordinates().get(1));
                        servicePos.setLongitude(geoJson.getCoordinates().get(0));

//                        Location userPos = new Location("");
                        Location userPos = new GPSTracker(context).getLoc();
//                        userPos.setLatitude(userLat);
//                        userPos.setLongitude(userLng);

                        if (userPos != null)
                        strKm = String.format("%.02f", userPos.distanceTo(servicePos)/1000) + "km";
                    }
                }

                if (strCity.equals("")) {
                    if (!strState.equals("")) {
                        strPos = strKm + "@" + strState;
                    } else {
                        strPos = strKm;
                    }
                } else {
                    strPos = strKm + "@" + strCity + ", " + strState;
                }

//                holder.txtPosition.setText(strPos);
            }
        }
    }

    @Override
    public int getItemCount() {
        return receivedOffersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private SquareImageView imgBuyer;
        private ImageView btnChat, btnCall;
        private TextView txtServiceDesc, txtPrice,txtPriceDesc,txtCreateddate,txtBuyerName;
        Button btnAccept;
        private LinearLayout layoutParent;
        RelativeLayout layoutUpper;
        private FrameLayout.LayoutParams layoutParams;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;

        public MyViewHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            layoutParent=(LinearLayout) itemView.findViewById(R.id.layout_parent);
            layoutParams=(FrameLayout.LayoutParams)layoutParent.getLayoutParams();

            imgBuyer=(SquareImageView)itemView.findViewById(R.id.img_service_provider);
            txtServiceDesc=(TextView)itemView.findViewById(R.id.txt_service_name);
//            txtPosition=(TextView)itemView.findViewById(R.id.txt_second_service_desc);
            txtPrice=(TextView)itemView.findViewById(R.id.txt_service_price);
            txtPriceDesc=(TextView)itemView.findViewById(R.id.txt_price_desc);
            txtCreateddate=(TextView)itemView.findViewById(R.id.txt_offer_created_date);

            txtBuyerName=(TextView)itemView.findViewById(R.id.txt_buyer_name);
            txtBuyerName.setOnClickListener(this);

            layoutUpper=(RelativeLayout)itemView.findViewById(R.id.upper_view);
            layoutUpper.setOnClickListener(this);

            btnAccept=(Button)itemView.findViewById(R.id.offer_btn_accept);
            btnAccept.setOnClickListener(this);

            btnChat =(ImageView)itemView.findViewById(R.id.img_chat_btn);
            btnChat.setOnClickListener(this);

            btnCall =(ImageView)itemView.findViewById(R.id.img_call_btn);
            btnCall.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }
}
