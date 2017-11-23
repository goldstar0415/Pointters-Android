package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.RecyclerViewItemClickWithSource;
import com.pointters.model.Prices;

import java.util.List;

/**
 * Created by prashantkumar on 14/11/17.
 */

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.MyViewHolder>{
    private Context context;
    private List<Prices> pricesList;
    private int count=1;
    private MyViewHolder myViewHolder;
    private RecyclerViewItemClickWithSource recyclerViewItemClickWithSource;

    public PriceAdapter(Context context, List<Prices> pricesList) {
        this.context = context;
        this.pricesList = pricesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_price, parent, false);
        return new PriceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        myViewHolder=holder;
        if(pricesList.size()>0) {

            if (!pricesList.get(position).getDescription().isEmpty() && !pricesList.get(position).getTimeUnitOfMeasure().isEmpty() && !String.valueOf(pricesList.get(position).getTime()).isEmpty() && !String.valueOf(pricesList.get(position).getPrice()).isEmpty()) {
                holder.txtPriceDesc.setText("$" + String.valueOf(pricesList.get(position).getPrice()) + "/" + String.valueOf(pricesList.get(position).getTime()) + "" + pricesList.get(position).getTimeUnitOfMeasure() + " - " + pricesList.get(position).getDescription());
            }
            holder.imgPlus.setTag(position);
            holder.imgMinus.setTag(position);
           /* holder.imgPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewItemClickWithSource.onItemClick(position,"plus");
                }
            });
            holder.imgMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewItemClickWithSource.onItemClick(position,"minus");
                }
            });
            holder.txtCount.setTag(holder);
*/

        }

    }

    @Override
    public int getItemCount() {
        return pricesList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPlus, imgMinus;
        private TextView txtCount, txtPriceDesc;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgPlus = (ImageView) itemView.findViewById(R.id.img_plus);
            imgMinus = (ImageView) itemView.findViewById(R.id.img_minus);
            txtPriceDesc = (TextView) itemView.findViewById(R.id.txt_price_desc);
            txtCount = (TextView) itemView.findViewById(R.id.txt_count);
        }
    }
}
