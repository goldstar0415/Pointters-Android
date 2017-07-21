package com.pointters.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.pointters.R;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;

import net.alhazmy13.mediapicker.FileProcessing;
import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Vishal Sharma on 19-Jul-17.
 */

public class RegistrationDetailsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private Toolbar toolbar;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private final int REQUEST_CHECK_SETTINGS = 1000;
    private final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 2000;
    private TextView txtLocation;
    private String imagePath;
    private String originalImagePath;
    private ImageView imgProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);

        initViews();

        AppUtils.setDefaultToolbarWithBackIcon(this, toolbar);

        turnOnLocation();

        setOnClick();

        //calligraphy library not applying fonts to text input layout hence done programmatically
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{
                (TextInputLayout) findViewById(R.id.text_input_first_name)
                , (TextInputLayout) findViewById(R.id.text_input_last_name)
                , (TextInputLayout) findViewById(R.id.text_input_company_name)
                , (TextInputLayout) findViewById(R.id.text_input_phone_number)
                , (TextInputLayout) findViewById(R.id.text_input_about_you)});

    }

    private void setOnClick() {
        findViewById(R.id.img_select_profile).setOnClickListener(this);
        imgProfile.setOnClickListener(this);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        imgProfile = (ImageView) findViewById(R.id.img_profile);
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
                txtLocation.setText(locationName);
                txtLocation.setTextColor(ContextCompat.getColor(RegistrationDetailsActivity.this, R.color.color_black_info));
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

                        //call the standard crop action intent (the user device may not support it)
                        Intent cropIntent = new Intent("com.android.camera.action.CROP");

                        Uri contentUri;

                        if (Build.VERSION.SDK_INT > M) {

                            contentUri = FileProvider.getUriForFile(RegistrationDetailsActivity.this,
                                    "android3.maxtingapp.provider", new File(imagePath));

                            //TODO:  Permission..

                            getApplicationContext().grantUriPermission("com.android.camera",
                                    contentUri,
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        } else {
                            contentUri = Uri.fromFile(new File(imagePath));
                        }

                        //indicate image type and Uri
                        cropIntent.setDataAndType(contentUri, "image/*");
                        //set crop properties
                        cropIntent.putExtra("crop", "true");
                        //indicate aspect of desired crop
                        cropIntent.putExtra("aspectX", ConstantUtils.CROP_ASPECT_X);
                        cropIntent.putExtra("aspectY", ConstantUtils.CROP_ASPECT_Y);
                        //indicate output X and Y
                        cropIntent.putExtra("outputX", ConstantUtils.CROP_IMAGE_DIMEN_X);
                        cropIntent.putExtra("outputY", ConstantUtils.CROP_IMAGE_DIMEN_Y);
                        //retrieve data on return
                        cropIntent.putExtra("return-data", false);
                        //start the activity - we handle returning in onActivityResult
                        startActivityForResult(cropIntent, ConstantUtils.ACTION_CROP_IMAGE);

                    }
                }
                break;

            case ConstantUtils.ACTION_CROP_IMAGE:
                if (resultCode == RESULT_OK) {

                    Bitmap photo;

                    if (data.getData() != null) {
                        originalImagePath = imagePath;
                        //used code of media picker library to convert to a valid path
                        imagePath = FileProcessing.getPath(RegistrationDetailsActivity.this, data.getData());
                        photo = BitmapFactory.decodeFile(imagePath);

                    } else if (data.getExtras().get("data") != null) {
                        photo = (Bitmap) data.getExtras().get("data");
                    } else {
                        photo = BitmapFactory.decodeFile(imagePath);
                    }

                    imgProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imgProfile.setImageBitmap(photo);

                } else {
                    File originalCoverFile = new File(imagePath);
                    if (originalCoverFile.exists())
                        originalCoverFile.delete();
                }

                break;
        }
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
            txtLocation.setText(locationName);
            txtLocation.setTextColor(ContextCompat.getColor(RegistrationDetailsActivity.this, R.color.color_black_info));
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
                if (txtLocation.getText().toString().equals(getResources().getString(R.string.location_service_required)))
                    turnOnLocation();
                break;

            case R.id.img_select_profile:
            case R.id.img_profile:
                new ImagePicker.Builder(RegistrationDetailsActivity.this)
                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .enableDebuggingMode(true)
                        .build();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            onBackPressed(); // close this activity and return to preview activity
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        //delete cover image file
        if (imagePath != null) {
            File coverFile = new File(imagePath);
            if (coverFile.exists())
                coverFile.delete();

        }

        //delete original cover image file
        if (originalImagePath != null) {
            File originalCoverFile = new File(originalImagePath);
            if (originalCoverFile.exists())
                originalCoverFile.delete();

        }

        super.onBackPressed();
    }
}
