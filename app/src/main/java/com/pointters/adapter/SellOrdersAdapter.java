package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.model.SellOrderModel;

import java.util.List;

/**
 * Created by prashantkumar on 21/8/17.
 */

public class SellOrdersAdapter extends RecyclerView.Adapter<SellOrdersAdapter.MyViewHolder> {

    private Context context;
    private List<SellOrderModel> sellOrderModelList;

    public SellOrdersAdapter(Context context, List<SellOrderModel> sellOrderModelList) {
        this.context = context;
        this.sellOrderModelList = sellOrderModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_orders, parent, false);
        return new SellOrdersAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(position==0) {
            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
            holder.layoutParent.setLayoutParams(holder.layoutParams);
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();


        if (sellOrderModelList != null && sellOrderModelList.size() > 0) {
            if (sellOrderModelList.get(position).getBuyer() != null) {
                if (sellOrderModelList.get(position).getBuyer().getFirstName() != null && !sellOrderModelList.get(position).getBuyer().getFirstName().isEmpty())
                    holder.txtName.setText(sellOrderModelList.get(position).getBuyer().getFirstName());
                else
                    holder.txtName.setText("NA");

                if (sellOrderModelList.get(position).getBuyer().getProfilePic() != null && !sellOrderModelList.get(position).getBuyer().getProfilePic().isEmpty())
                    ImageLoader.getInstance().displayImage(sellOrderModelList.get(position).getBuyer().getProfilePic(), holder.imgProvider, options);
                holder.txtServiceDesc.setText("NA");
                holder.txtPrice.setText("NA");
                holder.txtPriceDesc.setText("NA");
                holder.txtActualPrice.setText("NA");
                holder.txtPriceDesc.setText("NA");
                holder.txtPaidOf.setText("NA");
            }
        }

    }

    @Override
    public int getItemCount() {
        return sellOrderModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layoutParent;
        private RelativeLayout.LayoutParams layoutParams;

        private ImageView imgProvider;
        private TextView txtServiceDesc, txtPrice, txtActualPrice, txtPriceDesc, txtName,txtPaidOf;
        public MyViewHolder(View itemView) {
            super(itemView);
            layoutParent=(RelativeLayout)itemView.findViewById(R.id.layout_parent);
            layoutParams=( RelativeLayout.LayoutParams)layoutParent.getLayoutParams();


            imgProvider=(ImageView)itemView.findViewById(R.id.img_service_provider);
            txtServiceDesc = (TextView) itemView.findViewById(R.id.txt_service_name);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_service_price);
            txtPriceDesc=(TextView)itemView.findViewById(R.id.txt_price_desc);
            txtActualPrice = (TextView) itemView.findViewById(R.id.txt_service_actual_price);
            txtName = (TextView) itemView.findViewById(R.id.txt_service_provider_name);
            txtPaidOf=(TextView)itemView.findViewById(R.id.txt_paidof);
        }
    }
}
