package com.pointters.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.adapter.UserSettingAdapter;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.UserSettingsModel;
import com.pointters.model.request.UserPutSettingsRequest;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.pointters.R.id.btnSave;


public class UserSettingsActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener {
    private RecyclerView recyclerViewUserSetting;
    private UserSettingAdapter userSettingAdapter;
    private List<UserSettingsModel> userSettingList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private TextView txtBtn;
    private String json, summaryEmail, offerNotifications, orderNotifications, generalNotifications, phoneViewPermission, locationViewPermission;
    private UserSettingsModel userSettingsModel;
    private UserPutSettingsRequest userPutSettingsRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        AppUtils.setToolBarWithBothIcon(UserSettingsActivity.this, getResources().getString(R.string.user_settings), R.drawable.back_icon_grey, 0);

        txtBtn = (TextView) findViewById(R.id.txt_save);
        txtBtn.setOnClickListener(this);
        recyclerViewUserSetting = (RecyclerView) findViewById(R.id.rv_user_settings);
        recyclerViewUserSetting.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUserSetting.setLayoutManager(new LinearLayoutManager(this));


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
                    offerNotifications = jsonObjectSettings.get("offerNotifications").toString();

                }
                if (jsonObjectSettings.get("summaryEmail") != null && !jsonObjectSettings.get("summaryEmail").toString().isEmpty()) {
                    summaryEmail = jsonObjectSettings.get("summaryEmail").toString();
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

        UserSettingsModel userSettingsModelFirst = new UserSettingsModel(getResources().getString(R.string.who_can_view_my_location), locationViewPermission);
        userSettingList.add(userSettingsModelFirst);
        UserSettingsModel userSettingsModelSecond = new UserSettingsModel(getResources().getString(R.string.who_can_view_my_phone), phoneViewPermission);
        userSettingList.add(userSettingsModelSecond);

        userSettingAdapter = new UserSettingAdapter(UserSettingsActivity.this, userSettingList, this);
        recyclerViewUserSetting.setAdapter(userSettingAdapter);

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
                {   userPutSettingsRequest = new UserPutSettingsRequest(userSettingList.get(1).getSeletedItem(), userSettingList.get(0).getSeletedItem(), generalNotifications, orderNotifications, offerNotifications, summaryEmail);
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<Object> putUserSetting = apiService.putUserSettings(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), userPutSettingsRequest);
                    putUserSetting.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                onBackPressed();
                            } else if (response.code() == 400) {
                                AndroidUtils.showToast(UserSettingsActivity.this, "Please choose atleast one option");
                            }


                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {

                        }
                    });


                }

                break;
        }
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
}
