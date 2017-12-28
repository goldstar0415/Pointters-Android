package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.OnRecyclerViewItemClickListener;

/**
 * Created by prashantkumar on 28/8/17.
 */

public class LinkServiceAdapter extends RecyclerView.Adapter<LinkServiceAdapter.MyViewHolder> {
    private Context context;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public LinkServiceAdapter(Context context, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public LinkServiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_link_service, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LinkServiceAdapter.MyViewHolder holder, final int position) {
        holder.txtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecyclerViewItemClickListener.onItemClick(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtLink;
         public MyViewHolder(View itemView) {
            super(itemView);
             txtLink=(TextView)itemView.findViewById(R.id.txt_link);
        }
    }
}
