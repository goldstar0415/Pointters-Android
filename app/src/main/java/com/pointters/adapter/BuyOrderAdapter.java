package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.BuyOrderModel;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by prashantkumar on 21/8/17.
 */

public class BuyOrderAdapter extends RecyclerView.Adapter<BuyOrderAdapter.MyViewHolder> {

    private Context context;
    private List<BuyOrderModel> buyOrderList;
    private OnRecyclerViewButtonClickListener listener;

    public BuyOrderAdapter(Context context, List<BuyOrderModel> buyOrderList, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.buyOrderList = buyOrderList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_orders, parent, false);
        return new BuyOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        if(position == 0) {
            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
            holder.layoutParent.setLayoutParams(holder.layoutParams);
        }

        if (buyOrderList != null && buyOrderList.size() > 0) {
            if (buyOrderList.get(position).getOrder() != null) {
                if (buyOrderList.get(position).getOrder().getMedia() != null) {
                    if (buyOrderList.get(position).getOrder().getMedia().getFileName() != null && !buyOrderList.get(position).getOrder().getMedia().getFileName().isEmpty()) {
                        String strPic = buyOrderList.get(position).getOrder().getMedia().getFileName();
                        if (!strPic.contains("https://s3.amazonaws.com")) {
                            strPic = "https://s3.amazonaws.com" + strPic;
                        }
                        ImageLoader.getInstance().displayImage(strPic, holder.imgProvider, options);
                    }
                }

                if (buyOrderList.get(position).getOrder().getDescription() != null && !buyOrderList.get(position).getOrder().getDescription().isEmpty()) {
                    holder.txtServiceDesc.setText(buyOrderList.get(position).getOrder().getDescription());
                } else {
                    holder.txtServiceDesc.setText("");
                }

                String strSymbol = "$";
                if (buyOrderList.get(position).getOrder().getCurrencySymbol() != null && !buyOrderList.get(position).getOrder().getCurrencySymbol().isEmpty()) {
                    strSymbol = buyOrderList.get(position).getOrder().getCurrencySymbol();
                }

                if (buyOrderList.get(position).getOrder().getTotalAmount() != null)
                    holder.txtPrice.setText(strSymbol + String.valueOf(buyOrderList.get(position).getOrder().getTotalAmount()));
                else
                    holder.txtPrice.setText(strSymbol + "0");

                if (buyOrderList.get(position).getOrder().getTotalAmountBeforeDiscount() != null && buyOrderList.get(position).getOrder().getTotalAmountBeforeDiscount() > 0)
                    holder.txtActualPrice.setText(strSymbol + String.valueOf(buyOrderList.get(position).getOrder().getTotalAmountBeforeDiscount()));
                else
                    holder.txtActualPrice.setText("");

                if (buyOrderList.get(position).getOrder().getPriceDescription() != null && !buyOrderList.get(position).getOrder().getPriceDescription().isEmpty())
                    holder.txtPriceDesc.setText(buyOrderList.get(position).getOrder().getPriceDescription());
                else
                    holder.txtPriceDesc.setText("");

                if (buyOrderList.get(position).getOrder().getStatus() != null && !buyOrderList.get(position).getOrder().getStatus().isEmpty())
                    holder.txtStatus.setText(buyOrderList.get(position).getOrder().getStatus());
                else
                    holder.txtStatus.setText("NA");

                if (buyOrderList.get(position).getOrder().getPaymentDate() != null && !buyOrderList.get(position).getOrder().getPaymentDate().isEmpty()) {
                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    df.setTimeZone(tz);

                    SimpleDateFormat fmtOut = new SimpleDateFormat("d/M");
                    try {
                        holder.txtPaidOf.setText("Paid of "+ String.valueOf(fmtOut.format(df.parse(buyOrderList.get(position).getOrder().getPaymentDate()))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else
                    holder.txtPaidOf.setText("NA");

                if (buyOrderList.get(position).getOrder().getNotificationCount() != null && buyOrderList.get(position).getOrder().getNotificationCount() > 0) {
                    holder.notifyView.setVisibility(View.VISIBLE);
                    holder.txtNoteCnt.setVisibility(View.VISIBLE);
                    holder.txtNoteCnt.setText(String.valueOf(buyOrderList.get(position).getOrder().getNotificationCount()));
                } else {
                    holder.notifyView.setVisibility(View.GONE);
                    holder.txtNoteCnt.setVisibility(View.GONE);
                }
            }

            if (buyOrderList.get(position).getSeller() != null) {
                if (buyOrderList.get(position).getSeller().getFirstName() != null && !buyOrderList.get(position).getSeller().getFirstName().isEmpty())
                    holder.txtName.setText(buyOrderList.get(position).getSeller().getFirstName());
                else
                    holder.txtName.setText("");

                if (buyOrderList.get(position).getSeller().getPhone() != null && !buyOrderList.get(position).getSeller().getPhone().isEmpty()) {
                    holder.btnPhone.setVisibility(View.VISIBLE);
                } else {
                    holder.btnPhone.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return buyOrderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout layoutParent;
        private RelativeLayout.LayoutParams layoutParams;

        private ImageView imgProvider, btnPhone, btnChat, btnNotify;
        private RoundedImageView notifyView;
        private TextView txtServiceDesc, txtPrice, txtActualPrice, txtPriceDesc, txtName, txtPaidOf, txtStatus, txtNoteCnt;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;

        public MyViewHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            layoutParent = (RelativeLayout) itemView.findViewById(R.id.layout_parent);
            layoutParams = (RelativeLayout.LayoutParams) layoutParent.getLayoutParams();

            imgProvider=(ImageView) itemView.findViewById(R.id.img_service_provider);
            txtServiceDesc = (TextView) itemView.findViewById(R.id.txt_service_name);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_service_price);
            txtPriceDesc=(TextView) itemView.findViewById(R.id.txt_price_desc);
            txtActualPrice = (TextView) itemView.findViewById(R.id.txt_service_actual_price);
            txtName = (TextView) itemView.findViewById(R.id.txt_service_provider_name);
            txtName.setOnClickListener(this);
            txtPaidOf=(TextView) itemView.findViewById(R.id.txt_paidof);
            txtStatus=(TextView) itemView.findViewById(R.id.txt_payment_status);
            txtNoteCnt=(TextView) itemView.findViewById(R.id.txt_notification_count);

            btnPhone=(ImageView) itemView.findViewById(R.id.btn_phone);
            btnPhone.setOnClickListener(this);
            btnChat=(ImageView) itemView.findViewById(R.id.btn_chat);
            btnChat.setOnClickListener(this);
            btnNotify=(ImageView) itemView.findViewById(R.id.btn_notification);
            notifyView=(RoundedImageView) itemView.findViewById(R.id.imv_notification_alarm);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }
}
