package com.pointters.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.CustomOfferModels;
import com.pointters.model.LinkServiceModel;
import com.pointters.model.response.GetCustomOfferDetailsResponse;
import com.pointters.model.response.GetServiceByIdResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.SquareImageView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mac on 12/25/17.
 */

public class GetLiveOffersActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private final int REQUEST_CHECK_SETTINGS = 1000;
    private final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 2000;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout layoutCategory, layoutLocation;
    private RecyclerView recyclerSnapPhotos;
    private TextView txtTitle, txtCategory, txtDesc;//, txtPrice, txtOfferDesc, txtWorkTime, txtDeliveryMethod, btnAccept;
    private EditText edtDate, edtTime, edtMinPrice, edtMaxPrice;
    private Button submitButton, deleteButton;

    private KProgressHUD loader;

    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private String offer_id = "";
    private CustomOfferModels offerInfo;
    private List<Double> service_pos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_live_offers);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (!sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "").equals("")) {
            mUserLat = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "0"));
        }
        if (!sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "").equals("")) {
            mUserLng = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "0"));
        }

        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        initUI();

//        offer_id = getIntent().getStringExtra(ConstantUtils.SELECT_OFFER_ID);
//        if (!offer_id.equals("")) {
//            loader.show();
//            callGetOfferDetails(offer_id);
//        } else {
//            Toast.makeText(this, "Invalid offer!", Toast.LENGTH_SHORT).show();
////            finish();
//        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initUI() {
        txtTitle = (TextView) findViewById(R.id.toolbar_title);
        txtTitle.setText(R.string.custom_offer_detail);

        findViewById(R.id.toolbar_right_img).setVisibility(View.GONE);
        findViewById(R.id.toolbar_lft_img).setOnClickListener(this);

        txtCategory = (TextView) findViewById(R.id.txt_category_name);
        txtDesc = (TextView) findViewById(R.id.txt_desc);
        layoutCategory = (RelativeLayout)findViewById(R.id.category_view);
        layoutLocation = (RelativeLayout)findViewById(R.id.location_view);
        layoutCategory.setOnClickListener(this);
        layoutLocation.setOnClickListener(this);

        edtDate = (EditText) findViewById(R.id.edt_date);
        edtTime = (EditText) findViewById(R.id.edt_time);
        edtMinPrice = (EditText) findViewById(R.id.edt_min_price);
        edtMaxPrice = (EditText) findViewById(R.id.edt_max_price);

        edtDate.setOnClickListener(this);
        edtTime.setOnClickListener(this);

        deleteButton = (Button) findViewById(R.id.btn_delete);
        submitButton = (Button) findViewById(R.id.btn_submit);
        deleteButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    private void setCustomOfferInfo() {
    }

    private void callGetOfferDetails(final String offerId) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetCustomOfferDetailsResponse> customOfferDetailsCall = apiService.getCustomOfferDetails(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), offerId);
        customOfferDetailsCall.enqueue(new Callback<GetCustomOfferDetailsResponse>() {
            @Override
            public void onResponse(Call<GetCustomOfferDetailsResponse> call, Response<GetCustomOfferDetailsResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    offerInfo = response.body().getOffer();
                    if (offerInfo.getServiceId() != null && !offerInfo.getServiceId().isEmpty()) {
                        callGetServiceInfo(offerInfo.getServiceId());
                    } else {
                    }
                    setCustomOfferInfo();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(GetLiveOffersActivity.this, "callCustomOfferDetailsApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(GetLiveOffersActivity.this);
                }
                else if (response.code() == 404) {
                    Toast.makeText(GetLiveOffersActivity.this, "Can't get the offer info!", Toast.LENGTH_SHORT).show();
//                    finish();
                }
            }

            @Override
            public void onFailure(Call<GetCustomOfferDetailsResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(GetLiveOffersActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
//                finish();
            }
        });
    }

    private void callGetServiceInfo(final String serviceId) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetServiceByIdResponse> getServiceByIdResponseCall = apiService.getServiceById(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), serviceId);
        getServiceByIdResponseCall.enqueue(new Callback<GetServiceByIdResponse>() {
            @Override
            public void onResponse(Call<GetServiceByIdResponse> call, Response<GetServiceByIdResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getService() != null) {
                    } else {
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(GetLiveOffersActivity.this, "callServiceByIdApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(GetLiveOffersActivity.this);
                }
                else if (response.code() == 404) {
                    Toast.makeText(GetLiveOffersActivity.this, "No linked service info!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetServiceByIdResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(GetLiveOffersActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;

            case R.id.layout_service:
                if (offerInfo.getServiceId() != null && !offerInfo.getServiceId().isEmpty()) {
                    Intent intent = new Intent(GetLiveOffersActivity.this, ServiceDetailActivity.class);
                    intent.putExtra(ConstantUtils.SERVICE_ID, offerInfo.getServiceId());
                    startActivity(intent);
                }
                break;

            case R.id.btn_submit:
//                String directionUrl = "http://maps.apple.com/?saddr=" + String.valueOf(mUserLat) + "," + String.valueOf(mUserLng) + "&daddr=" + String.valueOf(service_pos.get(1)) + "," + String.valueOf(service_pos.get(0));
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(directionUrl));
//                startActivity(browserIntent);
                break;

            case R.id.btn_delete:
//                Intent checkoutIntent = new Intent(GetLiveOffersActivity.this, CheckoutActivity.class);
//                startActivity(checkoutIntent);
                break;

            case R.id.edt_date:
                Calendar now = Calendar.getInstance();
                if (datePickerDialog == null) {
                    datePickerDialog = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                }else{
                    datePickerDialog.initialize(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                }
                datePickerDialog.setThemeDark(false);
                datePickerDialog.vibrate(true);
                datePickerDialog.dismissOnPause(true);
                datePickerDialog.showYearPickerFirst(true);
                datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
                datePickerDialog.setAccentColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
                datePickerDialog.show(getFragmentManager(), "Date");
                break;
            case R.id.edt_time:
                Calendar now1 = Calendar.getInstance();
                if (timePickerDialog == null) {
                    timePickerDialog = TimePickerDialog.newInstance(this, now1.get(Calendar.HOUR_OF_DAY), now1.get(Calendar.MINUTE), false);
                }else{
                    timePickerDialog.initialize(this, now1.get(Calendar.HOUR_OF_DAY), now1.get(Calendar.MINUTE), now1.get(Calendar.SECOND), false);
                }
                timePickerDialog.setThemeDark(false);
                timePickerDialog.vibrate(true);
                timePickerDialog.dismissOnPause(true);
                timePickerDialog.enableSeconds(true);
                timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
                timePickerDialog.setAccentColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
                timePickerDialog.show(getFragmentManager(), "Time");
                break;

            default:
                break;
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onApiFail(String apiSource) {
        Log.e("Custom Offer Details Api Failed: ", apiSource);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(GetLiveOffersActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //Show permission explanation dialog...
                        } else {
                            Toast.makeText(GetLiveOffersActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                switch (resultCode) {
                    case RESULT_OK:
                        break;

                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }
}
