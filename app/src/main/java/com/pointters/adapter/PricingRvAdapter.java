package com.pointters.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.Prices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prashantkumar on 20/9/17.
 */

public class PricingRvAdapter extends RecyclerView.Adapter<PricingRvAdapter.MyViewHolder> {

    private Context context;
    private List<Prices> pricesList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public PricingRvAdapter(Context context, List<Prices> pricesList, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.pricesList = pricesList;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void setPricesList(ArrayList<Prices> pricesList){
        this.pricesList = pricesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pricing, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (pricesList != null && pricesList.size() > 0) {
            Integer valPrice = 0, valTime = 0;
            String strSymbol = "$", strUnit = "", strDesc = "";

            if (pricesList.get(position).getPrice() != null && pricesList.get(position).getPrice() > 0) {
                valPrice = pricesList.get(position).getPrice();
            }
            if (pricesList.get(position).getCurrencySymbol() != null && !pricesList.get(position).getCurrencySymbol().isEmpty())  {
                strSymbol = pricesList.get(position).getCurrencySymbol();
            }
            if (pricesList.get(position).getTime() != null && pricesList.get(position).getTime() > 0) {
                valTime = pricesList.get(position).getTime();
            }
            if (pricesList.get(position).getTimeUnitOfMeasure() != null && !pricesList.get(position).getTimeUnitOfMeasure().isEmpty()) {
                strUnit = pricesList.get(position).getTimeUnitOfMeasure();
            }
            if (pricesList.get(position).getDescription() != null && !pricesList.get(position).getDescription().isEmpty()) {
                strDesc = pricesList.get(position).getDescription();
            }

            if (valTime > 1) {
                holder.txtPrice.setText(strSymbol + String.valueOf(valPrice) + " per " + String.valueOf(valTime) + strUnit + "s");
            } else {
                holder.txtPrice.setText(strSymbol + String.valueOf(valPrice) + " per " + String.valueOf(valTime) + strUnit);
            }
        }

        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onRecyclerViewItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  pricesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layoutRoot;
        private TextView txtPrice;

        public MyViewHolder(View itemView) {
            super(itemView);
            layoutRoot = (RelativeLayout) itemView.findViewById(R.id.layout_root);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
        }
    }
}
