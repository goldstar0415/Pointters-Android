package com.pointters.activity;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.pointters.R;
import com.pointters.model.FulfillmentDetails;
import com.pointters.model.OrdersDetailModel;
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


        recyclerView = (RecyclerView) findViewById(R.id.mDeliveredFilesRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        stepView1 = (ImageView) findViewById(R.id.mCircleViewNumberReplacement);
        stepView2 = (ImageView) findViewById(R.id.mCircleViewNumberReplacement2);
        stepView3 = (ImageView) findViewById(R.id.mCircleViewNumberReplacement3);
        stepView4 = (ImageView) findViewById(R.id.mCircleViewNumberReplacement4);
        stepView5 = (ImageView) findViewById(R.id.mCircleViewNumberReplacement5);
        stepView1.setOnClickListener(this);
        stepView2.setOnClickListener(this);
        stepView3.setOnClickListener(this);
        stepView4.setOnClickListener(this);
        stepView5.setOnClickListener(this);

        orderAccept = (RelativeLayout) findViewById(R.id.view_order_accept);
        orderAccept.setOnClickListener(this);
        cancelOrderButton = (Button) findViewById(R.id.btn_cancel_order);
        cancelOrderButton.setOnClickListener(this);
        reviewOrderButton = (Button) findViewById(R.id.btn_review);
        reviewOrderButton.setOnClickListener(this);

        ArrayList fulfillmentList = prepareData();
        ImageAdapter adapter = new ImageAdapter(FulfillmentActivity.this, fulfillmentList);
        recyclerView.setAdapter(adapter);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapId);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                // Add a marker in Agicent and move the camera
                LatLng TutorialsPoint = new LatLng(28.6125, 77.3773);

                mMap.addMarker(new MarkerOptions().position(TutorialsPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_big)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.6125, 77.3773), 12.0f));

               // mMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
            }
        });

        CallGetOrderDetail(orderId);
    }

    public void CallGetOrderDetail(String orderid) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<OrdersDetailModel> callGetCategoryApi = apiService.getOrderDetail(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), orderid);
        callGetCategoryApi.enqueue(new Callback<OrdersDetailModel>() {
            @Override
            public void onResponse(Call<OrdersDetailModel> call, Response<OrdersDetailModel> response) {
                loader.dismiss();
                if (response.code() == 200 && response.body() != null) {
                    ordersDetailModel = response.body();
                    updateUI();
                }
            }

            @Override
            public void onFailure(Call<OrdersDetailModel> call, Throwable t) {
                loader.dismiss();
            }
        });
    }

    public void updateUI(){

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
            case R.id.toolbar_right_img:
                break;
            case R.id.mCircleViewNumberReplacement:
                stepView1.setVisibility(View.VISIBLE);

                break;

            case R.id.mCircleViewNumberReplacement2:
                stepView1.setVisibility(View.VISIBLE);
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
            case R.id.btn_cancel_order:
                Intent cancelOrderIntent = new Intent(FulfillmentActivity.this, CancelOrderActivity.class);
                cancelOrderIntent.putExtra("orderId", orderId);
                startActivityForResult(cancelOrderIntent, REQUEST_CANCEL_ORDER);
                break;
            case R.id.btn_review:
                Intent reviewOrderIntent = new Intent(FulfillmentActivity.this, ReviewOrderActivity.class);
                reviewOrderIntent.putExtra("orderId", orderId);
                startActivityForResult(reviewOrderIntent, REQUEST_REVIEW_ORDER);
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

