package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.SendServicesModel;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by prashantkumar on 28/8/17.
 */

public class LinkServiceAdapter extends RecyclerView.Adapter<LinkServiceAdapter.MyViewHolder> {
    private Context context;
    private OnRecyclerViewButtonClickListener listener;
    private String username;
    private String serviceType;
    private List<SendServicesModel> sendServicesList;

    public LinkServiceAdapter(Context context, List<SendServicesModel> sendServicesList, String username, String type, OnRecyclerViewButtonClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.listener = onRecyclerViewItemClickListener;
        this.username = username;
        this.serviceType = type;
        this.sendServicesList = sendServicesList;
    }

    @Override
    public LinkServiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_link_service, parent, false);
        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(LinkServiceAdapter.MyViewHolder holder, final int position) {
//        holder.txtBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onRecyclerViewItemClickListener.onItemClick(position);
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imgUser;
        private TextView txtDesc, txtName, txtPrices, txtduration;
        private Button txtBtn;
        private LinearLayout layoutParent;
        private FrameLayout.LayoutParams layoutParams;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;

        public MyViewHolder(View itemView, OnRecyclerViewButtonClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            layoutParent=(LinearLayout)itemView.findViewById(R.id.layout_parent);
            layoutParams=(FrameLayout.LayoutParams)layoutParent.getLayoutParams();

            imgUser=(ImageView)itemView.findViewById(R.id.img_profile);
            txtName=(TextView)itemView.findViewById(R.id.txt_login_username);
            txtDesc=(TextView)itemView.findViewById(R.id.txt_description);
            txtPrices=(TextView)itemView.findViewById(R.id.txt_price);
            txtPrices=(TextView)itemView.findViewById(R.id.txt_duration);

            txtBtn=(Button)itemView.findViewById(R.id.btn_send_link);
            txtBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }
}
