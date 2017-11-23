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
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.ServicesModel;
import com.pointters.utils.AndroidUtils;

import java.util.ArrayList;


public class ProfileServicesAdapter extends RecyclerView.Adapter<ProfileServicesAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<ServicesModel> serviceArrayList;

    private String profileUrl,name;
    private Location location=null;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public ProfileServicesAdapter(Context context, ArrayList<ServicesModel> serviceArrayList, String profileUrl, String name, Location location, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.serviceArrayList = serviceArrayList;
        this.profileUrl = profileUrl;
        this.name = name;
        this.location = location;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public ProfileServicesAdapter(Context context, ArrayList<ServicesModel> serviceArrayList) {
        this.context = context;
        this.serviceArrayList = serviceArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_service, parent, false);
        return new ProfileServicesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
     /*   if (position == 0) {
            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp), (int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
            holder.moveToServiceScreeen.setLayoutParams(holder.layoutParams);
        }
*/

     holder.moveToServiceScreeen.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             onRecyclerViewItemClickListener.onItemClick(position);
         }
     });
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        if ( serviceArrayList.get(position).getService() != null) {

            if(profileUrl!=null && !profileUrl.isEmpty())
            {


                    String profile;
                    if (profileUrl.startsWith("https://s3.amazonaws.com")) {
                        profile = profileUrl;
                    } else {
                        profile = "https://s3.amazonaws.com" +profileUrl;
                    }

                    ImageLoader.getInstance().displayImage(profile, holder.imgUser, options);

            }



                holder.txtPromated.setVisibility(View.GONE);

            if (name!=null && !name.isEmpty())
                holder.txtName.setText(name);
            else
                holder.txtName.setText("NA");

            if (!serviceArrayList.get(position).getService().getDescription().isEmpty())
                holder.txtServiceDesc.setText(serviceArrayList.get(position).getService().getDescription());
            else
                holder.txtServiceDesc.setText("NA");

            if(serviceArrayList.get(position).getService().getPrices()!=null) {

                if (!serviceArrayList.get(position).getService().getPrices().getDescription().isEmpty())
                    holder.txtPriceDesc.setText("For "+serviceArrayList.get(position).getService().getPrices().getDescription());
                else
                    holder.txtPriceDesc.setText("NA");

                if (!String.valueOf(serviceArrayList.get(position).getService().getPrices().getPrice()).isEmpty())
                    holder.txtServicePrice.setText("$"+String.valueOf(serviceArrayList.get(position).getService().getPrices().getPrice()));
                else
                    holder.txtServicePrice.setText("NA");

                if(serviceArrayList.get(position).getService().getPrices().getPriceWithoutDiscount()!=null)
                    holder.txtServiceActualPrice.setText(String.valueOf(serviceArrayList.get(position).getService().getPrices().getPriceWithoutDiscount()));
                else
                    holder.txtServiceActualPrice.setVisibility(View.GONE);
            }

            if (!String.valueOf(serviceArrayList.get(position).getPointValue()).isEmpty())
                holder.txtFlag.setText(String.valueOf(serviceArrayList.get(position).getPointValue()));
            else
                holder.txtFlag.setText("NA");

            if (!String.valueOf(serviceArrayList.get(position).getNumOrders()).isEmpty())
                holder.txtTick.setText(String.valueOf(serviceArrayList.get(position).getNumOrders()));
            else
                holder.txtTick.setText("NA");

            if (!String.valueOf(serviceArrayList.get(position).getAvgRating()).isEmpty())
                holder.txtTime.setText(String.valueOf(serviceArrayList.get(position).getAvgRating()));
            else
                holder.txtTime.setText("NA");

            if(serviceArrayList.get(position).getService().getLocation()!=null)
            { if(!serviceArrayList.get(position).getService().getLocation().get(0).getCity().isEmpty() && !serviceArrayList.get(position).getService().getLocation().get(0).getCountry().isEmpty() )
                holder.txtAddress.setText(serviceArrayList.get(position).getService().getLocation().get(0).getCity()+", "+serviceArrayList.get(position).getService().getLocation().get(0).getCountry());
            else
                holder.txtAddress.setText("NA");

                if(serviceArrayList.get(position).getService().getLocation().get(0).getGeoJson()!=null && !serviceArrayList.get(position).getService().getLocation().get(0).getGeoJson().getCoordinates().isEmpty()){
                    if(location!=null)
                    {
                        Double distance= AndroidUtils.getDistanceBwTwoLocation(location.getLatitude(),location.getLongitude(),serviceArrayList.get(position).getService().getLocation().get(0).getGeoJson().getCoordinates().get(1),serviceArrayList.get(position).getService().getLocation().get(0).getGeoJson().getCoordinates().get(0));
                        if(distance!=null)
                            //  Math.round(distance);
                            holder.txtDistance.setText(String.valueOf( Math.round(distance) +" Km"));
                    }else
                        holder.txtDistance.setText("NA");
                }
            }
        }
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
