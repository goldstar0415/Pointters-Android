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
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.adapter.EditProfileImageViewPagerAdapter;
import com.pointters.adapter.ProfileServicesAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.Media;
import com.pointters.model.ServicesModel;
import com.pointters.model.response.FollowUnfollowResponse;
import com.pointters.model.response.GetServicesReponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by prashantkumar on 16/8/17.
 */

public class ProfileScreenActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener, GoogleApiClient.ConnectionCallbacks , LocationListener, GoogleApiClient.OnConnectionFailedListener,OnRecyclerViewItemClickListener{
    private final int REQUEST_CHECK_SETTINGS = 1000;
    private final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 2000;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location = null;
    private TextView txtFollow, txtTitle, txtCompany, txtLocation, txtPhone, txtDescription,txtName,txtVerified;
    private RecyclerView recyclerViewServices;
    private ProfileServicesAdapter serviceAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RoundedImageView imgProfile;
    private String json, profileUrl, name,userId;
    private ArrayList<ServicesModel> serviceArrayList = new ArrayList<>();
    private EditProfileImageViewPagerAdapter editProfileImageViewPagerAdapter;
    private List<Media> bgFiles=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        googleApiClient = new GoogleApiClient.Builder(ProfileScreenActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        turnOnLocation();

        txtVerified=(TextView)findViewById(R.id.txt_verified);
        txtName=(TextView)findViewById(R.id.txt_name);
        txtTitle = (TextView) findViewById(R.id.toolbar_title);
        txtTitle.setText(getResources().getString(R.string.profile));
        txtFollow = (TextView) findViewById(R.id.txt_follow);
        txtFollow.setOnClickListener(this);
        txtCompany = (TextView) findViewById(R.id.txt_company);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtDescription = (TextView) findViewById(R.id.txt_descrption);
        recyclerViewServices = (RecyclerView) findViewById(R.id.recyclerview_services);
        imgProfile = (RoundedImageView) findViewById(R.id.img_profile);

        findViewById(R.id.toolbar_lft_img).setOnClickListener(this);
        findViewById(R.id.toolbar_right_img).setOnClickListener(this);


        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null)
            json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");

        getUserDataApiCall();

        getFollowedUserApi();

       // setUpSuggestedCategoryViewPager();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void getUserDataApiCall() {

        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("profilePic")) {
                if (jsonObject.get("profilePic") != null && !jsonObject.get("profilePic").toString().isEmpty()) {
                DisplayImageOptions    options = new DisplayImageOptions.Builder()
                            .showImageOnLoading(R.drawable.user_avatar_placeholder)
                            .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                            .showImageOnFail(R.drawable.user_avatar_placeholder)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .considerExifParams(true)
                            .build();

                    if (jsonObject.get("profilePic").toString().startsWith("https://s3.amazonaws.com")) {
                        profileUrl = jsonObject.get("profilePic").toString();
                    } else {
                        profileUrl = "https://s3.amazonaws.com" + jsonObject.get("profilePic").toString();
                    }

                    ImageLoader.getInstance().displayImage(profileUrl, imgProfile, options);


                }
            }

            if (jsonObject.has("firstName")) {
                if (jsonObject.get("firstName") != null && !jsonObject.get("firstName").toString().isEmpty())
                    name = (String) jsonObject.get("firstName");
                txtName.setText(name +" "+jsonObject.get("lastName"));
            }
            if(jsonObject.has("companyName") && jsonObject.get("companyName")!=null)
                txtCompany.setText(String .valueOf(jsonObject.get("companyName")));
            else
                txtCompany.setText("NA");

            if(jsonObject.has("phone") && jsonObject.get("phone")!=null)
                txtPhone.setText(String .valueOf(jsonObject.get("phone")));
            else
                txtPhone.setText("NA");

            if(jsonObject.has("description") && jsonObject.get("description")!=null)
                txtDescription.setText(String .valueOf(jsonObject.get("description")));
            else
                txtDescription.setText("NA");

            if(jsonObject.has("verified") && jsonObject.get("verified")!=null )
                if((boolean)jsonObject.get("verified"))
                txtVerified.setVisibility(View.VISIBLE);
            else
                txtVerified.setVisibility(View.GONE);

            if(jsonObject.has("_id") && jsonObject.get("_id")!=null )
                userId= (String) jsonObject.get("_id");

            if (jsonObject.has("location")) {
                JSONObject jsonObjectLocation= (JSONObject) jsonObject.get("location");
                if (jsonObjectLocation.has("city") && jsonObjectLocation.has("country") && jsonObjectLocation.has("state")) {
                    if (jsonObjectLocation.get("city") != null && jsonObjectLocation.get("country") != null && jsonObjectLocation.get("state") != null && jsonObjectLocation.get("geoJson")!=null) {
                        String cityName = jsonObjectLocation.get("city").toString();
                        String stateName = jsonObjectLocation.get("state").toString();
                        String countryName = jsonObjectLocation.get("country").toString();
                       String postalCode = jsonObjectLocation.get("postalCode").toString();
                         txtLocation.setText(cityName + ", " + stateName + " " + countryName);
                        /*JSONObject jsonObject1= (JSONObject) jsonObjectLocation.get("geoJson");
                        JSONArray jsonArrayCoordinates= new JSONArray(jsonObject1.get("coordinates").toString());
                        lat=Double.parseDouble(jsonArrayCoordinates.get(0).toString());
                        lng=Double.parseDouble(jsonArrayCoordinates.get(1).toString());*/
                    }
                }


            }
            if(jsonObject.has("profileBackgroundMedia")){
                JSONArray jsonArray = new JSONArray(jsonObject.get("profileBackgroundMedia").toString());
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObjectBackgroundMedia = jsonArray.getJSONObject(i);

                    jsonObjectBackgroundMedia.get("mediaType");

                    Media media=new Media( jsonObjectBackgroundMedia.get("fileName").toString(), jsonObjectBackgroundMedia.get("mediaType").toString());
                    bgFiles.add(media);



                }
                if(bgFiles.size()>0)
                    setUpSuggestedCategoryViewPager();
                  //  editProfileImageViewPagerAdapter.notifyDataSetChanged();


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void setUpSuggestedCategoryViewPager() {

        ViewPager viewPagerSuggestedServices = (ViewPager) findViewById(R.id.profile_bg_viewpager);
      //  SuggestedCategoriesProfileAdapter suggestedCategoriesProfileAdapter = new SuggestedCategoriesProfileAdapter(getSupportFragmentManager());
        editProfileImageViewPagerAdapter=new EditProfileImageViewPagerAdapter(ProfileScreenActivity.this,bgFiles);
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
            case R.id.txt_follow:

                if (txtFollow.getText().equals(getResources().getString(R.string.follow)))
                    callFollowApi();
                else if (txtFollow.getText().equals(getResources().getString(R.string.unfollow)))
                    callUnfollwApi();
                break;

        }
    }

    void callFollowApi() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> followuser = apiService.followUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), "59a8f5a6e5c7c90929fc48d7");
        followuser.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    txtFollow.setText(getResources().getString(R.string.unfollow));
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ProfileScreenActivity.this, "callFollowApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
            }
        });
    }

    void callUnfollwApi() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> unfollowuser = apiService.unFollowUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), "59a8f5a6e5c7c90929fc48d7");
        unfollowuser.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    txtFollow.setText(getResources().getString(R.string.follow));
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ProfileScreenActivity.this, "callUnfollowUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
            }
        });
    }


    void getFollowedUserApi() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<FollowUnfollowResponse> unfollowuser = apiService.getfollowedUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), "59a8f5a6e5c7c90929fc48d7");
        unfollowuser.enqueue(new Callback<FollowUnfollowResponse>() {
            @Override
            public void onResponse(Call<FollowUnfollowResponse> call, Response<FollowUnfollowResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().isFollowing()) {
                        txtFollow.setVisibility(View.VISIBLE);
                        txtFollow.setText(getResources().getString(R.string.unfollow));
                    } else {
                        txtFollow.setVisibility(View.VISIBLE);
                        txtFollow.setText(getResources().getString(R.string.follow));
                    }
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ProfileScreenActivity.this, "callGetFollowedUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }
            }

            @Override
            public void onFailure(Call<FollowUnfollowResponse> call, Throwable t) {
            }
        });
    }

    void getServices() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetServicesReponse> getServicesCall = apiService.getServicesByUserId(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""),userId);
        getServicesCall.enqueue(new Callback<GetServicesReponse>() {
            @Override
            public void onResponse(Call<GetServicesReponse> call, Response<GetServicesReponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    serviceArrayList.addAll(response.body().getDocs());
                    serviceAdapter.notifyDataSetChanged();

                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ProfileScreenActivity.this, "callGetServicesApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ProfileScreenActivity.this);
                }

            }

            @Override
            public void onFailure(Call<GetServicesReponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onApiFail(String apiSource) {
        if (apiSource.equals("callGetServicesApi")) {
            getServices();
        } else if (apiSource.equals("callGetFollowedUserApi")) {
            getFollowedUserApi();
        } else if (apiSource.equals("callUnfollowUserApi")) {
            callUnfollwApi();
        } else if (apiSource.equals("callFollowApi")) {
            callFollowApi();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

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
                            status.startResolutionForResult(ProfileScreenActivity.this, REQUEST_CHECK_SETTINGS);
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

        if (ContextCompat.checkSelfPermission(ProfileScreenActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ProfileScreenActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);

        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {


        if (ContextCompat.checkSelfPermission(ProfileScreenActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//            AndroidUtils.showToast(ServiceDetailActivity.this,String.valueOf(AndroidUtils.getDistanceBwTwoLocation(location.getLatitude(),location.getLongitude(),  40.848447,  -73.856077)));
        }
        if (location == null) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        } else {
            getServices();
            recyclerViewServices.setLayoutManager(new LinearLayoutManager(ProfileScreenActivity.this));
            serviceAdapter = new ProfileServicesAdapter(ProfileScreenActivity.this, serviceArrayList,profileUrl,name,location,ProfileScreenActivity.this);
            recyclerViewServices.setAdapter(serviceAdapter);



        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {

                if (grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getCurrentLocation();
                    }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileScreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //Show permission explanation dialog...
                        }else{
                            Toast.makeText(ProfileScreenActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();

                        }
                    }
                }



            }
            break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(int position) {
        Intent intent=new Intent(ProfileScreenActivity.this,ServiceDetailActivity.class);
        intent.putExtra(ConstantUtils.SERVICE_ID,serviceArrayList.get(position).getService().getId());
        startActivity(intent);
    }
}
