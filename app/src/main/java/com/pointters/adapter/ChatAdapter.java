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
import com.pointters.model.ConversationsModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;


/**
 * Created by prashantkumar on 17/8/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Context context;
    private List<ConversationsModel> conversationsList;


    public ChatAdapter(Context context, List<ConversationsModel> conversationsList) {
        this.context = context;
        this.conversationsList = conversationsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_iteam_bar, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

//        if (position == 0) {
//            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) 0);
//            holder.layoutParent.setLayoutParams(holder.layoutParams);
//        }

        if (conversationsList != null && conversationsList.size() > 0) {
            if (conversationsList.get(position).getUsers() != null && conversationsList.get(position).getUsers().size() > 0) {
                if (conversationsList.get(position).getUsers().get(0).getProfilePic() != null && !conversationsList.get(position).getUsers().get(0).getProfilePic().isEmpty()) {
                    String strPic = conversationsList.get(position).getUsers().get(0).getProfilePic();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
//                        strPic = "https://s3.amazonaws.com" + strPic;
                    }
                    ImageLoader.getInstance().displayImage(strPic, holder.imgUser, options);
                }

                String firstName = "", lastName = "";
                if (conversationsList.get(position).getUsers().get(0).getFirstName() != null && !conversationsList.get(position).getUsers().get(0).getFirstName().isEmpty()) {
                    firstName = conversationsList.get(position).getUsers().get(0).getFirstName();
                }
                if (conversationsList.get(position).getUsers().get(0).getLastName() != null && !conversationsList.get(position).getUsers().get(0).getLastName().isEmpty()) {
                    lastName = conversationsList.get(position).getUsers().get(0).getLastName();
                }

                String username = "";
                if (!firstName.equals("")) { username += firstName + " "; }
                if (!lastName.equals("")) { username += lastName; }

                holder.txtName.setText(username);
            }

            String strNew = "";
            if (conversationsList.get(position).getCountNewMessages() != null && conversationsList.get(position).getCountNewMessages() > 0) {
                holder.imgOnline.setVisibility(View.VISIBLE);
                strNew = "(" + String.valueOf(conversationsList.get(position).getCountNewMessages()) + " New) ";
            } else {
                holder.imgOnline.setVisibility(View.GONE);
            }

            if (conversationsList.get(position).getLastMessage() != null) {
                String firstName = "", lastName = "", lastMsg = "";
                if (conversationsList.get(position).getLastMessage().getFirstName() != null && !conversationsList.get(position).getLastMessage().getFirstName().isEmpty()) {
                    firstName = conversationsList.get(position).getLastMessage().getFirstName();
                }
                if (conversationsList.get(position).getLastMessage().getLastName() != null && !conversationsList.get(position).getLastMessage().getLastName().isEmpty()) {
                    lastName = conversationsList.get(position).getLastMessage().getLastName().substring(0, 1);
                }
                if (conversationsList.get(position).getLastMessage().getMessagge() != null && !conversationsList.get(position).getLastMessage().getMessagge().isEmpty()) {
                    lastMsg = conversationsList.get(position).getLastMessage().getMessagge();
                }

                String msg = strNew;
                if (!firstName.equals("")) { msg += firstName + " "; }
                if (!lastName.equals(""))  { msg += lastName.toUpperCase() + ".";  }
                if (!lastMsg.equals(""))   { msg += ": " + lastMsg;  }

                holder.txtMessage.setText(msg);

                if (conversationsList.get(position).getLastMessage().getTime() != null && !conversationsList.get(position).getLastMessage().getTime().isEmpty()) {
                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    df.setTimeZone(tz);

                    long offer_time = 0;
                    try {
                        offer_time = df.parse(conversationsList.get(position).getLastMessage().getTime()).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String strDuration = getDateDuration(offer_time);

                    holder.txtTime.setText(strDuration);
                } else
                    holder.txtTime.setText("");
            }
            else {
                holder.txtTime.setText(" ");
                holder.txtMessage.setText(" ");
            }
        }
    }

    @Override
    public int getItemCount() { return conversationsList.size(); }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imgUser;
        private ImageView imgOnline;
        private TextView txtName,txtTime,txtMessage;
        private RelativeLayout layoutParent;
        private RelativeLayout.LayoutParams layoutParams;

        public MyViewHolder(View itemView) {
            super(itemView);
            layoutParent=(RelativeLayout)itemView.findViewById(R.id.rl_chat_row);
            layoutParams=(RelativeLayout.LayoutParams)layoutParent.getLayoutParams();

            imgUser=(RoundedImageView)itemView.findViewById(R.id.img_profile);
            imgOnline=(ImageView)itemView.findViewById(R.id.online_indicator);
            txtName=(TextView)itemView.findViewById(R.id.mUserName);
            txtTime=(TextView)itemView.findViewById(R.id.mDate);
            txtMessage=(TextView)itemView.findViewById(R.id.mChatNotification);
        }
    }

    private String getDateDuration(long originTime) {
        long unit_sec = 1000;
        long unit_min = 60 * unit_sec;
        long unit_hour = 60 * unit_min;
        long unit_day = 24 * unit_hour;
        long unit_week = 7 * unit_day;
        long unit_month = 30 * unit_day;
        long unit_year = 365 * unit_day;

        long curTime = System.currentTimeMillis();
        long duration = curTime - originTime;

        long years = (long) Math.floor(duration/unit_year);
        long months = (long) Math.floor(duration/unit_month);
        long weeks = (long) Math.floor(duration/unit_week);
        long days = (long) Math.floor(duration/unit_day);
        long hours = (long) Math.floor(duration/unit_hour);
        long mins = (long) Math.floor(duration/unit_min);
        long secs = (long) Math.floor(duration/unit_sec);

        if (years > 0)       { return (years == 1)  ?  years + " Year"   :  years + " Years";   }
        else if (months > 0) { return (months == 1) ?  months + " Month" :  months + " Months"; }
        else if (weeks > 0)  { return (weeks == 1)  ?  weeks + " week"   :  weeks + " weeks";   }
        else if (days > 0)   { return (days == 1)   ?  days + " day"     :  days + " days";     }
        else if (hours > 0)  { return (hours == 1)  ?  hours + " hour"   :  hours + " hours";   }
        else if (mins > 0)   { return (mins == 1)   ?  mins + " min"     :  mins + " mins";     }
        else                 { return (secs == 1)   ?  secs + " sec"     :  secs + " secs";     }
    }
}
