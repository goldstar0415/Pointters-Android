package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.CurrentPageDetails;

import java.util.ArrayList;

/**
 * Created by vikas on 8/25/2017.
 */


public class CurrentNetworkAdapter extends RecyclerView.Adapter<CurrentNetworkAdapter.MyViewHolder>{

    private ArrayList<CurrentPageDetails> current_options;
    private Context context;



    @Override
    public CurrentNetworkAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nework_tab_comment_like_point, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView current_userName;

        public MyViewHolder(View view) {
            super(view);



        }
    }
}

