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
import com.pointters.model.SellerCancelOrderDetails;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 4/9/17.
 */

public class CancelOrderAdapter extends RecyclerView.Adapter<CancelOrderAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<DeliveryMethod> reasonToCancelOrder;


    public CancelOrderAdapter(Context context, ArrayList<DeliveryMethod> reasonToCancelOrder) {
        this.context = context;
        this.reasonToCancelOrder = reasonToCancelOrder;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cancel_order_list_details, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.cancelOrderReasonItemName.setText(reasonToCancelOrder.get(position).getTitle());
        if (reasonToCancelOrder.get(position).isSelected()) {
            holder.deliveryCheckBox.setImageResource(R.drawable.ellipse_checked_blue);
        } else {
            holder.deliveryCheckBox.setImageResource(R.drawable.ellipse_unchecked_grey);
        }
        holder.relativeLayoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int x = 0; x < reasonToCancelOrder.size(); x++) {

                    if (x == position) {
                        reasonToCancelOrder.get(x).setSelected(true);
                    } else {
                        reasonToCancelOrder.get(x).setSelected(false);
                    }
                }

                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return reasonToCancelOrder.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

      private   TextView cancelOrderReasonItemName;
        private ImageView deliveryCheckBox;
        private RelativeLayout relativeLayoutParent;



        public MyViewHolder(View view) {
            super(view);

            cancelOrderReasonItemName = (TextView) view.findViewById(R.id.cancel_order_reason_item_name);
            deliveryCheckBox = (ImageView) view.findViewById(R.id.img_checkbox);
            relativeLayoutParent = (RelativeLayout) view.findViewById(R.id.relativeLayoutParent);

        }
    }
}