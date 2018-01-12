package com.pointters.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.gson.internal.LinkedTreeMap;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.adapter.EditProfileImageViewPagerAdapter;
import com.pointters.adapter.PriceAdapter;
import com.pointters.adapter.RelatedServiceAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.Media;
import com.pointters.model.Prices;
import com.pointters.model.ServiceDetailModel;
import com.pointters.model.response.GetRelatedServicesResponse;
import com.pointters.model.response.GetServiceDeatilResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ServiceDetailActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private final int REQUEST_CHECK_SETTINGS = 1000;
    private final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 2000;

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location = null;
    private RelativeLayout moveToProfileScreen;
    private TextView txtName, txtVerified, buttonToggle, buyForButton, txtGetCustomOffer, txtCountFirst, txtCountSecond, txtPoint, txtWatch;
    private GoogleMap mMap;
    private RoundedImageView imgProfile;
    private LinearLayout layoutLike;
    private int firstCount = 1, secondCount = 1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MapFragment mapFragment;
    private LatLng latLng;
    private ExpandableTextView txtServiceDesc;
    private RecyclerView recyclerViewPrice, recyclerViewRelatedService;
    private PriceAdapter priceAdapter;
    private RelatedServiceAdapter serviceAdapter;
    private List<Prices> pricesList = new ArrayList<>();
    private List<Media> mediaList = new ArrayList<>();
    private List<ServiceDetailModel> serviceDetailModelList = new ArrayList<>();
    private EditProfileImageViewPagerAdapter editProfileImageViewPagerAdapter;
    private String serviceId = "5a029b03b37404568cb6f20e";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setUpSuggestedCategoryViewPager();

        if (getIntent() != null && getIntent().getStringExtra(ConstantUtils.SERVICE_ID) != null && !getIntent().getStringExtra(ConstantUtils.SERVICE_ID).isEmpty())
            serviceId = getIntent().getStringExtra(ConstantUtils.SERVICE_ID);


        googleApiClient = new GoogleApiClient.Builder(ServiceDetailActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        turnOnLocation();

        AppUtils.setToolBarWithBothIcon(ServiceDetailActivity.this, getResources().getString(R.string.service_detail), R.drawable.back_icon_grey, R.drawable.more_icon_horizontal);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtVerified = (TextView) findViewById(R.id.txt_verified);
        imgProfile = (RoundedImageView) findViewById(R.id.img_profile);
        txtServiceDesc = (ExpandableTextView) findViewById(R.id.txt_service_desc);
        txtPoint = (TextView) findViewById(R.id.txt_point);
        txtWatch = (TextView) findViewById(R.id.txt_watch);
        layoutLike = (LinearLayout) findViewById(R.id.layout_like);
        txtGetCustomOffer = (TextView) findViewById(R.id.txt_get_custom_offer);
        moveToProfileScreen = (RelativeLayout) findViewById(R.id.move_to_profile);
        buyForButton = (TextView) findViewById(R.id.buy_for_button);
        buyForButton.setOnClickListener(this);
        moveToProfileScreen.setOnClickListener(this);
        txtGetCustomOffer.setOnClickListener(this);
        buttonToggle = (TextView) this.findViewById(R.id.button_toggle);

        recyclerViewPrice = (RecyclerView) findViewById(R.id.recyclerview_price);
        recyclerViewPrice.setLayoutManager(new LinearLayoutManager(ServiceDetailActivity.this));
        priceAdapter = new PriceAdapter(ServiceDetailActivity.this, pricesList);
        recyclerViewPrice.setAdapter(priceAdapter);

        recyclerViewRelatedService = (RecyclerView) findViewById(R.id.recyclerview_related_service);
        recyclerViewRelatedService.setLayoutManager(new LinearLayoutManager(ServiceDetailActivity.this));
       /* serviceAdapter = new RelatedServiceAdapter(ServiceDetailActivity.this,serviceDetailModelList,location);
        recyclerViewRelatedService.setAdapter(serviceAdapter);*/


        //SetOn Click listener
        txtPoint.setOnClickListener(this);
        txtWatch.setOnClickListener(this);
        layoutLike.setOnClickListener(this);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapId);

        getServiceDetailApi();


        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        txtServiceDesc.setAnimationDuration(750L);

        // set interpolators for both expanding and collapsing animations
        txtServiceDesc.setInterpolator(new OvershootInterpolator());

        // or set them separately
        txtServiceDesc.setExpandInterpolator(new OvershootInterpolator());
        txtServiceDesc.setCollapseInterpolator(new OvershootInterpolator());

        // but, you can also do the checks yourself
        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (txtServiceDesc.isExpanded()) {
                    txtServiceDesc.collapse();
                    buttonToggle.setText("more");
                } else {
                    txtServiceDesc.expand();
                    buttonToggle.setText("less");
                }
            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setUpSuggestedCategoryViewPager() {

        ViewPager viewPagerSuggestedServices = (ViewPager) findViewById(R.id.view_pager_suggested_services);
        editProfileImageViewPagerAdapter = new EditProfileImageViewPagerAdapter(ServiceDetailActivity.this, mediaList);
        // SuggestedCategoriesProfileAdapter suggestedCategoriesProfileAdapter = new SuggestedCategoriesProfileAdapter(getSupportFragmentManager());
        viewPagerSuggestedServices.setAdapter(editProfileImageViewPagerAdapter);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator_view_pager_suggested_services);
        circlePageIndicator.setViewPager(viewPagerSuggestedServices);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;
            case R.id.toolbar_right_img:
                break;
            case R.id.move_to_profile:
                startActivity(new Intent(ServiceDetailActivity.this, ProfileScreenActivity.class));
                break;
            case R.id.buy_for_button:
                startActivity(new Intent(ServiceDetailActivity.this, CheckoutActivity.class));
                break;


          /*  case R.id.img_first_plus:
                firstCount++;
                txtCountFirst.setText(String.valueOf(firstCount));
                break;
            case R.id.img_first_minus:
                if (firstCount != 1) {
                    firstCount--;
                    txtCountFirst.setText(String.valueOf(firstCount));
                }
                break;
            case R.id.img_plus_second:
                secondCount++;
                txtCountSecond.setText(String.valueOf(secondCount));
                break;
            case R.id.img_second_minus:
                if (secondCount != 1) {
                    secondCount--;
                    txtCountSecond.setText(String.valueOf(secondCount));
                }
                break;*/

            case R.id.txt_point:
                break;
            case R.id.txt_watch:
                postWatchServiceApiCall();
                break;
            case R.id.layout_like:
                postLikeServiceApiCall();
                break;
        }
    }

    private void postLikeServiceApiCall() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> likeServiceCall = apiService.likeService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), serviceId);
        likeServiceCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    boolean success = (boolean) ((LinkedTreeMap) response.body()).get("success");
                    if (success)
                        AndroidUtils.showToast(ServiceDetailActivity.this, "Liked successfully");
                    else
                        AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");

                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callPostLikeServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                } else if (response.code() == 404) {
                    AndroidUtils.showToast(ServiceDetailActivity.this, "Like does not exits");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }

    private void postWatchServiceApiCall() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> watchServiceCall = apiService.watchService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), "5a029b03b37404568cb6f20e");
        watchServiceCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    boolean success = (boolean) ((LinkedTreeMap) response.body()).get("success");
                    if (success)
                        AndroidUtils.showToast(ServiceDetailActivity.this, "Watched Successfully");

                    else
                        AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callPostWatchServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                } else if (response.code() == 404) {
                    AndroidUtils.showToast(ServiceDetailActivity.this, "Watch does not exits");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }

    private void getLikeService() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);

    }

    private void getWatchService() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);


    }

    private void unLikeService() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> unLikeServiceResponse = apiService.UnlikeService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), "5a029b03b37404568cb6f20e");
        unLikeServiceResponse.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    boolean success = (boolean) ((LinkedTreeMap) response.body()).get("success");
                    if (success)
                        AndroidUtils.showToast(ServiceDetailActivity.this, "Unlike Successfully");

                    else
                        AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callUnLikeServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

    }

    private void unWatchService() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> unWatchServiceResponse = apiService.UnwatchService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), "5a029b03b37404568cb6f20e");
        unWatchServiceResponse.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    boolean success = (boolean) ((LinkedTreeMap) response.body()).get("success");
                    if (success)
                        AndroidUtils.showToast(ServiceDetailActivity.this, "Unwatche Successfully");

                    else
                        AndroidUtils.showToast(ServiceDetailActivity.this, "Oops something went wrong, Plaese try again");
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callUnWatchServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }

    private void getServiceDetailApi() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetServiceDeatilResponse> getServiceDetailCall = apiService.getServiceDetail(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), serviceId);
        getServiceDetailCall.enqueue(new Callback<GetServiceDeatilResponse>() {
            @Override
            public void onResponse(Call<GetServiceDeatilResponse> call, Response<GetServiceDeatilResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getResult().getSeller() != null) {
                        if (!response.body().getResult().getSeller().getFirstName().isEmpty())
                            txtName.setText(response.body().getResult().getSeller().getFirstName());
                        if (response.body().getResult().getSeller().getVerified()!=null && response.body().getResult().getSeller().getVerified())
                            txtVerified.setVisibility(View.VISIBLE);
                        else txtVerified.setVisibility(View.GONE);

                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                                .showImageOnFail(R.drawable.user_avatar_placeholder)
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .considerExifParams(true)
                                .build();

                        if (!response.body().getResult().getSeller().getProfilePic().isEmpty())
                            ImageLoader.getInstance().displayImage(response.body().getResult().getSeller().getProfilePic(), imgProfile, options);
                    }
                    if (response.body().getResult().getService().getLocation() != null && response.body().getResult().getService().getLocation().get(0).getGeoJson() != null) {
                        latLng = new LatLng(response.body().getResult().getService().getLocation().get(0).getGeoJson().getCoordinates().get(1), response.body().getResult().getService().getLocation().get(0).getGeoJson().getCoordinates().get(0));
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

                    }
                    if (!response.body().getResult().getService().getDescription().isEmpty())
                        txtServiceDesc.setText(response.body().getResult().getService().getDescription());
                    else
                        txtServiceDesc.setText("NA");

                    if (response.body().getResult().getService().getPrices() != null && !response.body().getResult().getService().getPrices().isEmpty()) {
                        pricesList.addAll(response.body().getResult().getService().getPrices());
                        //  pricesList.add(new Prices("hi",10,1,"day"));
                        priceAdapter.notifyDataSetChanged();
                    }
                    if (response.body().getResult().getService().getMedia() != null && !response.body().getResult().getService().getMedia().isEmpty()) {
                        mediaList.addAll(response.body().getResult().getService().getMedia());
                        editProfileImageViewPagerAdapter.notifyDataSetChanged();
                    }

                } else if (response.code() == 404) {

                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callGetServiceDetailApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }

            }

            @Override
            public void onFailure(Call<GetServiceDeatilResponse> call, Throwable t) {

            }
        });
    }

    void getRelatedServiceApi() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetRelatedServicesResponse> getRelatedServicesResponseCall = apiService.getRelatedService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), serviceId);
        getRelatedServicesResponseCall.enqueue(new Callback<GetRelatedServicesResponse>() {
            @Override
            public void onResponse(Call<GetRelatedServicesResponse> call, Response<GetRelatedServicesResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getDocs() != null) {
                        serviceDetailModelList.addAll(response.body().getDocs());
                        serviceAdapter.notifyDataSetChanged();
                    }

                } else if (response.code() == 404) {

                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ServiceDetailActivity.this, "callGetRelatedServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ServiceDetailActivity.this);
                }
            }

            @Override
            public void onFailure(Call<GetRelatedServicesResponse> call, Throwable t) {

            }
        });
    }


    @Override
    public void onApiFail(String apiSource) {
        if (apiSource.equals("callGetServiceDetailApi")) {
            getServiceDetailApi();
        } else if (apiSource.equals("callPostWatchServiceApi")) {
            postWatchServiceApiCall();
        } else if (apiSource.equals("callPostLikeServiceApi")) {
            postLikeServiceApiCall();
        } else if (apiSource.equals("callGetRelatedServiceApi")) {
            getRelatedServiceApi();
        } else if (apiSource.equals("callUnWatchServiceApi")) {
            unWatchService();
        } else if (apiSource.equals("callUnLikeServiceApi")) {
            unLikeService();
        }

    }

    private void turnOnLocation() {


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        requestCurrentLocation();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // LocationRequestModel settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(ServiceDetailActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // LocationRequestModel settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    private void requestCurrentLocation() {

        if (ContextCompat.checkSelfPermission(ServiceDetailActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ServiceDetailActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);

        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {


        if (ContextCompat.checkSelfPermission(ServiceDetailActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//            AndroidUtils.showToast(ServiceDetailActivity.this,String.valueOf(AndroidUtils.getDistanceBwTwoLocation(location.getLatitude(),location.getLongitude(),  40.848447,  -73.856077)));
        }
        if (location == null) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        } else {
            serviceAdapter = new RelatedServiceAdapter(ServiceDetailActivity.this, serviceDetailModelList, location);
            recyclerViewRelatedService.setAdapter(serviceAdapter);
            getRelatedServiceApi();


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
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ServiceDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //Show permission explanation dialog...
                        } else {
                            Toast.makeText(ServiceDetailActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();

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
        /*recyclerViewRelatedService.setAdapter(null);
        serviceAdapter = new RelatedServiceAdapter(ServiceDetailActivity.this,serviceDetailModelList,location);
        recyclerViewRelatedService.setAdapter(serviceAdapter);*/


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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}