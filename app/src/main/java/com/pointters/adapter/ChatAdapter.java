package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.pointters.R;
import com.pointters.listener.OnRecyclerViewItemClickListener;


/**
 * Created by prashantkumar on 17/8/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private Context context;
    private OnRecyclerViewItemClickListener onRecycleItemClickListener;

    public ChatAdapter(Context context, OnRecyclerViewItemClickListener onRecycleItemClickListener) {
        this.context = context;
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_iteam_bar, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.chatRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecycleItemClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout chatRow;

        public MyViewHolder(View itemView) {
            super(itemView);
            chatRow = (RelativeLayout) itemView.findViewById(R.id.rl_chat_row);
        }
    }
}
