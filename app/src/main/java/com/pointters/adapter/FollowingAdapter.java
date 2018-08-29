package com.pointters.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.FollowingModel;

import java.util.List;

/**
 * Created by mac on 12/5/17.
 */

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {
    private Context context;
    private List<FollowingModel> userfollowings;

    private OnRecyclerViewButtonClickListener listener;
    private OnRecyclerViewItemClickListener listener1;

    public FollowingAdapter(Context context, List<FollowingModel> userfollowings) {
        this.context = context;
        this.userfollowings = userfollowings;
    }

    public void setListener(OnRecyclerViewButtonClickListener listener){
        this.listener = listener;
    }

    public void setItemListener(OnRecyclerViewItemClickListener listener){
        this.listener1 = listener;
    }

    @Override
    public FollowingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_following_followers, parent, false);
        return new FollowingAdapter.ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(FollowingAdapter.ViewHolder holder, final int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        if (userfollowings != null && userfollowings.size() > 0) {
            holder.userName.setText(userfollowings.get(position).getFollowTo().getFirstName() + " " + userfollowings.get(position).getFollowTo().getLastName());

            if (userfollowings.get(position).getFollowTo().getProfilePic() != null && !userfollowings.get(position).getFollowTo().getProfilePic().isEmpty())
                ImageLoader.getInstance().displayImage(userfollowings.get(position).getFollowTo().getProfilePic(), holder.userAvatar, options);
            if (userfollowings.get(position).getCategories().size() > 0){
                String categoires = "";
                for (int i = 0; i < userfollowings.get(position).getCategories().size(); i++) {
                    String category = userfollowings.get(position).getCategories().get(i);
                    if (categoires.equals("")){
                        categoires = category;
                    }else{
                        categoires = String.format("%s, %s", categoires, category);
                    }
                }
                holder.dutyText.setText(categoires);
            }else{
                holder.dutyText.setText("");
            }
        }
        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onButtonClick(v, position);
            }
        });
        if (userfollowings.get(position).getFollowTo().isMutualFollow()) {
            holder.followButton.setText("FOLLOWING");
        }else{
            holder.followButton.setText("FOLLOW");
        }
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener1 != null){
                    listener1.onItemClick(position);
                }

            }
        });
    }


    @Override
    public int getItemCount() {

        return userfollowings.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView dutyText;
        Button followButton;
        RoundedImageView userAvatar;

        RelativeLayout rootView;
        public ViewHolder(View view) {

            super(view);
            rootView = (RelativeLayout) view.findViewById(R.id.root_view);
            userName = (TextView) view.findViewById(R.id.txt_name);
            dutyText = (TextView) view.findViewById(R.id.txt_duty);
            followButton = (Button) view.findViewById(R.id.follow_unfollow_button);
            userAvatar = (RoundedImageView) view.findViewById(R.id.img_profile);

        }
    }
}
