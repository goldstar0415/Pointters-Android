package com.pointters.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pointters.R;

/**
 * Created by prashantkumar on 16/8/17.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private Context context;
    private String [] menu_options;
    private TypedArray menu_icons;
    private int[] badge;

    public MenuAdapter(Context context, String[] menu_options, TypedArray menu_icons, int[] badge) {
        this.context = context;
        this.menu_options = menu_options;
        this.menu_icons = menu_icons;
        this.badge = badge;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_screen_activity_textview, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.menu_titiles.setText(menu_options[position]);
        holder.menu_icon.setImageResource(menu_icons.getResourceId(position, -1));
        if (badge[position] == 0) {
            holder.dotView.setVisibility(View.INVISIBLE);
        }else{
            holder.dotView.setVisibility(View.VISIBLE);
            holder.dotView.setText(String.valueOf(badge[position]));
        }
    }


    @Override
    public int getItemCount() {

        return menu_options.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView menu_titiles;
        ImageView menu_icon;
        TextView dotView;

        public ViewHolder(View view) {

            super(view);
            menu_titiles = (TextView) view.findViewById(R.id.mMenu_option);
            dotView = (TextView) view.findViewById(R.id.dot_view);
            menu_icon = (ImageView) view.findViewById(R.id.icon_settings);

        }
    }
}