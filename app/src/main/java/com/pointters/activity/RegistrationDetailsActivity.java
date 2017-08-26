package com.pointters.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.facebook.login.LoginManager;
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
import com.pointters.R;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.model.request.LongitudeLatitude;
import com.pointters.model.request.UserPutRequest;
import com.pointters.model.response.ResponsePutUser;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConnectivityController;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.MyTextWatcher;
import com.theartofdev.edmodo.cropper.CropImage;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 19-Jul-17.
 */

public class RegistrationDetailsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, OnEditTextChangeListener {

    private final int REQUEST_CHECK_SETTINGS = 1000;
    private final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 2000;
    AmazonS3 s3;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private EditText edtInputLocation;
    private String imagePath;
    private RoundedImageView imgProfile;
    private TextInputLayout txtInputFirstName;
    private TextInputLayout txtInputLastName;
    private TextInputLayout txtInputCompanyName;
    private TextInputLayout txtInputPhoneNo;
    private TextInputLayout txtInputAboutYou;
    private EditText edtFirstName;
    private EditText edtLastName;
    private SharedPreferences.Editor editor;
    private EditText edtCompanyName;
    private EditText edtPhoneNo;
    private EditText edtAboutYou;
    private Button btnNext;
    private TextView txtErrorProfile;
    private SpotsDialog spotsDialog;
    private boolean isRequiredFieldsFilled;
    private DisplayImageOptions options;
    private TransferUtility transferUtility;
    private SharedPreferences sharedPreferences;
    private String MY_BUCKET = "pointters_dev/dev", OBJECT_KEY = "unique";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);

        initViews();
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //set toolbar
        AppUtils.setToolBarWithBothIcon(RegistrationDetailsActivity.this, getResources().getString(R.string.tell_us_few_things),
                R.drawable.back_icon_grey, 0);

        turnOnLocation();

        setOnClick();

        //calligraphy library not applying fonts to text input layout hence done programmatically
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{txtInputFirstName, txtInputLastName
                , txtInputCompanyName, txtInputPhoneNo, txtInputAboutYou});

        setEditTextListener();


    }


    private void setOnClick() {
        findViewById(R.id.img_select_profile).setOnClickListener(this);
        findViewById(R.id.img_location).setOnClickListener(this);
        edtInputLocation.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void initViews() {

        edtInputLocation = (EditText) findViewById(R.id.edt_text_location);
        imgProfile = (RoundedImageView) findViewById(R.id.img_profile);
        txtInputFirstName = (TextInputLayout) findViewById(R.id.text_input_first_name);
        txtInputLastName = (TextInputLayout) findViewById(R.id.text_input_last_name);
        txtInputCompanyName = (TextInputLayout) findViewById(R.id.text_input_company_name);
        txtInputPhoneNo = (TextInputLayout) findViewById(R.id.text_input_phone_number);
        txtInputAboutYou = (TextInputLayout) findViewById(R.id.text_input_about_you);
        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        edtLastName = (EditText) findViewById(R.id.edt_last_name);
        edtCompanyName = (EditText) findViewById(R.id.edt_company_name);
        edtPhoneNo = (EditText) findViewById(R.id.edt_phone_number);
        edtAboutYou = (EditText) findViewById(R.id.edt_about_you);
        btnNext = (Button) findViewById(R.id.btn_next);
        txtErrorProfile = (TextView) findViewById(R.id.txt_error_profile);

    }

    private void setEditTextListener() {

        //Custom Edit text change listener with returning id of edit text
        edtFirstName.addTextChangedListener(new MyTextWatcher(edtFirstName, this));
        edtLastName.addTextChangedListener(new MyTextWatcher(edtLastName, this));
        edtCompanyName.addTextChangedListener(new MyTextWatcher(edtCompanyName, this));
        edtPhoneNo.addTextChangedListener(new MyTextWatcher(edtPhoneNo, this));
        edtAboutYou.addTextChangedListener(new MyTextWatcher(edtAboutYou, this));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void turnOnLocation() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

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
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(RegistrationDetailsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    private void requestCurrentLocation() {

        if (ContextCompat.checkSelfPermission(RegistrationDetailsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RegistrationDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);

        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {

        Location location = null;

        if (ContextCompat.checkSelfPermission(RegistrationDetailsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        if (location == null) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            String locationName = AndroidUtils.getLocationName(RegistrationDetailsActivity.this, location);
            if (!locationName.isEmpty()) {

                ((ImageView) findViewById(R.id.img_location)).setImageDrawable(ContextCompat.getDrawable(RegistrationDetailsActivity.this,
                        R.drawable.location_icon));


                edtInputLocation.setText(locationName);
                edtInputLocation.setEnabled(true);
                edtInputLocation.setTextColor(ContextCompat.getColor(RegistrationDetailsActivity.this, R.color.color_black_info));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        requestCurrentLocation();
                        break;

                    default:
                        break;
                }
                break;
            case ImagePicker.IMAGE_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
                    if (mPaths != null && mPaths.size() == 1) {
                        imagePath = mPaths.get(0);
                        imgProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgProfile.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                       /* if(imagePath!=null)
                        {
                            Uri contentUri;

                        if (Build.VERSION.SDK_INT > M) {

                            contentUri = FileProvider.getUriForFile(RegistrationDetailsActivity.this,
                                    "android3.maxtingapp.provider", new File(imagePath));
                        }else {
                            contentUri = Uri.fromFile(new File(imagePath));
                        }
                            CropImage.activity(contentUri)
                                    .start(this);
                        }*/


                    }
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {


                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (result != null) {
                        Uri resultUri = result.getUri();
                        imagePath = resultUri.getPath();

                        imgProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgProfile.setImageBitmap(BitmapFactory.decodeFile(resultUri.getPath()));
                    }

                }
                break;
        }
    }


    private void ImgUploadOnAws() {

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(ConstantUtils.AWS_ACCESS_KEY, ConstantUtils.AWS_SECRATE_KEY);
        s3 = new AmazonS3Client(basicAWSCredentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));

        transferUtility = new TransferUtility(s3, getApplicationContext());
        TransferObserver observer = transferUtility.upload(MY_BUCKET, OBJECT_KEY, new File(imagePath), CannedAccessControlList.PublicRead);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    //   AndroidUtils.showToast(RegistrationDetailsActivity.this, "Uploading finished");

                } else if (state.equals(TransferState.FAILED)) {
                    //AndroidUtils.showToast(RegistrationDetailsActivity.this, "Uploading failed please try again");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getCurrentLocation();

                }
            }
            break;
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

    @Override
    public void onLocationChanged(Location location) {

        String locationName = AndroidUtils.getLocationName(RegistrationDetailsActivity.this, location);
        if (!locationName.isEmpty()) {

            ((ImageView) findViewById(R.id.img_location)).setImageDrawable(ContextCompat.getDrawable(RegistrationDetailsActivity.this,
                    R.drawable.location_icon));

            edtInputLocation.setText(locationName);
            edtInputLocation.setEnabled(true);
            edtInputLocation.setTextColor(ContextCompat.getColor(RegistrationDetailsActivity.this, R.color.color_black_info));
        }


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
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_location:
            case R.id.edt_text_location:
                if (edtInputLocation.getText().toString().isEmpty())
                    turnOnLocation();
                break;

            case R.id.toolbar_lft_img:

                onBackPressed();

                break;

            case R.id.img_select_profile:
            case R.id.img_profile:
               /* CropImage.activity().setAspectRatio(ConstantUtils.CROP_ASPECT_X,ConstantUtils.CROP_ASPECT_Y)
                        .setGuidelines(CropImageView.Guidelines.ON).setAllowFlipping(false).setAllowRotation(false)
                        .start(this);
*/
                isRequiredFieldsFilled = false;
                txtErrorProfile.setVisibility(View.GONE);
                new ImagePicker.Builder(RegistrationDetailsActivity.this)
                        .mode(ImagePicker.Mode.GALLERY)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .enableDebuggingMode(true)
                        .build();
                break;

            case R.id.btn_next:

                 isRequiredFieldsFilled = AppUtils.isRequiredFieldsFilled(new TextInputLayout[]{txtInputFirstName, txtInputLastName
                                 /*, txtInputCompanyName, txtInputPhoneNo, txtInputAboutYou*/},
                        getResources().getStringArray(R.array.registration_details_errors));

                if (edtInputLocation.getText().toString().isEmpty()) {
                    isRequiredFieldsFilled = false;

                }

                if (imagePath == null) {
                    isRequiredFieldsFilled = false;
                    txtErrorProfile.setVisibility(View.VISIBLE);
                }


                if (isRequiredFieldsFilled) {


                    if (ConnectivityController.isNetworkAvailable(RegistrationDetailsActivity.this)) {

                        callGetUserApi();
                    } else {
                        Toast.makeText(RegistrationDetailsActivity.this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    // AndroidUtils.showToast(RegistrationDetailsActivity.this, "Please fill");
                }

                break;
        }
    }

    private void callGetUserApi() {

        spotsDialog = new SpotsDialog(RegistrationDetailsActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);
        ImgUploadOnAws();
        LongitudeLatitude longitudeLatitude = new LongitudeLatitude(-118.243685, 34.052234);
        com.pointters.model.request.Location location = new com.pointters.model.request.Location("Los Angeles", "USA", longitudeLatitude, 91007, " ", "ca");
        UserPutRequest userPutRequest = new UserPutRequest(txtInputCompanyName.getEditText().getText().toString().trim(), txtInputAboutYou.getEditText().getText().toString().trim(), txtInputFirstName.getEditText().getText().toString().trim(), txtInputLastName.getEditText().getText().toString().trim(), location, txtInputPhoneNo.getEditText().getText().toString(), "https://s3.amazonaws.com/pointters_dev/dev/unique");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsePutUser> responsePutUserCall = apiService.putUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.APP_PREF, ""), userPutRequest);
        responsePutUserCall.enqueue(new Callback<ResponsePutUser>() {
            @Override
            public void onResponse(Call<ResponsePutUser> call, Response<ResponsePutUser> response) {
                if (spotsDialog != null && spotsDialog.isShowing()) {
                    spotsDialog.cancel();
                }

                if (response.code() == 200 && response.body().isSuccess()) {
                    AndroidUtils.showToast(RegistrationDetailsActivity.this, "Done");
                } else if (response.code() == 403) {
                    finish();
                    startActivity(new Intent(RegistrationDetailsActivity.this, HomeActivity.class));
                } else if (response.code() == 401) {
                    finish();
                    editor.putBoolean(ConstantUtils.PREF_IS_LOGIN, true).apply();
                    startActivity(new Intent(RegistrationDetailsActivity.this, HomeActivity.class));
                }
            }

            @Override
            public void onFailure(Call<ResponsePutUser> call, Throwable t) {
                AndroidUtils.showToast(RegistrationDetailsActivity.this, "please try again");
            }
        });

    }

    @Override
    public void onBackPressed() {

        //delete cover image file
        if (imagePath != null) {
            File coverFile = new File(imagePath);
            if (coverFile.exists())
                coverFile.delete();

        }

        LoginManager.getInstance().logOut();

        super.onBackPressed();
    }

    @Override
    public void onTextChange(String text, View view) {
        EditText editText = (EditText) view;

        if (!text.trim().isEmpty()) {
            ((TextInputLayout) editText.getParentForAccessibility()).setError(null);
            ((TextInputLayout) editText.getParentForAccessibility()).setErrorEnabled(false);
        }

    }
}
