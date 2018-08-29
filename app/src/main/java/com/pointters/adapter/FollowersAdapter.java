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
import com.pointters.model.FollowersModel;

import java.util.List;

/**
 * Created by prashantkumar on 16/8/17.
 */
public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {
    private Context context;
    private List<FollowersModel> userfollowers;

    private OnRecyclerViewButtonClickListener listener;
    private OnRecyclerViewItemClickListener listener1;

    public FollowersAdapter(Context context, List<FollowersModel> userfollowers) {
        this.context = context;
        this.userfollowers = userfollowers;
    }

    public void setListener(OnRecyclerViewButtonClickListener listener){
        this.listener = listener;
    }
    public void setItemListener(OnRecyclerViewItemClickListener listener){
        this.listener1 = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_following_followers, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
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
            if (userfollowers.get(position).getCategories().size() > 0){
                String categoires = "";
                for (int i = 0; i < userfollowers.get(position).getCategories().size(); i++) {
                    String category = userfollowers.get(position).getCategories().get(i);
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
        if (userfollowers.get(position).getFollowFrom().isMutualFollow()) {
            holder.followButton.setText("FOLLOWING");
        }else{
            holder.followButton.setText("FOLLOW");
        }

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                listener.onButtonClick(v, position);
            }
        });
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
        return userfollowers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        RoundedImageView userAvatar;
        TextView dutyText;
        Button followButton;

        RelativeLayout rootView;
        public ViewHolder(View view) {

            super(view);
            rootView = (RelativeLayout) view.findViewById(R.id.root_view);
            userName = (TextView) view.findViewById(R.id.txt_name);
            userAvatar = (RoundedImageView) view.findViewById(R.id.img_profile);
            dutyText = (TextView) view.findViewById(R.id.txt_duty);
            followButton = (Button) view.findViewById(R.id.follow_unfollow_button);
        }
    }
}