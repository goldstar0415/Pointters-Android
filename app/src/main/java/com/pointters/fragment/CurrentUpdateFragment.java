package com.pointters.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.activity.AddServiceActivity;
import com.pointters.activity.HomeActivity;
import com.pointters.activity.LikeCommentsShareActivity;
import com.pointters.activity.PhotoSlideViewerActivity;
import com.pointters.activity.ProfileScreenActivity;
import com.pointters.activity.ServiceDetailActivity;
import com.pointters.adapter.CurrentUpdatesAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.CommentModel;
import com.pointters.model.CommentsModel;
import com.pointters.model.PostData;
import com.pointters.model.response.CommentsResponse;
import com.pointters.model.response.CurrentUpdateModel;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by prashant on 22-11-2017.
 */

public class CurrentUpdateFragment extends Fragment implements OnRecyclerViewButtonClickListener, OnApiFailDueToSessionListener {

    RecyclerView recyclerView;
    CurrentUpdatesAdapter adapterAdapter;
    private CardView newpostView;
    private KProgressHUD loader;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageButton cameraButton;

    private ArrayList<CurrentUpdateModel> currentUpdateModels = new ArrayList<>();
    private View view;
    private String json;
    private DisplayImageOptions options;
    String profileUrl;
    RoundedImageView userProfileImageView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blank, container, false);

        sharedPreferences = getContext().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null)
            json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");
        userProfileImageView = (RoundedImageView) view.findViewById(R.id.img_profile);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
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

                    if (jsonObject.get("profilePic").toString().startsWith("https://s3.amazonaws.com")) {
                        profileUrl = jsonObject.get("profilePic").toString();
                    } else {
//                                    profileUrl = "https://s3.amazonaws.com" + jsonObject.get("profilePic").toString();
                        profileUrl = jsonObject.get("profilePic").toString();
                    }
                    ImageLoader.getInstance().displayImage(profileUrl, userProfileImageView, options);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_update);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterAdapter = new CurrentUpdatesAdapter(getContext(), currentUpdateModels, this);
        recyclerView.setAdapter(adapterAdapter);
        adapterAdapter.setUserProfilePic(profileUrl);
        newpostView = (CardView) view.findViewById(R.id.view_new_post);
        newpostView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(ConstantUtils.LAST_SELECTED_TAB, "0");
                editor.apply();
                startActivity(new Intent(getActivity(), AddServiceActivity.class));
            }
        });
//        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
//            @Override
//            public void onChildViewAttachedToWindow(View view) {
//
//            }
//
//            @Override
//            public void onChildViewDetachedFromWindow(View view) {
//                JZVideoPlayer jzvd = view.findViewById(R.id.video_view);
//                if (jzvd != null && JZUtils.dataSourceObjectsContainsUri(jzvd.dataSourceObjects, JZMediaManager.getCurrentDataSource())) {
//                    JZVideoPlayer currentJzvd = JZVideoPlayerManager.getCurrentJzvd();
//                    if (currentJzvd != null && currentJzvd.currentScreen != JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
//                        JZVideoPlayer.releaseAllVideos();
//                    }
//                }
//            }
//        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                JZVideoPlayer.releaseAllVideos();
            }
        });
        cameraButton = (ImageButton) view.findViewById(R.id.btn_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(ConstantUtils.LAST_SELECTED_TAB, "0");
                editor.apply();
                startActivity(new Intent(getActivity(), AddServiceActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        CallGetCurrentUpdate();
    }

//    @Override
//    public void onBackPressed() {
//        if (JZVideoPlayer.backPress()) {
//            return;
//        }
//        super.onBackPressed();
//    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    public void CallGetCurrentUpdate(){
        Map<String, String> query = new HashMap<>();
        query.put("skip", "0");
        query.put("limit", "10");

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<ArrayList<CurrentUpdateModel>> callGetCategoryApi = apiService.getCurrentUpdate(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), query);
        callGetCategoryApi.enqueue(new Callback<ArrayList<CurrentUpdateModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CurrentUpdateModel>> call, Response<ArrayList<CurrentUpdateModel>> response) {

                if (response.code() == 200 && response.body() != null) {
                    currentUpdateModels.clear();
                    currentUpdateModels.addAll(response.body());
                    adapterAdapter.setData(currentUpdateModels);
                    adapterAdapter.notifyDataSetChanged();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(CurrentUpdateFragment.this);
                }
                else if (response.code() == 404) {

                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<ArrayList<CurrentUpdateModel>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e("post update error", t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onButtonClick(View v, int position) {
        switch (v.getId()){
            case R.id.bottomView:
                GotoDetailView(position);
                break;
            case R.id.btn_send:
                CallGetComments(position);
                break;
            case R.id.upper_view:   //profile
                Intent userIntent = new Intent(getActivity(), ProfileScreenActivity.class);
                userIntent.putExtra(ConstantUtils.PROFILE_LOGINUSER, false);
                userIntent.putExtra(ConstantUtils.PROFILE_USERID, currentUpdateModels.get(position).getUser().getId());
                startActivityForResult(userIntent, 1);
                break;
            case R.id.ll_service:   //type = service
                if (currentUpdateModels.get(position).getPost().getType().equals("service")) {
                    moveToServiceDetails(position);
                } else {    //type = post
                    Intent photo = new Intent(getActivity(), PhotoSlideViewerActivity.class);
                    Gson gson = new Gson();
                    photo.putExtra(ConstantUtils.MEDIA, gson.toJson(currentUpdateModels.get(position).getPost().getMedia()));
                    startActivityForResult(photo, 1);
                }
                break;
        }
    }

    private void moveToServiceDetails(int position) {
        if (currentUpdateModels.get(position).getService().getId() != null && !currentUpdateModels.get(position).getService().getId().isEmpty()) {
            String serviceId = currentUpdateModels.get(position).getService().getId();
            Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);
            intent.putExtra(ConstantUtils.SERVICE_ID, serviceId);
            startActivity(intent);
        }
    }

    private void CallGetComments(final int position) {

        final CurrentUpdateModel model = currentUpdateModels.get(position);
        final PostData postData = model.getPost();
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<CommentsResponse> callGetCategoryApi = apiService.getPostComments(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), postData.getId(), "");
        callGetCategoryApi.enqueue(new Callback<CommentsResponse>() {
            @Override
            public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    ArrayList<CommentsModel> commentsModels = response.body().getDocs();
                    ArrayList<CommentModel> commentModels = new ArrayList<>();

                    for (CommentsModel cm : commentsModels){
                        CommentModel commentModel = new CommentModel();
                        commentModel.setUser(cm.getUser());
                        commentModel.setComment(cm.getCommentText());
                        commentModel.setUpdatedAt(cm.getUpdatedAt());
                        commentModels.add(commentModel);
                    }

                    CurrentUpdateModel cmm = model;
                    cmm.getPost().setCountComments(cmm.getPost().getCountComments() + 1);
                    cmm.setComments(commentModels);
                    currentUpdateModels.remove(position);
                    currentUpdateModels.add(position, cmm);

                    adapterAdapter.setData(currentUpdateModels);
                    adapterAdapter.notifyItemChanged(position);


                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getContext(), "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(CurrentUpdateFragment.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<CommentsResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(getContext(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void GotoDetailView(int position){
        Intent intent = new Intent(getActivity(), LikeCommentsShareActivity.class);
        intent.putExtra("postId", currentUpdateModels.get(position).getPost().getId());
        intent.putExtra("postType", currentUpdateModels.get(position).getPost().getType());
        intent.putExtra("like_count", currentUpdateModels.get(position).getPost().getCountLikes());
        intent.putExtra("comment_count", currentUpdateModels.get(position).getPost().getCountComments());
        intent.putExtra("share_count", currentUpdateModels.get(position).getPost().getCountShares());
        startActivityForResult(intent, 101);
    }

    @Override
    public void onApiFail(String apiSource) {

    }


}
