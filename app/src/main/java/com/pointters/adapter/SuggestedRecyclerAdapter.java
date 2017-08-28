package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.InviteAurSuggestedDetails;

import java.util.ArrayList;

/**
 * Created by vikas on 8/24/2017.
 */

public class SuggestedRecyclerAdapter extends RecyclerView.Adapter<SuggestedRecyclerAdapter.MyViewHolder> {

    private ArrayList<InviteAurSuggestedDetails> seller_options;
    private Context context;




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_invite_friend, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView user_name_inviteFriend;

        public MyViewHolder(View itemView) {
            super(itemView);

            user_name_inviteFriend = (TextView) itemView.findViewById(R.id.mUserNameLiveOffer);


        }
    }
}