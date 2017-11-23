package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewItemClickListener;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 20/9/17.
 */

public class TagServiceSellerAdapter extends RecyclerView.Adapter<TagServiceSellerAdapter.MyViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<String> tagServiceSellerList;
    private String source;
    private OnRecyclerViewItemClickListener  onRecyclerViewItemClickListener;

    public TagServiceSellerAdapter(Context context, ArrayList<String> tagServiceSellerList, String source, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.tagServiceSellerList = tagServiceSellerList;
        this.source = source;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tags, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(source.equals("normal"))
        {
            holder.layoutRoot.setTag(position);
            holder.layoutRoot.setOnClickListener(this);
            holder.imgCross.setVisibility(View.GONE);
            if (position == 0) {
                holder.imgUser.setVisibility(View.VISIBLE);
                holder.imgService.setVisibility(View.GONE);
            } else if (position == 1) {
                holder.imgUser.setVisibility(View.VISIBLE);
                holder.imgService.setVisibility(View.GONE);
            } else if (position == 2) {
                holder.imgUser.setVisibility(View.GONE);
                holder.imgService.setVisibility(View.VISIBLE);
            } else if (position == 3) {
                holder.imgUser.setVisibility(View.GONE);
                holder.imgService.setVisibility(View.VISIBLE);
            }
        }else if(source.equals("delete"))
        {
            holder.imgCross.setTag(position);
            holder.layoutRoot.setOnClickListener(null);
            holder.imgCross.setOnClickListener(this);
            holder.imgCross.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public int getItemCount() {
        return tagServiceSellerList.size();
    }

    @Override
    public void onClick(View v) {
        int position=(int)v.getTag();

        switch (v.getId())
        {
            case R.id.img_cross:
                onRecyclerViewItemClickListener.onItemClick(position);
                break;
            case R.id.layout_root:
                onRecyclerViewItemClickListener.onItemClick(position);
                break;
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgUser,imgCross;
        private RoundedImageView imgService;
        private RelativeLayout layoutRoot;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgUser = (ImageView) itemView.findViewById(R.id.img_user);
            imgCross = (ImageView) itemView.findViewById(R.id.img_cross);
            imgService = (RoundedImageView) itemView.findViewById(R.id.img_service);
            layoutRoot=(RelativeLayout)itemView.findViewById(R.id.layout_root);
        }
    }
}
