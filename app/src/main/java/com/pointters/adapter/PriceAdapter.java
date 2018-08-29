package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.RecyclerViewItemClickWithSource;
import com.pointters.model.Prices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by prashantkumar on 14/11/17.
 */

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.MyViewHolder>{
    private Context context;
    private List<Prices> pricesList;
    private int count = 1;
    private MyViewHolder myViewHolder;
    private RecyclerViewItemClickWithSource recyclerViewItemClickWithSource;
    private ArrayList<Integer> priceCountList;
    public PriceAdapter(Context context, List<Prices> pricesList, RecyclerViewItemClickWithSource listener) {
        this.context = context;
        this.pricesList = pricesList;
        this.priceCountList = new ArrayList<Integer>(Collections.nCopies(pricesList.size(), 0));
        this.recyclerViewItemClickWithSource = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_price, parent, false);
        return new PriceAdapter.MyViewHolder(itemView);
    }

    public ArrayList<Integer> getPriceCountList() {
        return priceCountList;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        myViewHolder=holder;
        if(pricesList.size()>0) {
            final Prices prices = pricesList.get(position);
            if (!prices.getDescription().isEmpty() && !prices.getTimeUnitOfMeasure().isEmpty() && !String.valueOf(prices.getTime()).isEmpty() && !String.valueOf(prices.getPrice()).isEmpty()) {
                holder.txtPriceDesc.setText(prices.getCurrencySymbol() + String.valueOf(prices.getPrice()) + " For " + String.valueOf(prices.getTime()) + "" + prices.getTimeUnitOfMeasure() + " - " + prices.getDescription());
            }
            if (priceCountList.size() > position) {
                holder.txtCount.setText(String.valueOf(priceCountList.get(position)));
            }else{
                priceCountList = new ArrayList<Integer>(Collections.nCopies(pricesList.size(), 0));
                holder.txtCount.setText(String.valueOf(priceCountList.get(position)));
            }
            holder.imgPlus.setTag(position);
            holder.imgMinus.setTag(position);
            holder.imgPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = priceCountList.get(position);
                    p++;
                    priceCountList.remove(position);
                    priceCountList.add(position, p);
                    notifyItemChanged(position);
                    recyclerViewItemClickWithSource.onItemClick(position, prices, 0);
                }
            });
            holder.imgMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = priceCountList.get(position);
                    if (p > 0) {
                        p--;
                    }
                    priceCountList.remove(position);
                    priceCountList.add(position, p);
                    notifyItemChanged(position);
                    recyclerViewItemClickWithSource.onItemClick(position, prices, 1);
                }
            });
            holder.txtCount.setTag(holder);

        }

    }

    @Override
    public int getItemCount() {
        return pricesList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPlus, imgMinus;
        private TextView txtCount, txtPriceDesc;
        private LinearLayout itemview;
        public MyViewHolder(View itemView) {
            super(itemView);
            imgPlus = (ImageView) itemView.findViewById(R.id.img_plus);
            imgMinus = (ImageView) itemView.findViewById(R.id.img_minus);
            txtPriceDesc = (TextView) itemView.findViewById(R.id.txt_price_desc);
            txtCount = (TextView) itemView.findViewById(R.id.txt_count);
        }
    }
}
