package com.pointters.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.NotificationOption;
import com.pointters.model.request.UserPutSettingsRequest;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class NotificationOptionsActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener {

    private ArrayList<NotificationOption> mNotificationOption = new ArrayList<>();
    private RecyclerView recyclerViewNotificationOption;
    private NotificationOptionAdapter notificationOptionAdapter;
    private SharedPreferences sharedPreferences;
    private String json, generalNotifications, orderNotifications, offerNotification;
    private ImageView imgDaily, imgWeekly;
    private UserPutSettingsRequest userPutSettingsRequest;
    private String dailyWeeklySeletedItem="none", phoneViewPermission, locationViewPermission;
    private TextView btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_options);

        //set toolbar
        AppUtils.setToolBarWithBothIcon(NotificationOptionsActivity.this, getResources().getString(R.string.notification_option), R.drawable.back_icon_grey, 0);
        imgDaily = (ImageView) findViewById(R.id.img_daily);
        imgWeekly = (ImageView) findViewById(R.id.img_weekly);
        btnSave = (TextView) findViewById(R.id.btnSave);

        imgDaily.setOnClickListener(this);
        imgWeekly.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null)
            json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");

        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("settings")) {
                JSONObject jsonObjectSettings = (JSONObject) jsonObject.get("settings");
                if (jsonObjectSettings.get("generalNotifications") != null && !jsonObjectSettings.get("generalNotifications").toString().isEmpty()) {
                    generalNotifications = jsonObjectSettings.get("generalNotifications").toString();

                }
                if (jsonObjectSettings.get("orderNotifications") != null && !jsonObjectSettings.get("orderNotifications").toString().isEmpty()) {
                    orderNotifications = jsonObjectSettings.get("orderNotifications").toString();

                }
                if (jsonObjectSettings.get("offerNotifications") != null && !jsonObjectSettings.get("offerNotifications").toString().isEmpty()) {
                    offerNotification = jsonObjectSettings.get("offerNotifications").toString();

                }
                if (jsonObjectSettings.get("summaryEmail") != null && !jsonObjectSettings.get("summaryEmail").toString().isEmpty()) {
                    if (jsonObjectSettings.get("summaryEmail").toString().equals("all")) {
                        dailyWeeklySeletedItem = "all";
                        imgDaily.setSelected(true);
                        imgWeekly.setSelected(true);
                    } else if (jsonObjectSettings.get("summaryEmail").toString().equals("daily")) {
                        dailyWeeklySeletedItem = "daily";
                        imgDaily.setSelected(true);
                    } else if (jsonObjectSettings.get("summaryEmail").toString().equals("weekly")) {
                        dailyWeeklySeletedItem = "weekly";
                        imgWeekly.setSelected(true);
                    }
                }
                if (jsonObjectSettings.get("phoneViewPermission") != null && !jsonObjectSettings.get("phoneViewPermission").toString().isEmpty()) {
                    phoneViewPermission = jsonObjectSettings.get("phoneViewPermission").toString();
                }
                if (jsonObjectSettings.get("locationViewPermission") != null && !jsonObjectSettings.get("locationViewPermission").toString().isEmpty()) {
                    locationViewPermission = jsonObjectSettings.get("locationViewPermission").toString();


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        recyclerViewNotificationOption = (RecyclerView) findViewById(R.id.rv_notification_option);
        NotificationOption notificationOption = new NotificationOption();

        notificationOption.setHeader("General Notifications");
        notificationOption.setSubHeader("(Point,Like,Comment,Follow)");
        notificationOption.setSelectedItmes(generalNotifications);
        mNotificationOption.add(notificationOption);

        NotificationOption notificationOption1 = new NotificationOption();
        notificationOption1.setHeader("Order Notifications");
        notificationOption1.setSubHeader("(Order updates, Custom offers)");
        notificationOption1.setSelectedItmes(orderNotifications);
        mNotificationOption.add(notificationOption1);

        NotificationOption notificationOption2 = new NotificationOption();
        notificationOption2.setHeader("Offer Notifications");
        notificationOption2.setSubHeader("(Live Request)");
        notificationOption2.setSelectedItmes(offerNotification);
        mNotificationOption.add(notificationOption2);

        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationOptionsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewNotificationOption.setLayoutManager(layoutManager);
        notificationOptionAdapter = new NotificationOptionAdapter(NotificationOptionsActivity.this, mNotificationOption, this);
        recyclerViewNotificationOption.setAdapter(notificationOptionAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(NotificationOptionsActivity.this, R.drawable.divider_option));
        recyclerViewNotificationOption.addItemDecoration(divider);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.toolbar_lft_img:

                onBackPressed();

                break;
            case R.id.btnSave:
                if(btnSave.isSelected()) {
                    userPutSettingsRequest = new UserPutSettingsRequest(phoneViewPermission, locationViewPermission, mNotificationOption.get(0).getSelectedItmes(), mNotificationOption.get(1).getSelectedItmes(), mNotificationOption.get(2).getSelectedItmes(), dailyWeeklySeletedItem);
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<Object> putUserSetting = apiService.putUserSettings(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), userPutSettingsRequest);
                    putUserSetting.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                onBackPressed();
                            } else if (response.code() == 400) {
                                AndroidUtils.showToast(NotificationOptionsActivity.this, "Please choose atleast one option");
                            }


                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {

                        }
                    });
                }


                break;
            case R.id.img_daily:
                btnSave.setBackground(ContextCompat.getDrawable(NotificationOptionsActivity.this, R.color.colorAccent));
                if (imgDaily.isSelected()) {
                    btnSave.setSelected(true);
                    imgDaily.setImageResource(R.drawable.ellipse_unchecked_grey);
                    imgDaily.setSelected(false);

                    if (imgWeekly.isSelected())
                        dailyWeeklySeletedItem = "weekly";
                    else
                        dailyWeeklySeletedItem = "none";

                } else {
                    btnSave.setSelected(true);
                    imgDaily.setImageResource(R.drawable.ellipse_checked_blue);
                    imgDaily.setSelected(true);
                    if (imgWeekly.isSelected())
                        dailyWeeklySeletedItem = "all";
                    else
                        dailyWeeklySeletedItem = "daily";
                }
                break;
            case R.id.img_weekly:
                btnSave.setBackground(ContextCompat.getDrawable(NotificationOptionsActivity.this, R.color.colorAccent));
                if (imgWeekly.isSelected()) {
                    btnSave.setSelected(true);
                    imgWeekly.setImageResource(R.drawable.ellipse_unchecked_grey);
                    imgWeekly.setSelected(false);
                    if (imgDaily.isSelected())
                        dailyWeeklySeletedItem = "daily";
                    else
                        dailyWeeklySeletedItem = "none";
                } else {
                    btnSave.setSelected(true);
                    imgWeekly.setImageResource(R.drawable.ellipse_checked_blue);
                    imgWeekly.setSelected(true);
                    if (imgDaily.isSelected())
                        dailyWeeklySeletedItem = "all";
                    else
                        dailyWeeklySeletedItem = "weekly";
                }
                break;
        }

    }

    @Override
    public void onItemClick(int position) {
        btnSave.setSelected(true);
        btnSave.setBackground(ContextCompat.getDrawable(NotificationOptionsActivity.this, R.color.colorAccent));

    }

    //=====================================Notification Adapter=========================================
    public class NotificationOptionAdapter extends RecyclerView.Adapter<NotificationOptionAdapter.ViewHolder> {
        private Context context;
        private ArrayList<NotificationOption> notification_options;
        private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

        public NotificationOptionAdapter(Context context, ArrayList<NotificationOption> notification_options, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
            this.context = context;
            this.notification_options = notification_options;
            this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_option_data_strip, parent, false);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if(notification_options.size()>0) {
                if(notification_options.get(position).getHeader()!=null && !notification_options.get(position).getHeader().isEmpty())
                holder.titleTextView.setText(notification_options.get(position).getHeader());
                if(notification_options.get(position).getSubHeader()!=null && !notification_options.get(position).getSubHeader().isEmpty())
                holder.subTitleTextView.setText(notification_options.get(position).getSubHeader());

                if (notification_options.get(position).getSelectedItmes()!=null && !notification_options.get(position).getSelectedItmes().isEmpty()) {

                    if (notification_options.get(position).getSelectedItmes().equals("all")) {
                        holder.imgPush.setSelected(true);
                        holder.imgEmail.setSelected(true);
                    } else if (notification_options.get(position).getSelectedItmes().equals("pushNotification")) {
                        holder.imgPush.setSelected(true);
                    } else if (notification_options.get(position).getSelectedItmes().equals("email")) {
                        holder.imgEmail.setSelected(true);
                    }
                }


                holder.imgPush.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRecyclerViewItemClickListener.onItemClick(position);

                        if (holder.imgPush.isSelected()) {
                            holder.imgPush.setImageResource(R.drawable.ellipse_unchecked_grey);
                            holder.imgPush.setSelected(false);

                            if (holder.imgEmail.isSelected())
                                notification_options.get(position).setSelectedItmes("email");
                            else
                                notification_options.get(position).setSelectedItmes("none");
                        } else {
                            holder.imgPush.setImageResource(R.drawable.ellipse_checked_blue);
                            holder.imgPush.setSelected(true);
                            if (holder.imgEmail.isSelected())
                                notification_options.get(position).setSelectedItmes("all");
                            else
                                notification_options.get(position).setSelectedItmes("pushNotification");
                        }

                    }
                });
                holder.imgEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRecyclerViewItemClickListener.onItemClick(position);
                        if (holder.imgEmail.isSelected()) {
                            holder.imgEmail.setImageResource(R.drawable.ellipse_unchecked_grey);
                            holder.imgEmail.setSelected(false);
                            if (holder.imgPush.isSelected())
                                notification_options.get(position).setSelectedItmes("pushNotification");
                            else
                                notification_options.get(position).setSelectedItmes("none");
                        } else {
                            holder.imgEmail.setImageResource(R.drawable.ellipse_checked_blue);
                            holder.imgEmail.setSelected(true);
                            if (holder.imgPush.isSelected())
                                notification_options.get(position).setSelectedItmes("all");
                            else
                                notification_options.get(position).setSelectedItmes("email");
                        }
                    }
                });
            }


        }

        @Override
        public int getItemCount() {

            return notification_options.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView titleTextView, subTitleTextView;
            private ImageView imgPush, imgEmail;

            public ViewHolder(View view) {

                super(view);
                titleTextView = (TextView) view.findViewById(R.id.title);
                subTitleTextView = (TextView) view.findViewById(R.id.subtitle);
                imgPush = (ImageView) view.findViewById(R.id.img_push);
                imgEmail = (ImageView) view.findViewById(R.id.img_email);


            }
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();

        setResult(Activity.RESULT_OK, intent);

        finish();
    }
}
