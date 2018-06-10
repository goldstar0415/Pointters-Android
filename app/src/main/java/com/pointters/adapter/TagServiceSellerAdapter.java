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
                if (model.getType() != null && model.getType().equals("user")) {
                    holder.imgTagService.setVisibility(View.GONE);
//                    holder.imgTagUser.setVisibility(View.VISIBLE);

                    if (tagServiceSellerList.get(position).getService().getMedia().size() > 0 && !tagServiceSellerList.get(position).getService().getMedia().isEmpty()) {

                        String strPic = model.getService().getMedia().get(0).getFileName();
                        if (!strPic.contains("https://s3.amazonaws.com")) {
//                            strPic = "https://s3.amazonaws.com" + strPic;
                        }
//                        ImageLoader.getInstance().displayImage(strPic, holder.imgTagUser, options);
                    }

                    holder.txtTagName.setText(model.getService().getSeller().getFirstName() + " " + model.getService().getSeller().getLastName());
                }
                else {
//                    holder.imgTagUser.setVisibility(View.GONE);
                    holder.imgTagService.setVisibility(View.VISIBLE);

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
                }

                if (model.getService().getLocation() != null) {
                    String strCity = "", strState = "", strPos="", strKm="NA";
                    if (model.getService().getLocation().getCity() != null && !model.getService().getLocation().getCity().equals(""))
                        strCity = model.getService().getLocation().getCity();
                    if (model.getService().getLocation().getState() != null && !model.getService().getLocation().getState().equals(""))
                        strState = model.getService().getLocation().getState();
                    if (model.getService().getLocation().getGeoJson() != null) {
                        GeoJsonModel geoJson = model.getService().getLocation().getGeoJson();
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

//                    holder.txtTagPos.setText(strPos);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return tagServiceSellerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgTagService;
        private TextView txtTagName;
        private CardView layoutRoot;
        private ImageView deleteButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            layoutRoot=(CardView) itemView.findViewById(R.id.card_view);
            imgTagService = (ImageView) itemView.findViewById(R.id.img_tag_service);
            txtTagName = (TextView) itemView.findViewById(R.id.txt_tag_name);
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
