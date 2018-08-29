package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.InviteSuggestedUserModel;
import com.pointters.model.InviteUserServiceModel;
import com.pointters.model.Media;
import com.pointters.utils.EqualSpacingItemDecoration;

import java.util.ArrayList;

/**
 * Created by vikas on 8/24/2017.
 */

public class SuggestedRecyclerAdapter extends RecyclerView.Adapter<SuggestedRecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<InviteSuggestedUserModel> inviteSuggestedUserModels;
    DisplayImageOptions options;
    OnRecyclerViewButtonClickListener listener;

    public SuggestedRecyclerAdapter(Context context,ArrayList<InviteSuggestedUserModel> inviteSuggestedUserModels, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.inviteSuggestedUserModels = inviteSuggestedUserModels;
    }
    public void setData(ArrayList<InviteSuggestedUserModel> models){
        this.inviteSuggestedUserModels = models;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_invite_friend, parent, false);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
//        if (position == 0) {
//            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp), (int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
//            holder.layoutRoot.setLayoutParams(holder.layoutParams);
//        }
//
        InviteSuggestedUserModel model = inviteSuggestedUserModels.get(position);
        if (model != null){
            if (model.getFirstName() != null && model.getLastName() != null){
                String userNameString = model.getFirstName() + " " + model.getLastName();
                holder.username.setText(userNameString);
            }
            if (model.getNumServices() != null){
                holder.txtServiceCount.setText(model.getNumServices() + " Services");
            }
            if (model.getProfilePic() != null){
                String profilPic = model.getProfilePic();
                ImageLoader.getInstance().displayImage(profilPic, holder.userProfile, options);
            }
            if (model.isHasFollowed()) {
                holder.followButton.setText("FOLLOWING");
            }else{
                holder.followButton.setText("FOLLOW");
            }
            if (model.getPointValue() != null) {
                holder.txtPonitValue.setText(String.valueOf(model.getPointValue()));
            }else{
                holder.txtPonitValue.setText("");
            }
            if (model.getNumFollowers() != null) {
                holder.txtNumFollowers.setText(String.valueOf(model.getNumFollowers()));
            }else{
                holder.txtNumFollowers.setText("0");
            }
            if (model.getNumOrders() != null) {
                holder.txtNumOrders.setText(String.valueOf(model.getNumOrders()));
            }else{
                holder.txtNumOrders.setText("");
            }
            if (model.getAvgRating() != null) {
                holder.txtAvgRating.setText(String.valueOf(model.getAvgRating()));
            }else{
                holder.txtAvgRating.setText("");
            }

        }
        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onButtonClick(v, position);
                }
            }
        });

        if (model.getServices() != null) {
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            SuggestedFollowersRecyclerAdapter adapter = new SuggestedFollowersRecyclerAdapter(context, model.getServices());
            holder.recyclerView.addItemDecoration(new EqualSpacingItemDecoration(2));
            holder.recyclerView.setAdapter(adapter);
            holder.recyclerView.setVisibility(View.VISIBLE);
        }else{
//            holder.recyclerView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return inviteSuggestedUserModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        RecyclerView recyclerView;
        RoundedImageView userProfile;
        Button followButton;
        TextView txtServiceCount;
        TextView txtPonitValue, txtNumFollowers, txtNumOrders, txtAvgRating;
        private LinearLayout layoutRoot;

        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.txt_user_name);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.invite_recyclerView);
            followButton = (Button) itemView.findViewById(R.id.btn_follow);
            userProfile = (RoundedImageView) itemView.findViewById(R.id.img_profile);
            txtServiceCount = (TextView) itemView.findViewById(R.id.txt_services_count);
            txtPonitValue = (TextView) itemView.findViewById(R.id.txt_point_value);
            txtNumOrders = (TextView) itemView.findViewById(R.id.txt_num_orders);
            txtNumFollowers = (TextView) itemView.findViewById(R.id.txt_num_followers);
            txtAvgRating = (TextView) itemView.findViewById(R.id.txt_avg_rating);
        }
    }
}

class SuggestedFollowersRecyclerAdapter extends RecyclerView.Adapter<SuggestedFollowersRecyclerAdapter.MyViewHolder> {

    DisplayImageOptions options;
    private Context context;
    private ArrayList<InviteUserServiceModel> services;
    public SuggestedFollowersRecyclerAdapter(Context context, ArrayList<InviteUserServiceModel> services) {
        this.context = context;
        this.services = services;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_suggested_friend, parent, false);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        InviteUserServiceModel service = services.get(position);
        if (service.getDescription() != null){
            holder.serviceName.setText(service.getDescription());
        }
        if (service.getMedia() != null){
            Media media = service.getMedia().get(0);
            ImageLoader.getInstance().displayImage(media.getFileName(), holder.userProfile, options);
        }
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView userProfile;
        TextView serviceName;

        public MyViewHolder(View itemView) {
            super(itemView);
            userProfile = (RoundedImageView) itemView.findViewById(R.id.img_profile_round);
            serviceName = (TextView)itemView.findViewById(R.id.service_name);


        }
    }
}