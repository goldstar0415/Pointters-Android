package com.pointters.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.ExpandableRecyclerView;
import com.pointters.adapter.UserSettingAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.UserSettingsModel;
import com.pointters.model.request.UserPutSettingsRequest;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.DividerItemDecorationVer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class UserSettingsActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener, OnApiFailDueToSessionListener {
    private ExpandableRecyclerView recyclerViewUserSetting;
    private UserSettingAdapter userSettingAdapter;
    private List<UserSettingsModel> userSettingList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private TextView txtBtn;
    private String json, summaryEmail, offerNotifications, orderNotifications, generalNotifications, phoneViewPermission, locationViewPermission;
    private UserSettingsModel userSettingsModel;
    private UserPutSettingsRequest userPutSettingsRequest;
    private KProgressHUD loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        AppUtils.setToolBarWithBothIcon(UserSettingsActivity.this, getResources().getString(R.string.user_settings), R.drawable.back_icon, 0);

        txtBtn = (TextView) findViewById(R.id.txt_save);
        txtBtn.setOnClickListener(this);
        recyclerViewUserSetting = (ExpandableRecyclerView) findViewById(R.id.rv_user_settings);
        recyclerViewUserSetting.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUserSetting.setLayoutManager(new LinearLayoutManager(this));

        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        CallGetUserSetting();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;
            case R.id.txt_save:
                if(txtBtn.isSelected())
                {
                    String selectedLocation = "Public";
                    if (userSettingList.get(0).getSeletedItem() == 0){
                        selectedLocation = "public";
                    }else if (userSettingList.get(0).getSeletedItem() == 1){
                        selectedLocation = "followers";
                    }else{
                        selectedLocation = "onlyme";
                    }
                    String selectedPhone = "Public";
                    if (userSettingList.get(1).getSeletedItem() == 0){
                        selectedPhone = "public";
                    }else if (userSettingList.get(1).getSeletedItem() == 1){
                        selectedPhone = "followers";
                    }else{
                        selectedPhone = "onlyme";
                    }
                    loader.show();
                    userPutSettingsRequest = new UserPutSettingsRequest(selectedPhone, selectedLocation, generalNotifications, orderNotifications, offerNotifications, summaryEmail);
                    ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
                    Call<Object> putUserSetting = apiService.putUserSettings(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), userPutSettingsRequest);
                    putUserSetting.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            loader.dismiss();
                            if (response.code() == 200) {
                                onBackPressed();
                            } else if (response.code() == 400) {
                                AndroidUtils.showToast(UserSettingsActivity.this, "Please choose atleast one option");
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
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(UserSettingsActivity.this, "callCustomOfferDetailsApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(UserSettingsActivity.this);
                }
                else if (response.code() == 404) {
                    Toast.makeText(UserSettingsActivity.this, "Can't get the offer info!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserPutSettingsRequest> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(UserSettingsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setUserSettings() {
        generalNotifications = userPutSettingsRequest.getGeneralNotifications();
        orderNotifications = userPutSettingsRequest.getOrderNotifications();
        offerNotifications = userPutSettingsRequest.getOfferNotifications();
        summaryEmail = userPutSettingsRequest.getSummaryEmail();
        phoneViewPermission = userPutSettingsRequest.getPhoneViewPermission();
        locationViewPermission = userPutSettingsRequest.getLocationViewPermission();
        int selectedLocation = 0;
        if (locationViewPermission.toLowerCase().equals("public")) {
            selectedLocation = 0;
        }else if (locationViewPermission.toLowerCase().equals("followers")){
            selectedLocation = 1;
        }else{
            selectedLocation = 2;
        }
        int selectedPhone = 0;
        if (phoneViewPermission.toLowerCase().equals("public")) {
            selectedPhone = 0;
        }else if (phoneViewPermission.toLowerCase().equals("followers")){
            selectedPhone = 1;
        }else{
            selectedPhone = 2;
        }

        UserSettingsModel userSettingsModelFirst = new UserSettingsModel(getResources().getString(R.string.who_can_view_my_location), selectedLocation);
        userSettingList.add(userSettingsModelFirst);
        UserSettingsModel userSettingsModelSecond = new UserSettingsModel(getResources().getString(R.string.who_can_view_my_phone), selectedPhone);
        userSettingList.add(userSettingsModelSecond);
        DividerItemDecorationVer divider = new DividerItemDecorationVer(ContextCompat.getDrawable(this, R.drawable.divider_option));

        userSettingAdapter = new UserSettingAdapter(UserSettingsActivity.this, userSettingList, this);
        recyclerViewUserSetting.addItemDecoration(divider);
        recyclerViewUserSetting.setAdapter(userSettingAdapter);

    }


    @Override
    public void onItemClick(int position) {
        txtBtn.setSelected(true);
        txtBtn.setBackground(ContextCompat.getDrawable(UserSettingsActivity.this, R.color.colorAccent));

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();

        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    @Override
    public void onApiFail(String apiSource) {

    }
}
