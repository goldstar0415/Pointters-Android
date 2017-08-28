package com.pointters.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;

/**
 * Created by prashantkumar on 23/8/17.
 */

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.MyViewHolder> {
    @Override
    public JobsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_jobs, parent, false);
        return new JobsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JobsAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
