package com.pointters.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.adapter.MenuAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.RecyclerViewListener;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConnectivityController;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.DividerItemDecorationVer;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MenuScreenActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {
    private String[] purchaseList;
    private String[] salesList;
    private String[] generalList;
    private String[] userSettings;
    private RoundedImageView imgProfile;
    private TextView txtName, txtNotifications,txtStartSelling;
    private DisplayImageOptions options;
    private RecyclerView purchasesRecyclerView, salesRecyclerView, generalRecyclerView, userSettingsRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout layoutMoveToProfile;
    private String json;

    private void logOutCall() {

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> logOutCall = apiService.logOut(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));

        logOutCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    editor.clear().commit();
                    finish();
                    Intent intent = new Intent(MenuScreenActivity.this, IntroActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                } else if (response.code() == 401) {

                    // We will have to call login api as session is expired
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(MenuScreenActivity.this, "callLogOutApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(MenuScreenActivity.this);

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.getMessage();

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
        AppUtils.setToolBarWithBothIconWithShadow(MenuScreenActivity.this, getResources().getString(R.string.menu_header), R.drawable.back_icon_grey, 0);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);

        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null)
            json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");

        editor = sharedPreferences.edit();
        txtStartSelling=(TextView)findViewById(R.id.txt_start_selling);
        txtStartSelling.setOnClickListener(this);
        layoutMoveToProfile = (RelativeLayout) findViewById(R.id.layout_move_to_profile);
        layoutMoveToProfile.setOnClickListener(this);

        purchaseList = getResources().getStringArray(R.array.purchases);
        salesList = getResources().getStringArray(R.array.sales);
        generalList = getResources().getStringArray(R.array.general);
        userSettings = getResources().getStringArray(R.array.user_settings);

        imgProfile = (RoundedImageView) findViewById(R.id.img_profile);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtNotifications = (TextView) findViewById(R.id.txt_notifications);
        txtNotifications.setOnClickListener(this);

        purchasesRecyclerView = (RecyclerView) findViewById(R.id.rv_purchases);
        purchasesRecyclerView.setNestedScrollingEnabled(false);
        purchasesRecyclerView.setLayoutManager(new LinearLayoutManager(MenuScreenActivity.this));
        DividerItemDecorationVer divider = new DividerItemDecorationVer(ContextCompat.getDrawable(this, R.drawable.divider_option));
        purchasesRecyclerView.addItemDecoration(divider);
        MenuAdapter menuAdapter1 = new MenuAdapter(MenuScreenActivity.this, purchaseList);
        purchasesRecyclerView.setAdapter(menuAdapter1);

        salesRecyclerView = (RecyclerView) findViewById(R.id.rv_sales);
        salesRecyclerView.setNestedScrollingEnabled(false);
        salesRecyclerView.setLayoutManager(new LinearLayoutManager(MenuScreenActivity.this));
        divider = new DividerItemDecorationVer(ContextCompat.getDrawable(this, R.drawable.divider_option));
        salesRecyclerView.addItemDecoration(divider);
        MenuAdapter menuAdapter2 = new MenuAdapter(MenuScreenActivity.this, salesList);
        salesRecyclerView.setAdapter(menuAdapter2);

        userSettingsRecyclerView = (RecyclerView) findViewById(R.id.rv_user_settings);
        userSettingsRecyclerView.setNestedScrollingEnabled(false);
        userSettingsRecyclerView.setLayoutManager(new LinearLayoutManager(MenuScreenActivity.this));
        divider = new DividerItemDecorationVer(ContextCompat.getDrawable(this, R.drawable.divider_option));
        userSettingsRecyclerView.addItemDecoration(divider);
        MenuAdapter menuAdapter4 = new MenuAdapter(MenuScreenActivity.this, userSettings);
        userSettingsRecyclerView.setAdapter(menuAdapter4);


        generalRecyclerView = (RecyclerView) findViewById(R.id.rv_general);
        generalRecyclerView.setNestedScrollingEnabled(false);
        generalRecyclerView.setLayoutManager(new LinearLayoutManager(MenuScreenActivity.this));
        divider = new DividerItemDecorationVer(ContextCompat.getDrawable(this, R.drawable.divider_option));
        generalRecyclerView.addItemDecoration(divider);
        MenuAdapter menuAdapter3 = new MenuAdapter(MenuScreenActivity.this, generalList);
        generalRecyclerView.setAdapter(menuAdapter3);

        purchasesRecyclerView.addOnItemTouchListener(new RecyclerViewListener(MenuScreenActivity.this, new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        Intent ordersIntent=new Intent(MenuScreenActivity.this, HomeActivity.class);
                        ordersIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.buy)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.orders)).apply();
                        startActivity(ordersIntent);
                        break;
                    case 1:
                        Intent customOffersIntent=new Intent(MenuScreenActivity.this, HomeActivity.class);
                        customOffersIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.buy)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.custom_offer)).apply();
                        startActivity(customOffersIntent);
                        break;
                    case 2:
                        Intent liveOffersIntent=new Intent(MenuScreenActivity.this, HomeActivity.class);
                        liveOffersIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.buy)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.live_offer_req)).apply();
                        startActivity(liveOffersIntent);
                        break;
                }

            }
        }
        ));


        salesRecyclerView.addOnItemTouchListener(new RecyclerViewListener(MenuScreenActivity.this, new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        Intent ordersIntent=new Intent(MenuScreenActivity.this, HomeActivity.class);
                        ordersIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.sell)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.orders)).apply();
                        startActivity(ordersIntent);
                        break;
                    case 1:
                        startActivity(new Intent(MenuScreenActivity.this, BecomeASellerActivity.class));
                        break;
                    case 2:
                        Intent OffersIntent=new Intent(MenuScreenActivity.this, HomeActivity.class);
                        OffersIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.sell)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.offers)).apply();
                        startActivity(OffersIntent);
                        break;
                    case 3:
                        Intent jobsIntent=new Intent(MenuScreenActivity.this, HomeActivity.class);
                        jobsIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.sell)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.jobs)).apply();
                        startActivity(jobsIntent);
                        break;
                    case 5:
                        startActivity(new Intent(MenuScreenActivity.this, BackgroundCheckActivity.class));

                        break;

                }

            }
        }
        ));
        userSettingsRecyclerView.addOnItemTouchListener(new RecyclerViewListener(MenuScreenActivity.this, new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        startActivityForResult(new Intent(MenuScreenActivity.this, EditProfileActivity.class), 1);
                        break;
                    case 1:
                        startActivityForResult(new Intent(MenuScreenActivity.this, UserSettingsActivity.class), 1);
                        break;
                    case 2:
                        startActivityForResult(new Intent(MenuScreenActivity.this, NotificationOptionsActivity.class), 1);
                        break;
                    case 3:
                        startActivity(new Intent(MenuScreenActivity.this, PaymentMethodsActivity.class));
                        break;

                }

            }
        }
        ));


        generalRecyclerView.addOnItemTouchListener(new RecyclerViewListener(MenuScreenActivity.this, new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        Intent followingIntent = new Intent(MenuScreenActivity.this, UserFollowersActivity.class);
                        editor.putString(ConstantUtils.FOLLOW_TYPE, getResources().getString(R.string.following)).apply();
                        startActivity(followingIntent);
                        break;
                    case 1:
                        Intent followersIntent = new Intent(MenuScreenActivity.this, UserFollowersActivity.class);
                        editor.putString(ConstantUtils.FOLLOW_TYPE, getResources().getString(R.string.followers)).apply();
                        startActivity(followersIntent);
                        break;





                    case 6:
                        if (ConnectivityController.isNetworkAvailable(MenuScreenActivity.this)) {
                            logOutCall();
                        } else {
                            Toast.makeText(MenuScreenActivity.this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                        }

                        break;

                }

            }
        }
        ));
    }

    private void getUserDataApiCall() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);

        Call<Object> getUserInformation = apiService.getUserInformation(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        getUserInformation.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                if (response.code() == 200) {
                    try {

                         json = new Gson().toJson(((LinkedTreeMap) response.body()).get("user"));
                        editor.putString(ConstantUtils.USER_DATA, json).commit();
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.has("profilePic")) {
                            if (jsonObject.get("profilePic") != null && !jsonObject.get("profilePic").toString().isEmpty()) {
                                options = new DisplayImageOptions.Builder()
                                        .showImageOnLoading(R.drawable.user_avatar_placeholder)
                                        .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                                        .showImageOnFail(R.drawable.user_avatar_placeholder)
                                        .cacheInMemory(true)
                                        .cacheOnDisk(true)
                                        .considerExifParams(true)
                                        .build();
                                String profileUrl;

                                if (jsonObject.get("profilePic").toString().startsWith("https://s3.amazonaws.com")) {
                                    profileUrl = jsonObject.get("profilePic").toString();
                                } else {
                                    profileUrl = "https://s3.amazonaws.com" + jsonObject.get("profilePic").toString();
                                }
                                ImageLoader.getInstance().displayImage(profileUrl, imgProfile, options);
                            }
                        }

                        if (jsonObject.has("completedRegistration")) {
                            editor.putBoolean(ConstantUtils.IS_REGISTRATION_COMPLETED, (Boolean) jsonObject.get("completedRegistration")).commit();

                        }

                        if (jsonObject.has("firstName") && jsonObject.has("lastName")) {
                            if (jsonObject.get("firstName") != null && !jsonObject.get("firstName").toString().isEmpty() && jsonObject.get("lastName") != null && !jsonObject.get("lastName").toString().isEmpty())
                                txtName.setText(jsonObject.get("firstName").toString() + " " + jsonObject.get("lastName").toString());

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {

                    // We will have to call login api as session is expired
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(MenuScreenActivity.this, "callGetUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(MenuScreenActivity.this);

                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

    }

    private void loadUserData() {


        try {

            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("profilePic")) {
                if (jsonObject.get("profilePic") != null && !jsonObject.get("profilePic").toString().isEmpty()) {
                    options = new DisplayImageOptions.Builder()
                            .showImageOnLoading(R.drawable.user_avatar_placeholder)
                            .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                            .showImageOnFail(R.drawable.user_avatar_placeholder)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .considerExifParams(true)
                            .build();
                    String profileUrl;

                    if (jsonObject.get("profilePic").toString().startsWith("https://s3.amazonaws.com")) {
                        profileUrl = jsonObject.get("profilePic").toString();
                    } else {
                        profileUrl = "https://s3.amazonaws.com" + jsonObject.get("profilePic").toString();
                    }
                    ImageLoader.getInstance().displayImage(profileUrl, imgProfile, options);
                }
            }

            if (jsonObject.has("completedRegistration")) {
                editor.putBoolean(ConstantUtils.IS_REGISTRATION_COMPLETED, (Boolean) jsonObject.get("completedRegistration")).commit();

            }

            if (jsonObject.has("firstName") && jsonObject.has("lastName")) {
                if (jsonObject.get("firstName") != null && !jsonObject.get("firstName").toString().isEmpty() && jsonObject.get("lastName") != null && !jsonObject.get("lastName").toString().isEmpty())
                    txtName.setText(jsonObject.get("firstName").toString() + " " + jsonObject.get("lastName").toString());

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


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

            case R.id.layout_move_to_profile:
                startActivity(new Intent(MenuScreenActivity.this, ProfileScreenActivity.class));
                break;

            case R.id.txt_start_selling:
                startActivity(new Intent(MenuScreenActivity.this, BecomeASellerActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  getUserDataApiCall();
        loadUserData();
        Log.d("call", "onResume: ");
    }

    @Override
    public void onApiFail(String apiSource) {

        if (apiSource.equals("callGetUserApi")) {

            getUserDataApiCall();
        } else if (apiSource.equals("callLogOutApi")) {

            logOutCall();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                getUserDataApiCall();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("json", json);

        setResult(Activity.RESULT_OK, intent);

        finish();
    }
}
