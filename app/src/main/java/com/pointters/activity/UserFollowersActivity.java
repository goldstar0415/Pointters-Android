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
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.FollowersAdapter;
import com.pointters.adapter.FollowingAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecycleItemClickListener;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null)
            json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");

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
        recyclerFollowers.addOnItemTouchListener(new OnRecycleItemClickListener(this, new OnRecycleItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(UserFollowersActivity.this, ProfileScreenActivity.class);
                startActivity(intent);
            }
        }));

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
            loader.show();
            getUserFollowingApi(true, "");
        }
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
