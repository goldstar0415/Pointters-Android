package com.pointters.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.activity.LikeCommentsShareActivity;
import com.pointters.activity.NotificationsActivity;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.BuyOrderModel;
import com.pointters.model.CommentModel;
import com.pointters.model.CommentsModel;
import com.pointters.model.GeoJsonModel;
import com.pointters.model.LocationModel;
import com.pointters.model.Media;
import com.pointters.model.NotificationModel;
import com.pointters.model.PostData;
import com.pointters.model.Prices;
import com.pointters.model.Service;
import com.pointters.model.TagSourceModel;
import com.pointters.model.UserProfileModel;
import com.pointters.model.request.CommentRequest;
import com.pointters.model.response.BaseResponse;
import com.pointters.model.response.CurrentUpdateModel;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.GPSTracker;
import com.pointters.utils.RoundedCornerLayout;
import com.pointters.utils.TypefaceSpan;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by prashantkumar on 21/8/17.
 */

public class UpdatesAdapterAdapter extends RecyclerView.Adapter<UpdatesAdapterAdapter.MyViewHolder> {

    private Context context;
    private List<CurrentUpdateModel> currentUpdateModels;
    private OnRecyclerViewButtonClickListener listener;
    private String userProfilePic;
    private SharedPreferences sharedPreferences;

    public UpdatesAdapterAdapter(Context context, List<CurrentUpdateModel> models, OnRecyclerViewButtonClickListener listener) {
        this.context = context;
        this.currentUpdateModels = models;
        this.listener = listener;
        sharedPreferences = context.getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
    }

    public void setUserProfilePic(String pic) {
        this.userProfilePic = pic;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_updates, parent, false);
        return new MyViewHolder(itemView);
    }

    public void setData(ArrayList<CurrentUpdateModel> models){
        this.currentUpdateModels = models;
    }
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        CurrentUpdateModel model = currentUpdateModels.get(position);
        final PostData postData = model.getPost();
        if (model.isLiked()) {
            holder.likeButton.setLiked(true);
        }else{
            holder.likeButton.setLiked(false);
        }
        UserProfileModel userProfileModel = model.getUser();
        if (userProfileModel != null) {
            ImageLoader.getInstance().displayImage(userProfileModel.getProfilePic(), holder.imgUserProfile, options);

            String username = userProfileModel.getFirstName() + " " + userProfileModel.getLastName();
            String descText = username + " " + (postData.getType().equals("post") ? "Shared a Update" : "Posted a Service");
            holder.txtUserName.setTextSize(12);
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Medium.ttf");
            Typeface font2 = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.ttf");
            SpannableStringBuilder ss = new SpannableStringBuilder( descText);
            ss.setSpan(new TypefaceSpan(font), 0, username.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new TypefaceSpan(font2), username.length(), descText.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.txtUserName.setText(ss);

            String timeago = DateTimeUtils.getTimeAgo(context, postData.getUpdatedAt());
            holder.txtPostTime.setText(timeago);
        }
        if (postData.getMessage() != null) {
            holder.txtDesc.setVisibility(View.VISIBLE);
            holder.txtDesc.setText(postData.getMessage());
        }else{
            holder.txtDesc.setVisibility(View.GONE);
        }
        holder.txtComments.setText(String.format("%d Comments", postData.getCountComments()));
        holder.txtLikes.setText(String.format("%d Likes", postData.getCountLikes()));
        holder.txtShares.setText(String.format("%d Shares", postData.getCountShares()));
        if (postData.getTags() != null) {
            if (postData.getTags().size() > 0) {
                TagSourceModel tagSourceModel = postData.getTags().get(0);
                holder.view1.setVisibility(View.VISIBLE);
                holder.view2.setVisibility(View.GONE);
                if (tagSourceModel.getType().equals("user")) {
                    ImageLoader.getInstance().displayImage(tagSourceModel.getProfilePic(), holder.imgUserProfile1, options);
                    String tagusername = tagSourceModel.getFirstName() + " " + tagSourceModel.getLastName();
                    holder.txtName1.setText(tagusername);
                    if (tagSourceModel.getLocation() != null) {
                        LocationModel locationModel = tagSourceModel.getLocation();
                        GeoJsonModel geoJsonModel = locationModel.getGeoJson();
                        Double lon = geoJsonModel.getCoordinates().get(0);
                        Double lat = geoJsonModel.getCoordinates().get(1);

                        Location servicePos = new Location("");
                        servicePos.setLatitude(lat);
                        servicePos.setLongitude(lon);

                        Location userPos = new Location("");
                        GPSTracker tracker = new GPSTracker(context);

                        userPos.setLatitude(tracker.getLatitude());
                        userPos.setLongitude(tracker.getLongitude());

                        String strKm = String.format("%.02f", userPos.distanceTo(servicePos)/1000) + "km";

                        holder.txtLocation1.setText(strKm + " " + locationModel.FullAddress());

                    }
                }else if (tagSourceModel.getType().equals("service")) {
                    Media media = tagSourceModel.getMedia();
                    if (media.getMediaType().equals("image")){
                        ImageLoader.getInstance().displayImage(media.getFileName(), holder.imgUserProfile1, options);
                    }
                    String tagusername = tagSourceModel.getDescription();
                    holder.txtName1.setText(tagusername);
                    if (tagSourceModel.getLocation() != null) {
                        LocationModel locationModel = tagSourceModel.getLocation();
                        GeoJsonModel geoJsonModel = locationModel.getGeoJson();
                        Double lon = geoJsonModel.getCoordinates().get(0);
                        Double lat = geoJsonModel.getCoordinates().get(1);

                        Location servicePos = new Location("");
                        servicePos.setLatitude(lat);
                        servicePos.setLongitude(lon);

                        Location userPos = new Location("");
                        GPSTracker tracker = new GPSTracker(context);

                        userPos.setLatitude(tracker.getLatitude());
                        userPos.setLongitude(tracker.getLongitude());

                        String strKm = String.format("%.02f", userPos.distanceTo(servicePos)/1000) + "km";

                        holder.txtLocation1.setText(strKm + " " + locationModel.FullAddress());
                    }
                }

            }
        }else{
            holder.view1.setVisibility(View.GONE);
            holder.view2.setVisibility(View.GONE);
        }
        if (postData.getType().equals("post")) {
            Media media = postData.getMedia();
            if (media.getMediaType().equals("image")) {
                ImageLoader.getInstance().displayImage(media.getFileName(), holder.postImageView, options);
            }
        }else if (postData.getType().equals("service")) {
            Service service = model.getService();
            Media media = service.getMedia().get(0);
            if (media.getMediaType().equals("image")) {
                ImageLoader.getInstance().displayImage(media.getFileName(), holder.postImageView, options);
            }
            holder.view1.setVisibility(View.GONE);
            holder.view2.setVisibility(View.VISIBLE);
            holder.txtName2.setText(service.getDescription());
            Prices prices = service.getPrices().get(0);
            String strPrice = "";
            strPrice = String.format("Starting at %s%d for %d%s of Service", prices.getCurrencySymbol(), prices.getPrice(), prices.getTime(), prices.getTimeUnitOfMeasure());
            holder.txtServiceDesc.setText(strPrice);
            if (service.getLocation() != null) {
                LocationModel locationModel = service.getLocation();
                GeoJsonModel geoJsonModel = locationModel.getGeoJson();
                Double lon = geoJsonModel.getCoordinates().get(0);
                Double lat = geoJsonModel.getCoordinates().get(1);

                Location servicePos = new Location("");
                servicePos.setLatitude(lat);
                servicePos.setLongitude(lon);

                Location userPos = new Location("");
                GPSTracker tracker = new GPSTracker(context);

                userPos.setLatitude(tracker.getLatitude());
                userPos.setLongitude(tracker.getLongitude());

                String strKm = String.format("%.02f", userPos.distanceTo(servicePos)/1000) + "km ";

                holder.txtLocation2.setText(strKm + " " + locationModel.FullAddress());

                holder.txtPoints.setText(String.valueOf(service.getPointValue()));
                holder.txtBusiness.setText(String.valueOf(service.getNumOrders()));
                holder.txtStars.setText(String.valueOf(service.getAvgRating()) + "%");
            }

        }


        if (model.getComments() != null) {
            ArrayList<CommentModel> commentModels = model.getComments();
            if (commentModels.size() > 0) {
                int remainComments = commentModels.size();
                while (commentModels.size() > 2) {
                    commentModels.remove(commentModels.size() - 1);
                }
                Adapter adapter = new Adapter(context, R.layout.adapter_current_update_comment_cell, commentModels);
                holder.commentRecyclerView.setAdapter(adapter);
                holder.viewallCommentsButton.setText(String.format("View all %d Comments", remainComments));
            }else{
                holder.commentRecyclerView.setVisibility(View.GONE);
                holder.viewallCommentsButton.setVisibility(View.GONE);
            }
        }else{
            holder.commentRecyclerView.setVisibility(View.GONE);
            holder.viewallCommentsButton.setVisibility(View.GONE);
        }

        if (userProfilePic != null) {
            ImageLoader.getInstance().displayImage(userProfilePic, holder.imgUserProfile3, options);
        }
        holder.viewallCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonClick(v, position);
            }
        });

        holder.sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.edtComment.getText().toString().length() > 0) {
                    if (!holder.edtComment.getText().toString().equals(" ")) {
                        CallSendComment(holder.edtComment.getText().toString(), postData.getId(), v, position);
                        holder.edtComment.setText("");
                    }
                }
            }
        });

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                CallLikePost(postData.getId(), position);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                CallUnlikePost(postData.getId(), position);
            }
        });

        holder.bottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonClick(v, position);
            }
        });

//        if(position == 0) {
//            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp),(int) context.getResources().getDimension(R.dimen._6sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
//            holder.layoutParent.setLayoutParams(holder.layoutParams);
//        }

    }

    public void CallSendComment(final String commentText, String postId, final View view, final int position) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<BaseResponse> callGetCategoryApi = apiService.postComment(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), postId, new CommentRequest(commentText));
        callGetCategoryApi.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    boolean object = response.body().isSuccess();
                    if (object) {
                        listener.onButtonClick(view, position);
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(context, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void CallLikePost(String postId, final int position) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<BaseResponse> callGetCategoryApi = apiService.postLike(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), postId);
        callGetCategoryApi.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.e("liked: ", String.valueOf(position));
                CurrentUpdateModel model = currentUpdateModels.get(position);
                CurrentUpdateModel cm = model;
                PostData postData = model.getPost();
                PostData pd = postData;
                pd.setCountLikes(postData.getCountLikes() + 1);
                cm.setPost(pd);
                cm.setLiked(true);
                currentUpdateModels.remove(position);
                currentUpdateModels.add(position, cm);
                notifyItemChanged(position);
                if (response.code() == 200 && response.body() != null) {
                    boolean object = response.body().isSuccess();
                    if (object) {

//                        CurrentUpdateModel model = currentUpdateModels.get(position);
//                        CurrentUpdateModel cm = model;
//                        PostData postData = model.getPost();
//                        PostData pd = postData;
//                        pd.setCountLikes(postData.getCountLikes() + 1);
//                        cm.setPost(pd);
//                        currentUpdateModels.remove(position);
//                        currentUpdateModels.add(position, cm);
//                        notifyItemChanged(position);
                        Log.e("liked: ", String.valueOf(position));
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(context, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void CallSharePost(String postId, final View view, final int position) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<BaseResponse> callGetCategoryApi = apiService.postShare(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), postId);
        callGetCategoryApi.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    boolean object = response.body().isSuccess();
                    if (object) {
                        listener.onButtonClick(view, position);
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(context, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void CallUnlikePost(String postId, final int position) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<BaseResponse> callGetCategoryApi = apiService.postUnlike(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), postId);
        callGetCategoryApi.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.e("unliked: ", String.valueOf(position));
                CurrentUpdateModel model = currentUpdateModels.get(position);
                CurrentUpdateModel cm = model;
                PostData postData = model.getPost();
                PostData pd = postData;
                pd.setCountLikes(postData.getCountLikes() - 1);
                cm.setPost(pd);
                cm.setLiked(false);
                currentUpdateModels.remove(position);
                currentUpdateModels.add(position, cm);
                notifyItemChanged(position);
                if (response.code() == 200 && response.body() != null) {
                    boolean object = response.body().isSuccess();
                    Log.e("unliked: ", String.valueOf(position));
                    if (object) {
//                        CurrentUpdateModel model = currentUpdateModels.get(position);
//                        CurrentUpdateModel cm = model;
//                        PostData postData = model.getPost();
//                        PostData pd = postData;
//                        pd.setCountLikes(postData.getCountLikes() - 1);
//                        cm.setPost(pd);
//                        currentUpdateModels.remove(position);
//                        currentUpdateModels.add(position, cm);
//                        notifyDataSetChanged();
                        Log.e("unliked: ", String.valueOf(position));
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(context, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setAdapter() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return currentUpdateModels.size();
        //        return buyOrderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RoundedImageView imgUserProfile, imgUserProfile1, imgUserProfile2, imgUserProfile3;

        private ImageView postImageView;
        private TextView txtUserName, txtPostTime, txtDesc, txtName1, txtName2, txtLocation1, txtLocation2, txtServiceDesc, txtPoints, txtLikes, txtComments, txtShares, txtBusiness;
        private TextView txtStars;
        private LinearLayout layoutLikes, layoutComments;
        private ImageButton shareButton;
        private ImageView imgLike, imgComment;
        private Button viewallCommentsButton, sendCommentButton;
        private RelativeLayout view1, view2;
        private RecyclerView commentRecyclerView;

        private LikeButton likeButton;

        private EditText edtComment;
        private LinearLayout bottomView;
        private WeakReference<OnRecyclerViewButtonClickListener> listenerRef;

        public MyViewHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            imgUserProfile=(RoundedImageView) itemView.findViewById(R.id.img_profile);
            imgUserProfile1=(RoundedImageView) itemView.findViewById(R.id.img_profile1);
            imgUserProfile2=(RoundedImageView) itemView.findViewById(R.id.img_profile2);
            imgUserProfile3=(RoundedImageView) itemView.findViewById(R.id.img_profile3);

            txtServiceDesc = (TextView) itemView.findViewById(R.id.txt_service_desc);
            txtDesc = (TextView) itemView.findViewById(R.id.txt_desc);

            txtUserName = (TextView) itemView.findViewById(R.id.txt_user_name);
            txtName1 = (TextView) itemView.findViewById(R.id.txt_user_name1);
            txtName2 = (TextView) itemView.findViewById(R.id.txt_service_name);
            txtPostTime = (TextView) itemView.findViewById(R.id.txt_posttime_agp);

            txtLocation1 = (TextView) itemView.findViewById(R.id.txt_location1);
            txtLocation2 = (TextView) itemView.findViewById(R.id.txt_location2);
            postImageView = (ImageView) itemView.findViewById(R.id.post_image_view);

            txtPoints = (TextView) itemView.findViewById(R.id.txt_points);
            txtBusiness = (TextView) itemView.findViewById(R.id.txt_business);
            txtStars = (TextView) itemView.findViewById(R.id.txt_star);
            txtLikes = (TextView) itemView.findViewById(R.id.txt_likes);
            txtShares = (TextView) itemView.findViewById(R.id.txt_share);
            txtComments = (TextView) itemView.findViewById(R.id.txt_comments);

            shareButton = (ImageButton) itemView.findViewById(R.id.btn_share);
            layoutComments = (LinearLayout) itemView.findViewById(R.id.view_comment);
            layoutLikes = (LinearLayout) itemView.findViewById(R.id.view_like);

            imgLike = (ImageView) itemView.findViewById(R.id.img_like);
            imgComment = (ImageView) itemView.findViewById(R.id.img_comment);
            viewallCommentsButton = (Button) itemView.findViewById(R.id.btn_view_all_comment);
            sendCommentButton =  (Button) itemView.findViewById(R.id.btn_send);
            likeButton = (LikeButton) itemView.findViewById(R.id.like_button);

            edtComment = (EditText) itemView.findViewById(R.id.edt_comment);

            view1 = (RelativeLayout)itemView.findViewById(R.id.view1);
            view2 = (RelativeLayout) itemView.findViewById(R.id.view2);
            commentRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_comments);
            commentRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            bottomView = (LinearLayout) itemView.findViewById(R.id.bottomView);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onButtonClick(v, getAdapterPosition());
        }
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {

        Context obj;
        int res;
        ArrayList<CommentModel> Al;
        LayoutInflater inflater;

        Adapter(Context obj, int res, ArrayList<CommentModel> Al) {
            this.obj = obj;
            this.res = res;
            this.Al = Al;
            inflater = LayoutInflater.from(obj);
        }

        public void setData(ArrayList<CommentModel> models){
            this.Al = models;
            notifyDataSetChanged();
        }
        @Override
        public Adapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Adapter.MyHolder hold;

            View vv = LayoutInflater.from(obj).inflate(res, parent, false);
            hold = new Adapter.MyHolder(vv);
            return hold;

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(Adapter.MyHolder hold, final int position) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.photo_placeholder)
                    .showImageForEmptyUri(R.drawable.photo_placeholder)
                    .showImageOnFail(R.drawable.photo_placeholder)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
            CommentModel model = Al.get(position);
            ImageLoader.getInstance().displayImage(model.getUser().getProfilePic(), hold.profileImageView, options);
            hold.title.setText(model.getUser().getFirstName() + " " + model.getUser().getLastName());
            hold.txtComment.setText(model.getComment());

            String time = DateTimeUtils.getTimeAgo(context, model.getUpdatedAt());
            hold.timeago.setText(time);
        }

        @Override
        public int getItemCount() {
            return Al.size();

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
