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
import com.pointters.model.SentOfferModel;

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
    private List<SentOfferModel> sentOffersList;

    public OffersAdapter(Context context, List<SentOfferModel> sentOffersList) {
        this.context = context;
        this.sentOffersList = sentOffersList;
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
        if(position==0) {
            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
            holder.layoutParent.setLayoutParams(holder.layoutParams);
        }
        if(sentOffersList!=null && sentOffersList.size()>0)
        {
            if(sentOffersList.get(position).getBuyer()!=null) {
                if (sentOffersList.get(position).getBuyer().getProfilePic() != null && !sentOffersList.get(position).getBuyer().getProfilePic().isEmpty())
                    ImageLoader.getInstance().displayImage(sentOffersList.get(position).getBuyer().getProfilePic(), holder.imgSeller, options);

                if (sentOffersList.get(position).getBuyer().getFirstName() != null && !sentOffersList.get(position).getBuyer().getFirstName().isEmpty())
                    holder.txtSellerName.setText(sentOffersList.get(position).getBuyer().getFirstName());
                else
                    holder.txtSellerName.setText("NA");

            }
            if(sentOffersList.get(position).getServiceDescription()!=null && !sentOffersList.get(position).getServiceDescription().isEmpty() ) {
                holder.txtServiceDesc.setText(sentOffersList.get(position).getServiceDescription());
                holder.txtSubServiceDesc.setText(sentOffersList.get(position).getServiceDescription());
            }
            else {
                holder.txtServiceDesc.setText("NA");
                holder.txtSubServiceDesc.setText("NA");
            }

            if(sentOffersList.get(position).getPrice()!=null)
                holder.txtPrice.setText("$"+String.valueOf(sentOffersList.get(position).getPrice()));
            else
                holder.txtPrice.setText("NA");

            if(sentOffersList.get(position).getWorkDuration()!=null && !sentOffersList.get(position).getWorkDurationUom().isEmpty())
                holder.txtPriceDesc.setText("For "+String.valueOf(sentOffersList.get(position).getWorkDuration())+" "+sentOffersList.get(position).getWorkDurationUom());
            else
                holder.txtPriceDesc.setText("NA");

            if(sentOffersList.get(position).getCreatedAt()!=null && !sentOffersList.get(position).getCreatedAt().isEmpty()) {
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
                holder.txtCreateddate.setText("NA");

        }


    }

    @Override
    public int getItemCount() {
        return sentOffersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgSeller;
        private TextView txtServiceDesc,txtSubServiceDesc,txtPrice,txtPriceDesc,txtCreateddate,txtSellerName;
        private RelativeLayout layoutParent;
        private RelativeLayout.LayoutParams layoutParams;
        public MyViewHolder(View itemView) {
            super(itemView);
            layoutParent=(RelativeLayout)itemView.findViewById(R.id.layout_parent);
            layoutParams=( RelativeLayout.LayoutParams)layoutParent.getLayoutParams();

            imgSeller=(ImageView)itemView.findViewById(R.id.img_seller);
            txtServiceDesc=(TextView)itemView.findViewById(R.id.txt_service_desc);
            txtSubServiceDesc=(TextView)itemView.findViewById(R.id.txt_second_service_desc);
            txtPrice=(TextView)itemView.findViewById(R.id.txt_price);
            txtPriceDesc=(TextView)itemView.findViewById(R.id.txt_price_desc);
            txtCreateddate=(TextView)itemView.findViewById(R.id.txt_offer_created_date);
            txtSellerName=(TextView)itemView.findViewById(R.id.txt_seller_name);
        }
    }
}
