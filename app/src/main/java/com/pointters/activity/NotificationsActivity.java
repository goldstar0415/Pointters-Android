package com.pointters.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.pointters.model.NotificationModel;
import com.pointters.model.response.BaseResponse;
import com.pointters.model.response.GetNotificationsResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.RoundedCornerLayout;
import com.pointters.utils.TypefaceSpan;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class NotificationsActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener, OnApiFailDueToSessionListener, SwipyRefreshLayout.OnRefreshListener {

    ArrayList<NotificationModel> notifications = new ArrayList<>();
    RecyclerView recyclerNotifications;
    private OnRecyclerViewItemClickListener listener;
    Adapter notificationsAdapter;
    private KProgressHUD loader;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private NotificationModel selectedNotification;
    SwipyRefreshLayout swipyRefreshLayout;

    private String lastDocId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        AppUtils.setToolBarWithBothIcon(NotificationsActivity.this, getResources().getString(R.string.title_notifications), R.drawable.back_icon, R.drawable.icon_edit_gray);
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh);
        swipyRefreshLayout.setOnRefreshListener(this);


        recyclerNotifications = (RecyclerView) findViewById(R.id.recycler_notificaiton);
        DividerItemDecoration divider = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getBaseContext(), R.drawable.divider_option)));
        recyclerNotifications.addItemDecoration(divider);


        notificationsAdapter = new Adapter(this, R.layout.adapter_notification, notifications, this);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotifications.setAdapter(notificationsAdapter);
        CallGetNotifications(lastDocId);
    }

    public void CallGetNotifications(String lt_id) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetNotificationsResponse> callGetCategoryApi = apiService.getNotifications(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lt_id);
        callGetCategoryApi.enqueue(new Callback<GetNotificationsResponse>() {
            @Override
            public void onResponse(Call<GetNotificationsResponse> call, Response<GetNotificationsResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    if (lastDocId == "") {
                        notifications = new ArrayList<>();
                    }
                    ArrayList<NotificationModel> models = response.body().getDocs();
                    notifications.addAll(models);
                    lastDocId = response.body().getLastDocId();

                    notificationsAdapter.setData(notifications);
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(NotificationsActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(NotificationsActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetNotificationsResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(NotificationsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
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

        }
    }

    @Override
    public void onItemClick(int position) {

        selectedNotification = notifications.get(position);
        CallPutReadNotification(selectedNotification);

    }

    public void CallPutReadNotification(final NotificationModel model) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<BaseResponse> callGetCategoryApi = apiService.putNotificationRead(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), model.getId());
        callGetCategoryApi.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    boolean object = response.body().isSuccess();
                    if (object) {
                        NotificationModel readmodel = model;
                        readmodel.setMarkedRead(true);
                        int position = notifications.indexOf(model);
                        notifications.remove(model);
                        notifications.add(position, readmodel);
                        notificationsAdapter.notifyItemChanged(position);
                        gotoDetailView(model);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(NotificationsActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(NotificationsActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(NotificationsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void gotoDetailView(NotificationModel model){
        if (selectedNotification.getType().contains("service")) {
            Intent intent = new Intent(NotificationsActivity.this, ServiceDetailActivity.class);
            intent.putExtra(ConstantUtils.SERVICE_ID, selectedNotification.getServiceId());
            startActivity(intent);
        }else if(selectedNotification.getType().contains("post")) {
//            Intent intent = new Intent(NotificationsActivity.this, .class);
//            intent.putExtra(ConstantUtils.SERVICE_ID, selectedNotification.getServiceId());
//            startActivity(intent);

        }else if (selectedNotification.getType().contains("follow") || selectedNotification.getType().contains("friend")) {
            Intent intent = new Intent(NotificationsActivity.this, ProfileScreenActivity.class);
            intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, false);
            intent.putExtra(ConstantUtils.PROFILE_USERID, selectedNotification.getUserId());
            startActivity(intent);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onApiFail(String apiSource) {

    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        swipyRefreshLayout.setRefreshing(false);
        if (direction == SwipyRefreshLayoutDirection.BOTTOM){
            CallGetNotifications(lastDocId);
        }else{
            lastDocId = "";
            CallGetNotifications(lastDocId);
        }

    }
//======================================PaymentAdapter============================================

    public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {

        Activity obj;
        int res;
        ArrayList<NotificationModel> Al;
        LayoutInflater inflater;
        OnRecyclerViewItemClickListener listener;

        Adapter(Activity obj, int res, ArrayList<NotificationModel> Al, OnRecyclerViewItemClickListener listener) {
            this.obj = obj;
            this.res = res;
            this.Al = Al;
            inflater = LayoutInflater.from(obj);
            this.listener = listener;
        }

        public void setData(ArrayList<NotificationModel> models){
            this.Al = models;
            notifyDataSetChanged();
        }
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHolder hold;

            View vv = LayoutInflater.from(obj).inflate(res, parent, false);
            hold = new MyHolder(vv);
            return hold;

        }

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
            NotificationModel model = Al.get(position);
            ImageLoader.getInstance().displayImage(model.getProfilePic(), hold.profileImageView, options);
            hold.title.setText(model.getNames());
            hold.title.setTextSize(12);
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Medium.ttf");
            Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf");
            SpannableStringBuilder ss = new SpannableStringBuilder( model.getNames() + " " + model.getActivity());
            ss.setSpan(new TypefaceSpan(font), 0, model.getNames().length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new TypefaceSpan(font2), model.getNames().length(), model.getNames().length() + model.getActivity().length() + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            hold.title.setText(ss);

            if (model.getMedia() != null) {
                if (model.getMedia().getMediaType() == "image"){
                    ImageLoader.getInstance().displayImage(model.getMedia().getFileName(), hold.mediaImageView, options);
                }
                hold.llServiceMedia.setVisibility(View.VISIBLE);
            }else{
                hold.llServiceMedia.setVisibility(View.GONE);
            }

            if (model.isMarkedRead()) {
                hold.markedRead.setVisibility(View.INVISIBLE);
            }else{
                hold.markedRead.setVisibility(View.VISIBLE);
            }

            String time = DateTimeUtils.getTimeAgo(getBaseContext(), model.getTime());
            hold.timeago.setText(time);
            hold.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return Al.size();

        }

        //holder
        public class MyHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView timeago;
            RoundedImageView profileImageView;
            ImageView mediaImageView;
            ImageView markedRead;
            RoundedCornerLayout llServiceMedia;

            public MyHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.txt_title);
                timeago = (TextView) itemView.findViewById(R.id.txt_time_ago_request_accepted);
                profileImageView = (RoundedImageView) itemView.findViewById(R.id.img_accept_request);
                markedRead = (ImageView) itemView.findViewById(R.id.unread_badge);
                mediaImageView = (ImageView) itemView.findViewById(R.id.img_media);
                llServiceMedia = (RoundedCornerLayout) itemView.findViewById(R.id.layout_notification_media);
            }
        }
    }

}