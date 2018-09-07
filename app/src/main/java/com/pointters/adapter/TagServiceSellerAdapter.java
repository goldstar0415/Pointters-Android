package com.pointters.adapter;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.model.GeoJsonModel;
import com.pointters.model.TagServiceSellerModel;

import java.util.List;

/**
 * Created by prashantkumar on 20/9/17.
 */

public class TagServiceSellerAdapter extends RecyclerView.Adapter<TagServiceSellerAdapter.MyViewHolder> {

    private Context context;
    private double userLat;
    private double userLng;
    private List<TagServiceSellerModel> tagServiceSellerList;

    public TagServiceSellerAdapter(Context context, List<TagServiceSellerModel> tagServiceSellerList, double lat, double lng) {
        this.context = context;
        this.userLat = lat;
        this.userLng = lng;
        this.tagServiceSellerList = tagServiceSellerList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_service_tag, parent, false);
        return new MyViewHolder(view);
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

        if (tagServiceSellerList != null && tagServiceSellerList.size() > 0) {
            TagServiceSellerModel model = tagServiceSellerList.get(position);

            if (model.getService() != null) {

                if (model.getService().getMedia().size() > 0) {
                    String strPic = model.getService().getMedia().get(0).getFileName();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
//                            strPic = "https://s3.amazonaws.com" + strPic;
                    }
                    ImageLoader.getInstance().displayImage(strPic, holder.imgTagService, options);
                }

                if (model.getService().getDescription() != null) {
                    holder.txtTagName.setText(model.getService().getDescription());
                } else {
                    holder.txtTagName.setText("");
                }

                holder.txtName.setText(model.getService().getSeller().getFirstName());
                holder.txtFlag.setText(String.valueOf(model.getService().getNumOrders()));
                holder.txtTick.setText(String.valueOf(model.getService().getAvgRating()));
                holder.txtTime.setText(String.valueOf(model.getService().getPointValue()));
//                holder.txtServicePrice.setText("");
//                holder.txtServiceDesc.setText("");

                holder.txtStatus.setText("By:");
            } else {

                String strPic = model.getUser().getProfilePic();
                if (!strPic.contains("https://s3.amazonaws.com")) {
                    strPic = "https://s3.amazonaws.com" + strPic;
                }
                ImageLoader.getInstance().displayImage(strPic, holder.imgTagService, options);

                holder.txtTagName.setText(model.getUser().getFirstName() + " " + model.getUser().getLastName());

                holder.txtName.setText(model.getUser().getLocation().getProvince());

                holder.txtFlag.setText(String.valueOf(model.getUser().getNumOrders()));
                holder.txtTick.setText(String.valueOf(model.getUser().getAvgRating()));
                holder.txtTime.setText(String.valueOf(model.getUser().getPointValue()));

                holder.txtStatus.setText("@ ");
            }
        }
    }

    @Override
    public int getItemCount() {
        return tagServiceSellerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgTagService;
        private TextView txtTagName, txtName, txtFlag, txtTick, txtTime, txtServicePrice, txtServiceDesc, txtStatus;
        private CardView layoutRoot;
        private ImageView deleteButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            layoutRoot=(CardView) itemView.findViewById(R.id.card_view);
            imgTagService = (ImageView) itemView.findViewById(R.id.img_tag_service);
            txtTagName = (TextView) itemView.findViewById(R.id.txt_tag_name);

            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtFlag = (TextView) itemView.findViewById(R.id.txt_flag);
            txtTick = (TextView) itemView.findViewById(R.id.txt_tick);
            txtTime = (TextView) itemView.findViewById(R.id.txt_time);
            txtServicePrice = (TextView) itemView.findViewById(R.id.txt_service_price);
            txtServiceDesc = (TextView) itemView.findViewById(R.id.txt_price_desc);

            txtStatus = (TextView) itemView.findViewById(R.id.txt_status);

            deleteButton = (ImageView) itemView.findViewById(R.id.delete_tag_button);
            deleteButton.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                }
            });
        }
    }
}
