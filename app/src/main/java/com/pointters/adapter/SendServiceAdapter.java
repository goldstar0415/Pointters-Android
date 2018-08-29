package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.GetserviceSearch;
import com.pointters.model.SendServicesModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prashantkumar on 28/8/17.
 */

public class SendServiceAdapter extends RecyclerView.Adapter<SendServiceAdapter.MyViewHolder> {

    private Context context;
    private String username;
    private String serviceType;
    private List<SendServicesModel> sendServicesList = new ArrayList<>();
    private List<GetserviceSearch> searchServicesList = new ArrayList<>();
    private OnRecyclerViewButtonClickListener listener;

    private boolean isSearch = false;

    public SendServiceAdapter(Context context, String username, String type, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.username = username;
        this.serviceType = type;
        this.listener = listener;
    }

    public void setSendServicesList(List<SendServicesModel> arr){
        this.sendServicesList = arr;
    }

    public void setSearchServicesList(List<GetserviceSearch> arr){
        this.searchServicesList = arr;
    }

    public void setType(boolean type){
        this.isSearch = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_send_service, parent, false);
        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        if (isSearch) {
            if (searchServicesList != null && searchServicesList.size() > 0) {
                if (searchServicesList.get(position).get_source() != null) {
                    if (searchServicesList.get(position).get_source().getMedia() != null) {
                        if (searchServicesList.get(position).get_source().getMedia() != null) {
                            String strPic = searchServicesList.get(position).get_source().getMedia().get(0).getFileName();
                            if (!strPic.contains("https://s3.amazonaws.com")) {
//                            strPic = "https://s3.amazonaws.com" + strPic;
                            }
                            ImageLoader.getInstance().displayImage(strPic, holder.imgUser, options);
                        }

                        if (searchServicesList.get(position).get_source().getDescription() != null && !searchServicesList.get(position).get_source().getDescription().isEmpty()) {
                            holder.txtDesc.setText(searchServicesList.get(position).get_source().getDescription());
                        } else {
                            holder.txtDesc.setText("NA");
                        }

                        if (searchServicesList.get(position).get_source().getPrices() != null) {
                            String strSymbol="$", strPrice="", strTime="", strTimeUnit="";
                            if (searchServicesList.get(position).get_source().getPrices().size() > 0) {
                                if (searchServicesList.get(position).get_source().getPrices().get(0).getCurrencySymbol() != null )
                                    strSymbol = searchServicesList.get(position).get_source().getPrices().get(0).getCurrencySymbol();

                                if (searchServicesList.get(position).get_source().getPrices().get(0).getPrice() != null)
                                    strPrice = String.valueOf(searchServicesList.get(position).get_source().getPrices().get(0).getPrice());

                                if (searchServicesList.get(position).get_source().getPrices().get(0).getTime() != null)
                                    strTime = String.valueOf(searchServicesList.get(position).get_source().getPrices().get(0).getTime());

                                if (searchServicesList.get(position).get_source().getPrices().get(0).getTimeUnitOfMeasure() != null && !searchServicesList.get(position).get_source().getPrices().get(0).getTimeUnitOfMeasure().isEmpty())
                                    strTimeUnit = searchServicesList.get(position).get_source().getPrices().get(0).getTimeUnitOfMeasure();

                                String strDuration = strSymbol;
                                if (!strPrice.equals("")) { strDuration += strPrice; }
                                else                      { strDuration += "0"; }

                                if (!strTime.equals("") && !strTimeUnit.equals("")) {
                                    if (strTime.equals("1")) { strDuration += " for " + strTime + " " + strTimeUnit; }
                                    else                     { strDuration += " for " + strTime + " " + strTimeUnit + "s"; }
                                }

                                holder.txtduration.setText(strDuration);
                                holder.txtPrices.setText(strSymbol+strPrice);
                            }
                        } else {
                            holder.txtPrices.setText("NA");
                            holder.txtduration.setText("");
                        }
                    } else {
                        holder.txtDesc.setText("NA");
                        holder.txtPrices.setText("NA");
                        holder.txtduration.setText("");
                    }
                }

                if (!username.equals("")) { holder.txtName.setText(username); }
                else                      { holder.txtName.setText("NA");     }
            }
        }else{
            if (sendServicesList != null && sendServicesList.size() > 0) {
                if (sendServicesList.get(position).getService() != null) {
                    if (sendServicesList.get(position).getService().getMedia() != null) {
                        if (sendServicesList.get(position).getService().getMedia().getFileName() != null && !sendServicesList.get(position).getService().getMedia().getFileName().isEmpty()) {
                            String strPic = sendServicesList.get(position).getService().getMedia().getFileName();
                            if (!strPic.contains("https://s3.amazonaws.com")) {
//                            strPic = "https://s3.amazonaws.com" + strPic;
                            }
                            ImageLoader.getInstance().displayImage(strPic, holder.imgUser, options);
                        }

                        if (sendServicesList.get(position).getService().getDescription() != null && !sendServicesList.get(position).getService().getDescription().isEmpty()) {
                            holder.txtDesc.setText(sendServicesList.get(position).getService().getDescription());
                        } else {
                            holder.txtDesc.setText("NA");
                        }

                        if (sendServicesList.get(position).getService().getPrices() != null) {
                            String strSymbol="$", strPrice="", strTime="", strTimeUnit="";
                            if (sendServicesList.get(position).getService().getPrices().getCurrencySymbol() != null && !sendServicesList.get(position).getService().getPrices().getCurrencySymbol().isEmpty())
                                strSymbol = sendServicesList.get(position).getService().getPrices().getCurrencySymbol();

                            if (sendServicesList.get(position).getService().getPrices().getPrice() != null && sendServicesList.get(position).getService().getPrices().getPrice() > 0)
                                strPrice = String.valueOf(sendServicesList.get(position).getService().getPrices().getPrice());

                            if (sendServicesList.get(position).getService().getPrices().getTime() != null && sendServicesList.get(position).getService().getPrices().getTime() > 0)
                                strTime = String.valueOf(sendServicesList.get(position).getService().getPrices().getTime());

                            if (sendServicesList.get(position).getService().getPrices().getTimeUnitOfMeasure() != null && !sendServicesList.get(position).getService().getPrices().getTimeUnitOfMeasure().isEmpty())
                                strTimeUnit = sendServicesList.get(position).getService().getPrices().getTimeUnitOfMeasure();

                            String strDuration = strSymbol;
                            if (!strPrice.equals("")) { strDuration += strPrice; }
                            else                      { strDuration += "0"; }

                            if (!strTime.equals("") && !strTimeUnit.equals("")) {
                                if (strTime.equals("1")) { strDuration += " for " + strTime + " " + strTimeUnit; }
                                else                     { strDuration += " for " + strTime + " " + strTimeUnit + "s"; }
                            }

                            holder.txtduration.setText(strDuration);
                            holder.txtPrices.setText(strSymbol+strPrice);
                        } else {
                            holder.txtPrices.setText("NA");
                            holder.txtduration.setText("");
                        }
                    } else {
                        holder.txtduration.setText("");
                        holder.txtDesc.setText("NA");
                        holder.txtPrices.setText("NA");
                    }
                }

                if (!username.equals("")) { holder.txtName.setText(username); }
                else                      { holder.txtName.setText("NA");     }
            }
        }


        if (serviceType.equals("Send Service"))
            { holder.txtBtn.setText("SEND"); }
        else
            { holder.txtBtn.setText("LINK"); }
    }

    @Override
    public int getItemCount() {
        return isSearch ? searchServicesList.size(): sendServicesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private ImageView imgUser;
        private TextView txtDesc, txtName, txtPrices,  txtduration;
        private Button txtBtn;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;

        public MyViewHolder(View itemView, OnRecyclerViewButtonClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            imgUser=(ImageView)itemView.findViewById(R.id.img_profile);
            txtName=(TextView)itemView.findViewById(R.id.txt_login_username);
            txtDesc=(TextView)itemView.findViewById(R.id.txt_description);
            txtPrices=(TextView)itemView.findViewById(R.id.txt_price);
            txtduration=(TextView)itemView.findViewById(R.id.txt_duration);

            txtBtn=(Button)itemView.findViewById(R.id.btn_send_link);
            txtBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }
}
