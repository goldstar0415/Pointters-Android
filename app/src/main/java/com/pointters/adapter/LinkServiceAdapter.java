package com.pointters.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;

/**
 * Created by prashantkumar on 28/8/17.
 */

public class LinkServiceAdapter extends RecyclerView.Adapter<LinkServiceAdapter.MyViewHolder> {
    @Override
    public LinkServiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_link_service, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LinkServiceAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
