package com.pointters.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;
import com.pointters.fragment.PointFragment;
import com.pointters.model.CurrentPageDetails;
import com.pointters.utils.CustomTabLayout;

import java.util.ArrayList;

/**
 * Created by vikas on 8/26/2017.
 */

public class CurrentPagePostAdapter extends RecyclerView.Adapter<CurrentPagePostAdapter.MyViewHolder> {


    private Context context;

    public CurrentPagePostAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CurrentPagePostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_view_current_update, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.customTabLayout.addTab(holder.customTabLayout.newTab().setText(context.getResources().getString(R.string.comments)));
        holder.customTabLayout.addTab(holder.customTabLayout.newTab().setText(context.getResources().getString(R.string.points)));
        holder.customTabLayout.addTab(holder.customTabLayout.newTab().setText(context.getResources().getString(R.string.likes)));
        if(holder.customTabLayout.getSelectedTabPosition()==0)
        {
        }
        holder.customTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (holder.customTabLayout.getSelectedTabPosition() == 0) {

                } else if (holder.customTabLayout.getSelectedTabPosition() == 1) {

                } else if (holder.customTabLayout.getSelectedTabPosition() == 2) {

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTabLayout customTabLayout;
        private RecyclerView recyclerView;

        public MyViewHolder(View view) {
            super(view);
            customTabLayout = (CustomTabLayout) view.findViewById(R.id.tabs);
            recyclerView=(RecyclerView)view.findViewById(R.id.tabs_rv);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            DividerItemDecoration divider2 = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            divider2.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider_option));
            recyclerView.addItemDecoration(divider2);
            CurrentNetworkAdapter currentNetworkAdapter=new CurrentNetworkAdapter();
            recyclerView.setAdapter(currentNetworkAdapter);


        }
    }
}


