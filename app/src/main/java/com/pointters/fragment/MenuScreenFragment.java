package com.pointters.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.activity.BackgroundCheckActivity;
import com.pointters.activity.BecomeASellerActivity;
import com.pointters.activity.EditProfileActivity;
import com.pointters.activity.HomeActivity;
import com.pointters.activity.IntroActivity;
import com.pointters.activity.InviteFriendActivity;
import com.pointters.activity.NotificationOptionsActivity;
import com.pointters.activity.NotificationsActivity;
import com.pointters.activity.PaymentMethodsActivity;
import com.pointters.activity.ProfileScreenActivity;
import com.pointters.activity.StoreLocationsActivity;
import com.pointters.activity.TermsActivity;
import com.pointters.activity.TransactionHistoryActivity;
import com.pointters.activity.UserFollowersActivity;
import com.pointters.activity.UserSettingsActivity;
import com.pointters.activity.WatchingLikeActivity;
import com.pointters.adapter.MenuAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.RecyclerViewListener;
import com.pointters.model.UserMenuModel;
import com.pointters.model.response.GetUserMenuResponse;
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


public class MenuScreenFragment extends Fragment implements View.OnClickListener, OnApiFailDueToSessionListener {
    private String[] purchaseList;
    private String[] salesList;
    private String[] generalList;
    private String[] userSettings;

    private TypedArray buyicons;
    private TypedArray sellicons;
    private TypedArray userseticons;
    private TypedArray generalicons;

    private RoundedImageView imgProfile;
    private TextView txtName, txtNotifications,txtStartSelling, txtLogout;
    private DisplayImageOptions options;
    private RecyclerView purchasesRecyclerView, salesRecyclerView, generalRecyclerView, userSettingsRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout layoutMoveToProfile;
    private String json;
    private View view;
    Fragment fragment;// = new MeFragment();
    private Button editProfileButton;
    private RelativeLayout notificationViewButton;

    private TextView txtFollowCount, txtFollowingCount, txtUserPoints, txtNotificationBadgeView;
    int[] buyBadge, sellBadge;
    MenuAdapter menuAdapter1, menuAdapter2, menuAdapter3, menuAdapter4;
    private UserMenuModel userMenu = new UserMenuModel();

    private void logOutCall() {

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> logOutCall = apiService.logOut(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));

        logOutCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    editor.clear().commit();
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), IntroActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                } else if (response.code() == 401) {

                    // We will have to call login api as session is expired
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callLogOutApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(MenuScreenFragment.this);

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.getMessage();

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_menu, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);

        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null)
            json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");

        editor = sharedPreferences.edit();

        notificationViewButton = (RelativeLayout) view.findViewById(R.id.bnt_notification);
        notificationViewButton.setOnClickListener(this);

        txtFollowCount = (TextView) view.findViewById(R.id.text_follower_count);
        txtFollowingCount = (TextView) view.findViewById(R.id.text_following_count);
        txtUserPoints = (TextView) view.findViewById(R.id.text_points);
        txtNotificationBadgeView = (TextView) view.findViewById(R.id.dot_view);
        editProfileButton = (Button) view.findViewById(R.id.edit_profile_button);
        editProfileButton.setOnClickListener(this);

        txtStartSelling=(TextView)view.findViewById(R.id.btn_start_selling);
        txtLogout=(TextView)view.findViewById(R.id.btn_logout);
        txtStartSelling.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
        layoutMoveToProfile = (RelativeLayout) view.findViewById(R.id.layout_move_to_profile);
        layoutMoveToProfile.setOnClickListener(this);

        buyicons = getResources().obtainTypedArray(R.array.buyicons);
        sellicons = getResources().obtainTypedArray(R.array.sellicons);
        generalicons = getResources().obtainTypedArray(R.array.generalicons);
        userseticons = getResources().obtainTypedArray(R.array.userseticons);

        purchaseList = getResources().getStringArray(R.array.purchases);
        salesList = getResources().getStringArray(R.array.sales);
        generalList = getResources().getStringArray(R.array.general);
        userSettings = getResources().getStringArray(R.array.user_settings);

        imgProfile = (RoundedImageView) view.findViewById(R.id.img_profile);
        txtName = (TextView)view.findViewById(R.id.txt_name);
        txtNotifications = (TextView) view.findViewById(R.id.txt_notifications);
        txtNotifications.setOnClickListener(this);

        purchasesRecyclerView = (RecyclerView) view.findViewById(R.id.rv_purchases);
        purchasesRecyclerView.setNestedScrollingEnabled(false);
        purchasesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecorationVer divider = new DividerItemDecorationVer(ContextCompat.getDrawable(getActivity(), R.drawable.divider_option));
        purchasesRecyclerView.addItemDecoration(divider);

        buyBadge = new int[]{userMenu.getBuy().getOrders(), userMenu.getBuy().getOffers(), userMenu.getBuy().getRequest(), 0, 0};
        menuAdapter1 = new MenuAdapter(getActivity(), purchaseList, buyicons, buyBadge);
        purchasesRecyclerView.setAdapter(menuAdapter1);

        salesRecyclerView = (RecyclerView) view.findViewById(R.id.rv_sales);
        salesRecyclerView.setNestedScrollingEnabled(false);
        salesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        divider = new DividerItemDecorationVer(ContextCompat.getDrawable(getActivity(), R.drawable.divider_option));
        salesRecyclerView.addItemDecoration(divider);

        sellBadge = new int[]{userMenu.getSell().getOrders(),0, userMenu.getSell().getOffers(), userMenu.getSell().getJobs(), 0, 0, 0, 0, 0};
        menuAdapter2 = new MenuAdapter(getActivity(), salesList,sellicons, sellBadge);
        salesRecyclerView.setAdapter(menuAdapter2);

        userSettingsRecyclerView = (RecyclerView) view.findViewById(R.id.rv_user_settings);
        userSettingsRecyclerView.setNestedScrollingEnabled(false);
        userSettingsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        divider = new DividerItemDecorationVer(ContextCompat.getDrawable(getActivity(), R.drawable.divider_option));
        userSettingsRecyclerView.addItemDecoration(divider);

        menuAdapter4 = new MenuAdapter(getActivity(), userSettings, userseticons, new int[]{0, 0,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 });
        userSettingsRecyclerView.setAdapter(menuAdapter4);

        if (userMenu.getNotifications() == 0) {
            txtNotificationBadgeView.setVisibility(View.INVISIBLE);
        }else{
            txtNotificationBadgeView.setVisibility(View.VISIBLE);
            txtNotificationBadgeView.setText(String.valueOf(userMenu.getNotifications()));
        }
        txtUserPoints.setText(String.valueOf(userMenu.getPoints()));
        txtFollowingCount.setText(String.valueOf(userMenu.getFollowings()));
        txtFollowCount.setText(String.valueOf(userMenu.getFollowers()));


        generalRecyclerView = (RecyclerView) view.findViewById(R.id.rv_general);
        generalRecyclerView.setNestedScrollingEnabled(false);
        generalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        divider = new DividerItemDecorationVer(ContextCompat.getDrawable(getActivity(), R.drawable.divider_option));
        generalRecyclerView.addItemDecoration(divider);
        menuAdapter3 = new MenuAdapter(getActivity(), generalList,generalicons, new int[]{0, 0,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 });
        generalRecyclerView.setAdapter(menuAdapter3);

        purchasesRecyclerView.addOnItemTouchListener(new RecyclerViewListener(getActivity(), new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.buy)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.orders)).apply();
                        fragment = new MeFragment();
                        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment)
                                .commitAllowingStateLoss();

//                        Intent ordersIntent=new Intent(getActivity(), HomeActivity.class);
//                        ordersIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
//                        startActivity(ordersIntent);
                        break;
                    case 1:
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.buy)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.custom_offer)).apply();
                        fragment = new MeFragment();
                        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment)
                                .commitAllowingStateLoss();
//                        Intent customOffersIntent=new Intent(getActivity(), HomeActivity.class);
//                        customOffersIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
//                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.buy)).apply();
//                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.custom_offer)).apply();
//                        startActivity(customOffersIntent);
                        break;
                    case 2:
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.buy)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.live_offer)).apply();
                        fragment = new MeFragment();
                        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment)
                                .commitAllowingStateLoss();
//                        Intent liveOffersIntent=new Intent(getActivity(), HomeActivity.class);
//                        liveOffersIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
//                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.buy)).apply();
//                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.live_offer_req)).apply();
//                        startActivity(liveOffersIntent);
                        break;
                    case 3:
                        Intent watchingIntent=new Intent(getActivity(), WatchingLikeActivity.class);
                        editor.putString(ConstantUtils.WATCHING_LIKED_TYPE,getResources().getString(R.string.watching)).apply();
                        startActivity(watchingIntent);
                        break;
                    case 4:
                        Intent likedIntent = new Intent(getActivity(), WatchingLikeActivity.class);
                        editor.putString(ConstantUtils.WATCHING_LIKED_TYPE, getResources().getString(R.string.liked)).apply();
                        startActivity(likedIntent);
                        break;
                }

            }
        }
        ));

        salesRecyclerView.addOnItemTouchListener(new RecyclerViewListener(getActivity(), new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.sell)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.orders)).apply();
                        fragment = new MeFragment();
                        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment)
                                .commitAllowingStateLoss();
//                        Intent ordersIntent=new Intent(getActivity(), HomeActivity.class);
//                        ordersIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
//                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.sell)).apply();
//                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.orders)).apply();
//                        startActivity(ordersIntent);
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), BecomeASellerActivity.class));
                        break;
                    case 2:
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.sell)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.offers)).apply();
                        fragment = new MeFragment();
                        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment)
                                .commitAllowingStateLoss();
//                        Intent OffersIntent=new Intent(getActivity(), HomeActivity.class);
//                        OffersIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
//                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.sell)).apply();
//                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.offers)).apply();
//                        startActivity(OffersIntent);
                        break;
                    case 3:
                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.sell)).apply();
                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.jobs)).apply();
                        fragment = new MeFragment();
                        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment)
                                .commitAllowingStateLoss();
//                        Intent jobsIntent=new Intent(getActivity(), HomeActivity.class);
//                        jobsIntent.putExtra(ConstantUtils.SOURCE,ConstantUtils.MENU_SCREEN);
//                        editor.putString(ConstantUtils.SOURCE,getResources().getString(R.string.sell)).apply();
//                        editor.putString(ConstantUtils.SELECTED_TAB,getResources().getString(R.string.jobs)).apply();
//                        startActivity(jobsIntent);
                        break;
                    case 5:
                        startActivity(new Intent(getActivity(), BackgroundCheckActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(getActivity(), StoreLocationsActivity.class));
                        break;

                    case 8:
                        startActivity(new Intent(getActivity(), TransactionHistoryActivity.class));
                        break;

                }

            }
        }
        ));
        userSettingsRecyclerView.addOnItemTouchListener(new RecyclerViewListener(getActivity(), new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        startActivityForResult(new Intent(getActivity(), EditProfileActivity.class), 1);
                        break;
                    case 1:
                        startActivityForResult(new Intent(getActivity(), UserSettingsActivity.class), 1);
                        break;
                    case 2:
                        startActivityForResult(new Intent(getActivity(), NotificationOptionsActivity.class), 1);
                        break;
                    case 3:
                        startActivity(new Intent(getActivity(), PaymentMethodsActivity.class));
                        break;

                }

            }
        }
        ));


        generalRecyclerView.addOnItemTouchListener(new RecyclerViewListener(getActivity(), new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        Intent followingIntent = new Intent(getActivity(), UserFollowersActivity.class);
                        editor.putString(ConstantUtils.FOLLOW_TYPE, getResources().getString(R.string.following)).apply();
                        startActivity(followingIntent);
                        break;
                    case 1:
                        Intent followersIntent = new Intent(getActivity(), UserFollowersActivity.class);
                        editor.putString(ConstantUtils.FOLLOW_TYPE, getResources().getString(R.string.followers)).apply();
                        startActivity(followersIntent);
                        break;


                    case 2:
                        Intent inviteIntent = new Intent(getActivity(), InviteFriendActivity.class);
                        startActivity(inviteIntent);
                        break;


                    case 4:
                        Intent terms = new Intent(getActivity(), TermsActivity.class);
                        terms.putExtra("type", "terms");
                        startActivity(terms);
                        break;
                    case 5:
                        Intent privacy = new Intent(getActivity(), TermsActivity.class);
                        privacy.putExtra("type", "privacy");
                        startActivity(privacy);
                        break;





                    case 6:
                        if (ConnectivityController.isNetworkAvailable(getActivity())) {
                            logOutCall();
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                        }

                        break;

                }

            }
        }
        ));
        getUserMenu();
        return view;
    }

    public void getUserMenu(){
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);

        Call<GetUserMenuResponse> getUserInformation = apiService.getUserMenu(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        getUserInformation.enqueue(new Callback<GetUserMenuResponse>() {

            @Override
            public void onResponse(Call<GetUserMenuResponse> call, Response<GetUserMenuResponse> response) {

                if (response.code() == 200) {
                    userMenu = response.body().getCounts();
                    updateView();

                } else if (response.code() == 401) {

                    // We will have to call login api as session is expired
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(MenuScreenFragment.this);

                }

            }

            @Override
            public void onFailure(Call<GetUserMenuResponse> call, Throwable t) {

            }
        });
    }

    public void updateView(){
        if (userMenu.getNotifications() == 0) {
            txtNotificationBadgeView.setVisibility(View.INVISIBLE);
        }else{
            txtNotificationBadgeView.setVisibility(View.VISIBLE);
            txtNotificationBadgeView.setText(String.valueOf(userMenu.getNotifications()));
        }
        txtUserPoints.setText(String.valueOf(userMenu.getPoints()));
        txtFollowingCount.setText(String.valueOf(userMenu.getFollowings()));
        txtFollowCount.setText(String.valueOf(userMenu.getFollowers()));

        buyBadge = new int[]{userMenu.getBuy().getOrders(), userMenu.getBuy().getOffers(), userMenu.getBuy().getRequest(), 0, 0, 0};
        menuAdapter1 = new MenuAdapter(getActivity(), purchaseList, buyicons, buyBadge);
        sellBadge = new int[]{userMenu.getSell().getOrders(),0, userMenu.getSell().getOffers(), userMenu.getSell().getJobs(), 0, 0, 0, 0, 0};
        menuAdapter2 = new MenuAdapter(getActivity(), salesList, sellicons, sellBadge);
        purchasesRecyclerView.setAdapter(menuAdapter1);
        salesRecyclerView.setAdapter(menuAdapter2);
        menuAdapter1.notifyDataSetChanged();
        menuAdapter2.notifyDataSetChanged();
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
//                                    profileUrl = "https://s3.amazonaws.com" + jsonObject.get("profilePic").toString();
                                    profileUrl = jsonObject.get("profilePic").toString();
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
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(MenuScreenFragment.this);

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
//                        profileUrl = "https://s3.amazonaws.com" + jsonObject.get("profilePic").toString();
                        profileUrl = jsonObject.get("profilePic").toString();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bnt_notification:
                startActivityForResult(new Intent(getActivity(), NotificationsActivity.class), 99);
                break;
            case R.id.toolbar_lft_img:

                Intent intent = new Intent();
                intent.putExtra("json", json);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();

//                getActivity().onBackPressed();
                break;

            case R.id.layout_move_to_profile:
                Intent userIntent = new Intent(getActivity(), ProfileScreenActivity.class);
                userIntent.putExtra(ConstantUtils.PROFILE_LOGINUSER, true);
                startActivityForResult(userIntent, 1);
                break;

            case R.id.btn_start_selling:
                startActivity(new Intent(getActivity(), BecomeASellerActivity.class));
                break;

            case R.id.btn_logout:
                if (ConnectivityController.isNetworkAvailable(getActivity())) {
                    logOutCall();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edit_profile_button:
                startActivityForResult(new Intent(getActivity(), EditProfileActivity.class), 1);
                break;

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {
                getUserDataApiCall();
            }
        }
    }

}
