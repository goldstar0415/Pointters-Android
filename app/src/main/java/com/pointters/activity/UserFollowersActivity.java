package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.FollowersAdapter;
import com.pointters.adapter.FollowingAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecycleItemClickListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.Followers;
import com.pointters.model.FollowersModel;
import com.pointters.model.FollowingModel;
import com.pointters.model.response.GetFollowersResponse;
import com.pointters.model.response.GetFollowingResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.DividerItemDecorationVer;
import com.pointters.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by prashantkumar on 16/8/17.
 */

public class UserFollowersActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {

    private TextView txtTitle;
    private RecyclerView recyclerFollowers;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private KProgressHUD loader;
    private TextView txtNotFound;
    private String json, followType;
    private String mloginUserId = "";

    private String lastDocId = "";
    private int limitCnt = 0;
    private int totalCnt = 0;

    List<FollowingModel> userFollowings = new ArrayList<>();
    FollowingAdapter followingAdapter;
    List<FollowersModel> userFollowers = new ArrayList<>();
    FollowersAdapter followersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        followType = sharedPreferences.getString(ConstantUtils.FOLLOW_TYPE, "");
        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null) {
            json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.has("_id") && jsonObject.get("_id") != null && !jsonObject.get("_id").equals("")) {
                    mloginUserId = (String) jsonObject.get("_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        txtTitle = (TextView) findViewById(R.id.toolbar_title);
        findViewById(R.id.toolbar_right_img).setVisibility(View.GONE);
        findViewById(R.id.toolbar_lft_img).setOnClickListener(this);

        txtNotFound = (TextView) findViewById(R.id.txt_notfound);
        if (followType.equals("Followers")) {
            txtNotFound.setText("No follower found");
        } else {
            txtNotFound.setText("No following found");
        }
        txtNotFound.setVisibility(View.GONE);

        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        recyclerFollowers = (RecyclerView) findViewById(R.id.recycler_followers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserFollowersActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerFollowers.setLayoutManager(linearLayoutManager);
        recyclerFollowers.setItemAnimator(new DefaultItemAnimator());

//        recyclerFollowers.set(new OnRecycleItemClickListener(this, new OnRecycleItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                moveToProfile(position);
//            }
//        }));
//
        DividerItemDecorationVer divider = new DividerItemDecorationVer(ContextCompat.getDrawable(this, R.drawable.divider_option));
        divider = new DividerItemDecorationVer(ContextCompat.getDrawable(this, R.drawable.divider_option));
        recyclerFollowers.addItemDecoration(divider);

        if (followType.equals("Followers")) {
            txtTitle.setText(getResources().getString(R.string.followers));
            followersAdapter = new FollowersAdapter(UserFollowersActivity.this, userFollowers);
            recyclerFollowers.setAdapter(followersAdapter);
            recyclerFollowers.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    if (totalCnt > limitCnt) {
                        getUserFollowersApi(false, lastDocId);
                    }
                }
            });
            followersAdapter.setListener(new OnRecyclerViewButtonClickListener() {
                @Override
                public void onButtonClick(View v, int position) {
                    if (((Button)v).getText().toString().equals("FOLLOWING")){
                        CallUnfollowApi(position);
                    }else{
                        CallFollowApi(position);
                    }
                }
            });
            followersAdapter.setItemListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    moveToProfile(position);
                }

            });
            loader.show();
            getUserFollowersApi(true, "");
        }
        else {
            txtTitle.setText(getResources().getString(R.string.following));
            followingAdapter = new FollowingAdapter(UserFollowersActivity.this, userFollowings);
            recyclerFollowers.setAdapter(followingAdapter);
            recyclerFollowers.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    if (totalCnt > limitCnt) {
                        getUserFollowingApi(false, lastDocId);
                    }
                }
            });
            followingAdapter.setListener(new OnRecyclerViewButtonClickListener() {
                @Override
                public void onButtonClick(View v, int position) {
                    if (((Button)v).getText().toString().equals("FOLLOWING")){
                        CallUnfollowApi(position);
                    }else{
                        CallFollowApi(position);
                    }

                }
            });
            followingAdapter.setItemListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    moveToProfile(position);
                }

            });
            loader.show();
            getUserFollowingApi(true, "");
        }
    }

    public void CallUnfollowApi(int position) {
        loader.show();
        String id = "";
        if (followType.equals("Followers")) {
            id = userFollowers.get(position).getFollowFrom().getId();
        }else{
            id = userFollowings.get(position).getFollowTo().getId();
        }
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> unfollowuser = apiService.unFollowUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        unfollowuser.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()){
                    loader.dismiss();
                }
                if (response.code() == 200) {
                    if (followType.equals("Followers")) {
                        FollowersModel followersModel = userFollowers.get(position);
                        Followers followers = followersModel.getFollowFrom();
                        followers.setMutualFollow(false);
                        followersModel.setFollowFrom(followers);
                        userFollowers.remove(position);
                        userFollowers.add(position, followersModel);
                        followersAdapter.notifyItemChanged(position);
                    }else{
                        FollowingModel followeringModel = userFollowings.get(position);
                        Followers followers = followeringModel.getFollowTo();
                        followers.setMutualFollow(false);
                        followeringModel.setFollowTo(followers);
                        userFollowings.remove(position);
                        userFollowings.add(position, followeringModel);
                        followingAdapter.notifyItemChanged(position);
                    }
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(UserFollowersActivity.this, "callDeleteFollowUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(UserFollowersActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing()){
                    loader.dismiss();
                }
                Toast.makeText(UserFollowersActivity.this, "Can't unfollow the user.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void CallFollowApi(int position) {
        loader.show();
        String id = "";
        if (followType.equals("Followers")) {
            id = userFollowers.get(position).getFollowFrom().getId();
        }else{
            id = userFollowings.get(position).getFollowTo().getId();
        }
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> followuser = apiService.followUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        followuser.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()){
                    loader.dismiss();
                }
                if (response.code() == 200) {
                    if (followType.equals("Followers")) {
                        FollowersModel followersModel = userFollowers.get(position);
                        Followers followers = followersModel.getFollowFrom();
                        followers.setMutualFollow(true);
                        followersModel.setFollowFrom(followers);
                        userFollowers.remove(position);
                        userFollowers.add(position, followersModel);
                        followersAdapter.notifyItemChanged(position);
                    }else{
                        FollowingModel followeringModel = userFollowings.get(position);
                        Followers followers = followeringModel.getFollowTo();
                        followers.setMutualFollow(true);
                        followeringModel.setFollowTo(followers);
                        userFollowings.remove(position);
                        userFollowings.add(position, followeringModel);
                        followingAdapter.notifyItemChanged(position);
                    }
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(UserFollowersActivity.this, "callPostUserFollowApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(UserFollowersActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing()){
                    loader.dismiss();
                }
                Toast.makeText(UserFollowersActivity.this, "Can't follow the user.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void moveToProfile(int position) {
        Followers followers;
        if (followType.equals("Followers")) {
            followers = userFollowers.get(position).getFollowFrom();
        } else {
            followers = userFollowings.get(position).getFollowTo();
        }

        String strId = "";
        if (followers.getId() != null && !followers.getId().isEmpty()) {
            strId = followers.getId();
        }

        Intent intent = new Intent(UserFollowersActivity.this, ProfileScreenActivity.class);

        if (strId == mloginUserId) {
            intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, true);
        } else {
            intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, false);
            intent.putExtra(ConstantUtils.PROFILE_USERID, strId);
        }

        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;

            default:
                break;
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    void getUserFollowersApi(final boolean inited, String lastId) {
        if (inited) {
            userFollowers.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<GetFollowersResponse> followersRequest = apiService.userFollowers(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        followersRequest.enqueue(new Callback<GetFollowersResponse>() {
            @Override
            public void onResponse(Call<GetFollowersResponse> call, Response<GetFollowersResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    userFollowers.addAll(response.body().getDocs());
                    followersAdapter.notifyItemRangeInserted(followersAdapter.getItemCount(), userFollowers.size()-1);

                    if (inited && userFollowers.size() == 0) {
                        txtNotFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(UserFollowersActivity.this, "callGetFollowedUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(UserFollowersActivity.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetFollowersResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(UserFollowersActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getUserFollowingApi(final boolean inited, String lastId) {
        if (inited) {
            userFollowings.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<GetFollowingResponse> followersRequest = apiService.userFollowing(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        followersRequest.enqueue(new Callback<GetFollowingResponse>() {
            @Override
            public void onResponse(Call<GetFollowingResponse> call, Response<GetFollowingResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    userFollowings.addAll(response.body().getDocs());
                    followingAdapter.notifyItemRangeInserted(followingAdapter.getItemCount(), userFollowings.size()-1);

                    if (inited && userFollowings.size() == 0) {
                        txtNotFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(UserFollowersActivity.this, "callGetFollowedUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(UserFollowersActivity.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetFollowingResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(UserFollowersActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onApiFail(String apiSource) {
        if (loader.isShowing()) { loader.dismiss(); }
        Log.e("User Followers Api Failed: ", apiSource);
    }
}
