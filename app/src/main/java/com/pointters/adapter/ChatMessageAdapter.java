package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;
import com.pointters.model.ChatResultModel;
import com.pointters.utils.ChatViewHolder;

import java.util.List;

/**
 * Created by mac on 1/5/18.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    Context mContext;
    List<ChatResultModel> mChatHistoryList;
    String mloginUserId;
    int mRequestCode;

    public ChatMessageAdapter(Context context, List<ChatResultModel> chatHistoryList, String loginUserId, int requestCode) {
        this.mContext = context;
        this.mChatHistoryList = chatHistoryList;
        this.mloginUserId = loginUserId;
        this.mRequestCode = requestCode;
    }

    @Override
    public int getItemViewType(int position) {
        String userId = "";
        if (mChatHistoryList.get(position).getResult() != null) {
            if (mChatHistoryList.get(position).getResult().getUser() != null) {
                if (mChatHistoryList.get(position).getResult().getUser().getUserId() != null && !mChatHistoryList.get(position).getResult().getUser().getUserId().isEmpty()) {
                    userId = mChatHistoryList.get(position).getResult().getUser().getUserId();
                }
            }
        }

        if (userId.equals(mloginUserId)) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ChatViewHolder viewHolder = null;

        switch (viewType) {
            case 1:
            {
                View v1 = inflater.inflate(R.layout.recycler_item_chat1, parent, false);
                viewHolder = new ChatViewHolder(v1, mloginUserId, mRequestCode);
            }
                break;
            case 2:
            {
                View v2 = inflater.inflate(R.layout.recycler_item_chat2, parent, false);
                viewHolder = new ChatViewHolder(v2, mloginUserId, mRequestCode);
            }
                break;
            default:
            {
                View v1 = inflater.inflate(R.layout.recycler_item_chat1, parent, false);
                viewHolder = new ChatViewHolder(v1, mloginUserId, mRequestCode);
            }
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        String strPic = "";
        if (mChatHistoryList.get(position).getResult() != null) {
            if (mChatHistoryList.get(position).getResult().getUser() != null) {
                if (mChatHistoryList.get(position).getResult().getUser().getProfilePic() != null && !mChatHistoryList.get(position).getResult().getUser().getProfilePic().isEmpty()) {
                    strPic = mChatHistoryList.get(position).getResult().getUser().getProfilePic();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
//                        strPic = "https://s3.amazonaws.com" + strPic;
                    }
                }
            }

            int ntype = getItemViewType(position);
            if (ntype == 1 || ntype == 2) {
                holder.bindData(mChatHistoryList.get(position).getResult(), strPic, ntype);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChatHistoryList.size();
    }

    @Override
    public void onViewRecycled(ChatViewHolder holder) {
        super.onViewRecycled(holder);
    }
}
