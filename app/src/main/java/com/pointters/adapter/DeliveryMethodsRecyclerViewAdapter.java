package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.DeliveryMethod;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * Created by Anil Jha on 10-Aug-2017.
 */

public class DeliveryMethodsRecyclerViewAdapter extends RecyclerView.Adapter<DeliveryMethodsRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DeliveryMethod> deliveryMethods;
    private OnRecyclerViewButtonClickListener listener;

    public DeliveryMethodsRecyclerViewAdapter(Context context, ArrayList<DeliveryMethod> deliveryMethods, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.deliveryMethods = deliveryMethods;
        this.listener = listener;
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
            holder.deliveryCheckBox.setChecked(true);
        } else {
            holder.deliveryCheckBox.setChecked(false);
        }

        if (position == 2 && deliveryMethods.get(2).isSelected()) {
            holder.layoutTemp.setVisibility(View.VISIBLE);
        } else {
            holder.layoutTemp.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return deliveryMethods.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RadioButton deliveryCheckBox;
        private TextView deliveryItemName;
        private RelativeLayout layoutTemp;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;

        public MyViewHolder(View view) {
            super(view);
            listenerRef = new WeakReference<>(listener);

            deliveryItemName = (TextView) view.findViewById(R.id.delivery_method_name);
            layoutTemp = (RelativeLayout)view.findViewById(R.id.layout_temp);
            deliveryCheckBox = (RadioButton) view.findViewById(R.id.img_checkbox);
            deliveryCheckBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }

    }
}

