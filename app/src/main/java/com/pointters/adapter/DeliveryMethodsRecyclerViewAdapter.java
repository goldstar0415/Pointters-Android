package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.DeliveryMethod;

import java.util.ArrayList;


/**
 * Created by Anil Jha on 10-Aug-2017.
 */

public class DeliveryMethodsRecyclerViewAdapter extends RecyclerView.Adapter<DeliveryMethodsRecyclerViewAdapter.MyViewHolder> implements View.OnClickListener {

    private ArrayList<DeliveryMethod> deliveryMethods;

    public DeliveryMethodsRecyclerViewAdapter(ArrayList<DeliveryMethod> deliveryMethods) {

        this.deliveryMethods = deliveryMethods;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_delivery_method, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.deliveryItemName.setText(deliveryMethods.get(position).getTitle());
        if (deliveryMethods.get(position).isSelected()) {
            holder.deliveryCheckBox.setImageResource(R.drawable.checkbox_checked);
        } else {
            holder.deliveryCheckBox.setImageResource(R.drawable.checkbox_unchecked);
        }

        holder.relativeLayoutParent.setTag(position);
        holder.relativeLayoutParent.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {

        return deliveryMethods.size();
    }

    @Override
    public void onClick(View v) {

        int clickedPosition = (int) v.getTag();

        for (int x = 0; x < deliveryMethods.size(); x++) {

            if (x == clickedPosition) {
                deliveryMethods.get(x).setSelected(true);
            } else {
                deliveryMethods.get(x).setSelected(false);
            }
        }

        notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView deliveryCheckBox;
        private TextView deliveryItemName;
        private RelativeLayout relativeLayoutParent;

        public MyViewHolder(View view) {
            super(view);

            deliveryCheckBox = (ImageView) view.findViewById(R.id.img_checkbox);
            deliveryItemName = (TextView) view.findViewById(R.id.delivery_method_name);
            relativeLayoutParent = (RelativeLayout) view.findViewById(R.id.relativeLayoutParent);
        }

    }

}

