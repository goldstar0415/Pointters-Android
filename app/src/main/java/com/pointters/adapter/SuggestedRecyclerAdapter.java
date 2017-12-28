package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.InviteAurSuggestedDetails;

import java.util.ArrayList;

/**
 * Created by vikas on 8/24/2017.
 */

public class SuggestedRecyclerAdapter extends RecyclerView.Adapter<SuggestedRecyclerAdapter.MyViewHolder> {

    private Context context;

    public SuggestedRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_invite_friend, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == 0) {
            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp), (int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
            holder.layoutRoot.setLayoutParams(holder.layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user_name_inviteFriend;
        private LinearLayout layoutRoot;
        private LinearLayout.LayoutParams layoutParams;

        public MyViewHolder(View itemView) {
            super(itemView);
            layoutRoot = (LinearLayout) itemView.findViewById(R.id.layout_root);
            layoutParams = (LinearLayout.LayoutParams) layoutRoot.getLayoutParams();
            user_name_inviteFriend = (TextView) itemView.findViewById(R.id.mUserNameLiveOffer);


        }
    }
}