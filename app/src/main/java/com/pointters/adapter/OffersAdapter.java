package com.pointters.adapter;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.GeoJsonModel;
import com.pointters.model.SentOfferModel;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by prashantkumar on 23/8/17.
 */

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.MyViewHolder> {
    private Context context;
    private double userLat;
    private double userLng;
    private List<SentOfferModel> sentOffersList;
    private OnRecyclerViewButtonClickListener listener;

    public OffersAdapter(Context context, List<SentOfferModel> sentOffersList, double lat, double lng, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.userLat = lat;
        this.userLng = lng;
        this.sentOffersList = sentOffersList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_offers, parent, false);
        return new OffersAdapter.MyViewHolder(itemView);
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
        if (position == 0) {
            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
            holder.layoutParent.setLayoutParams(holder.layoutParams);
        }
        if (sentOffersList != null && sentOffersList.size() > 0) {
            if (sentOffersList.get(position).getMedia() != null) {
                if (sentOffersList.get(position).getMedia().getFileName() != null && !sentOffersList.get(position).getMedia().getFileName().isEmpty()) {
                    String strPic = sentOffersList.get(position).getMedia().getFileName();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
                        strPic = "https://s3.amazonaws.com" + strPic;
                    }
                    ImageLoader.getInstance().displayImage(strPic, holder.imgSeller, options);
                }
            }
            if (sentOffersList.get(position).getBuyer() != null) {
                if (sentOffersList.get(position).getBuyer().getFirstName() != null && !sentOffersList.get(position).getBuyer().getFirstName().isEmpty())
                    holder.txtSellerName.setText(sentOffersList.get(position).getBuyer().getFirstName());
                else
                    holder.txtSellerName.setText("");

                if (sentOffersList.get(position).getBuyer().getPhone() != null && !sentOffersList.get(position).getBuyer().getPhone().isEmpty()) {
                    holder.btnCall.setVisibility(View.VISIBLE);
                } else {
                    holder.btnCall.setVisibility(View.INVISIBLE);
                }
            }
            if (sentOffersList.get(position).getServiceDescription() != null && !sentOffersList.get(position).getServiceDescription().isEmpty() ) {
                holder.txtServiceDesc.setText(sentOffersList.get(position).getServiceDescription());
            }
            else {
                holder.txtServiceDesc.setText("");
            }

            if (sentOffersList.get(position).getPrice() != null)
                holder.txtPrice.setText("$"+String.valueOf(sentOffersList.get(position).getPrice()));
            else
                holder.txtPrice.setText("");

            if (sentOffersList.get(position).getWorkDuration() != null && !sentOffersList.get(position).getWorkDurationUom().isEmpty()) {
                if (sentOffersList.get(position).getWorkDuration() > 1) {
                    holder.txtPriceDesc.setText("for " + String.valueOf(sentOffersList.get(position).getWorkDuration()) + " " + sentOffersList.get(position).getWorkDurationUom() + "s");
                } else {
                    holder.txtPriceDesc.setText("for " + String.valueOf(sentOffersList.get(position).getWorkDuration()) + " " + sentOffersList.get(position).getWorkDurationUom());
                }
            } else
                holder.txtPriceDesc.setText("");

            if (sentOffersList.get(position).getCreatedAt() != null && !sentOffersList.get(position).getCreatedAt().isEmpty()) {
                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(tz);

                SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy  hh:mm a");
                try {
                    holder.txtCreateddate.setText("Posted on "+ String.valueOf(fmtOut.format(df.parse(sentOffersList.get(position).getCreatedAt()))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else
                holder.txtCreateddate.setText("");

            if (sentOffersList.get(position).getLocation() != null) {
                String strCity = "", strState = "", strPos="", strKm="NA";
                if (sentOffersList.get(position).getLocation().getCity() != null && !sentOffersList.get(position).getLocation().getCity().equals(""))
                    strCity = sentOffersList.get(position).getLocation().getCity();
                if (sentOffersList.get(position).getLocation().getState() != null && !sentOffersList.get(position).getLocation().getState().equals(""))
                    strState = sentOffersList.get(position).getLocation().getState();
                if (sentOffersList.get(position).getLocation().getGeoJson() != null) {
                    GeoJsonModel geoJson = sentOffersList.get(position).getLocation().getGeoJson();
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
                        strPos = strKm + "@" + strState;
                    } else {
                        strPos = strKm;
                    }
                } else {
                    strPos = strKm + "@" + strCity + ", " + strState;
                }

                holder.txtPosition.setText(strPos);
            }
        }
    }

    @Override
    public int getItemCount() {
        return sentOffersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgSeller, btnChat, btnCall;
        private TextView txtServiceDesc,txtPosition,txtPrice,txtPriceDesc,txtCreateddate,txtSellerName, btnAccept;
        private RelativeLayout layoutParent, layoutUpper;
        private RelativeLayout.LayoutParams layoutParams;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;

        public MyViewHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            layoutParent = (RelativeLayout) itemView.findViewById(R.id.layout_parent);
            layoutParams = (RelativeLayout.LayoutParams) layoutParent.getLayoutParams();

            imgSeller = (ImageView) itemView.findViewById(R.id.img_seller);
            txtServiceDesc = (TextView) itemView.findViewById(R.id.txt_service_desc);
            txtPosition = (TextView) itemView.findViewById(R.id.txt_second_service_desc);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
            txtPriceDesc = (TextView) itemView.findViewById(R.id.txt_price_desc);
            txtCreateddate = (TextView) itemView.findViewById(R.id.txt_offer_created_date);

            txtSellerName = (TextView) itemView.findViewById(R.id.txt_seller_name);
            txtSellerName.setOnClickListener(this);

            layoutUpper = (RelativeLayout) itemView.findViewById(R.id.upper_view);
            layoutUpper.setOnClickListener(this);

            btnAccept = (TextView) itemView.findViewById(R.id.offer_btn_accept);
            btnAccept.setOnClickListener(this);

            btnChat = (ImageView) itemView.findViewById(R.id.img_chat_btn);
            btnChat.setOnClickListener(this);

            btnCall = (ImageView) itemView.findViewById(R.id.img_call_btn);
            btnCall.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }
}
