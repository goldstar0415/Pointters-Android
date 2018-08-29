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
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.pointters.R;
import com.pointters.adapter.ExpandableRecyclerView;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.NotificationOption;
import com.pointters.model.request.UserPutSettingsRequest;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class NotificationOptionsActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener, OnApiFailDueToSessionListener {

    private ArrayList<NotificationOption> mNotificationOption = new ArrayList<>();
    private ExpandableRecyclerView recyclerViewNotificationOption;
    private NotificationOptionAdapter notificationOptionAdapter;
    private SharedPreferences sharedPreferences;
    private String json, generalNotifications, orderNotifications, offerNotification, emailstatusSetting;
    private UserPutSettingsRequest userPutSettingsRequest;
    private String dailyWeeklySeletedItem="none", phoneViewPermission, locationViewPermission;
    private TextView btnSave;
    private KProgressHUD loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_options);

        //set toolbar
        AppUtils.setToolBarWithBothIcon(NotificationOptionsActivity.this, getResources().getString(R.string.notification_option), R.drawable.back_icon, 0);
        btnSave = (TextView) findViewById(R.id.btnSave);

        btnSave.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);


        recyclerViewNotificationOption = (ExpandableRecyclerView) findViewById(R.id.rv_notification_option);
        CallGetUserSetting();

    }

    public void CallGetUserSetting() {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<UserPutSettingsRequest> customOfferDetailsCall = apiService.getUserSettings(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        customOfferDetailsCall.enqueue(new Callback<UserPutSettingsRequest>() {
            @Override
            public void onResponse(Call<UserPutSettingsRequest> call, Response<UserPutSettingsRequest> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    userPutSettingsRequest = response.body();
                    setUserSettings();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(NotificationOptionsActivity.this, "callCustomOfferDetailsApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(NotificationOptionsActivity.this);
                }
                else if (response.code() == 404) {
                    Toast.makeText(NotificationOptionsActivity.this, "Can't get the offer info!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserPutSettingsRequest> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(NotificationOptionsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setUserSettings() {
        generalNotifications = userPutSettingsRequest.getGeneralNotifications();
        orderNotifications = userPutSettingsRequest.getOrderNotifications();
        offerNotification = userPutSettingsRequest.getOfferNotifications();
        emailstatusSetting = userPutSettingsRequest.getSummaryEmail();
        phoneViewPermission = userPutSettingsRequest.getPhoneViewPermission();
        locationViewPermission = userPutSettingsRequest.getLocationViewPermission();
        NotificationOption notificationOption = new NotificationOption();
        int selectedgeneralNotifications = 0;
        if (generalNotifications.equals("pushNotification")) {
            selectedgeneralNotifications = 0;
        }else if (generalNotifications.equals("email")) {
            selectedgeneralNotifications = 1;
        }else if (generalNotifications.equals("all")) {
            selectedgeneralNotifications = 2;
        }else{
            selectedgeneralNotifications = 3;
        }

        notificationOption.setHeader("General Notifications");
        notificationOption.setSubHeader("(Point,Like,Comment,Follow)");
        notificationOption.setSelectedItmes(selectedgeneralNotifications);
        mNotificationOption.add(notificationOption);

        int selectedorderNotifications = 0;
        if (orderNotifications.equals("pushNotification")) {
            selectedorderNotifications = 0;
        }else if (orderNotifications.equals("email")) {
            selectedorderNotifications = 1;
        }else if (orderNotifications.equals("all")) {
            selectedorderNotifications = 2;
        }else{
            selectedorderNotifications = 3;
        }
        NotificationOption notificationOption1 = new NotificationOption();
        notificationOption1.setHeader("Order Notifications");
        notificationOption1.setSubHeader("(Order updates, Custom offers)");
        notificationOption1.setSelectedItmes(selectedorderNotifications);
        mNotificationOption.add(notificationOption1);

        int selectedofferNotification = 0;
        if (offerNotification.equals("pushNotification")) {
            selectedofferNotification = 0;
        }else if (offerNotification.equals("email")) {
            selectedofferNotification = 1;
        }else if (offerNotification.equals("all")) {
            selectedofferNotification = 2;
        }else{
            selectedofferNotification = 3;
        }
        NotificationOption notificationOption2 = new NotificationOption();
        notificationOption2.setHeader("Offer Notifications");
        notificationOption2.setSubHeader("(Live Request)");
        notificationOption2.setSelectedItmes(selectedofferNotification);
        mNotificationOption.add(notificationOption2);

        int selectedemailStatus = 0;
        if (emailstatusSetting.equals("daily")) {
            selectedemailStatus = 0;
        }else if (emailstatusSetting.equals("weekly")) {
            selectedemailStatus = 1;
        }else if (emailstatusSetting.equals("all")) {
            selectedemailStatus = 2;
        }else{
            selectedemailStatus = 3;
        }

        NotificationOption notificationOption3 = new NotificationOption();
        notificationOption3.setHeader("Email Status");
        notificationOption3.setSubHeader("");
        notificationOption3.setSelectedItmes(selectedemailStatus);
        mNotificationOption.add(notificationOption3);

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
                    if (mNotificationOption.get(0).getSelectedItmes() == 0){
                        generalNotifications = "pushNotification";
                    }else if (mNotificationOption.get(0).getSelectedItmes() == 1){
                        generalNotifications = "email";
                    }else if (mNotificationOption.get(0).getSelectedItmes() == 2){
                        generalNotifications = "all";
                    }else{
                        generalNotifications = "none";
                    }
                    if (mNotificationOption.get(1).getSelectedItmes() == 0){
                        orderNotifications = "pushNotification";
                    }else if (mNotificationOption.get(1).getSelectedItmes() == 1){
                        orderNotifications = "email";
                    }else if (mNotificationOption.get(1).getSelectedItmes() == 2){
                        orderNotifications = "all";
                    }else{
                        orderNotifications = "none";
                    }
                    if (mNotificationOption.get(2).getSelectedItmes() == 0){
                        offerNotification = "pushNotification";
                    }else if (mNotificationOption.get(2).getSelectedItmes() == 1){
                        offerNotification = "email";
                    }else if (mNotificationOption.get(2).getSelectedItmes() == 2){
                        offerNotification = "all";
                    }else{
                        offerNotification = "none";
                    }
                    if (mNotificationOption.get(3).getSelectedItmes() == 0){
                        emailstatusSetting = "daily";
                    }else if (mNotificationOption.get(3).getSelectedItmes() == 1){
                        emailstatusSetting = "weekly";
                    }else if (mNotificationOption.get(3).getSelectedItmes() == 2){
                        emailstatusSetting = "all";
                    }else{
                        emailstatusSetting = "none";
                    }

                    loader.show();
                    userPutSettingsRequest = new UserPutSettingsRequest(phoneViewPermission, locationViewPermission, generalNotifications, orderNotifications, offerNotification, emailstatusSetting);
                    ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
                    Call<Object> putUserSetting = apiService.putUserSettings(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), userPutSettingsRequest);
                    putUserSetting.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            loader.dismiss();
                            if (response.code() == 200) {
                                onBackPressed();
                            } else if (response.code() == 400) {
                                AndroidUtils.showToast(NotificationOptionsActivity.this, "Please choose at east one option");
                            }

                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            loader.dismiss();
                        }
                    });
                }


                break;
        }

    }

    @Override
    public void onItemClick(int position) {
        btnSave.setSelected(true);
        btnSave.setBackground(ContextCompat.getDrawable(NotificationOptionsActivity.this, R.color.colorAccent));

    }

    @Override
    public void onApiFail(String apiSource) {

    }

    //=====================================Notification Adapter=========================================
    public class NotificationOptionAdapter extends ExpandableRecyclerView.Adapter<NotificationOptionAdapter.ChildViewHolder, NotificationOptionAdapter.GroupViewHolder, String, String> implements View.OnClickListener {
        private Context context;
        private ArrayList<NotificationOption> notification_options;
        private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

        public NotificationOptionAdapter(Context context, ArrayList<NotificationOption> notification_options, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
            this.context = context;
            this.notification_options = notification_options;
            this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
        }

        @Override
        protected GroupViewHolder onCreateGroupViewHolder(ViewGroup viewGroup) {
            return new NotificationOptionAdapter.GroupViewHolder(context);
        }


        @Override
        public int getGroupItemCount() {
            return notification_options.size() - 1;
        }

        @Override
        public int getChildItemCount(int group) {
            return 2;
        }

        @Override
        public String getGroupItem(int position) {
            if (position < notification_options.size()) {
                return notification_options.get(position).getHeader();
            }else{
                return "";
            }
        }

        public String getGroupSubItem(int position) {
            if (position < notification_options.size()) {
                return notification_options.get(position).getSubHeader();
            }else{
                return "";
            }
        }

        @Override
        public String getChildItem(int group, int position) {
            if (group == 3) {
                switch (position){
                    case 0:
                        return "Daily";
                    case 1:
                        return "Weekly";
                    case 2:
                        return "All";
                    case 3:
                        return "None";
                }
            }else {
                switch (position){
                    case 0:
                        return "Push";
                    case 1:
                        return "Email";
                    case 2:
                        return "All";
                    case 3:
                        return "None";
                }
            }
            return "";
        }


        @Override
        protected NotificationOptionAdapter.ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.adapter_user_setting, parent, false);
            return new NotificationOptionAdapter.ChildViewHolder(view);
        }

        @Override
        public int getChildItemViewType(int group, int position) {
            return 1;
        }


        @Override
        public void onBindGroupViewHolder(NotificationOptionAdapter.GroupViewHolder holder, int group) {
            super.onBindGroupViewHolder(holder, group);
            holder.setText(getGroupItem(group));
            holder.setSubtext(getGroupSubItem(group));
            holder.setRightText(getChildItem(group, notification_options.get(group).getSelectedItmes()));
        }

        @Override
        public void onBindChildViewHolder(NotificationOptionAdapter.ChildViewHolder holder, int group, final int position) {
            super.onBindChildViewHolder(holder, group, position);
            holder.tv.setText(getChildItem(group, position));
            if (notification_options.get(group).getSelectedItmes() == 2) {
                holder.checkbox.setSelected(true);
            }else if(notification_options.get(group).getSelectedItmes() == 3){
                holder.checkbox.setSelected(false);
            }else{
                if (position == notification_options.get(group).getSelectedItmes()) {
                    holder.checkbox.setSelected(true);
                }else{
                    holder.checkbox.setSelected(false);
                }
            }
            holder.checkbox.setTag(group+"0"+position);
            holder.checkbox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getClass().equals(ImageButton.class)) {
                ((ImageButton)v).setSelected(!((ImageButton)v).isSelected());
            }
            this.onRecyclerViewItemClickListener.onItemClick(0);
            NotificationOption model;
            switch ((String)v.getTag()) {
                case "000":
                    model = notification_options.get(0);
                    if (model.getSelectedItmes() == 0){
                        model.setSelectedItmes(3);
                    }else if (model.getSelectedItmes() == 1){
                        model.setSelectedItmes(2);
                    }else if (model.getSelectedItmes() == 2) {
                        model.setSelectedItmes(1);
                    }else{
                        model.setSelectedItmes(0);
                    }
                    notification_options.set(0, model);
                    notifyDataSetChanged();
                    break;
                case "001":
                    model = notification_options.get(0);
                    if (model.getSelectedItmes() == 0){
                        model.setSelectedItmes(2);
                    }else if (model.getSelectedItmes() == 1){
                        model.setSelectedItmes(3);
                    }else if (model.getSelectedItmes() == 2) {
                        model.setSelectedItmes(0);
                    }else{
                        model.setSelectedItmes(1);
                    }
                    notification_options.set(0, model);
                    notifyDataSetChanged();
                    break;
                case "100":
                    model = notification_options.get(1);
                    if (model.getSelectedItmes() == 0){
                        model.setSelectedItmes(3);
                    }else if (model.getSelectedItmes() == 1){
                        model.setSelectedItmes(2);
                    }else if (model.getSelectedItmes() == 2) {
                        model.setSelectedItmes(1);
                    }else{
                        model.setSelectedItmes(0);
                    }
                    notification_options.set(1, model);
                    notifyDataSetChanged();
                    break;
                case "101":
                    model = notification_options.get(1);
                    if (model.getSelectedItmes() == 0){
                        model.setSelectedItmes(2);
                    }else if (model.getSelectedItmes() == 1){
                        model.setSelectedItmes(3);
                    }else if (model.getSelectedItmes() == 2) {
                        model.setSelectedItmes(0);
                    }else{
                        model.setSelectedItmes(1);
                    }
                    notification_options.set(1, model);
                    notifyDataSetChanged();
                    break;
                case "200":
                    model = notification_options.get(2);
                    if (model.getSelectedItmes() == 0){
                        model.setSelectedItmes(3);
                    }else if (model.getSelectedItmes() == 1){
                        model.setSelectedItmes(2);
                    }else if (model.getSelectedItmes() == 2) {
                        model.setSelectedItmes(1);
                    }else{
                        model.setSelectedItmes(0);
                    }
                    notification_options.set(2, model);
                    notifyDataSetChanged();
                    break;
                case "201":
                    model = notification_options.get(2);
                    if (model.getSelectedItmes() == 0){
                        model.setSelectedItmes(2);
                    }else if (model.getSelectedItmes() == 1){
                        model.setSelectedItmes(3);
                    }else if (model.getSelectedItmes() == 2) {
                        model.setSelectedItmes(0);
                    }else{
                        model.setSelectedItmes(1);
                    }
                    notification_options.set(2, model);
                    notifyDataSetChanged();
                    break;
                case "300":
                    model = notification_options.get(3);
                    if (model.getSelectedItmes() == 0){
                        model.setSelectedItmes(3);
                    }else if (model.getSelectedItmes() == 1){
                        model.setSelectedItmes(2);
                    }else if (model.getSelectedItmes() == 2) {
                        model.setSelectedItmes(1);
                    }else{
                        model.setSelectedItmes(0);
                    }
                    notification_options.set(3, model);
                    notifyDataSetChanged();
                    break;
                case "301":
                    model = notification_options.get(3);
                    if (model.getSelectedItmes() == 0){
                        model.setSelectedItmes(2);
                    }else if (model.getSelectedItmes() == 1){
                        model.setSelectedItmes(3);
                    }else if (model.getSelectedItmes() == 2) {
                        model.setSelectedItmes(0);
                    }else{
                        model.setSelectedItmes(1);
                    }
                    notification_options.set(3, model);
                    notifyDataSetChanged();
                    break;
            }
        }

        public class GroupViewHolder extends ExpandableRecyclerView.GroupViewHolder {
            ImageView expandedIndicator;
            TextView text;
            TextView subtext;
            TextView rightText;
            private boolean expanded;

            public GroupViewHolder(Context context) {
                super(View.inflate(context, R.layout.adapter_notification_setting_group, (ViewGroup)null));
                this.itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                this.expandedIndicator = (ImageView)this.itemView.findViewById(R.id.carbon_groupExpandedIndicator);
                this.text = (TextView)this.itemView.findViewById(R.id.carbon_groupText);
                this.subtext = (TextView)this.itemView.findViewById(R.id.carbon_groupsubText);
                this.rightText = (TextView)this.itemView.findViewById(R.id.carbon_grouprightText);
            }

            public void expand() {
                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F});
                animator.setInterpolator(new DecelerateInterpolator());
                animator.setDuration(200L);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ViewHelper.setRotation(NotificationOptionAdapter.GroupViewHolder.this.expandedIndicator, 180.0F * ((Float)animation.getAnimatedValue()).floatValue());
                        NotificationOptionAdapter.GroupViewHolder.this.expandedIndicator.postInvalidate();
                    }
                });
                animator.start();
                this.expanded = true;
            }

            public void collapse() {
                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{1.0F, 0.0F});
                animator.setInterpolator(new DecelerateInterpolator());
                animator.setDuration(200L);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ViewHelper.setRotation(NotificationOptionAdapter.GroupViewHolder.this.expandedIndicator, 180.0F * ((Float)animation.getAnimatedValue()).floatValue());
                        NotificationOptionAdapter.GroupViewHolder.this.expandedIndicator.postInvalidate();
                    }
                });
                animator.start();
                this.expanded = false;
            }

            public void setExpanded(boolean expanded) {
                ViewHelper.setRotation(this.expandedIndicator, expanded?180.0F:0.0F);
                this.expanded = expanded;
            }

            public boolean isExpanded() {
                return this.expanded;
            }

            public void setText(String t) {
                this.text.setText(t);
            }

            public void setSubtext(String t) {
                this.subtext.setText(t);
            }

            public void setRightText(String t) {
                this.rightText.setText(t);
            }

            public String getText() {
                return this.text.getText().toString();
            }
        }

        public class ChildViewHolder extends RecyclerView.ViewHolder {
            private final TextView tv;
            private final ImageButton checkbox;

            public ChildViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.txt_title);
                checkbox = (ImageButton)itemView.findViewById(R.id.checkbox);
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
