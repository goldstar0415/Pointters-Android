package com.pointters.adapter;

import android.content.Context;
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
import com.pointters.model.ReceivedOfferModel;

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
    private List<ReceivedOfferModel> receivedOffersList;

    public CustomOffersAdapter(Context context, List<ReceivedOfferModel> receivedOffersList) {
        this.context = context;
        this.receivedOffersList = receivedOffersList;
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

        if(position==0) {
            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
            holder.layoutParent.setLayoutParams(holder.layoutParams);
        }
        if(receivedOffersList!=null && receivedOffersList.size()>0)
        {
            if(receivedOffersList.get(position).getSeller()!=null) {
                if (receivedOffersList.get(position).getSeller().getProfilePic() != null && !receivedOffersList.get(position).getSeller().getProfilePic().isEmpty())
                    ImageLoader.getInstance().displayImage(receivedOffersList.get(position).getSeller().getProfilePic(), holder.imgBuyer, options);

                if (receivedOffersList.get(position).getSeller().getFirstName() != null && !receivedOffersList.get(position).getSeller().getFirstName().isEmpty())
                    holder.txtBuyerName.setText(receivedOffersList.get(position).getSeller().getFirstName());
                else
                    holder.txtBuyerName.setText("NA");

            }
            if(receivedOffersList.get(position).getServiceDescription()!=null && !receivedOffersList.get(position).getServiceDescription().isEmpty() ) {
                holder.txtServiceDesc.setText(receivedOffersList.get(position).getServiceDescription());
                holder.txtSubServiceDesc.setText(receivedOffersList.get(position).getServiceDescription());
            }
            else {
                holder.txtServiceDesc.setText("NA");
                holder.txtSubServiceDesc.setText("NA");
            }

            if(receivedOffersList.get(position).getPrice()!=null)
                holder.txtPrice.setText("$"+String.valueOf(receivedOffersList.get(position).getPrice()));
            else
                holder.txtPrice.setText("NA");

            if(receivedOffersList.get(position).getWorkDuration()!=null && !receivedOffersList.get(position).getWorkDurationUom().isEmpty())
            holder.txtPriceDesc.setText("For "+String.valueOf(receivedOffersList.get(position).getWorkDuration())+" "+receivedOffersList.get(position).getWorkDurationUom());
            else
                holder.txtPriceDesc.setText("NA");

            if(receivedOffersList.get(position).getCreatedAt()!=null && !receivedOffersList.get(position).getCreatedAt().isEmpty()) {
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
                holder.txtCreateddate.setText("NA");

        }

    }

    @Override
    public int getItemCount() {
        return receivedOffersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgBuyer;
        private TextView txtServiceDesc,txtSubServiceDesc,txtPrice,txtPriceDesc,txtCreateddate,txtBuyerName;
        private RelativeLayout layoutParent;
        private RelativeLayout.LayoutParams layoutParams;
        public MyViewHolder(View itemView) {
            super(itemView);
            layoutParent=(RelativeLayout)itemView.findViewById(R.id.layout_parent);
            layoutParams=( RelativeLayout.LayoutParams)layoutParent.getLayoutParams();

            imgBuyer=(ImageView)itemView.findViewById(R.id.img_buyer);
            txtServiceDesc=(TextView)itemView.findViewById(R.id.txt_service_desc);
            txtSubServiceDesc=(TextView)itemView.findViewById(R.id.txt_second_service_desc);
            txtPrice=(TextView)itemView.findViewById(R.id.txt_price);
            txtPriceDesc=(TextView)itemView.findViewById(R.id.txt_price_desc);
            txtCreateddate=(TextView)itemView.findViewById(R.id.txt_offer_created_date);
            txtBuyerName=(TextView)itemView.findViewById(R.id.txt_buyer_name);
        }
    }
}
