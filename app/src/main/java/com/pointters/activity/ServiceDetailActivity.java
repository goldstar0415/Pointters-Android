package com.pointters.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.adapter.EditProfileImageViewPagerAdapter;
import com.pointters.adapter.PriceAdapter;
import com.pointters.adapter.RelatedServiceAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.listener.RecyclerViewItemClickWithSource;
import com.pointters.model.Media;
import com.pointters.model.MetricsModel;
import com.pointters.model.Prices;
import com.pointters.model.SellerModel;
import com.pointters.model.Service;
import com.pointters.model.ServiceDetailModel;
import com.pointters.model.ServiceReviewModel;
import com.pointters.model.request.FlagInappropriateRequest;
import com.pointters.model.response.GetRelatedServicesResponse;
import com.pointters.model.response.GetServiceDetailResponse;
import com.pointters.model.response.LikeUnlikeResponse;
import com.pointters.model.response.ShareUnshareResponse;
import com.pointters.model.response.WatchUnwatchResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
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


public class ServiceDetailActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {

    private final int CALL_PHONE_REQUEST = 3;
    private final int DELETE_SERVICE_REQUEST = 4;

    private LinearLayout moveToProfileScreen;
    private RelativeLayout layoutMap, layoutReviewDesc1, layoutReviewDesc2;
    private TextView txtName, txtVerified, buttonToggle, txtGetCustomOffer, btnPoint, btnWatch;
    private TextView txtOnTime, txtQuality, txtHour, txtOrder, txtRating, txtBuyAgain, txtDeliveryMethod, txtPoint, txtLike, txtWatch;
    private TextView txtRelatedTitle, txtReviewTitle;
    private Button btnShowMoreService, btnReviewMore, btnBuyforService;
    private GoogleMap mMap;
    private RoundedImageView imgProfile;
    private LikeButton imgLike;
    private ImageView btnCall, btnChat,imgWatching;
    private RelativeLayout  btnDirection;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MapFragment mapFragment;
    private LatLng latLng;
    private TextView txtServiceDesc;
    private RecyclerView recyclerViewPrice, recyclerViewRelatedService;
    private PriceAdapter priceAdapter;
    private RelatedServiceAdapter serviceAdapter;
    private EditProfileImageViewPagerAdapter editProfileImageViewPagerAdapter;
    private KProgressHUD loader;

    private SellerModel seller = new SellerModel();
    private Service service = new Service();
    private MetricsModel metrics = new MetricsModel();
    private List<Prices> pricesList = new ArrayList<>();
    private List<Media> mediaList = new ArrayList<>();
    private List<Double> service_pos = new ArrayList<>();
    private List<ServiceDetailModel> relatedServiceList = new ArrayList<>();
    private List<ServiceReviewModel> reviewList = new ArrayList<>();

    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;

    private int limitCnt = 10;
    private int totalCnt = 0;
    private int pageCnt = 1;

    private String serviceId = "";
    private String loginUserId = "";
    private String serviceDesc = "NA";

    private String sellerId = "";
    private String sellerName = "";
    private String sellerPic = "";
    private String sellerPhone = "";
    private int sellerVerified = 0;

    private Boolean isWatched = false;
    private Boolean isLiked = false;
    private Boolean isShared = false;
    private Boolean isExpanable = true;
    private String flagInappropriateComment = "";
    private LinearLayout btnFlagInappropriate;
    private float buyPrice = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setUpSuggestedCategoryViewPager();

        if (!sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "").equals("")) {
            mUserLat = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "0"));
        }
        if (!sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "").equals("")) {
            mUserLng = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "0"));
        }
        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null) {
            String json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.has("_id") && jsonObject.get("_id") != null && !jsonObject.get("_id").equals("")) {
                    loginUserId = (String) jsonObject.get("_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        initUI();

        if (getIntent() != null && getIntent().getStringExtra(ConstantUtils.SERVICE_ID) != null && !getIntent().getStringExtra(ConstantUtils.SERVICE_ID).isEmpty()) {
            serviceId = getIntent().getStringExtra(ConstantUtils.SERVICE_ID);
            if (!serviceId.equals("")) {
                loader.show();
                callGetServiceDetailApi(serviceId);
            } else {
                Toast.makeText(ServiceDetailActivity.this, "No Service Details!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(ServiceDetailActivity.this, "No Service Details!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initUI() {
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        txtName = (TextView) findViewById(R.id.txt_name);
        txtVerified = (TextView) findViewById(R.id.txt_verified);
        imgProfile = (RoundedImageView) findViewById(R.id.img_profile);
        imgWatching = (ImageView) findViewById(R.id.img_watching_icon);
        imgLike = (LikeButton) findViewById(R.id.like_button);
        LinearLayout llLike = (LinearLayout) findViewById(R.id.ll_like);
        LinearLayout llWatch = (LinearLayout) findViewById(R.id.ll_watch);
        llLike.setOnClickListener(this);
        llWatch.setOnClickListener(this);
        txtGetCustomOffer = (TextView) findViewById(R.id.txt_get_custom_offer);
        moveToProfileScreen = (LinearLayout) findViewById(R.id.move_to_profile);
        moveToProfileScreen.setOnClickListener(this);
        txtGetCustomOffer.setOnClickListener(this);
        buttonToggle = (TextView) this.findViewById(R.id.button_toggle);
        btnCall = (ImageView) findViewById(R.id.img_call);
        btnCall.setOnClickListener(this);
        btnChat = (ImageView) findViewById(R.id.img_chat);
        btnChat.setOnClickListener(this);

        txtOnTime = (TextView) findViewById(R.id.mOnTimeValue);
        txtQuality = (TextView) findViewById(R.id.mQualityNum);
        txtHour = (TextView) findViewById(R.id.mHour);
        txtOrder = (TextView) findViewById(R.id.txt_order_cnt);
        txtRating = (TextView) findViewById(R.id.txt_rating_cnt);
        txtBuyAgain = (TextView) findViewById(R.id.txt_again_cnt);

        txtReviewTitle = (TextView) findViewById(R.id.txt_review_title);

        btnBuyforService = (Button) findViewById(R.id.buy_service_button);
        btnBuyforService.setOnClickListener(this);
        btnBuyforService.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
        btnBuyforService.setEnabled(false);
        btnReviewMore = (Button) findViewById(R.id.btn_review_more);
        btnReviewMore.setOnClickListener(this);

        btnShowMoreService = (Button) findViewById(R.id.show_more_service_button);
        btnShowMoreService.setOnClickListener(this);

        txtPoint = (TextView) findViewById(R.id.mPointsValue);
        txtLike = (TextView) findViewById(R.id.mLikesNum);
        txtWatch = (TextView) findViewById(R.id.mWatchingValue);

        recyclerViewPrice = (RecyclerView) findViewById(R.id.recyclerview_price);
        recyclerViewPrice.setLayoutManager(new LinearLayoutManager(ServiceDetailActivity.this));
        priceAdapter = new PriceAdapter(ServiceDetailActivity.this, pricesList, new RecyclerViewItemClickWithSource() {
            @Override
            public void onItemClick(int position, Prices source, int type) {
                calculatePrice();
            }

        });
        recyclerViewPrice.setAdapter(priceAdapter);

        imgLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                callPostLikeServiceApi(serviceId);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                callDeleteLikeServiceApi(serviceId);
            }
        });
        imgWatching.setOnClickListener(this);

        txtDeliveryMethod = (TextView) findViewById(R.id.txt_delivery_method);
        btnDirection = (RelativeLayout) findViewById(R.id.direction_view);
        btnDirection.setOnClickListener(this);

        layoutMap = (RelativeLayout) findViewById(R.id.layout_map);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapId);

        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        txtServiceDesc = (TextView)findViewById(R.id.txt_service_description);

        txtRelatedTitle = (TextView) findViewById(R.id.txt_related_title);
        btnFlagInappropriate = (LinearLayout) findViewById(R.id.ll_flag_inappropriate);
        btnFlagInappropriate.setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
    }

    @SuppressLint("DefaultLocale")
    private void calculatePrice() {
        float total = 0f;
        String unit = "";
        ArrayList<Integer> priceCountList = priceAdapter.getPriceCountList();
        for (int i = 0; i < priceCountList.size(); i++) {
            Prices prices = pricesList.get(i);
            unit = prices.getCurrencySymbol();
            int count = priceCountList.get(i);
            total = prices.getPrice() * count;
        }
        if (total > 0) {
            btnBuyforService.setText(String.format("BUY FOR %s%.2f", unit, total));
            btnBuyforService.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
            btnBuyforService.setEnabled(true);
        }else{
            btnBuyforService.setText("BUY FOR");
            btnBuyforService.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
            btnBuyforService.setEnabled(false);
        }

    }

    private void initRecyclerView() {
        recyclerViewRelatedService = (RecyclerView) findViewById(R.id.recyclerview_related_service);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ServiceDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewRelatedService.setLayoutManager(linearLayoutManager);

        serviceAdapter = new RelatedServiceAdapter(ServiceDetailActivity.this, relatedServiceList, mUserLat, mUserLng, new OnRecyclerViewButtonClickListener() {
            @Override
            public void onButtonClick(View v, int position) {
                if (v.getId() == R.id.txt_name) {
                    if (relatedServiceList.get(position).getSeller().getUserId() != null && !relatedServiceList.get(position).getSeller().getUserId().isEmpty()) {
                        String userId = relatedServiceList.get(position).getSeller().getUserId();
                        moveToProfile(userId);
                    }
                }else{
                    Intent intentService = new Intent(ServiceDetailActivity.this, ServiceDetailActivity.class);
                    intentService.putExtra(ConstantUtils.SERVICE_ID, relatedServiceList.get(position).getService().getId());
                    startActivity(intentService);
                }
            }

        });
        recyclerViewRelatedService.setAdapter(serviceAdapter);
        recyclerViewRelatedService.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
            }
        });
    }

    private void setServiceDetails() {
        if (mediaList.size() > 0) {
            editProfileImageViewPagerAdapter.notifyDataSetChanged();
        }

        if (seller.getUserId() != null && !seller.getUserId().isEmpty()) {
            sellerId = seller.getUserId();
            Log.e("seller id" , sellerId);
            Log.e("loginuser id" , loginUserId);
            if (sellerId.equals(loginUserId)) {
                btnBuyforService.setVisibility(View.GONE);
            }else{
                btnBuyforService.setVisibility(View.VISIBLE);
            }
        }

        String strFirst = "", strLast = "";
        if (seller.getFirstName() != null && !seller.getFirstName().isEmpty()) {
            strFirst = seller.getFirstName();
        }
        if (seller.getLastName() != null && !seller.getLastName().isEmpty()) {
            strLast = seller.getLastName();
        }
        sellerName = strFirst + " " + strLast;
        txtName.setText(sellerName);

        if (seller.getVerified() != null && seller.getVerified()) {
            sellerVerified = 1;
            txtVerified.setText("Verified");
        } else {
            sellerVerified = 0;
            txtVerified.setText("Not Verified");
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        if (seller.getProfilePic() != null && !seller.getProfilePic().isEmpty()) {
            sellerPic = seller.getProfilePic();
            if (!sellerPic.contains("https://s3.amazonaws.com")) {
//                sellerPic = "https://s3.amazonaws.com" + sellerPic;
            }
            ImageLoader.getInstance().displayImage(sellerPic, imgProfile, options);
        }

        if (seller.getPhone() != null && !seller.getPhone().isEmpty() && !sellerId.equals(loginUserId)) {
            sellerPhone = seller.getPhone();
            btnCall.setVisibility(View.VISIBLE);
        } else {
            btnCall.setVisibility(View.GONE);
        }

        if (sellerId.equals(loginUserId)) {
            btnChat.setVisibility(View.GONE);
            btnCall.setVisibility(View.GONE);
        } else {
            btnChat.setVisibility(View.VISIBLE);
        }

        txtServiceDesc.setText(serviceDesc);

        if (pricesList.size() > 0) {
            priceAdapter.notifyDataSetChanged();
        }

        if (metrics.getAvgOnTime() != null && !metrics.getAvgOnTime().isEmpty() && !metrics.getAvgOnTime().equals("NA")) {
            float valOnTime = Float.parseFloat(metrics.getAvgOnTime());
            txtOnTime.setText(String.format("%.1f", valOnTime) + "%");
        } else {
            txtOnTime.setText("NA");
        }

        if (metrics.getAvgQuality() != null && !metrics.getAvgQuality().isEmpty() && !metrics.getAvgQuality().equals("NA")) {
            float valQuality = Float.parseFloat(metrics.getAvgQuality());
            txtQuality.setText(String.format("%.1f", valQuality) + "/5");
        } else {
            txtQuality.setText("NA");
        }

        if (metrics.getAvgResponseTime() != null && !metrics.getAvgResponseTime().isEmpty() && !metrics.getAvgResponseTime().equals("NA")) {
            float valResponse = Float.parseFloat(metrics.getAvgResponseTime());
            txtHour.setText(String.format("%.1f", valResponse) + "hr");
        } else {
            txtHour.setText("NA");
        }

        if (metrics.getNumOrdersCompleted() != null && !metrics.getNumOrdersCompleted().isEmpty() && !metrics.getNumOrdersCompleted().equals("NA")) {
            txtOrder.setText(metrics.getNumOrdersCompleted() + " service order completed");
        } else {
            txtOrder.setText("0 service order completed");
        }

        if (metrics.getAvgRating() != null && !metrics.getAvgRating().isEmpty() && !metrics.getAvgRating().equals("NA")) {
            txtRating.setText(metrics.getAvgRating() + "% avg rating");
        } else {
            txtRating.setText("0% avg rating");
        }

        if (metrics.getAvgWillingToBuyAgain() != null && !metrics.getAvgWillingToBuyAgain().isEmpty() && !metrics.getAvgWillingToBuyAgain().equals("NA")) {
            txtBuyAgain.setText(metrics.getAvgWillingToBuyAgain() + "% of customers will buy this service again");
        } else {
            txtBuyAgain.setText("0% of customers will buy this service again");
        }

        if (metrics.getPointValue() != null && !metrics.getPointValue().isEmpty() && !metrics.getPointValue().equals("NA")) {
            txtPoint.setText(metrics.getPointValue() + " Points");
        } else {
            txtPoint.setText("NA");
        }

        if (metrics.getNumLikes() != null && !metrics.getNumLikes().isEmpty() && !metrics.getNumLikes().equals("NA")) {
            txtLike.setText(metrics.getNumLikes() + " Likes");
        } else {
            txtLike.setText("NA");
        }

        if (metrics.getNumWatching() != null && !metrics.getNumWatching().isEmpty() && !metrics.getNumWatching().equals("NA")) {
            txtWatch.setText(metrics.getNumWatching() + " Watching");
        } else {
            txtWatch.setText("NA");
        }


        boolean isLocal = false;
        Integer localRadius = 0;
        if (service.getFulfillmentMethod() != null) {
            boolean isOnline = service.getFulfillmentMethod().getOnline();
            boolean isShipment = service.getFulfillmentMethod().getShipment();
            boolean isStore = service.getFulfillmentMethod().getStore();

            isLocal = service.getFulfillmentMethod().getLocal();
            localRadius = service.getFulfillmentMethod().getLocalServiceRadius();

            String locationRadiusUnit = "mile";
            if (service.getFulfillmentMethod().getLocalServiceRadiusUom() != null && !service.getFulfillmentMethod().getLocalServiceRadiusUom().isEmpty()) {
                locationRadiusUnit = service.getFulfillmentMethod().getLocalServiceRadiusUom();
            }

            if (isOnline) {
                txtDeliveryMethod.setText("Online service");
            }
            if (isShipment) {
                txtDeliveryMethod.setText("Shipment service");
            }
            if (isLocal) {
                txtDeliveryMethod.setText("Servicing locally in the city you service");
            }
            if (isStore) {
                txtDeliveryMethod.setText("Servicing locally at your store locations");
            }

            if (isLocal || isStore) {
                btnDirection.setVisibility(View.VISIBLE);
                layoutMap.setVisibility(View.VISIBLE);

                service_pos.clear();
                if (service.getLocation() != null && service.getLocation().getGeoJson() != null) {
                    if (service.getLocation().getGeoJson().getCoordinates() != null && service.getLocation().getGeoJson().getCoordinates().size() > 0) {
                        service_pos = service.getLocation().getGeoJson().getCoordinates();
                    } else {
                        service_pos.add(-73.856077);
                        service_pos.add(40.848447);
                    }
                } else {
                    service_pos.add(-73.856077);
                    service_pos.add(40.848447);
                }

                latLng = new LatLng(service_pos.get(1), service_pos.get(0));
                if (latLng != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_big)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                        }
                    });
                }
            } else {
                btnDirection.setVisibility(View.GONE);
                layoutMap.setVisibility(View.GONE);
            }
        }

        setServiceReviews();
        initRecyclerView();
    }

    private void setServiceReviews() {
        if (reviewList.size() == 0) {
            txtReviewTitle.setText("No Review");
            btnReviewMore.setVisibility(View.GONE);
//            layoutReviewDesc1.setVisibility(View.GONE);
//            layoutReviewValue1.setVisibility(View.GONE);
//            layoutReviewDesc2.setVisibility(View.GONE);
//            layoutReviewValue2.setVisibility(View.GONE);
//            btnReviewMore.setVisibility(View.GONE);
        } else {
            txtReviewTitle.setText("Review");
            btnReviewMore.setVisibility(View.VISIBLE);

//            layoutReviewDesc1.setVisibility(View.VISIBLE);
//            layoutReviewValue1.setVisibility(View.VISIBLE);
//            btnReviewMore.setVisibility(View.VISIBLE);

//            if (reviewList.get(0).getComment() != null && !reviewList.get(0).getComment().isEmpty()) {
//                txtReviewDesc1.setText(reviewList.get(0).getComment());
//            } else {
//                txtReviewDesc1.setText("NA");
//            }
//
//            if (reviewList.get(0).getOverallRating() != null && reviewList.get(0).getOverallRating() >= 0) {
//                txtReviewRate1.setText(String.format("%.1f", reviewList.get(0).getOverallRating()) + "%");
//            } else {
//                txtReviewRate1.setText("NA");
//            }
//
//            if (reviewList.get(0).getQualityOfService() != null && reviewList.get(0).getQualityOfService() >= 0) {
//                txtReviewQuality1.setText(String.format("%.1f", reviewList.get(0).getQualityOfService()));
//            } else {
//                txtReviewQuality1.setText("NA'");
//            }
//
//            if (reviewList.get(0).getWillingToBuyServiceAgain() != null && reviewList.get(0).getWillingToBuyServiceAgain() > 0) {
//                imgReviewAgain1.setImageResource(R.drawable.ellipse_checked_blue);
//            } else {
//                imgReviewAgain1.setImageResource(R.drawable.ellipse_unchecked_grey);
//            }
//
//            if (reviewList.size() > 1) {
//                btnReviewMore.setEnabled(true);
//                btnReviewMore.setSelected(true);
//                layoutReviewDesc2.setVisibility(View.VISIBLE);
//                layoutReviewValue2.setVisibility(View.VISIBLE);
//
//                if (reviewList.get(1).getComment() != null && !reviewList.get(1).getComment().isEmpty()) {
//                    txtReviewDesc2.setText(reviewList.get(1).getComment());
//                } else {
//                    txtReviewDesc2.setText("NA");
//                }
//
//                if (reviewList.get(1).getOverallRating() != null && reviewList.get(1).getOverallRating() >= 0) {
//                    txtReviewRate2.setText(String.format("%.1f", reviewList.get(1).getOverallRating()) + "%");
//                } else {
//                    txtReviewRate2.setText("NA");
//                }
//
//                if (reviewList.get(1).getQualityOfService() != null && reviewList.get(1).getQualityOfService() >= 0) {
//                    txtReviewQuality2.setText(String.format("%.1f", reviewList.get(1).getQualityOfService()));
//                } else {
//                    txtReviewQuality2.setText("NA'");
//                }
//
//                if (reviewList.get(1).getWillingToBuyServiceAgain() != null && reviewList.get(1).getWillingToBuyServiceAgain() > 0) {
//                    imgReviewAgain2.setImageResource(R.drawable.ellipse_checked_blue);
//                } else {
//                    imgReviewAgain2.setImageResource(R.drawable.ellipse_unchecked_grey);
//                }
//            } else {
//                btnReviewMore.setEnabled(false);
//                btnReviewMore.setSelected(false);
//                layoutReviewDesc2.setVisibility(View.GONE);
//                layoutReviewValue2.setVisibility(View.GONE);
//            }
        }
    }

    private void setLikeStatus() {
        if (isLiked) {
            imgLike.setLiked(true);
        } else {
            imgLike.setLiked(false);
        }
    }

    private void setWatchStatus() {
        if (isWatched) {
            imgWatching.setImageResource(R.drawable.icon_setting_watch);
        } else {
            imgWatching.setImageResource(R.drawable.icon_watching_white);
        }
    }

    private void setShareStatus() {
        if (isShared) {
            btnPoint.setText("Sharing");
            btnPoint.setTextColor(getResources().getColor(R.color.colorWhite));
            btnPoint.setBackgroundColor(getResources().getColor(R.color.blue));
        } else {
            btnPoint.setText("Share");
            btnPoint.setTextColor(getResources().getColor(R.color.color_black_info));
            btnPoint.setBackgroundColor(getResources().getColor(R.color.btn_unset_color));
        }
    }

    private void moveToProfile(String strId) {
        Intent intent = new Intent(ServiceDetailActivity.this, ProfileScreenActivity.class);

        if (strId == loginUserId) {
            intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, true);
        } else{
            intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, false);
            intent.putExtra(ConstantUtils.PROFILE_USERID, strId);
        }

        startActivity(intent);
    }

    private void moveToCall() {
        if (!checkPhoneCallPermission()) {
            return;
        }

        if (sellerPhone.contains("+")) {
            sellerPhone = sellerPhone.substring(1);
        }

        if (!sellerPhone.equals("")) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + sellerPhone));
            startActivity(callIntent);
        }
    }

    private void moveToChat() {
        Intent intent = new Intent(ServiceDetailActivity.this, ChatActivity.class);
        editor.putInt(ConstantUtils.USER_VERIFIED, sellerVerified).apply();
        editor.putString(ConstantUtils.CHAT_USER_ID, sellerId).apply();
        editor.putString(ConstantUtils.CHAT_USER_NAME, sellerName).apply();
        editor.putString(ConstantUtils.CHAT_USER_PIC, sellerPic).apply();
        editor.putString(ConstantUtils.CHAT_CONVERSATION_ID, "");
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setUpSuggestedCategoryViewPager() {
        ViewPager viewPagerSuggestedServices = (ViewPager) findViewById(R.id.view_pager_suggested_services);
        editProfileImageViewPagerAdapter = new EditProfileImageViewPagerAdapter(ServiceDetailActivity.this, mediaList);
        editProfileImageViewPagerAdapter.setListener1(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Media media = mediaList.get(position);
                Intent intent = new Intent(ServiceDetailActivity.this, PhotoViewerActivity.class);
                intent.putExtra(ConstantUtils.CHAT_TAP_PHOTO, media.getFileName());
                startActivity(intent);
            }
        });
        viewPagerSuggestedServices.setAdapter(editProfileImageViewPagerAdapter);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator_view_pager_suggested_services);
        circlePageIndicator.setViewPager(viewPagerSuggestedServices);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btn_share:
                String shareBody = "Awesome app! Please check in your side Pointters";
                Intent shareintent = new Intent(Intent.ACTION_SEND);
                shareintent.setType("text/plain");
                shareintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareintent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
                shareintent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareintent, "Share Pointters"));
                break;
            case R.id.move_to_profile:
                moveToProfile(sellerId);
                break;

            case R.id.ll_like:
                break;
            case R.id.ll_watch:
                break;
            case R.id.show_more_service_button:
                if (totalCnt > limitCnt * pageCnt) {
                    callGetRelatedServiceApi(serviceId, false, pageCnt+1, limitCnt, true);
                }

                break;
            case R.id.txt_get_custom_offer:
                Intent chatintent = new Intent(ServiceDetailActivity.this, ChatActivity.class);
//                editor.putInt(ConstantUtils.USER_VERIFIED, seller.getVerified() ? 1 : 0).apply();
                editor.putInt(ConstantUtils.USER_VERIFIED, 0).apply();
                editor.putString(ConstantUtils.CHAT_USER_ID, seller.getUserId()).apply();
                editor.putString(ConstantUtils.CHAT_USER_NAME, seller.getFirstName() + " " + seller.getLastName()).apply();
                editor.putString(ConstantUtils.CHAT_USER_PIC, seller.getProfilePic()).apply();
                editor.putString(ConstantUtils.CHAT_CONVERSATION_ID, "");
                startActivity(chatintent);
                break;
            case R.id.buy_service_button:
                Intent intent1 = new Intent(ServiceDetailActivity.this, CheckoutActivity.class);
                intent1.putExtra(ConstantUtils.SERVICE_ID, serviceId);
                startActivity(intent1);
                break;
//            case R.id.txt_point:
//                if (isShared) {
//                    callDeleteShareServiceApi(serviceId);
//                } else {
//                    callPostShareServiceApi(serviceId);
//                }
//                break;
//            case R.id.txt_watch:
//                if (isWatched) {
//                    callDeleteWatchServiceApi(serviceId);
//                } else {
//                    callPostWatchServiceApi(serviceId);
//                }
//                break;
//            case R.id.layout_like:
//                if (isLiked) {
//                    callDeleteLikeServiceApi(serviceId);
//                } else {
//                    callPostLikeServiceApi(serviceId);
//                }
//                break;

            case R.id.ll_flag_inappropriate:
                showDialogFlag();
                break;
            case R.id.img_call:
                moveToCall();
                break;
            case R.id.img_chat:
                moveToChat();
                break;
            case R.id.direction_view:
                String directionUrl = "http://maps.apple.com/?saddr=" + String.valueOf(mUserLat) + "," + String.valueOf(mUserLng) + "&daddr=" + String.valueOf(service_pos.get(1)) + "," + String.valueOf(service_pos.get(0));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(directionUrl));
                startActivity(browserIntent);
                break;
            case R.id.btn_review_more:
                Intent reviewIntent = new Intent(ServiceDetailActivity.this, ServiceReviewActivity.class);
                startActivity(reviewIntent);
                break;
//            case R.id.btn_share:
//                Intent intents = new Intent(ServiceDetailActivity.this, EditServiceDetailActivity.class);
//                intents.putExtra(ConstantUtils.EDIT_SERVICE_ID, serviceId);
//                startActivityForResult(intents, DELETE_SERVICE_REQUEST);
//                txtMenuEdit.setVisibility(View.GONE);
//                break;
            default:
                break;
        }
    }

    public void showDialogFlag(){
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_flag, null, false);
        final EditText editText = (EditText) layout.findViewById(R.id.edt_flag);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Flag Inappropriate")
                .setMessage("Tell us why you think this service is inappropriate")
                .setView(layout)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flagInappropriateComment = editText.getText().toString();
                        postInappropriate(flagInappropriateComment);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void postInappropriate(String comment){
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> watchServiceCall = apiService.postInappropriate(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), serviceId, new FlagInappropriateRequest(comment));
        watchServiceCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    AndroidUtils.showToast(ServiceDetailActivity.this, "Flaged Successfully");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callPostFlagInappropriateApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DELETE_SERVICE_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data.getSerializableExtra(ConstantUtils.EDIT_SERVICE_DELETE).equals("yes")) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    loader.show();
                    callGetServiceDetailApi(serviceId);
                }
            }
        }
    }

    // Get Service Detail Info
    private void callGetServiceDetailApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetServiceDetailResponse> getServiceDetailCall = apiService.getServiceDetail(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        getServiceDetailCall.enqueue(new Callback<GetServiceDetailResponse>() {
            @Override
            public void onResponse(Call<GetServiceDetailResponse> call, Response<GetServiceDetailResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getResult().getSeller() != null) {
                        seller = response.body().getResult().getSeller();
                        if (seller.getUserId().equals(loginUserId)) {
                            btnBuyforService.setVisibility(View.GONE);
                        }else{
                            btnBuyforService.setVisibility(View.VISIBLE);
                        }
                    }
                    if (response.body().getResult().getService() != null) {
                        service = response.body().getResult().getService();

                        if (response.body().getResult().getService().getDescription() != null && !response.body().getResult().getService().getDescription().isEmpty()) {
                            serviceDesc = response.body().getResult().getService().getDescription();
                        }
                        if (response.body().getResult().getService().getMedia() != null && response.body().getResult().getService().getMedia().size() > 0) {
                            mediaList.clear();
                            mediaList.addAll(response.body().getResult().getService().getMedia());
                        }
                        if (response.body().getResult().getService().getPrices() != null && response.body().getResult().getService().getPrices().size() > 0) {
                            pricesList.clear();
                            pricesList.addAll(response.body().getResult().getService().getPrices());
                        }
                    }
                    if (response.body().getResult().getServiceMetrics() != null) {
                        metrics = response.body().getResult().getServiceMetrics();
                    }
                    if (response.body().getResult().getReviews() != null && response.body().getResult().getReviews().size() > 0) {
                        reviewList.clear();
                        reviewList.addAll(response.body().getResult().getReviews());
                    }
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callGetServiceDetailApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }

                setServiceDetails();
                callGetServiceWatchApi(serviceId);
            }

            @Override
            public void onFailure(Call<GetServiceDetailResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(ServiceDetailActivity.this, "No Service Details!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Get related services
    private void callGetRelatedServiceApi(String id, final Boolean inited, int page, int limit, final boolean ismore) {
        if (inited) {
            relatedServiceList.clear();
        }

        Map<String, Integer> params = new HashMap<>();
        params.put("page", page);
        params.put("limit", limit);

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetRelatedServicesResponse> getRelatedServicesResponseCall = apiService.getRelatedService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id, params);
        getRelatedServicesResponseCall.enqueue(new Callback<GetRelatedServicesResponse>() {
            @Override
            public void onResponse(Call<GetRelatedServicesResponse> call, Response<GetRelatedServicesResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200) {
                    limitCnt = response.body().getLimit();
                    totalCnt = response.body().getTotal();
                    pageCnt = response.body().getPage();

                    if (response.body().getDocs() != null) {
                        relatedServiceList.addAll(response.body().getDocs());
                        serviceAdapter.notifyItemRangeInserted(serviceAdapter.getItemCount(), relatedServiceList.size()-1);
                        serviceAdapter.notifyDataSetChanged();
                    }
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callGetRelatedServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }

                if (!ismore) {
                    if (inited && relatedServiceList.size() == 0) {
                        txtRelatedTitle.setText("No Related Service");
                        btnShowMoreService.setVisibility(View.GONE);
                    } else {
                        txtRelatedTitle.setText("Related Service");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetRelatedServicesResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                if (!ismore) {
                    Toast.makeText(ServiceDetailActivity.this, "Can't find the related services", Toast.LENGTH_SHORT);
                    txtRelatedTitle.setText("No Related Service");
                    btnShowMoreService.setVisibility(View.GONE);
                }
            }
        });
    }

    // Get watch status
    private void callGetServiceWatchApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<WatchUnwatchResponse> watchStatus = apiService.getWatchService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        watchStatus.enqueue(new Callback<WatchUnwatchResponse>() {
            @Override
            public void onResponse(Call<WatchUnwatchResponse> call, Response<WatchUnwatchResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getWatched() != null && response.body().getWatched()) {
                        isWatched = true;
                    } else {
                        isWatched = false;
                    }
                    setWatchStatus();
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callGetServiceWatchApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
                callGetServiceLikeApi(serviceId);
            }

            @Override
            public void onFailure(Call<WatchUnwatchResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(ServiceDetailActivity.this, "Can't find the service watching status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get like status
    private void callGetServiceLikeApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<LikeUnlikeResponse> likeStatus = apiService.getLikeService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        likeStatus.enqueue(new Callback<LikeUnlikeResponse>() {
            @Override
            public void onResponse(Call<LikeUnlikeResponse> call, Response<LikeUnlikeResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getLiked() != null && response.body().getLiked()) {
                        isLiked = true;
                    } else {
                        isLiked = false;
                    }
                    setLikeStatus();
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callGetServiceLikeApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
                callGetServiceShareApi(serviceId);
            }

            @Override
            public void onFailure(Call<LikeUnlikeResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(ServiceDetailActivity.this, "Can't find the service liking status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get share status
    private void callGetServiceShareApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<ShareUnshareResponse> shareStatus = apiService.getShareService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        shareStatus.enqueue(new Callback<ShareUnshareResponse>() {
            @Override
            public void onResponse(Call<ShareUnshareResponse> call, Response<ShareUnshareResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getShared() != null && response.body().getShared()) {
                        isShared = true;
                    } else {
                        isShared = false;
                    }
                    setShareStatus();
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callGetServiceShareApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
                callGetRelatedServiceApi(serviceId, true, pageCnt, limitCnt, false);
            }

            @Override
            public void onFailure(Call<ShareUnshareResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(ServiceDetailActivity.this, "Can't find the service sharing status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Post watch status
    private void callPostWatchServiceApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> watchServiceCall = apiService.postWatchService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        watchServiceCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    isWatched = true;
                    setWatchStatus();
                    AndroidUtils.showToast(ServiceDetailActivity.this, "Watched Successfully");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callPostWatchServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");
            }
        });
    }

    // Post like status
    private void callPostLikeServiceApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> likeServiceCall = apiService.postLikeService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        likeServiceCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    isLiked = true;
                    setLikeStatus();
                    AndroidUtils.showToast(ServiceDetailActivity.this, "Liked Successfully");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callPostLikeServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");
            }
        });
    }

    // Post share status
    private void callPostShareServiceApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> shareServiceCall = apiService.postShareService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        shareServiceCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    isShared = true;
                    setShareStatus();
                    AndroidUtils.showToast(ServiceDetailActivity.this, "Shared Successfully");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callPostShareServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");
            }
        });
    }

    // Delete watch status
    private void callDeleteWatchServiceApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> unWatchServiceCall = apiService.delWatchService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        unWatchServiceCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    isWatched = false;
                    setWatchStatus();
                    AndroidUtils.showToast(ServiceDetailActivity.this, "Unwatched Successfully");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callDeleteWatchServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");
            }
        });
    }

    // Delete like status
    private void callDeleteLikeServiceApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> unLikeServiceCall = apiService.delLikeService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        unLikeServiceCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    isLiked = false;
                    setLikeStatus();
                    AndroidUtils.showToast(ServiceDetailActivity.this, "Unliked Successfully");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callDeleteLikeServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");
            }
        });
    }

    // Delete share status
    private void callDeleteShareServiceApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> unShareServiceCall = apiService.delShareService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        unShareServiceCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    isShared = false;
                    setShareStatus();
                    AndroidUtils.showToast(ServiceDetailActivity.this, "Unshared Successfully");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callDeleteShareServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");
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
        if (apiSource.equals("callGetServiceDetailApi")) {
            callGetServiceDetailApi(serviceId);
        } else if (apiSource.equals("callGetRelatedServiceApi")) {
            callGetRelatedServiceApi(serviceId, true, pageCnt, limitCnt, false);
        } else if (apiSource.equals("callGetWatchServiceApi")) {
            callGetServiceWatchApi(serviceId);
        } else if (apiSource.equals("callGetLikeServiceApi")) {
            callGetServiceLikeApi(serviceId);
        } else if (apiSource.equals("callGetShareServiceApi")) {
            callGetServiceShareApi(serviceId);
        } else if (apiSource.equals("callPostWatchServiceApi")) {
            callPostWatchServiceApi(serviceId);
        } else if (apiSource.equals("callPostLikeServiceApi")) {
            callPostLikeServiceApi(serviceId);
        } else if (apiSource.equals("callPostShareServiceApi")) {
            callPostShareServiceApi(serviceId);
        } else if (apiSource.equals("callDeleteWatchServiceApi")) {
            callDeleteWatchServiceApi(serviceId);
        } else if (apiSource.equals("callDeleteLikeServiceApi")) {
            callDeleteLikeServiceApi(serviceId);
        } else if (apiSource.equals("callDeleteShareServiceApi")) {
            callDeleteShareServiceApi(serviceId);
        } else if (apiSource.equals("callPostFlagInappropriateApi")) {
            postInappropriate(flagInappropriateComment);
        }
    }
}