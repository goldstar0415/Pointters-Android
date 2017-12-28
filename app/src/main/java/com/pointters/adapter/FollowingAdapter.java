package com.pointters.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.model.FollowersModel;
import com.pointters.model.FollowingModel;

import java.util.List;

/**
 * Created by mac on 12/5/17.
 */

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {
    private Context context;
    private List<FollowingModel> userfollowings;


    public FollowingAdapter(Context context, List<FollowingModel> userfollowings) {
        this.context = context;
        this.userfollowings = userfollowings;
    }

    @Override
    public FollowingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_following_followers, parent, false);
        return new FollowingAdapter.ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(FollowingAdapter.ViewHolder holder, int position) {
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
        }
        //holder.userAvatar.setText(menu_options[position]);
    }


    @Override
    public int getItemCount() {

        return userfollowings.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        RoundedImageView userAvatar;

        public ViewHolder(View view) {

            super(view);
            userName = (TextView) view.findViewById(R.id.txt_name);
            userAvatar = (RoundedImageView) view.findViewById(R.id.img_profile);

        }
    }
}
