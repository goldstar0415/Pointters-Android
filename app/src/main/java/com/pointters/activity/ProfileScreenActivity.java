package com.pointters.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.adapter.EditProfileImageViewPagerAdapter;
import com.pointters.adapter.ProfileServicesAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.Media;
import com.pointters.model.MetricsModel;
import com.pointters.model.ServicesModel;
import com.pointters.model.UserProfileModel;
import com.pointters.model.response.FollowUnfollowResponse;
import com.pointters.model.response.GetServicesReponse;
import com.pointters.model.response.GetUserProfileResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.EndlessRecyclerViewScrollListener;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by prashantkumar on 16/8/17.
 */

public class ProfileScreenActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {

    private final int CALL_PHONE_REQUEST = 3;

    private TextView txtFollow, txtTitle, txtCompany, txtLocation, txtPhone, txtDescription, txtName, txtVerified, txtOffer;
    private RecyclerView recyclerViewServices;
    private ProfileServicesAdapter serviceAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RoundedImageView imgProfile;
    private ImageView btnCall, btnChat;
    private TextView txtOnTime, txtQuality, txtHour, txtOrders, txtRating, txtCustomers, txtPointNum, txtLikeNum, txtWatchNum;
    private String json;
    private ArrayList<ServicesModel> arrServiceList = new ArrayList<>();
    private EditProfileImageViewPagerAdapter editProfileImageViewPagerAdapter;
    private KProgressHUD loader;

    private List<Media> bgFiles = new ArrayList<>();
    private UserProfileModel userProfile;
    private MetricsModel userMetrics;

    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;

    private Boolean userType = false;
    private String userId = "";
    private String mUserName = "";
    private String mFullName = "";
    private String mUserPic = "";
    private String mUserPhone = "";
    private int mUserVerified = 0;
    private Boolean isFollowed = false;

    private String lastDocId = "";
    private int limitCnt = 0;
    private int totalCnt = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (!sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "").equals("")) {
            mUserLat = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "0"));
        }
        if (!sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "").equals("")) {
            mUserLng = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "0"));
        }

        initUI();

        userType = getIntent().getBooleanExtra(ConstantUtils.PROFILE_LOGINUSER, false);
        if (userType) {
            txtFollow.setVisibility(View.GONE);

            if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null) {
                json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.has("_id") && jsonObject.get("_id") != null && !jsonObject.get("_id").equals("")) {
                        userId = (String) jsonObject.get("_id");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            txtFollow.setVisibility(View.VISIBLE);
            userId = getIntent().getStringExtra(ConstantUtils.PROFILE_USERID);
        }

        if (!userId.equals("")) {
            getUserProfileApiCall(userId);
        } else {
            Toast.makeText(ProfileScreenActivity.this, "Can't find user profile!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initUI() {
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        txtVerified = (TextView) findViewById(R.id.txt_verified);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtTitle = (TextView) findViewById(R.id.toolbar_title);
        txtTitle.setText(getResources().getString(R.string.profile));
        txtFollow = (TextView) findViewById(R.id.txt_follow);
        txtFollow.setOnClickListener(this);
        txtCompany = (TextView) findViewById(R.id.txt_company);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtDescription = (TextView) findViewById(R.id.txt_descrption);
        imgProfile = (RoundedImageView) findViewById(R.id.img_profile);
        btnCall = (ImageView) findViewById(R.id.img_call);
        btnCall.setOnClickListener(this);
        btnChat = (ImageView) findViewById(R.id.img_chat);
        btnChat.setOnClickListener(this);

        txtOnTime = (TextView) findViewById(R.id.mOneTimeValue);
        txtQuality = (TextView) findViewById(R.id.mQualityNum);
        txtHour = (TextView) findViewById(R.id.mHour);
        txtOrders = (TextView) findViewById(R.id.txt_order_num);
        txtRating = (TextView) findViewById(R.id.txt_rating_num);
        txtCustomers = (TextView) findViewById(R.id.txt_customers_num);
        txtPointNum = (TextView) findViewById(R.id.mPointsValue);
        txtLikeNum = (TextView) findViewById(R.id.mLikesNum);
        txtWatchNum = (TextView) findViewById(R.id.mWatchingValue);
        txtOffer = (TextView) findViewById(R.id.txt_custom_offer);
        txtOffer.setOnClickListener(this);

        findViewById(R.id.toolbar_lft_img).setOnClickListener(this);
        findViewById(R.id.toolbar_right_img).setOnClickListener(this);
    }

    private void initRecyclerView() {
        recyclerViewServices = (RecyclerView) findViewById(R.id.recyclerview_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileScreenActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewServices.setLayoutManager(linearLayoutManager);
        serviceAdapter = new ProfileServicesAdapter(ProfileScreenActivity.this, arrServiceList, mUserLat, mUserLng, mUserName);
        recyclerViewServices.setAdapter(serviceAdapter);
        recyclerViewServices.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt > limitCnt) {
                    getUserServiceApiCall(userId, false, lastDocId);
                }
            }
        });
    }

    private void setUserProfile() {
        if (bgFiles.size() > 0) {
            setUpSuggestedCategoryViewPager();
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        if (userProfile.getProfilePic() != null && !userProfile.getProfilePic().isEmpty()) {
            String strPic = userProfile.getProfilePic();
            if (!strPic.contains("https://s3.amazonaws.com")) {
                strPic = "https://s3.amazonaws.com" + strPic;
            }

            mUserPic = strPic;
            ImageLoader.getInstance().displayImage(strPic, imgProfile, options);
        }

        String strFirst = "", strLast = "";
        if (userProfile.getFirstName() != null && !userProfile.getFirstName().isEmpty()) {
            strFirst = userProfile.getFirstName();
        }
        if (userProfile.getLastName() != null && !userProfile.getLastName().isEmpty()) {
            strLast = userProfile.getLastName();
        }
        mUserName = strFirst;
        mFullName = strFirst + " " + strLast;
        txtName.setText(mFullName);

        initRecyclerView();

        if (userProfile.getVerified() != null && userProfile.getVerified()) {
            mUserVerified = 1;
            txtVerified.setText("Verified");
        } else {
            mUserVerified = 0;
            txtVerified.setText("Not Verified");
        }

        if (userType) {
            btnChat.setVisibility(View.GONE);
            btnCall.setVisibility(View.GONE);
        } else {
            btnChat.setVisibility(View.VISIBLE);
            if (userProfile.getPhone() != null && !userProfile.getPhone().isEmpty()) {
                btnCall.setVisibility(View.VISIBLE);
            } else {
                btnCall.setVisibility(View.GONE);
            }
        }

        if (userProfile.getCompanyName() != null && !userProfile.getCompanyName().isEmpty()) {
            txtCompany.setText(userProfile.getCompanyName());
        }

        if (userProfile.getDescription() != null && !userProfile.getDescription().isEmpty()) {
            txtDescription.setText(userProfile.getDescription());
        }

        if (userMetrics.getAvgOnTime() != null && !userMetrics.getAvgOnTime().isEmpty() && !userMetrics.getAvgOnTime().equals("NA")) {
            float valOnTime = Float.parseFloat(userMetrics.getAvgOnTime());
            txtOnTime.setText(String.format("%.1f", valOnTime) + "%");
        } else {
            txtOnTime.setText("NA");
        }

        if (userMetrics.getAvgQuality() != null && !userMetrics.getAvgQuality().isEmpty() && !userMetrics.getAvgQuality().equals("NA")) {
            float valQuality = Float.parseFloat(userMetrics.getAvgQuality());
            txtQuality.setText(String.format("%.1f", valQuality) + "/5");
        } else {
            txtQuality.setText("NA");
        }

        if (userMetrics.getAvgResponseTime() != null && !userMetrics.getAvgResponseTime().isEmpty() && !userMetrics.getAvgResponseTime().equals("NA")) {
            float valResponse = Float.parseFloat(userMetrics.getAvgResponseTime());
            txtHour.setText(String.format("%.1f", valResponse) + "hr");
        } else {
            txtHour.setText("NA");
        }

        if (userMetrics.getNumOrdersCompleted() != null && !userMetrics.getNumOrdersCompleted().isEmpty() && !userMetrics.getNumOrdersCompleted().equals("NA")) {
            txtOrders.setText(userMetrics.getNumOrdersCompleted() + " service order completed");
        } else {
            txtOrders.setText("0 service order completed");
        }

        if (userMetrics.getAvgRating() != null && !userMetrics.getAvgRating().isEmpty() && !userMetrics.getAvgRating().equals("NA")) {
            txtRating.setText(userMetrics.getAvgRating() + "% avg rating");
        } else {
            txtRating.setText("0% avg rating");
        }

        if (userMetrics.getAvgWillingToBuyAgain() != null && !userMetrics.getAvgWillingToBuyAgain().isEmpty() && !userMetrics.getAvgWillingToBuyAgain().equals("NA")) {
            txtCustomers.setText(userMetrics.getAvgWillingToBuyAgain() + "% of customers will buy this service again");
        } else {
            txtCustomers.setText("0% of customers will buy this service again");
        }

        if (userMetrics.getPointValue() != null && !userMetrics.getPointValue().isEmpty() && !userMetrics.getPointValue().equals("NA")) {
            txtPointNum.setText(userMetrics.getPointValue());
        } else {
            txtPointNum.setText("NA");
        }

        if (userMetrics.getNumLikes() != null && !userMetrics.getNumLikes().isEmpty() && !userMetrics.getNumLikes().equals("NA")) {
            txtLikeNum.setText(userMetrics.getNumLikes());
        } else {
            txtLikeNum.setText("NA");
        }

        if (userMetrics.getNumWatching() != null && !userMetrics.getNumWatching().isEmpty() && !userMetrics.getNumWatching().equals("NA")) {
            txtWatchNum.setText(userMetrics.getNumWatching());
        } else {
            txtWatchNum.setText("NA");
        }
    }

    private void setFollowStatus() {
        txtFollow.setVisibility(View.VISIBLE);

        if (isFollowed) {
            txtFollow.setText("Following");
        } else {
            txtFollow.setText("Follow");
        }
    }

    private void setUpSuggestedCategoryViewPager() {
        ViewPager viewPagerSuggestedServices = (ViewPager) findViewById(R.id.profile_bg_viewpager);
        editProfileImageViewPagerAdapter = new EditProfileImageViewPagerAdapter(ProfileScreenActivity.this, bgFiles);
        viewPagerSuggestedServices.setAdapter(editProfileImageViewPagerAdapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator_view_pager_suggested_services);
        circlePageIndicator.setViewPager(viewPagerSuggestedServices);
    }

    private void moveToCall() {
        if (!checkPhoneCallPermission()) {
            return;
        }

        if (mUserPhone.contains("+")) {
            mUserPhone = mUserPhone.substring(1);
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mUserPhone));
        startActivity(callIntent);
    }

    private void moveToChat() {
        Intent intent = new Intent(ProfileScreenActivity.this, ChatActivity.class);
        editor.putInt(ConstantUtils.USER_VERIFIED, mUserVerified).apply();
        editor.putString(ConstantUtils.CHAT_USER_ID, userId).apply();
        editor.putString(ConstantUtils.CHAT_USER_NAME, mFullName).apply();
        editor.putString(ConstantUtils.CHAT_USER_PIC, mUserPic).apply();
        editor.putString(ConstantUtils.CHAT_CONVERSATION_ID, "");
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;

            case R.id.toolbar_right_img:
                break;

            case R.id.txt_follow:
                if (isFollowed) {
                    deleteUserFollowApi(userId);
                } else {
                    postUserFollowingApi(userId);
                }
                break;

            case R.id.img_call:
                if (!userType) {
                    moveToCall();
                }
                break;

            case R.id.img_chat:
                if (!userType) {
                    moveToChat();
                }
                break;

            case R.id.txt_custom_offer:
                if (!userType) {
                    moveToChat();
                }
                break;

            default:
                break;
        }
    }

    private void getUserProfileApiCall(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetUserProfileResponse> getUserProfile = apiService.getUserProfile(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        getUserProfile.enqueue(new Callback<GetUserProfileResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileResponse> call, Response<GetUserProfileResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    userProfile = response.body().getResult();
                    if (userProfile.getProfileBackgroundMedia() != null) {
                        bgFiles = userProfile.getProfileBackgroundMedia();
                    }
                    if (userProfile.getUserMetrics() != null) {
                        userMetrics = userProfile.getUserMetrics();
                    }

                    setUserProfile();
                    if (!userType) {
                        getUserFollowStatusApi(userId);
                    } else {
                        getUserServiceApiCall(userId, true, "");
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ProfileScreenActivity.this, "callUserProfileApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileResponse> call, Throwable t) {
                Toast.makeText(ProfileScreenActivity.this, "Can't find user Profile!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void getUserServiceApiCall(String id, Boolean inited, String lastId) {
        if (inited) {
            arrServiceList.clear();
        }

        Map<String, String> params = new HashMap<>();
        params.put("userId", id);
        if (!lastId.equals("")) {
            params.put("lt_id", lastId);
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetServicesReponse> userServiceCall = apiService.getUserServices(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), params);
        userServiceCall.enqueue(new Callback<GetServicesReponse>() {
            @Override
            public void onResponse(Call<GetServicesReponse> call, Response<GetServicesReponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    arrServiceList.addAll(response.body().getDocs());
                    serviceAdapter.notifyItemRangeInserted(serviceAdapter.getItemCount(), arrServiceList.size()-1);
                    serviceAdapter.notifyDataSetChanged();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ProfileScreenActivity.this, "callGetUserServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }
            }

            @Override
            public void onFailure(Call<GetServicesReponse> call, Throwable t) {
                Toast.makeText(ProfileScreenActivity.this, "Can't find the service info.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserFollowStatusApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<FollowUnfollowResponse> followStatus = apiService.getUserFollowStatus(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        followStatus.enqueue(new Callback<FollowUnfollowResponse>() {
            @Override
            public void onResponse(Call<FollowUnfollowResponse> call, Response<FollowUnfollowResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getFollowed() != null && response.body().getFollowed()) {
                        isFollowed = true;
                    } else {
                        isFollowed = false;
                    }
                    setFollowStatus();
                    getUserServiceApiCall(userId, true, "");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ProfileScreenActivity.this, "callGetUserFollowStatusApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }
            }

            @Override
            public void onFailure(Call<FollowUnfollowResponse> call, Throwable t) {
                Toast.makeText(ProfileScreenActivity.this, "Can't find the user following status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postUserFollowingApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> followuser = apiService.followUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        followuser.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    isFollowed = true;
                    setFollowStatus();
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ProfileScreenActivity.this, "callPostUserFollowApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(ProfileScreenActivity.this, "Can't follow the user.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUserFollowApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> unfollowuser = apiService.unFollowUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        unfollowuser.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    isFollowed = false;
                    setFollowStatus();
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ProfileScreenActivity.this, "callDeleteFollowUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(ProfileScreenActivity.this, "Can't unfollow the user.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_PHONE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveToCall();
                }
                break;

            default:
                break;
        }
    }

    private boolean checkPhoneCallPermission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( this, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST);
            return false;

        } else {
            return true;
        }
    }

    @Override
    public void onApiFail(String apiSource) {
        if (apiSource.equals("callGetUserServiceApi")) {
            getUserServiceApiCall(userId, true, "");
        } else if (apiSource.equals("callGetUserFollowStatusApi")) {
            getUserFollowStatusApi(userId);
        } else if (apiSource.equals("callDeleteFollowUserApi")) {
            deleteUserFollowApi(userId);
        } else if (apiSource.equals("callPostUserFollowApi")) {
            postUserFollowingApi(userId);
        }
    }
}
