package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.MenuScreenDetails;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 16/8/17.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private Context context;
    private String [] menu_options;


    public MenuAdapter(Context context, String[] menu_options) {
        this.context = context;
        this.menu_options = menu_options;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_screen_activity_textview, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.menu_titiles.setText(menu_options[position]);
    }


    @Override
    public int getItemCount() {

        return menu_options.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView menu_titiles;

        public ViewHolder(View view) {

            super(view);
            menu_titiles = (TextView) view.findViewById(R.id.mMenu_option);

        }
    }
}