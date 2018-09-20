package com.pointters.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mac on 12/25/17.
 */

public class CustomOfferDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private final int REQUEST_CHECK_SETTINGS = 1000;
    private final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 2000;

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location = null;
    private GoogleMap mMap;
    private LatLng latLng;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MapFragment mapFragment;
    private RelativeLayout layoutService, layoutMap;
    private SquareImageView imgService;
    private TextView txtTitle, txtLinkedDesc, txtPrice, txtOfferDesc, txtWorkTime, txtDeliveryMethod, btnAccept;
    private KProgressHUD loader;

    private RelativeLayout layoutDirection;

    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;

    private String offer_id = "";
    private CustomOfferModels offerInfo;
    private List<Double> service_pos = new ArrayList<>();

    private Integer isMessage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_offer_details);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        isMessage = getIntent().getIntExtra(ConstantUtils.CHAT_OFFER_DIRECTION, 0);

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

        googleApiClient = new GoogleApiClient.Builder(CustomOfferDetailsActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
//        turnOnLocation();

        initUI();
        hideLinkedService();

        offer_id = getIntent().getStringExtra(ConstantUtils.SELECT_OFFER_ID);
        if (!offer_id.equals("")) {
            loader.show();

            if (isMessage == 1) {
                callGetOfferDetails(offer_id);
            }
        } else {
            Toast.makeText(this, "Invalid offer!", Toast.LENGTH_SHORT).show();
//            finish();
        }
    }

    private void initUI() {
        txtTitle = (TextView) findViewById(R.id.toolbar_title);
        txtTitle.setText(R.string.custom_offer_detail);

        findViewById(R.id.toolbar_right_img).setVisibility(View.GONE);
        findViewById(R.id.toolbar_lft_img).setOnClickListener(this);

        layoutService = (RelativeLayout) findViewById(R.id.layout_service);
        layoutService.setOnClickListener(this);

        imgService = (SquareImageView) findViewById(R.id.img_profile_round);
        txtLinkedDesc = (TextView) findViewById(R.id.txt_linked_desc);

        txtPrice = (TextView) findViewById(R.id.txt_offer_price);
        txtOfferDesc = (TextView) findViewById(R.id.txt_offer_desc);
        txtWorkTime = (TextView) findViewById(R.id.txt_work_time);
        txtDeliveryMethod = (TextView) findViewById(R.id.txt_delivery_method);

        layoutDirection = (RelativeLayout) findViewById(R.id.layout_get_direction);
        layoutDirection.setOnClickListener(this);

        layoutMap = (RelativeLayout) findViewById(R.id.layout_map);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_view);

        btnAccept = (TextView) findViewById(R.id.btn_accept);
        btnAccept.setSelected(true);
        btnAccept.setOnClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void showLinkedService(LinkServiceModel linked) {
//        txtLinked.setVisibility(View.VISIBLE);
        layoutService.setVisibility(View.VISIBLE);

        if (linked.getDescription() != null && !linked.getDescription().isEmpty()) {
            txtLinkedDesc.setText(linked.getDescription());
        } else {
            txtLinkedDesc.setText("NA");
        }

        if (linked.getMedia() != null) {
            if (linked.getMedia().getFileName() != null && !linked.getMedia().getFileName().isEmpty()) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.photo_placeholder)
                        .showImageForEmptyUri(R.drawable.photo_placeholder)
                        .showImageOnFail(R.drawable.photo_placeholder)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .build();

                String strPic = linked.getMedia().getFileName();
                if (!strPic.contains("https://s3.amazonaws.com")) {
//                    strPic = "https://s3.amazonaws.com" + strPic;
                }
                ImageLoader.getInstance().displayImage(strPic, imgService, options);
            }
        }
    }

    private void hideLinkedService() {
//        txtLinked.setVisibility(View.GONE);
        layoutService.setVisibility(View.GONE);
    }

    private void setCustomOfferInfo() {
        String strSymbol = "$";
        if (offerInfo.getCurrencySymbol() != null && !offerInfo.getCurrencySymbol().isEmpty()) {
            strSymbol = offerInfo.getCurrencySymbol();
        }
        Integer valPrice = 0;
        if (offerInfo.getPrice() != null && offerInfo.getPrice() > 0) {
            valPrice = offerInfo.getPrice();
        }
        txtPrice.setText(strSymbol + String.valueOf(valPrice));

        String strDesc = "";
        if (offerInfo.getDescription() != null && !offerInfo.getDescription().isEmpty()) {
            strDesc = offerInfo.getDescription();
        }
        txtOfferDesc.setText(strDesc);

        Integer valTime = 0;
        if (offerInfo.getWorkDuration() != null && offerInfo.getWorkDuration() > 0) {
            valTime = offerInfo.getWorkDuration();
        }
        String strTimeUnit = "";
        if (offerInfo.getWorkDurationUom() != null && !offerInfo.getWorkDurationUom().isEmpty()) {
            strTimeUnit = offerInfo.getWorkDurationUom();
        }
        if (valTime > 1) {
            txtWorkTime.setText(String.valueOf(valTime) + " " + strTimeUnit + "s");
        } else {
            txtWorkTime.setText(String.valueOf(valTime) + " " + strTimeUnit);
        }

        boolean isLocal = false;
        Integer localRadius = 0;
        if (offerInfo.getFulfillmentMethod() != null) {
            boolean isOnline = offerInfo.getFulfillmentMethod().isOnline();
            boolean isShipment = offerInfo.getFulfillmentMethod().isShipment();
            boolean isStore = offerInfo.getFulfillmentMethod().isStore();

            isLocal = offerInfo.getFulfillmentMethod().isLocal();
            localRadius = offerInfo.getFulfillmentMethod().getLocalServiceRadius();

            String locationRadiusUnit = "mile";
            if (offerInfo.getFulfillmentMethod().getLocalServiceRadiusUom() != null && !offerInfo.getFulfillmentMethod().getLocalServiceRadiusUom().isEmpty()) {
                locationRadiusUnit = offerInfo.getFulfillmentMethod().getLocalServiceRadiusUom();
            }

            if (isOnline)   { txtDeliveryMethod.setText("Online service");   }
            if (isShipment) { txtDeliveryMethod.setText("Shipment service"); }
            if (isLocal)    { txtDeliveryMethod.setText("Servicing locally in the city you service"); }
            if (isStore)    { txtDeliveryMethod.setText("Servicing locally at your store locations"); }

            if (isLocal || isStore) {
                layoutDirection.setVisibility(View.VISIBLE);
                layoutMap.setVisibility(View.VISIBLE);

                service_pos.clear();
                if (offerInfo.getLocation() != null && offerInfo.getLocation().getGeoJson() != null) {
                    if (offerInfo.getLocation().getGeoJson().getCoordinates() != null && offerInfo.getLocation().getGeoJson().getCoordinates().size() > 0) {
                        service_pos = offerInfo.getLocation().getGeoJson().getCoordinates();
                    } else {
                        service_pos.add(-73.856077);
                        service_pos.add(40.848447);
                    }
                } else {
                    service_pos.add(-73.856077);
                    service_pos.add(40.848447);
                }

                latLng = new LatLng(service_pos.get(1), service_pos.get(0));
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_big)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                    }
                });
            } else {
                layoutDirection.setVisibility(View.INVISIBLE);
                layoutMap.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void callGetOfferDetails(final String offerId) {    //Buyer -> Offer
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetCustomOfferDetailsResponse> customOfferDetailsCall = apiService.getCustomOfferDetails1(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), offerId);
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
                        hideLinkedService();
                    }
                    setCustomOfferInfo();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(CustomOfferDetailsActivity.this, "callCustomOfferDetailsApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(CustomOfferDetailsActivity.this);
                }
                else if (response.code() == 404) {
                    Toast.makeText(CustomOfferDetailsActivity.this, "Can't get the offer info!", Toast.LENGTH_SHORT).show();
//                    finish();
                }
            }

            @Override
            public void onFailure(Call<GetCustomOfferDetailsResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(CustomOfferDetailsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
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
                        showLinkedService(response.body().getService());
                    } else {
                        hideLinkedService();
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(CustomOfferDetailsActivity.this, "callServiceByIdApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(CustomOfferDetailsActivity.this);
                }
                else if (response.code() == 404) {
                    Toast.makeText(CustomOfferDetailsActivity.this, "No linked service info!", Toast.LENGTH_SHORT).show();
                    hideLinkedService();
                }
            }

            @Override
            public void onFailure(Call<GetServiceByIdResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(CustomOfferDetailsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
                hideLinkedService();
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
                    Intent intent = new Intent(CustomOfferDetailsActivity.this, ServiceDetailActivity.class);
                    intent.putExtra(ConstantUtils.SERVICE_ID, offerInfo.getServiceId());
                    startActivity(intent);
                }
                break;

            case R.id.layout_get_direction:
                String directionUrl = "http://maps.apple.com/?saddr=" + String.valueOf(mUserLat) + "," + String.valueOf(mUserLng) + "&daddr=" + String.valueOf(service_pos.get(1)) + "," + String.valueOf(service_pos.get(0));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(directionUrl));
                startActivity(browserIntent);
                break;

            case R.id.btn_accept:
                Intent checkoutIntent = new Intent(CustomOfferDetailsActivity.this, CheckoutActivity.class);
                startActivity(checkoutIntent);
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

//    private void turnOnLocation() {
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(30 * 1000);
//        locationRequest.setFastestInterval(5 * 1000);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//
//        //**************************
//        builder.setAlwaysShow(true); //this is the key ingredient
//        //**************************
//
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                final LocationSettingsStates state = result.getLocationSettingsStates();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        requestCurrentLocation();
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // LocationRequestModel settings are not satisfied. But could be fixed by showing the user
//                        // a dialog.
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult(CustomOfferDetailsActivity.this, REQUEST_CHECK_SETTINGS);
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // LocationRequestModel settings are not satisfied. However, we have no way to fix the
//                        // settings so we won't show the dialog.
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//
//    }

    private void requestCurrentLocation() {
        if (ContextCompat.checkSelfPermission(CustomOfferDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomOfferDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_GET_LOCATION);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(CustomOfferDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//          AndroidUtils.showToast(ServiceDetailActivity.this,String.valueOf(AndroidUtils.getDistanceBwTwoLocation(location.getLatitude(),location.getLongitude(),  40.848447,  -73.856077)));
        }
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            callGetOfferDetails(offer_id);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getCurrentLocation();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(CustomOfferDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //Show permission explanation dialog...
                        } else {
                            Toast.makeText(CustomOfferDetailsActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                switch (resultCode) {
                    case RESULT_OK:
                        requestCurrentLocation();
                        break;

                    default:
                        break;
                }
            }
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
