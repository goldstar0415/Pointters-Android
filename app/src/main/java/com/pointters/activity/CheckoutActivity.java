package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.adapter.PriceAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.RecyclerViewItemClickWithSource;
import com.pointters.model.AddressModel;
import com.pointters.model.Media;
import com.pointters.model.MetricsModel;
import com.pointters.model.ParcelModel;
import com.pointters.model.PaymentMethod;
import com.pointters.model.Prices;
import com.pointters.model.SellerModel;
import com.pointters.model.Service;
import com.pointters.model.StoreLocationModel;
import com.pointters.model.response.GetServiceDetailResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class CheckoutActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {
    private static int REQUEST_EDIT_SHIPPING_ADDRESS = 1;
    private static int REQUEST_ENTER_SHIPPING_ADDRESS  = 2;
    private static int REQUEST_EDIT_PAYMENT_METHOD = 1;
    private static int REQUEST_ENTER_PAYMENT_METHOD  = 2;

    private TextView payButton;
    private RelativeLayout changePaymentMethodRelativeLayout, changeShippingAddressLayout;
    private int serviceCount = 1, addOnCount = 1;
    private String serviceId = "";
    private KProgressHUD loader;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SellerModel seller = new SellerModel();
    private Service service = new Service();
    private MetricsModel metrics = new MetricsModel();
    private List<Prices> pricesList = new ArrayList<>();
    private List<Media> mediaList = new ArrayList<>();
    private List<Double> service_pos = new ArrayList<>();
    private String serviceDesc = "NA";

    private String sellerId = "";
    private RecyclerView recyclerViewPrice;
    private PriceAdapter priceAdapter;
    private ImageView imgService;
    private TextView txtServiceDesc;
    private TextView txtPrice, txtPriceDesc;
    private TextView txtSeller, txtCompleteTime, txtTotalBudget, txtPaymentMethod, txtShippingAddress;

    private StoreLocationModel shippingAddress;
    private PaymentMethod paymentMethod;
    private CardView card_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        card_view = (CardView) findViewById(R.id.card_view);
        card_view.setOnClickListener(this);
        payButton = (TextView) findViewById(R.id.btn_pay);
        changePaymentMethodRelativeLayout = (RelativeLayout) findViewById(R.id.change_payment);
        changeShippingAddressLayout = (RelativeLayout) findViewById(R.id.change_shipping_address);
        changePaymentMethodRelativeLayout.setOnClickListener(this);
        changeShippingAddressLayout.setOnClickListener(this);
        payButton.setOnClickListener(this);

        txtPrice = (TextView) findViewById(R.id.txt_service_price);
        txtPriceDesc = (TextView) findViewById(R.id.txt_service_time);
        txtServiceDesc = (TextView) findViewById(R.id.txt_service_name);
        txtSeller = (TextView) findViewById(R.id.txt_service_provider_name);
        imgService = (ImageView) findViewById(R.id.img_service_provider);

        txtCompleteTime = (TextView) findViewById(R.id.txt_complete_time);
        txtTotalBudget = (TextView) findViewById(R.id.txt_total_budget);
        txtPaymentMethod = (TextView) findViewById(R.id.txt_payment_method);
        txtShippingAddress = (TextView) findViewById(R.id.txt_shipping_address);

        recyclerViewPrice = (RecyclerView) findViewById(R.id.recycler_price);
        recyclerViewPrice.setLayoutManager(new LinearLayoutManager(CheckoutActivity.this));
        priceAdapter = new PriceAdapter(CheckoutActivity.this, pricesList, new RecyclerViewItemClickWithSource() {
            @Override
            public void onItemClick(int position, Prices source, int type) {
                calculatePrice();
            }

        });
        recyclerViewPrice.setAdapter(priceAdapter);

        AppUtils.setToolBarWithBothIcon(CheckoutActivity.this, getResources().getString(R.string.checkout), R.drawable.back_icon, 0);
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        serviceId = getIntent().getStringExtra(ConstantUtils.SERVICE_ID);
        if (!serviceId.equals("")) {
            loader.show();
            callGetServiceDetailApi(serviceId);
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
            case R.id.btn_pay:
                if (!txtTotalBudget.getText().equals("") && !txtTotalBudget.getText().equals("$0.00")) {
                    Intent intent = new Intent(this, FulfillmentActivity.class);
//                    intent.putExtra(ConstantUtils.SELECT_ORDER_ID, strId);
                    intent.putExtra(ConstantUtils.SELECT_ORDER_TYPE, ConstantUtils.BUYER);
                    startActivity(intent);
                }
                break;
            case R.id.change_payment:
                startActivity(new Intent(CheckoutActivity.this, PaymentMethodsActivity.class));
                break;
            case R.id.change_shipping_address:
                Intent shipIntent = new Intent(CheckoutActivity.this, ShippingAddressesActivity.class);
                if (shippingAddress != null) {
                    Gson gson = new Gson();
                    shipIntent.putExtra(ConstantUtils.ADD_ADDRESS, gson.toJson(shippingAddress));
                    startActivityForResult(shipIntent, REQUEST_EDIT_SHIPPING_ADDRESS);
                }else{
                    startActivityForResult(shipIntent, REQUEST_ENTER_SHIPPING_ADDRESS);
                }
                break;
            case R.id.card_view:
                onBackPressed();
                break;
        }
    }

    @SuppressLint("DefaultLocale")
    private void calculatePrice() {
        ArrayList<Integer> priceCountList = priceAdapter.getPriceCountList();
        Prices prices = pricesList.get(0);
        String unit = prices.getTimeUnitOfMeasure();
        int count = priceCountList.get(0);
        if (count > 1) {
            txtCompleteTime.setText(String.format("%d %ss", count, unit));
        }else{
            txtCompleteTime.setText(String.format("%d %s", count, unit));
        }
        float total = 0f;
        total = prices.getPrice() * count;
        txtTotalBudget.setText(String.format("%s%.2f", prices.getCurrencySymbol(), total));

    }
    // Get Service Detail Info
    private void callGetServiceDetailApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetServiceDetailResponse> getServiceDetailCall = apiService.getServiceDetail(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        getServiceDetailCall.enqueue(new Callback<GetServiceDetailResponse>() {
            @Override
            public void onResponse(Call<GetServiceDetailResponse> call, Response<GetServiceDetailResponse> response) {
                loader.dismiss();
                if (response.code() == 200) {
                    if (response.body().getResult().getSeller() != null) {
                        seller = response.body().getResult().getSeller();
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
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(CheckoutActivity.this, "callGetServiceDetailApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(CheckoutActivity.this);
                }

                setServiceDetails();
            }

            @Override
            public void onFailure(Call<GetServiceDetailResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(CheckoutActivity.this, "No Service Details!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setServiceDetails() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        if (mediaList.size() > 0) {
            String pic = mediaList.get(0).getFileName();
            ImageLoader.getInstance().displayImage(pic, imgService, options);
        }

        if (seller.getUserId() != null && !seller.getUserId().isEmpty()) {
            sellerId = seller.getUserId();
        }

        String strFirst = "", strLast = "";
        if (seller.getFirstName() != null && !seller.getFirstName().isEmpty()) {
            strFirst = seller.getFirstName();
        }
        if (seller.getLastName() != null && !seller.getLastName().isEmpty()) {
            strLast = seller.getLastName();
        }

        txtServiceDesc.setText(serviceDesc);
        txtSeller.setText(strFirst+" "+strLast);
        if (pricesList.size() > 0) {
            priceAdapter.notifyDataSetChanged();
            Prices prices = pricesList.get(0);
            txtPrice.setText(String.format("%s%.2f", prices.getCurrencySymbol(), prices.getPrice()));
            txtPriceDesc.setText(String.format(" For %d%s, %s",prices.getTime(), prices.getTimeUnitOfMeasure(), prices.getDescription()));
            String unit = prices.getTimeUnitOfMeasure();
            txtCompleteTime.setText(String.format("%d %s", 0, unit));
            float total = 0f;
            txtTotalBudget.setText(String.format("%s%.2f", prices.getCurrencySymbol(), total));
        }

        if (service.getFulfillmentMethod().getShipment()) {
            changeShippingAddressLayout.setVisibility(View.VISIBLE);
        } else {
            changeShippingAddressLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_EDIT_SHIPPING_ADDRESS) {
                Gson gson = new Gson();
                String strAddress = data.getStringExtra(ConstantUtils.ADD_ADDRESS);
                shippingAddress = gson.fromJson(strAddress, StoreLocationModel.class);
                txtShippingAddress.setText(shippingAddress.getFullAddress());
                CallGetShippingRate();
            }else if (requestCode == REQUEST_ENTER_SHIPPING_ADDRESS) {
                Gson gson = new Gson();
                String strAddress = data.getStringExtra(ConstantUtils.ADD_ADDRESS);
                shippingAddress = gson.fromJson(strAddress, StoreLocationModel.class);
                txtShippingAddress.setText(shippingAddress.getFullAddress());
                CallGetShippingRate();
            }
        }
    }

    public void CallGetShippingRate() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<JSONObject> getServiceDetailCall = apiService.getShipmentRate(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), shippingAddress.get_id());
        getServiceDetailCall.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                loader.dismiss();
                if (response.code() == 200) {
                    JSONObject obj = response.body();
                    try {
                        if (obj.get("message").toString().equals("Not Found")) {
                            Toast.makeText(CheckoutActivity.this, "Not Found!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(CheckoutActivity.this, "callGetServiceDetailApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(CheckoutActivity.this);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(CheckoutActivity.this, "No Shipment rate detail!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onApiFail(String apiSource) {

    }
}
