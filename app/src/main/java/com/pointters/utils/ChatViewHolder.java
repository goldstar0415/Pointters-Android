package com.pointters.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.activity.ChatActivity;
import com.pointters.activity.CustomOfferDetailsActivity;
import com.pointters.activity.MediaPlayerActivity;
import com.pointters.activity.PhotoViewerActivity;
import com.pointters.activity.SendCustomOfferActivity;
import com.pointters.activity.ServiceDetailActivity;
import com.pointters.listener.AsyncResponse;
import com.pointters.model.ChatMessageModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;


/**
 * Created by mac on 1/6/18.
 */

public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    Context mContext;
    ChatMessageModel message;

    RoundedImageView mUserPhoto;
    TextView mMsgTextView;
    RelativeLayout mMsgMediaView;
    RoundedImageView mMediaPhoto;
    RelativeLayout mMsgServiceView;
    RoundedImageView mServicePic;
    TextView mServiceDesc, mServicePrice, mServiceDuration, mServiceName;
    RelativeLayout mMsgOfferView;
    RoundedImageView mOfferServicePic;
    TextView mOfferDesc, mOfferServiceDesc, mOfferServicePrice, mOfferServiceDuration, mOfferServiceName, mChatTime;
    JZVideoPlayerStandard mMediaVideoView;

    private String mLoginUserId = "";
    private int mRequestCode = 0;


    public ChatViewHolder(View itemView, String loginUserId, int requestCode) {
        super(itemView);

        mRequestCode = requestCode;
        mLoginUserId = loginUserId;
        mContext = itemView.getContext();

        mUserPhoto = (RoundedImageView)itemView.findViewById(R.id.iv_chat_user_photo);
        mChatTime = (TextView)itemView.findViewById(R.id.txt_chat_msg_time);

        mMsgTextView = (TextView)itemView.findViewById(R.id.layout_chat_msg_text);

        mMsgMediaView = (RelativeLayout)itemView.findViewById(R.id.layout_chat_msg_photo);
        mMsgMediaView.setOnClickListener(this);
        mMediaPhoto = (RoundedImageView)itemView.findViewById(R.id.iv_chat_msg_photo);
        mMediaVideoView = (JZVideoPlayerStandard) itemView.findViewById(R.id.iv_chat_msg_video);

        mMsgServiceView = (RelativeLayout)itemView.findViewById(R.id.layout_chat_msg_service);
        mMsgServiceView.setOnClickListener(this);
        mServicePic = (RoundedImageView)itemView.findViewById(R.id.iv_chat_service_photo);
        mServiceDesc = (TextView)itemView.findViewById(R.id.txt_chat_service_desc);
        mServicePrice = (TextView)itemView.findViewById(R.id.txt_chat_service_price);
        mServiceDuration = (TextView)itemView.findViewById(R.id.txt_chat_service_duration);
        mServiceName = (TextView)itemView.findViewById(R.id.txt_chat_service_name);

        mMsgOfferView = (RelativeLayout)itemView.findViewById(R.id.layout_chat_msg_offer);
        mMsgOfferView.setOnClickListener(this);
        mOfferDesc = (TextView)itemView.findViewById(R.id.txt_chat_offer_content);
        mOfferServicePic = (RoundedImageView)itemView.findViewById(R.id.iv_chat_offer_photo);
        mOfferServiceDesc = (TextView)itemView.findViewById(R.id.txt_chat_offer_desc);
        mOfferServicePrice = (TextView)itemView.findViewById(R.id.txt_chat_offer_duration);
        mOfferServiceName = (TextView)itemView.findViewById(R.id.txt_chat_offer_name);
    }

    @SuppressLint("SetTextI18n")
    public void bindData(ChatMessageModel chatMessage, String otherPic, int inComing) {
        message = chatMessage;

        DisplayImageOptions options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        if (inComing == 2 && !otherPic.equals("")) {
            ImageLoader.getInstance().displayImage(otherPic, mUserPhoto, options1);
        }

        DisplayImageOptions options2 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        if (chatMessage != null && chatMessage.getMessage() != null) {
            if (chatMessage.getMessage().getCreatedAt() != null && !chatMessage.getMessage().getCreatedAt().isEmpty()) {
                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(tz);

                SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy  hh:mm a");
                try {
                    mChatTime.setText(String.valueOf(fmtOut.format(df.parse(chatMessage.getMessage().getCreatedAt()))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (chatMessage.getMessage().getMessageText() != null && !chatMessage.getMessage().getMessageText().isEmpty()) {
                mMsgTextView.setVisibility(View.VISIBLE);
                mMsgMediaView.setVisibility(View.GONE);
                mMsgServiceView.setVisibility(View.GONE);
                mMsgOfferView.setVisibility(View.GONE);

                mMsgTextView.setText(chatMessage.getMessage().getMessageText());
            }
            else if (chatMessage.getMessage().getMedia() != null && chatMessage.getMessage().getMedia().size() > 0) {
                mMsgTextView.setVisibility(View.GONE);
                mMsgMediaView.setVisibility(View.VISIBLE);
                mMsgServiceView.setVisibility(View.GONE);
                mMsgOfferView.setVisibility(View.GONE);

                String strType = "image";
                if (chatMessage.getMessage().getMedia().get(0).getMediaType() != null && !chatMessage.getMessage().getMedia().get(0).getMediaType().isEmpty()) {
                    strType = chatMessage.getMessage().getMedia().get(0).getMediaType();
                }

                if (chatMessage.getMessage().getMedia().get(0).getFileName() != null && !chatMessage.getMessage().getMedia().get(0).getFileName().isEmpty()) {
                    String strPic  = chatMessage.getMessage().getMedia().get(0).getFileName();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
//                        strPic = "https://s3.amazonaws.com" + strPic;
                    }

                    if (strType.equals("video")) {
                        if (inComing == 1) {
                            mMediaPhoto.setImageResource(R.drawable.video_player_white);
                        } else if (inComing == 2) {
                            mMediaPhoto.setImageResource(R.drawable.video_player_grey);
                        }
                        mMediaVideoView.setUp(strPic, JZVideoPlayer.SCREEN_WINDOW_LIST, "");
                        mMediaVideoView.setVisibility(View.VISIBLE);
                        AndroidUtils.MyAsyncTask asyncTask =new AndroidUtils.MyAsyncTask();
                        asyncTask.delegate = new AsyncResponse() {
                            @Override
                            public void processFinish(Bitmap output) {
                                mMediaVideoView.thumbImageView.setImageBitmap(output);

                            }
                        };
                        asyncTask.execute(strPic);
                        mMediaPhoto.setVisibility(View.GONE);
                    } else {
                        ImageLoader.getInstance().displayImage(strPic, mMediaPhoto, options2);
                        mMediaPhoto.setVisibility(View.VISIBLE);
                        mMediaVideoView.setVisibility(View.GONE);
                    }
                }
            }
            else if (chatMessage.getMessage().getService() != null) {
                mMsgTextView.setVisibility(View.GONE);
                mMsgMediaView.setVisibility(View.GONE);
                mMsgServiceView.setVisibility(View.VISIBLE);
                mMsgOfferView.setVisibility(View.GONE);

                if (chatMessage.getMessage().getService().getMedia() != null) {
                    if (chatMessage.getMessage().getService().getMedia().getFileName() != null && !chatMessage.getMessage().getService().getMedia().getFileName().isEmpty()) {
                        String strPic = chatMessage.getMessage().getService().getMedia().getFileName();
                        if (!strPic.contains("https://s3.amazonaws.com")) {
//                            strPic = "https://s3.amazonaws.com" + strPic;
                        }
                        ImageLoader.getInstance().displayImage(strPic, mServicePic, options2);
                    }
                }

                if (chatMessage.getMessage().getService().getDescription() != null && !chatMessage.getMessage().getService().getDescription().isEmpty()) {
                    mServiceDesc.setText(chatMessage.getMessage().getService().getDescription());
                }

                if (chatMessage.getMessage().getService().getPrice() != null) {
                    float valPrice = 0f;
                    Integer valTime = 1;
                    if (chatMessage.getMessage().getService().getPrice().getPrice() != null && chatMessage.getMessage().getService().getPrice().getPrice() > 0) {
                        valPrice = chatMessage.getMessage().getService().getPrice().getPrice();
                    }
                    if (chatMessage.getMessage().getService().getPrice().getTime() != null && chatMessage.getMessage().getService().getPrice().getTime() > 0) {
                        valTime = chatMessage.getMessage().getService().getPrice().getTime();
                    }
                    String strSymbol = "$", strUnit = "hour";
                    if (chatMessage.getMessage().getService().getPrice().getCurrencySymbol() != null && !chatMessage.getMessage().getService().getPrice().getCurrencySymbol().isEmpty()) {
                        strSymbol = chatMessage.getMessage().getService().getPrice().getCurrencySymbol();
                    }
                    if (chatMessage.getMessage().getService().getPrice().getTimeUnitOfMeasure() != null && !chatMessage.getMessage().getService().getPrice().getTimeUnitOfMeasure().isEmpty()) {
                        strUnit = chatMessage.getMessage().getService().getPrice().getTimeUnitOfMeasure();
                    }

                    if (valTime > 1) {
                        mServicePrice.setText(strSymbol + String.valueOf(valPrice));
                        mServiceDuration.setText("for " + String.valueOf(valTime) + " " + strUnit + "s");
                    } else {
                        mServicePrice.setText(strSymbol + String.valueOf(valPrice));
                        mServiceDuration.setText("for " + String.valueOf(valTime) + " " + strUnit);
                    }
                }

                if (chatMessage.getMessage().getService().getSeller() != null) {
                    String strFirst = "", strLast = "";
                    if (chatMessage.getMessage().getService().getSeller().getFirstName() != null && !chatMessage.getMessage().getService().getSeller().getFirstName().isEmpty()) {
                        strFirst = chatMessage.getMessage().getService().getSeller().getFirstName();
                    }
                    if (chatMessage.getMessage().getService().getSeller().getLastName() != null && !chatMessage.getMessage().getService().getSeller().getLastName().isEmpty()) {
                        strLast = chatMessage.getMessage().getService().getSeller().getLastName();
                    }
                    mServiceName.setText(strFirst + " " + strLast);
                }
            }
            else if (chatMessage.getMessage().getOffer() != null) {
                if (chatMessage.getMessage().getOffer().getService() != null) {
                    mMsgTextView.setVisibility(View.GONE);
                    mMsgMediaView.setVisibility(View.GONE);
                    mMsgServiceView.setVisibility(View.GONE);
                    mMsgOfferView.setVisibility(View.VISIBLE);
                } else {
                    mMsgTextView.setVisibility(View.VISIBLE);
                    mMsgMediaView.setVisibility(View.GONE);
                    mMsgServiceView.setVisibility(View.GONE);
                    mMsgOfferView.setVisibility(View.GONE);
                    mMsgTextView.setOnClickListener(this);
                }

                Integer valPrice = 0, valTime = 1;
                if (chatMessage.getMessage().getOffer().getPrice() != null && chatMessage.getMessage().getOffer().getPrice() > 0) {
                    valPrice = chatMessage.getMessage().getOffer().getPrice();
                }
                if (chatMessage.getMessage().getOffer().getWorkDuration() != null && chatMessage.getMessage().getOffer().getWorkDuration() > 0) {
                    valTime = chatMessage.getMessage().getOffer().getWorkDuration();
                }
                String strSymbol = "$", strUnit = "hour";
                if (chatMessage.getMessage().getOffer().getCurrencySymbol() != null && !chatMessage.getMessage().getOffer().getCurrencySymbol().isEmpty()) {
                    strSymbol = chatMessage.getMessage().getOffer().getCurrencySymbol();
                }
                if (chatMessage.getMessage().getOffer().getWorkDurationUom() != null && !chatMessage.getMessage().getOffer().getWorkDurationUom().isEmpty()) {
                    strUnit = chatMessage.getMessage().getOffer().getWorkDurationUom();
                }

                if (valTime > 1) {
                    if (chatMessage.getMessage().getOffer().getService() != null) {
                        mOfferDesc.setText("Custom offer " + strSymbol + String.valueOf(valPrice) + " Service for " + String.valueOf(valTime) + " " + strUnit + "s");
                    } else {
                        mMsgTextView.setText("Custom offer " + strSymbol + String.valueOf(valPrice) + " Service for " + String.valueOf(valTime) + " " + strUnit + "s");
                    }
                } else {
                    if (chatMessage.getMessage().getOffer().getService() != null) {
                        mOfferDesc.setText("Custom offer " + strSymbol + String.valueOf(valPrice) + " Service for " + String.valueOf(valTime) + " " + strUnit);
                    } else {
                        mMsgTextView.setText("Custom offer " + strSymbol + String.valueOf(valPrice) + " Service for " + String.valueOf(valTime) + " " + strUnit);
                    }
                }

                if (chatMessage.getMessage().getOffer().getService() != null) {
                    if (chatMessage.getMessage().getOffer().getService().getMedia() != null) {
                        if (chatMessage.getMessage().getOffer().getService().getMedia().getFileName() != null && !chatMessage.getMessage().getOffer().getService().getMedia().getFileName().isEmpty()) {
                            String strPic = chatMessage.getMessage().getOffer().getService().getMedia().getFileName();
                            if (!strPic.contains("https://s3.amazonaws.com")) {
//                                strPic = "https://s3.amazonaws.com" + strPic;
                            }
                            ImageLoader.getInstance().displayImage(strPic, mOfferServicePic, options2);
                        }
                    }

                    if (chatMessage.getMessage().getOffer().getService().getDescription() != null && !chatMessage.getMessage().getOffer().getService().getDescription().isEmpty()) {
                        mOfferServiceDesc.setText(chatMessage.getMessage().getOffer().getService().getDescription());
                    }

                    if (chatMessage.getMessage().getOffer().getService().getPrice() != null) {
                        float valServicePrice = 0f;
                        Integer valServiceTime = 1;
                        if (chatMessage.getMessage().getOffer().getService().getPrice().getPrice() != null && chatMessage.getMessage().getOffer().getService().getPrice().getPrice() > 0) {
                            valServicePrice = chatMessage.getMessage().getOffer().getService().getPrice().getPrice();
                        }
                        if (chatMessage.getMessage().getOffer().getService().getPrice().getTime() != null && chatMessage.getMessage().getOffer().getService().getPrice().getTime() > 0) {
                            valServiceTime = chatMessage.getMessage().getOffer().getService().getPrice().getTime();
                        }
                        String strServiceSymbol = "$", strServiceUnit = "hour";
                        if (chatMessage.getMessage().getOffer().getService().getPrice().getCurrencySymbol() != null && !chatMessage.getMessage().getOffer().getService().getPrice().getCurrencySymbol().isEmpty()) {
                            strServiceSymbol = chatMessage.getMessage().getOffer().getService().getPrice().getCurrencySymbol();
                        }
                        if (chatMessage.getMessage().getOffer().getService().getPrice().getTimeUnitOfMeasure() != null && !chatMessage.getMessage().getOffer().getService().getPrice().getTimeUnitOfMeasure().isEmpty()) {
                            strServiceUnit = chatMessage.getMessage().getOffer().getService().getPrice().getTimeUnitOfMeasure();
                        }

                        if (valTime > 1) {
                            mOfferServicePrice.setText(strServiceSymbol + String.valueOf(valServicePrice) + " for " + String.valueOf(valServiceTime) + " " + strServiceUnit + "s");
                        } else {
                            mOfferServicePrice.setText(strServiceSymbol + String.valueOf(valServicePrice) + " for " + String.valueOf(valServiceTime) + " " + strServiceUnit);
                        }
                    }

                    if (chatMessage.getMessage().getOffer().getService().getSeller() != null) {
                        String strFirst = "", strLast = "";
                        if (chatMessage.getMessage().getOffer().getService().getSeller().getFirstName() != null && !chatMessage.getMessage().getOffer().getService().getSeller().getFirstName().isEmpty()) {
                            strFirst = chatMessage.getMessage().getOffer().getService().getSeller().getFirstName();
                        }
                        if (chatMessage.getMessage().getOffer().getService().getSeller().getLastName() != null && !chatMessage.getMessage().getOffer().getService().getSeller().getLastName().isEmpty()) {
                            strLast = chatMessage.getMessage().getOffer().getService().getSeller().getLastName();
                        }
                        mOfferServiceName.setText(strFirst + " " + strLast);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_chat_msg_photo:
                String strType = "";
                if (message.getMessage().getMedia().get(0).getMediaType() != null && !message.getMessage().getMedia().get(0).getMediaType().isEmpty()) {
                    strType = message.getMessage().getMedia().get(0).getMediaType();
                }

                if (message.getMessage().getMedia().get(0).getFileName() != null && !message.getMessage().getMedia().get(0).getFileName().isEmpty()) {
                    String strPic  = message.getMessage().getMedia().get(0).getFileName();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
//                        strPic = "https://s3.amazonaws.com" + strPic;
                    }

                    if (strType.equals("video")) {
                        MediaPlayerActivity.start(mContext, strPic);
                    } else {
                        Intent intentPhoto = new Intent(mContext, PhotoViewerActivity.class);
                        intentPhoto.putExtra(ConstantUtils.CHAT_TAP_PHOTO, strPic);
                        mContext.startActivity(intentPhoto);
                    }
                }

                break;

            case R.id.layout_chat_msg_service:
                if (message.getMessage().getService() != null){
                    Intent intentService = new Intent(mContext, ServiceDetailActivity.class);
                    intentService.putExtra(ConstantUtils.SERVICE_ID, message.getMessage().getService().getServiceId());
                    mContext.startActivity(intentService);
                }else if (message.getMessage().getOffer() != null) {
                    Intent intentOffer = new Intent(mContext, CustomOfferDetailsActivity.class);
                    intentOffer.putExtra(ConstantUtils.SELECT_OFFER_ID, message.getMessage().getOffer().getOfferId());
                    mContext.startActivity(intentOffer);
                }
                break;

            case R.id.layout_chat_msg_offer:
                if (message.getMessage().getOffer() != null) {
                    if (message.getMessage().getOffer().getOfferId() != null && !message.getMessage().getOffer().getOfferId().isEmpty()) {
                        String offerId = message.getMessage().getOffer().getOfferId();

                        if (message.getUser().getUserId() != null && message.getUser().getUserId().equals(mLoginUserId)) {
                            Intent intentSend = new Intent(mContext, SendCustomOfferActivity.class);
                            intentSend.putExtra(ConstantUtils.SELECT_OFFER_ID, offerId);
                            intentSend.putExtra(ConstantUtils.CHAT_OFFER_DIRECTION, 2);
                            ((ChatActivity)mContext).startActivityForResult(intentSend, mRequestCode);
                        } else {
                            Intent intentOffer = new Intent(mContext, CustomOfferDetailsActivity.class);
                            intentOffer.putExtra(ConstantUtils.SELECT_OFFER_ID, offerId);
                            mContext.startActivity(intentOffer);
                        }
                    }
                }
                break;

            case R.id.layout_chat_msg_text:
                if (message.getMessage().getOffer() != null) {
                    if (message.getMessage().getOffer().getOfferId() != null && !message.getMessage().getOffer().getOfferId().isEmpty()) {
                        String offerId = message.getMessage().getOffer().getOfferId();

                        if (message.getUser().getUserId() != null && message.getUser().getUserId().equals(mLoginUserId)) {
                            Intent intentSend = new Intent(mContext, SendCustomOfferActivity.class);
                            intentSend.putExtra(ConstantUtils.SELECT_OFFER_ID, offerId);
                            intentSend.putExtra(ConstantUtils.CHAT_OFFER_DIRECTION, 2);
                            ((ChatActivity)mContext).startActivityForResult(intentSend, mRequestCode);
                        } else {
                            Intent intentOffer = new Intent(mContext, CustomOfferDetailsActivity.class);
                            intentOffer.putExtra(ConstantUtils.SELECT_OFFER_ID, offerId);
                            mContext.startActivity(intentOffer);
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

}
