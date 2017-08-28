package com.pointters.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;

/**
 * Created by Vishal Sharma on 29-Jul-17.
 */

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_service, parent, false);
        return new ServiceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout moveToServiceScreeen;


        public MyViewHolder(View itemView) {
            super(itemView);
            moveToServiceScreeen=(ConstraintLayout) itemView.findViewById(R.id.move_to_service_detail);
        }
    }

}
