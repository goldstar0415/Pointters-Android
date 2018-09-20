package com.pointters.activity;

import android.app.backup.FullBackupDataOutput;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.model.CategoryModel;
import com.pointters.model.FulfillmentDetails;
import com.pointters.model.FulfillmentMethod;
import com.pointters.model.LocationModel;
import com.pointters.model.OrderItem;
import com.pointters.model.OrderModel;
import com.pointters.model.OrdersDetailModel;
import com.pointters.model.response.GetCategoryResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class FulfillmentActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_CANCEL_ORDER = 121;
    private final int REQUEST_REVIEW_ORDER = 122;
    private final int REQUEST_ORDER_PROCESSING = 123;

    private final Integer android_image[] = {R.drawable.thumb_small_one, R.drawable.thumb_small_two, R.drawable.thumb_small_one, R.drawable.thumb_small_two,
            R.drawable.thumb_small_one, R.drawable.thumb_small_two, R.drawable.thumb_small_one};
    RecyclerView recyclerView;
    private GoogleMap mMap;
    private ImageView stepView1, stepView2, stepView3, stepView4, stepView5;
    private RelativeLayout orderAccept;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private KProgressHUD loader;

    private OrdersDetailModel ordersDetailModel;

    private String viewType = "";
    private String orderId = "";
    private Button  cancelOrderButton, reviewOrderButton;
    private TextView txtStartData, txtOrderId, txtTotalFiles, txtUserName, txtUserDescription, txtTotalBudget, txtPaidDate, txtWorkTime, txtDeliberyMethod;
    private RoundedImageView userImage;
    private ImageView sellerAcceptScheduleTime;
    private RecyclerView serviceOrderd;
    private EditText edtStartScheduleDate, edtStartScheduleTime;

    private ImageView toolbar_right_img;

    private Button btn_status_description;
    private TextView txt_stated_date, txt_order_id, mDeliveredFile, txt_name, txt_view_verified, txt_user_descriptiom, txt_one_hour_service, txt_service_fee, txt_taxes, txt_shipping_fee, txt_total_budget, txt_paid_date, txt_work_time, txt_service_location, txt_shipment_date, txt_carrier, txt_tracking_number, txt_expectedArrival, txt_price, txt_starts_date;
    private RelativeLayout rl_review, add_file_view, direction_view, cancel_order_view;
    private RoundedImageView img_profile;
    private ImageView btn_call, btn_chat, check_seller_accepted_schedule_time;
    private EditText edt_date, edt_time;
    private LinearLayout ll_date, ll_time, ll_delivery_method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulfillment);
        loader = KProgressHUD.create(FulfillmentActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        AppUtils.setToolBarWithBothIcon(FulfillmentActivity.this, getResources().getString(R.string.fulfillment), R.drawable.back_icon, R.drawable.more_icon_horizontal);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Intent intent = getIntent();
        if (intent.getStringExtra(ConstantUtils.SELECT_ORDER_ID) != null) {
            orderId = intent.getStringExtra(ConstantUtils.SELECT_ORDER_ID);
            viewType = intent.getStringExtra(ConstantUtils.SELECT_ORDER_TYPE);
        }else{
            finish();
        }

        toolbar_right_img = (ImageView) findViewById(R.id.toolbar_right_img);
        toolbar_right_img.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.mDeliveredFilesRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        orderAccept = (RelativeLayout) findViewById(R.id.view_order_accept);
        orderAccept.setOnClickListener(this);
        cancel_order_view = (RelativeLayout) findViewById(R.id.cancel_order_view);
        cancel_order_view.setOnClickListener(this);
        rl_review = (RelativeLayout) findViewById(R.id.rl_review);
        rl_review.setOnClickListener(this);
        add_file_view = (RelativeLayout) findViewById(R.id.add_file_view);
        add_file_view.setOnClickListener(this);
        direction_view = (RelativeLayout) findViewById(R.id.direction_view);
        direction_view.setOnClickListener(this);

        btn_status_description = (Button) findViewById(R.id.btn_status_description);

        txt_stated_date = (TextView) findViewById(R.id.txt_stated_date);
        txt_order_id = (TextView) findViewById(R.id.txt_order_id);
        mDeliveredFile = (TextView) findViewById(R.id.mDeliveredFile);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_view_verified = (TextView) findViewById(R.id.txt_view_verified);
        txt_user_descriptiom = (TextView) findViewById(R.id.txt_user_descriptiom);
        txt_one_hour_service = (TextView) findViewById(R.id.txt_one_hour_service);
        txt_price = (TextView) findViewById(R.id.txt_price);
        txt_service_fee = (TextView) findViewById(R.id.txt_service_fee);
        txt_taxes = (TextView) findViewById(R.id.txt_taxes);
        txt_shipping_fee = (TextView) findViewById(R.id.txt_shipping_fee);
        txt_total_budget = (TextView) findViewById(R.id.txt_total_budget);
        txt_paid_date = (TextView) findViewById(R.id.txt_paid_date);
        txt_work_time = (TextView) findViewById(R.id.txt_work_time);
        txt_service_location = (TextView) findViewById(R.id.txt_service_location);
        txt_shipment_date = (TextView) findViewById(R.id.txt_shipment_date);
        txt_carrier = (TextView) findViewById(R.id.txt_carrier);
        txt_tracking_number = (TextView) findViewById(R.id.txt_tracking_number);
        txt_expectedArrival = (TextView) findViewById(R.id.txt_expectedArrival);
        txt_starts_date = (TextView) findViewById(R.id.txt_starts_date);

        img_profile = (RoundedImageView) findViewById(R.id.img_profile);

        btn_call = (ImageView) findViewById(R.id.btn_call);
        btn_chat = (ImageView) findViewById(R.id.btn_chat);
        check_seller_accepted_schedule_time = (ImageView) findViewById(R.id.check_seller_accepted_schedule_time);

        edt_date = (EditText) findViewById(R.id.edt_date);
        edt_time = (EditText) findViewById(R.id.edt_time);

        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        ll_delivery_method = (LinearLayout) findViewById(R.id.ll_delivery_method);

        ArrayList fulfillmentList = prepareData();
        ImageAdapter adapter = new ImageAdapter(FulfillmentActivity.this, fulfillmentList);
        recyclerView.setAdapter(adapter);

        CallGetOrderDetail(orderId);
    }

    public void CallGetOrderDetail(String orderid) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<OrderModel> callGetCategoryApi = apiService.getOrderDetail(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), orderid);
        callGetCategoryApi.enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                loader.dismiss();
                if (response.code() == 200 && response.body() != null) {
                    ordersDetailModel = response.body().getOrder();
                    updateUI();
                }
            }

            @Override
            public void onFailure(Call<OrderModel> call, Throwable t) {
                loader.dismiss();
            }
        });
    }

    public void updateUI(){

        DisplayImageOptions options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        String[] startedDate = ordersDetailModel.getCreatedAt().split("T");
        String[] arr = startedDate[0].split("-");

        txt_stated_date.setText(String.format("Started On %s/%s", arr[1], arr[2]));
        txt_order_id.setText(String.format("Order #%s", orderId));

        ImageLoader.getInstance().displayImage(ordersDetailModel.getContact().getProfilePic(), img_profile, options1);
        txt_name.setText(String.format("%s %s", ordersDetailModel.getContact().getFirstName(), ordersDetailModel.getContact().getLastName()));

        String verifed = (ordersDetailModel.getContact().isVerified()) ? "Verified" : "No verified";
        txt_view_verified.setText(verifed);
        txt_user_descriptiom.setText(ordersDetailModel.getDescription());

        txt_one_hour_service.setText(ordersDetailModel.getOrderItems().get(0).getDescription());
        txt_price.setText(String.format("%d x %s%d", ordersDetailModel.getOrderItems().get(0).getQuantity(), ordersDetailModel.getCurrencySymbol(), ordersDetailModel.getOrderItems().get(0).getPrice()));
        txt_service_fee.setText(String.format("%s%.02f", ordersDetailModel.getCurrencySymbol(), ordersDetailModel.getServicesPrices().get(0).getPrice()));
        txt_taxes.setText(String.format("%s%.02f", ordersDetailModel.getCurrencySymbol(), ordersDetailModel.getTaxAmount()));
        txt_shipping_fee.setText(String.format("%s%.02f", ordersDetailModel.getCurrencySymbol(), ordersDetailModel.getShippingFee()));
        txt_total_budget.setText(String.format("%s%.02f", ordersDetailModel.getCurrencySymbol(), ordersDetailModel.getSubtotalAmount()));
        txt_paid_date.setText("Paid on:" + DateTimeUtils.getTimeAgo(this, DateTimeUtils.formatDate(ordersDetailModel.getPaymentDate()), DateTimeStyle.MEDIUM));

        if (viewType.equals("seller")) {
            txt_starts_date.setText("Starts");
        } else {
            txt_starts_date.setText("Starts (click to change)");

            ll_date.setOnClickListener(this);
            ll_time.setOnClickListener(this);
        }
//        edt_date.setText(DateTimeUtils.getTimeAgo(this, DateTimeUtils.formatDate(ordersDetailModel.getServiceScheduleDate()), DateTimeStyle.MEDIUM));

        if (ordersDetailModel.getFulfillmentMethod().getOnline()) {
            ll_delivery_method.setVisibility(View.GONE);
        } else {
            ll_delivery_method.setVisibility(View.VISIBLE);

//            if (ordersDetailModel.getFulfillmentMethod().getLocal() || ordersDetailModel.getFulfillmentMethod().getShipment()) {
//
//            } else if(ordersDetailModel.getFulfillmentMethod().getStore()) {
//
//            }

            double lat, lng;
            LocationModel location = ordersDetailModel.getSellerServiceLocation().get(0);
            lat = location.getGeoJson().getCoordinates().get(0);
            lng = location.getGeoJson().getCoordinates().get(1);

            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapId);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    // Add a marker in Agicent and move the camera
                    LatLng TutorialsPoint = new LatLng(lat, lng);

                    mMap.addMarker(new MarkerOptions().position(TutorialsPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_big)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12.0f));

                    // mMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
                }
            });

            txt_service_location.setText(String.format("%s %s, %s", location.getPostalCode(), location.getCity(), location.getState()));
        }
    }

    private ArrayList prepareData() {

        ArrayList arrayList_fulfillment = new ArrayList<>();
        for (int i = 0; i < android_image.length; i++) {
            FulfillmentDetails fulfillmentDetails = new FulfillmentDetails();
            fulfillmentDetails.setFulfillment_img(android_image[i]);
            arrayList_fulfillment.add(fulfillmentDetails);
        }
        return arrayList_fulfillment;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    //===================StepView===================================================================
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;
            case R.id.view_order_accept:
                Intent orderProcessingIntent = new Intent(FulfillmentActivity.this, OrderProcessingActivity.class);
                orderProcessingIntent.putExtra("orderId", orderId);
                startActivityForResult(orderProcessingIntent, REQUEST_ORDER_PROCESSING);
                break;

//            case R.id.btn_start_now:
//                MaterialDialog dialog = new MaterialDialog.Builder(this)
//                        .title("Schedule Start Now")
//                        .content("Add Not Here\n6 Oct 2018   12:30 PM")
//                        .positiveText("OK")
//                        .negativeText("CANCEL")
//                        .show();
//                break;
            case R.id.cancel_order_view:
                Intent cancelOrderIntent = new Intent(FulfillmentActivity.this, CancelOrderActivity.class);
                cancelOrderIntent.putExtra("orderId", orderId);
                startActivityForResult(cancelOrderIntent, REQUEST_CANCEL_ORDER);
                break;
            case R.id.rl_review:
                Intent reviewOrderIntent = new Intent(FulfillmentActivity.this, ReviewOrderActivity.class);
                reviewOrderIntent.putExtra("orderId", orderId);
                startActivityForResult(reviewOrderIntent, REQUEST_REVIEW_ORDER);
                break;
            case R.id.ll_date:
                break;
            case R.id.ll_time:
                break;
        }

    }

    //===============Adapter==================================================================

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private ArrayList<FulfillmentDetails> android_versions;
        private Context context;

        public ImageAdapter(Context context, ArrayList<FulfillmentDetails> android_versions) {
            this.context = context;
            this.android_versions = android_versions;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fulfillment_details_list,
                    viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.img_rv.setImageResource(android_versions.get(i).getFulfillment_img());
        }

        @Override
        public int getItemCount() {
            return android_versions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img_rv;

            public ViewHolder(View view) {
                super(view);
                img_rv = (ImageView) view.findViewById(R.id.mUserDpOfFulfillment);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CANCEL_ORDER:
                        break;
                case REQUEST_REVIEW_ORDER:
                    break;
                case REQUEST_ORDER_PROCESSING:
                    break;
            }

        }
    }

}

