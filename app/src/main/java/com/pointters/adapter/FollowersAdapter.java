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

import java.util.List;

/**
 * Created by prashantkumar on 16/8/17.
 */
public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {
    private Context context;
    private List<FollowersModel> userfollowers;


    public FollowersAdapter(Context context, List<FollowersModel> userfollowers) {
        this.context = context;
        this.userfollowers = userfollowers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_following_followers, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        if (userfollowers != null && userfollowers.size() > 0) {
            holder.userName.setText(userfollowers.get(position).getFollowFrom().getFirstName() + " " + userfollowers.get(position).getFollowFrom().getLastName());

            if (userfollowers.get(position).getFollowFrom().getProfilePic() != null && !userfollowers.get(position).getFollowFrom().getProfilePic().isEmpty())
                ImageLoader.getInstance().displayImage(userfollowers.get(position).getFollowFrom().getProfilePic(), holder.userAvatar, options);
        }
    }


    @Override
    public int getItemCount() {
        return userfollowers.size();
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