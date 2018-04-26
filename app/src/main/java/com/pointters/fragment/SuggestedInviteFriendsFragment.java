package com.pointters.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.flurgle.camerakit.CameraKit;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.activity.ProfileScreenActivity;
import com.pointters.activity.TagServiceActivity;
import com.pointters.adapter.PostDataAdapter;
import com.pointters.adapter.SuggestedRecyclerAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.CategoryDetails;
import com.pointters.model.InviteSuggestedUserModel;
import com.pointters.model.Media;
import com.pointters.model.TagData;
import com.pointters.model.Tags;
import com.pointters.model.request.PostUpdateRequest;
import com.pointters.model.response.GetCategoryResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.EqualSpacingItemDecoration;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by mac on 12/7/17.
 */

public class SuggestedInviteFriendsFragment extends Fragment implements View.OnClickListener, TabLayout.OnTabSelectedListener, OnRecyclerViewButtonClickListener {

    private View view;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private KProgressHUD loader;

    private ArrayList<InviteSuggestedUserModel> inviteSuggestedUserModels = new ArrayList<>();

    RecyclerView suggestedInviteFriendsRecyclerView;
    Button inviteButton;
    SuggestedRecyclerAdapter postDataAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_suggested_invite_friends, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initViews();
        setOnClickListners();

        loader = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        return view;
    }

    private void initViews() {


        //Camera Layout
        inviteButton = (Button) view.findViewById(R.id.btn_invite);

        suggestedInviteFriendsRecyclerView = (RecyclerView)view.findViewById(R.id.suggessted_invite_friends_recyclerview);
        LinearLayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        suggestedInviteFriendsRecyclerView.setLayoutManager(lm);
        postDataAdapter = new SuggestedRecyclerAdapter(getContext(), inviteSuggestedUserModels, this);
        suggestedInviteFriendsRecyclerView.setAdapter(postDataAdapter);
        suggestedInviteFriendsRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(0));
        postDataAdapter.notifyDataSetChanged();

        getInviteSuggestedData();
    }

    private void getInviteSuggestedData() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<ArrayList<InviteSuggestedUserModel>> callGetCategoryApi = apiService.getInviteSuggested(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        callGetCategoryApi.enqueue(new Callback<ArrayList<InviteSuggestedUserModel>>() {
            @Override
            public void onResponse(Call<ArrayList<InviteSuggestedUserModel>> call, Response<ArrayList<InviteSuggestedUserModel>> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    inviteSuggestedUserModels = response.body();
                    postDataAdapter.setData(inviteSuggestedUserModels);
                    postDataAdapter.notifyDataSetChanged();

                }
                else if (response.code() == 401) {
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<ArrayList<InviteSuggestedUserModel>> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
//                Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnClickListners() {
        inviteButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch ((v.getId())) {
            case R.id.btn_invite:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                break;

            case 1:
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CameraKit.Constants.PERMISSION_REQUEST_CAMERA) {
        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onButtonClick(View v, int position) {
        Button button = (Button) v;
        String userid = inviteSuggestedUserModels.get(position).getUserId();
        if (button.getText().toString().equals("FOLLOW")) {
            postUserFollowingApi(userid, position);
        }else{
            deleteUserFollowApi(userid, position);
        }
    }

    // Post user follow status
    private void postUserFollowingApi(String id, final int position) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> followuser = apiService.followUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        followuser.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()){
                    loader.dismiss();
                }
                if (response.code() == 200) {
                    InviteSuggestedUserModel model = inviteSuggestedUserModels.get(position);
                    model.setHasFollowed(true);
                    inviteSuggestedUserModels.get(position).setHasFollowed(true);// = model;
                    postDataAdapter.setData(inviteSuggestedUserModels);
                    postDataAdapter.notifyItemChanged(position);
                } else if (response.code() == 401) {
//                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callPostUserFollowApi");
//                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing()){
                    loader.dismiss();
                }
//                Toast.makeText(ProfileScreenActivity.this, "Can't follow the user.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Delete user follow status
    private void deleteUserFollowApi(String id, final int position) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> unfollowuser = apiService.unFollowUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        unfollowuser.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()){
                    loader.dismiss();
                }
                if (response.code() == 200) {
                    InviteSuggestedUserModel model = inviteSuggestedUserModels.get(position);
                    model.setHasFollowed(false);
                    inviteSuggestedUserModels.get(position).setHasFollowed(false);// = model;
                    postDataAdapter.setData(inviteSuggestedUserModels);
                    postDataAdapter.notifyItemChanged(position);
//                    isFollowed = false;
//                    setFollowStatus();
                } else if (response.code() == 401) {
//                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ProfileScreenActivity.this, "callDeleteFollowUserApi");
//                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing()){
                    loader.dismiss();
                }
//                Toast.makeText(ProfileScreenActivity.this, "Can't unfollow the user.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}