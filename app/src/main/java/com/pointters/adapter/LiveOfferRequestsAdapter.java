package com.pointters.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.LiveOfferRequestsModel;
import com.pointters.model.ReceivedOfferModel;
import com.pointters.utils.CommonUtils;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by mac on 12/4/17.
 */

public class LiveOfferRequestsAdapter extends RecyclerView.Adapter<LiveOfferRequestsAdapter.MyViewHolder> {
    private Context context;
    private List<LiveOfferRequestsModel> liveOfferrequestsList;
    private OnRecyclerViewButtonClickListener listener;

    public LiveOfferRequestsAdapter(Context context, List<LiveOfferRequestsModel> liveOfferrequestsList, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.liveOfferrequestsList = liveOfferrequestsList;
        this.listener = listener;
    }

    @Override
    public LiveOfferRequestsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_live_offer_request, parent, false);
        return new LiveOfferRequestsAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(LiveOfferRequestsAdapter.MyViewHolder holder, int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

//        if (position == 0) {
//            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
//            holder.layoutParent.setLayoutParams(holder.layoutParams);
//        }
        if (liveOfferrequestsList != null && liveOfferrequestsList.size() > 0) {
            if (liveOfferrequestsList.get(position).getRequests() != null) {
                if (liveOfferrequestsList.get(position).getRequests().getMedia() != null && liveOfferrequestsList.get(position).getRequests().getMedia().size() > 0) {
                    if (liveOfferrequestsList.get(position).getRequests().getMedia().get(0).getFileName() != null && !liveOfferrequestsList.get(position).getRequests().getMedia().get(0).getFileName().isEmpty()) {
                        String strPic = liveOfferrequestsList.get(position).getRequests().getMedia().get(0).getFileName();
                        if (!strPic.contains("https://s3.amazonaws.com")) {
//                            strPic = "https://s3.amazonaws.com" + strPic;
                        }
                        ImageLoader.getInstance().displayImage(strPic, holder.imgProfile, options);
                    }
                }

                if (liveOfferrequestsList.get(position).getRequests().getDescription() != null && !liveOfferrequestsList.get(position).getRequests().getDescription().isEmpty())
                    holder.txtOfferDesc.setText(liveOfferrequestsList.get(position).getRequests().getDescription());
                else
                    holder.txtOfferDesc.setText("NA");

                if (liveOfferrequestsList.get(position).getRequests().getCreatedAt() != null && !liveOfferrequestsList.get(position).getRequests().getCreatedAt().isEmpty()) {
//                    TimeZone tz = TimeZone.getTimeZone("UTC");
//                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//                    df.setTimeZone(tz);
//
//                    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy  hh:mm a");
//                    try {
//                        holder.txtCreateddate.setText(""+ String.valueOf(fmtOut.format(df.parse(liveOfferrequestsList.get(position).getRequests().getCreatedAt()))));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
                    long timeInMilliseconds = DateTimeUtils.formatDate(liveOfferrequestsList.get(position).getRequests().getCreatedAt()).getTime();
                    String date = CommonUtils.getDateDuration(timeInMilliseconds);
                    holder.txtCreateddate.setText(date);
                }
                else
                    holder.txtCreateddate.setText("NA");

                if (liveOfferrequestsList.get(position).getRequests().getNumOffers() != null) {
                    holder.txtOffersCnt.setText(liveOfferrequestsList.get(position).getRequests().getNumOffers()+"");
                }
                else
                    holder.txtOffersCnt.setText("0");

//                if (liveOfferrequestsList.get(position).getRequests().getNumNewOffers() != null) {
//                    holder.txtNewCnt.setText(liveOfferrequestsList.get(position).getRequests().getNumNewOffers()+" New offers");
//                }
//                else
//                    holder.txtNewCnt.setText("0 New offers");

                if (liveOfferrequestsList.get(position).getRequests().getLow() != null && liveOfferrequestsList.get(position).getRequests().getHigh() != null) {
                    holder.txtPriceRange.setText("$"+liveOfferrequestsList.get(position).getRequests().getLow()+"-"+liveOfferrequestsList.get(position).getRequests().getHigh());
                }
                else
                    holder.txtPriceRange.setText("NA");

                if (liveOfferrequestsList.get(position).getRequests().getExpiresIn() != null) {
                    if (liveOfferrequestsList.get(position).getRequests().getExpiresIn() < 0) {
                        holder.txtValidity.setText("Expired");
                    } else {
                        holder.txtValidity.setText("Expires in " + liveOfferrequestsList.get(position).getRequests().getExpiresIn() + " days");
                    }
                }
                else
                    holder.txtValidity.setText("Expires in NA days");
            }
        }

    }

    @Override
    public int getItemCount() {
        return liveOfferrequestsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgProfile;
        private TextView txtOfferDesc,txtOffersCnt,txtPriceRange,txtCreateddate,txtValidity;
        Button txtNewCnt;
        private LinearLayout layoutParent;
        RelativeLayout layoutUpper;
        private FrameLayout.LayoutParams layoutParams;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;

        public MyViewHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            layoutParent=(LinearLayout)itemView.findViewById(R.id.layout_parent);
            layoutParams=( FrameLayout.LayoutParams)layoutParent.getLayoutParams();

            imgProfile=(ImageView)itemView.findViewById(R.id.img_profile);
            txtOfferDesc=(TextView)itemView.findViewById(R.id.txt_offer_desc);
            txtCreateddate=(TextView)itemView.findViewById(R.id.txt_created_date);
            txtOffersCnt=(TextView)itemView.findViewById(R.id.txt_no_of_offers);

            layoutUpper=(RelativeLayout)itemView.findViewById(R.id.upper_view);
            layoutUpper.setOnClickListener(this);

            txtNewCnt=(Button) itemView.findViewById(R.id.btn_new_offers);
            txtNewCnt.setOnClickListener(this);

            txtPriceRange=(TextView)itemView.findViewById(R.id.txt_price_range);
            txtValidity=(TextView)itemView.findViewById(R.id.txt_validity);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }
}
