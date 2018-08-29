package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.CommentsModel;

import java.util.List;

/**
 * Created by vikas on 8/25/2017.
 */


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder>{
    private Context context;
    private List<CommentsModel> commentList;

    public CommentsAdapter(Context context, List<CommentsModel> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public CommentsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comments, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,commentText,dateTime;

        public MyViewHolder(View view) {
            super(view);
            name=(TextView)itemView.findViewById(R.id.txt_name);
            commentText=(TextView)itemView.findViewById(R.id.txt_comment);



        }
    }
}

