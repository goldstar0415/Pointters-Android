package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.FollowersAdapter;
import com.pointters.adapter.FollowingAdapter;
import com.pointters.adapter.WatchingLikedAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecycleItemClickListener;
import com.pointters.model.FollowersModel;
import com.pointters.model.FollowingModel;
import com.pointters.model.Service;
import com.pointters.model.ServiceModel;
import com.pointters.model.ServicesModel;
import com.pointters.model.response.GetFollowersResponse;
import com.pointters.model.response.GetFollowingResponse;
import com.pointters.model.response.GetServicesReponse;
import com.pointters.model.response.GetTagServiceSellerResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.DividerItemDecorationVer;
import com.pointters.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mac on 12/18/17.
 */

public class WatchingLikeActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {
    private TextView txtTitle;
    private RecyclerView recyclerWatchingLike;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private KProgressHUD loader;
    private TextView txtNotFound;
    private String json, watchingLikedType;

    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;

    private String lastDocId = "";
    private int limitCnt = 0;
    private int totalCnt = 0;

    ArrayList<ServicesModel> watchingLikedList = new ArrayList<>();
    WatchingLikedAdapter watchingLikedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (!sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "").equals("")) {
            mUserLat = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "0"));
        }
        if (!sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "").equals("")) {
            mUserLng = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "0"));
        }
        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null)
            json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");

        watchingLikedType = sharedPreferences.getString(ConstantUtils.WATCHING_LIKED_TYPE, "");

        txtTitle = (TextView) findViewById(R.id.toolbar_title);
        findViewById(R.id.toolbar_right_img).setVisibility(View.GONE);
        findViewById(R.id.toolbar_lft_img).setOnClickListener(this);

        txtNotFound = (TextView) findViewById(R.id.txt_notfound);
        if (watchingLikedType.equals("Watching")) {
            txtNotFound.setText("No watching found");
        } else {
            txtNotFound.setText("No liked found");
        }
        txtNotFound.setVisibility(View.GONE);

        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        recyclerWatchingLike = (RecyclerView) findViewById(R.id.recycler_followers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WatchingLikeActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerWatchingLike.setLayoutManager(linearLayoutManager);
        recyclerWatchingLike.setItemAnimator(new DefaultItemAnimator());
        recyclerWatchingLike.addOnItemTouchListener(new OnRecycleItemClickListener(this, new OnRecycleItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(WatchingLikeActivity.this, ServiceDetailActivity.class);
                ServicesModel model = watchingLikedList.get(position);
                String id = model.getService().getId();
                intent.putExtra(ConstantUtils.SERVICE_ID, id);
                startActivity(intent);
            }
        }));

//        DividerItemDecorationVer divider = new DividerItemDecorationVer(ContextCompat.getDrawable(this, R.drawable.divider_option));
//        divider = new DividerItemDecorationVer(ContextCompat.getDrawable(this, R.drawable.divider_option));
//        recyclerWatchingLike.addItemDecoration(divider);

        watchingLikedAdapter = new WatchingLikedAdapter(WatchingLikeActivity.this, watchingLikedList, mUserLat, mUserLng);
        recyclerWatchingLike.setAdapter(watchingLikedAdapter);

        if (watchingLikedType.equals("Watching")) {
            txtTitle.setText(getResources().getString(R.string.watching));
            recyclerWatchingLike.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    if (totalCnt > limitCnt) {
                        getWatchingApi(false, lastDocId);
                    }
                }
            });
            loader.show();
            getWatchingApi(true, "");
        }
        else {
            txtTitle.setText(getResources().getString(R.string.liked));
            recyclerWatchingLike.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    if (totalCnt > limitCnt) {
                        getLikedApi(false, lastDocId);
                    }
                }
            });
            loader.show();
            getLikedApi(true, "");
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

            default:
                break;
        }
    }

    void getWatchingApi(final boolean inited, String lastId) {
        if (inited) {
            watchingLikedList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<GetServicesReponse> watchingRequest = apiService.getWatchingServices(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        watchingRequest.enqueue(new Callback<GetServicesReponse>() {
            @Override
            public void onResponse(Call<GetServicesReponse> call, Response<GetServicesReponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    watchingLikedList.addAll(response.body().getDocs());
                    watchingLikedAdapter.notifyItemRangeInserted(watchingLikedAdapter.getItemCount(), watchingLikedList.size()-1);

                    if (inited && watchingLikedList.size() == 0) {
                        txtNotFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(WatchingLikeActivity.this, "callGetWatchingApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(WatchingLikeActivity.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetServicesReponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(WatchingLikeActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getLikedApi(final boolean inited, String lastId) {
        if (inited) {
            watchingLikedList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<GetServicesReponse> likedRequest = apiService.getLikedServices(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        likedRequest.enqueue(new Callback<GetServicesReponse>() {
            @Override
            public void onResponse(Call<GetServicesReponse> call, Response<GetServicesReponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    watchingLikedList.addAll(response.body().getDocs());
                    watchingLikedAdapter.notifyItemRangeInserted(watchingLikedAdapter.getItemCount(), watchingLikedList.size()-1);

                    if (inited && watchingLikedList.size() == 0) {
                        txtNotFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(WatchingLikeActivity.this, "callGetLikedApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(WatchingLikeActivity.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetServicesReponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(WatchingLikeActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onApiFail(String apiSource) {
        if (loader.isShowing()) { loader.dismiss(); }
        Log.e("Watching & Liked Api Failed: ", apiSource);
    }
}
