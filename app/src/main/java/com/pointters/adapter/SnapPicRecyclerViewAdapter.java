package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.Media;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 12/10/17.
 */

public class SnapPicRecyclerViewAdapter extends RecyclerView.Adapter<SnapPicRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Media> files;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;


    public SnapPicRecyclerViewAdapter(Context context, ArrayList<Media> files, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.files = files;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_snap_photo, parent, false);
        return new SnapPicRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        if(files.size()>0 && !files.get(position).getFileName().isEmpty())
        ImageLoader.getInstance().displayImage(files.get(position).getFileName(),holder.imgSnap,options);

        holder.imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecyclerViewItemClickListener.onItemClick(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgSnap,imgCross;
        public MyViewHolder(View itemView) {
            super(itemView);
            imgSnap=(ImageView)itemView.findViewById(R.id.img_pick);
            imgCross=(ImageView)itemView.findViewById(R.id.img_cross);
        }
    }
}
