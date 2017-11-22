package com.pointters.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pointters.R;
import com.pointters.listener.OnRecyclerViewItemClickListener;

/**
 * Created by prashantkumar on 23/8/17.
 */

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.MyViewHolder> {
    private Context context;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public JobsAdapter(Context context, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public JobsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_jobs, parent, false);
        return new JobsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JobsAdapter.MyViewHolder holder, final int position) {
        holder.layoutEditOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecyclerViewItemClickListener.onItemClick(position);
            }
        }); if(position==0) {
            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
            holder.layoutParent.setLayoutParams(holder.layoutParams);
        }



    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView btnViewJob;
        private RelativeLayout layoutEditOffer,layoutParent;
        private RelativeLayout.LayoutParams layoutParams;
        public MyViewHolder(View itemView) {
            super(itemView);
            btnViewJob=(ImageView)itemView.findViewById(R.id.img_view_job);
            layoutEditOffer=(RelativeLayout)itemView.findViewById(R.id.layout_edit_offer);
            layoutParent=(RelativeLayout)itemView.findViewById(R.id.layout_parent);
            layoutParams=( RelativeLayout.LayoutParams)layoutParent.getLayoutParams();

        }
    }
}
