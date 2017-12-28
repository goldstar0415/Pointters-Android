package com.pointters.adapter;

import android.content.Context;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.model.ServiceDetailModel;
import com.pointters.utils.AndroidUtils;

import java.util.List;

/**
 * Created by prashantkumar on 15/11/17.
 */

public class RelatedServiceAdapter extends RecyclerView.Adapter<RelatedServiceAdapter.MyViewHolder> {
    private Context context;
    private List<ServiceDetailModel> serviceArrayList;
    private Location location = null;

    public RelatedServiceAdapter(Context context, List<ServiceDetailModel> serviceArrayList, Location location) {
        this.context = context;
        this.serviceArrayList = serviceArrayList;
        this.location = location;
    }

    @Override
    public RelatedServiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_service, parent, false);
        return new RelatedServiceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RelatedServiceAdapter.MyViewHolder holder, int position) {
        if (position == 0) {
            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp), (int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
            holder.moveToServiceScreeen.setLayoutParams(holder.layoutParams);
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        if (serviceArrayList.get(position).getSeller().getProfilePic() != null && !serviceArrayList.get(position).getSeller().getProfilePic().isEmpty())
        {
          final   String profileUrl;

            if (serviceArrayList.get(position).getSeller().getProfilePic().startsWith("https://s3.amazonaws.com")) {
                profileUrl = serviceArrayList.get(position).getSeller().getProfilePic();
            } else {
                profileUrl = "https://s3.amazonaws.com" +serviceArrayList.get(position).getSeller().getProfilePic();
            }

            ImageLoader.getInstance().displayImage(profileUrl, holder.imgUser, options);
        }


        if (serviceArrayList.get(position).getSeller().getFirstName() != null && !serviceArrayList.get(position).getSeller().getFirstName().isEmpty())
            holder.txtName.setText(serviceArrayList.get(position).getSeller().getFirstName());
        else
            holder.txtName.setText("NA");

        if (serviceArrayList.get(position).getService().getDescription() != null && !serviceArrayList.get(position).getService().getDescription().isEmpty())
            holder.txtServiceDesc.setText(serviceArrayList.get(position).getService().getDescription());
        else
            holder.txtServiceDesc.setText("NA");

        if (serviceArrayList.get(position).getService().getPrices() != null && !serviceArrayList.get(position).getService().getPrices().isEmpty()) {
            if (serviceArrayList.get(position).getService().getPrices().get(0).getPrice() != null)
                holder.txtServicePrice.setText("$" + String.valueOf(serviceArrayList.get(position).getService().getPrices().get(0).getPrice()));
            else
                holder.txtPromated.setVisibility(View.GONE);

            if (!serviceArrayList.get(position).getService().getPrices().get(0).getDescription().isEmpty())
                holder.txtPriceDesc.setText("For " + serviceArrayList.get(position).getService().getPrices().get(0).getDescription());
            else
                holder.txtPromated.setVisibility(View.GONE);

            if(serviceArrayList.get(position).getService().getPrices().get(0).getPriceWithoutDiscount()!=null )
                holder.txtServiceActualPrice.setText("$"+String.valueOf(serviceArrayList.get(position).getService().getPrices().get(0).getPriceWithoutDiscount()));
            else
                holder.txtServiceActualPrice.setVisibility(View.GONE);
        }

        if(serviceArrayList.get(position).getService().isPromoted())
            holder.txtPromated.setVisibility(View.VISIBLE);
        else
            holder.txtPromated.setVisibility(View.GONE);

        if(serviceArrayList.get(position).getService().getLocation()!=null && serviceArrayList.get(position).getService().getLocation().get(0).getGeoJson()!=null){
            if(!serviceArrayList.get(position).getService().getLocation().get(0).getCountry().isEmpty() && !serviceArrayList.get(position).getService().getLocation().get(0).getCity().isEmpty()){
                holder.txtAddress.setText(serviceArrayList.get(position).getService().getLocation().get(0).getCity()+", "+serviceArrayList.get(position).getService().getLocation().get(0).getCountry());
            }
            else
                holder.txtAddress.setText("NA");

            if(serviceArrayList.get(position).getService().getLocation().get(0).getGeoJson()!=null && !serviceArrayList.get(position).getService().getLocation().get(0).getGeoJson().getCoordinates().isEmpty() ){
               if(location!=null)
               {
                   Double distance=AndroidUtils.getDistanceBwTwoLocation(location.getLatitude(),location.getLongitude(),serviceArrayList.get(position).getService().getLocation().get(0).getGeoJson().getCoordinates().get(1),serviceArrayList.get(position).getService().getLocation().get(0).getGeoJson().getCoordinates().get(0));
               if(distance!=null)
                   holder.txtDistance.setText(String.valueOf( Math.round(distance) +" Km"));
               }else
                   holder.txtDistance.setText("NA");
            }
        }

        if (serviceArrayList.get(position).getService().getNumOrders()!=null && !String.valueOf(serviceArrayList.get(position).getService().getNumOrders()).isEmpty())
            holder.txtTick.setText(String.valueOf(serviceArrayList.get(position).getService().getNumOrders()));
        else
            holder.txtTick.setText("NA");

        if (serviceArrayList.get(position).getService().getPointValue()!=null && !String.valueOf(serviceArrayList.get(position).getService().getPointValue()).isEmpty())
            holder.txtFlag.setText(String.valueOf(serviceArrayList.get(position).getService().getPointValue()));
        else
            holder.txtFlag.setText("NA");

        if (serviceArrayList.get(position).getService().getAvgRating()!=null && !String.valueOf(serviceArrayList.get(position).getService().getAvgRating()).isEmpty())
            holder.txtTime.setText(String.valueOf(serviceArrayList.get(position).getService().getAvgRating()));
        else
            holder.txtTime.setText("NA");
    }

    @Override
    public int getItemCount() {
        return serviceArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout moveToServiceScreeen;
        private ConstraintLayout.LayoutParams layoutParams;
        private ImageView imgUser;
        private TextView txtServiceDesc,txtServicePrice,txtServiceActualPrice,txtPriceDesc,txtName,txtDistance,txtAddress,txtPromated,txtFlag,txtTick,txtTime;
        public MyViewHolder(View itemView) {
            super(itemView);
            moveToServiceScreeen = (ConstraintLayout) itemView.findViewById(R.id.move_to_service_detail);
            layoutParams = (ConstraintLayout.LayoutParams) moveToServiceScreeen.getLayoutParams();
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
        }
    }
}
