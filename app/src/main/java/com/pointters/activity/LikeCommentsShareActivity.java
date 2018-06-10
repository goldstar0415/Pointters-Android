package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.CommentsModel;
import com.pointters.model.LikesModel;
import com.pointters.model.ShareModel;
import com.pointters.model.request.CommentRequest;
import com.pointters.model.response.BaseResponse;
import com.pointters.model.response.CommentsResponse;
import com.pointters.model.response.LikesResponse;
import com.pointters.model.response.SharesResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.LikeTabLayout;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LikeCommentsShareActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener, OnApiFailDueToSessionListener, SwipyRefreshLayout.OnRefreshListener {

    ArrayList<CommentsModel> comments = new ArrayList<>();
    ArrayList<ShareModel> shares = new ArrayList<>();
    ArrayList<LikesModel> likes = new ArrayList<>();
    private RecyclerView recyclerComments;
    private RecyclerView recyclerLikes;
    private RecyclerView recyclerShares;
    private OnRecyclerViewItemClickListener listener;
    private Adapter likesAdapter, commentsAdapter, sharesAdapter;
    private KProgressHUD loader;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SwipyRefreshLayout swipyRefreshLayoutLikes;
    private SwipyRefreshLayout swipyRefreshLayoutComments;
    private SwipyRefreshLayout swipyRefreshLayoutShares;

    private LikeTabLayout tabLikes, tabComments, tabShares;

    private ImageView backButton;
    private TextView titleView;
    private String postId;
    private int selecteTab = 1;
    String postType = "";
    private EditText edtComment;

    private Button sendComment;
    private String lastDocIdCommentes = "";
    private String lastDocIdLikes = "";
    private String lastDocIdShares = "";
    private int likecount, commentcount,  sharecount;
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_update_comment_like_share);

        Intent intent = getIntent();
        if (intent == null){
            finish();
        }

        postId = intent.getStringExtra("postId");
        postType = intent.getStringExtra("postType");
        likecount = intent.getIntExtra("like_count", 0);
        commentcount = intent.getIntExtra("comment_count", 0);
        sharecount = intent.getIntExtra("share_count", 0);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        titleView = (TextView) findViewById(R.id.toolbar_title);
        backButton = (ImageView) findViewById(R.id.toolbar_lft_img);
        backButton.setOnClickListener(this);
        titleView.setText(postType);

        swipyRefreshLayoutLikes = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_likes);
        swipyRefreshLayoutLikes.setOnRefreshListener(this);
        swipyRefreshLayoutComments = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_comments);
        swipyRefreshLayoutComments.setOnRefreshListener(this);
        swipyRefreshLayoutShares = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_shares);
        swipyRefreshLayoutShares.setOnRefreshListener(this);

        tabLikes = (LikeTabLayout) findViewById(R.id.tab_likes);
        tabComments = (LikeTabLayout) findViewById(R.id.tab_comments);
        tabShares = (LikeTabLayout) findViewById(R.id.tab_shares);

        tabLikes.setOnClickListener(this);
        tabComments.setOnClickListener(this);
        tabShares.setOnClickListener(this);

        tabLikes.setTitle(String.format("%d Likes", likecount));
        tabComments.setTitle(String.format("%d Comments", commentcount));
        tabShares.setTitle(String.format("%d Shares", sharecount));

        DividerItemDecoration divider = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getBaseContext(), R.drawable.divider_option)));


        recyclerComments = (RecyclerView) findViewById(R.id.recycler_comments);
        recyclerComments.addItemDecoration(divider);
        recyclerLikes = (RecyclerView) findViewById(R.id.recycler_likes);
        recyclerLikes.addItemDecoration(divider);
        recyclerShares = (RecyclerView) findViewById(R.id.recycler_shares);
        recyclerShares.addItemDecoration(divider);

        edtComment = (EditText) findViewById(R.id.edt_comment);
        sendComment = (Button) findViewById(R.id.btn_send);
        sendComment.setOnClickListener(this);
        likesAdapter = new Adapter(this, R.layout.adapter_current_update_comment_cell, 0);
        commentsAdapter = new Adapter(this, R.layout.adapter_current_update_comment_cell, 1);
        sharesAdapter = new Adapter(this, R.layout.adapter_current_update_comment_cell, 2);
        recyclerLikes.setLayoutManager(new LinearLayoutManager(this));
        recyclerLikes.setAdapter(likesAdapter);
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerComments.setAdapter(commentsAdapter);
        recyclerShares.setLayoutManager(new LinearLayoutManager(this));
        recyclerShares.setAdapter(sharesAdapter);
        CallGetComments(lastDocIdCommentes);
    }

    private void CallGetComments(String lt_id) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<CommentsResponse> callGetCategoryApi = apiService.getPostComments(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), postId, lt_id);
        callGetCategoryApi.enqueue(new Callback<CommentsResponse>() {
            @Override
            public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    if (lastDocIdCommentes == "") {
                        comments = new ArrayList<>();
                    }
                    ArrayList<CommentsModel> models = response.body().getDocs();
                    comments.addAll(models);
                    lastDocIdCommentes = response.body().getLastDocId();
                    commentsAdapter.setCommentData(comments);
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(LikeCommentsShareActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(LikeCommentsShareActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<CommentsResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(LikeCommentsShareActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void CallGetLikes(String lt_id) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<LikesResponse> callGetCategoryApi = apiService.getPostLikes(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), postId, lt_id);
        callGetCategoryApi.enqueue(new Callback<LikesResponse>() {
            @Override
            public void onResponse(Call<LikesResponse> call, Response<LikesResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    if (lastDocIdLikes == "") {
                        likes = new ArrayList<>();
                    }
                    ArrayList<LikesModel> models = response.body().getDocs();
                    likes.addAll(models);
                    lastDocIdLikes = response.body().getLastDocId();
                    likesAdapter.setLikesData(likes);
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(LikeCommentsShareActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(LikeCommentsShareActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<LikesResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(LikeCommentsShareActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void CallGetShares(String lt_id) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<SharesResponse> callGetCategoryApi = apiService.getPostShares(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), postId, lt_id);
        callGetCategoryApi.enqueue(new Callback<SharesResponse>() {
            @Override
            public void onResponse(Call<SharesResponse> call, Response<SharesResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    if (lastDocIdShares == "") {
                        shares = new ArrayList<>();
                    }
                    ArrayList<ShareModel> models = response.body().getDocs();
                    shares.addAll(models);
                    lastDocIdShares = response.body().getLastDocId();
                    sharesAdapter.setSharesData(shares);
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(LikeCommentsShareActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(LikeCommentsShareActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<SharesResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(LikeCommentsShareActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;

            case R.id.tab_likes:
                selectTab(0);
                break;

            case R.id.tab_comments:
                selectTab(1);
                break;

            case R.id.tab_shares:
                selectTab(2);
                break;

            case R.id.btn_send:
                if (edtComment.getText().toString().length() > 0 && !edtComment.getText().toString().equals(" ")) {
                    CallSendComment(edtComment.getText().toString());
                }
                break;

        }
    }

    public void selectTab(int index) {
        selecteTab = index;
        tabShares.setSelect(false);
        tabComments.setSelect(false);
        tabLikes.setSelect(false);
        swipyRefreshLayoutLikes.setVisibility(View.GONE);
        swipyRefreshLayoutComments.setVisibility(View.GONE);
        swipyRefreshLayoutShares.setVisibility(View.GONE);
        switch (index){
            case 0:
                tabLikes.setSelect(true);
                swipyRefreshLayoutLikes.setVisibility(View.VISIBLE);
                if (likes.size() == 0) {
                    CallGetLikes(lastDocIdLikes);
                }
                break;
            case 1:
                tabComments.setSelect(true);
                swipyRefreshLayoutComments.setVisibility(View.VISIBLE);
                if (comments.size() == 0) {
                    CallGetComments(lastDocIdCommentes);
                }
                break;
            case 2:
                tabShares.setSelect(true);
                swipyRefreshLayoutShares.setVisibility(View.VISIBLE);
                if (shares.size() == 0) {
                    CallGetShares(lastDocIdShares);
                }
                break;
        }
    }


    @Override
    public void onItemClick(int position) {

//        selectedNotification = notifications.get(position);
//        CallPutReadNotification(selectedNotification);

    }

    public void CallSendComment(final String commentText) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<BaseResponse> callGetCategoryApi = apiService.postComment(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), postId, new CommentRequest(commentText));
        callGetCategoryApi.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    boolean object = response.body().isSuccess();
                    if (object) {
                        lastDocIdCommentes = "";
                        edtComment.setText("");
                        CallGetComments(lastDocIdCommentes);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(LikeCommentsShareActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(LikeCommentsShareActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(LikeCommentsShareActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public void gotoDetailView(NotificationModel model){
//        if (selectedNotification.getType().contains("service")) {
//            Intent intent = new Intent(LikeCommentsShareActivity.this, ServiceDetailActivity.class);
//            intent.putExtra(ConstantUtils.SERVICE_ID, selectedNotification.getServiceId());
//            startActivity(intent);
//        }else if(selectedNotification.getType().contains("post")) {
////            Intent intent = new Intent(NotificationsActivity.this, .class);
////            intent.putExtra(ConstantUtils.SERVICE_ID, selectedNotification.getServiceId());
////            startActivity(intent);
//
//        }else if (selectedNotification.getType().contains("follow") || selectedNotification.getType().contains("friend")) {
//            Intent intent = new Intent(LikeCommentsShareActivity.this, ProfileScreenActivity.class);
//            intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, false);
//            intent.putExtra(ConstantUtils.PROFILE_USERID, selectedNotification.getUserId());
//            startActivity(intent);
//        }
//
//
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onApiFail(String apiSource) {

    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        swipyRefreshLayoutLikes.setRefreshing(false);
        swipyRefreshLayoutShares.setRefreshing(false);
        swipyRefreshLayoutComments.setRefreshing(false);
        if (direction == SwipyRefreshLayoutDirection.BOTTOM){
            if (selecteTab == 0) {
                CallGetLikes(lastDocIdLikes);

            }else if (selecteTab == 1){
                CallGetComments(lastDocIdCommentes);
            }else{
                CallGetShares(lastDocIdShares);
            }
        }else{
            if (selecteTab == 0) {
                lastDocIdLikes = "";
                CallGetLikes(lastDocIdLikes);
            }else if (selecteTab == 1){
                lastDocIdCommentes = "";
                CallGetComments(lastDocIdCommentes);
            }else{
                lastDocIdShares = "";
                CallGetShares(lastDocIdShares);
            }
        }

    }
//======================================PaymentAdapter============================================

    public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {

        Context obj;
        int res;
        ArrayList<CommentsModel> commentsModels = new ArrayList<>();
        ArrayList<LikesModel> likesModels = new ArrayList<>();
        ArrayList<ShareModel> shareModels = new ArrayList<>();
        LayoutInflater inflater;

        int type = 0;

        Adapter(Context obj, int res, int type) {
            this.obj = obj;
            this.res = res;
            inflater = LayoutInflater.from(obj);
            this.type = type;
        }

        public void setCommentData(ArrayList<CommentsModel> models){
            this.commentsModels = models;
            notifyDataSetChanged();
        }

        public void setLikesData(ArrayList<LikesModel> models){
            this.likesModels = models;
            notifyDataSetChanged();
        }

        public void setSharesData(ArrayList<ShareModel> models){
            this.shareModels = models;
            notifyDataSetChanged();
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHolder hold;

            View vv = LayoutInflater.from(obj).inflate(res, parent, false);
            hold = new MyHolder(vv);
            return hold;

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(MyHolder hold, final int position) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.photo_placeholder)
                    .showImageForEmptyUri(R.drawable.photo_placeholder)
                    .showImageOnFail(R.drawable.photo_placeholder)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
            if (type == 0){
                LikesModel model = likesModels.get(position);
                ImageLoader.getInstance().displayImage(model.getUser().getProfilePic(), hold.profileImageView, options);
                hold.title.setText(model.getUser().getFirstName() + " " + model.getUser().getLastName());
                hold.txtComment.setText("");
                DateTime dateTime = new DateTime(model.getLikeDate());
                String timeago = TimeAgo.using(dateTime.toDate().getTime());
                hold.timeago.setText(timeago);
            }else if (type == 1){
                CommentsModel model = commentsModels.get(position);
                ImageLoader.getInstance().displayImage(model.getUser().getProfilePic(), hold.profileImageView, options);
                hold.title.setText(model.getUser().getFirstName() + " " + model.getUser().getLastName());
                hold.txtComment.setText(model.getCommentText());
                DateTime dateTime = new DateTime(model.getUpdatedAt());
                String timeago = TimeAgo.using(dateTime.toDate().getTime());
                hold.timeago.setText(timeago);
            }else{
                ShareModel model = shareModels.get(position);
                ImageLoader.getInstance().displayImage(model.getUser().getProfilePic(), hold.profileImageView, options);
                hold.title.setText(model.getUser().getFirstName() + " " + model.getUser().getLastName());
                hold.txtComment.setText("");
                DateTime dateTime = new DateTime(model.getShareDate());
                String timeago = TimeAgo.using(dateTime.toDate().getTime());
                hold.timeago.setText(timeago);
            }

        }

        @Override
        public int getItemCount() {
            if (type == 0){
                return likesModels.size();
            }else if (type == 1){
                return commentsModels.size();
            }else{
                return shareModels.size();
            }

        }

        //holder
        public class MyHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView timeago;
            TextView txtComment;
            RoundedImageView profileImageView;

            public MyHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.txt_name);
                timeago = (TextView) itemView.findViewById(R.id.txt_dateTime);
                txtComment = (TextView) itemView.findViewById(R.id.txt_comment);
                profileImageView = (RoundedImageView) itemView.findViewById(R.id.img_profile1);
            }
        }
    }

}