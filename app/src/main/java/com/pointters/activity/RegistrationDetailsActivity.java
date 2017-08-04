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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.MyTextWatcher;

import net.alhazmy13.mediapicker.FileProcessing;
import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Vishal Sharma on 19-Jul-17.
 */

public class RegistrationDetailsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, OnEditTextChangeListener {

    private Toolbar toolbar;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private final int REQUEST_CHECK_SETTINGS = 1000;
    private final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 2000;
    private TextView txtLocation;
    private String imagePath;
    private String originalImagePath;
    private RoundedImageView imgProfile;
    private TextInputLayout txtInputFirstName;
    private TextInputLayout txtInputLastName;
    private TextInputLayout txtInputCompanyName;
    private TextInputLayout txtInputPhoneNo;
    private TextInputLayout txtInputAboutYou;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtCompanyName;
    private EditText edtPhoneNo;
    private EditText edtAboutYou;
    private Button btnNext;
    private TextView txtErrorLocation;
    private TextView txtErrorProfile;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);

        initViews();

        AppUtils.setDefaultToolbarWithBackIcon(this, toolbar);

        turnOnLocation();

        setOnClick();

        //calligraphy library not applying fonts to text input layout hence done programmatically
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{txtInputFirstName, txtInputLastName
                , txtInputCompanyName, txtInputPhoneNo, txtInputAboutYou});

        setEditTextListener();

        if (AccessToken.getCurrentAccessToken() != null)
            getRequiredFbData();

    }

    private void getRequiredFbData() {

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {


                            if (object.has("first_name"))
                                edtFirstName.setText(object.getString("first_name"));


                            if (object.has("last_name"))
                                edtLastName.setText(object.getString("last_name"));

                            if (object.has("id")) {

                                options = new DisplayImageOptions.Builder()
                                        .showImageOnLoading(R.drawable.user_avatar_placeholder)
                                        .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                                        .showImageOnFail(R.drawable.user_avatar_placeholder)
                                        .cacheInMemory(true)
                                        .cacheOnDisk(true)
                                        .considerExifParams(true)
                                        .build();

                                imagePath=String.format(
                                        "https://graph.facebook.com/%s/picture/?type=large", object.getString("id"));

                                ImageLoader.getInstance().displayImage(imagePath, imgProfile, options);


                            }

                            // location of user
                            if (object.has("location")) {
                                JSONObject jsonLocation = object.getJSONObject("location");

                                if (jsonLocation.has("name"))
                                    txtLocation.setText(object.getJSONObject("location").getString("name"));

                            }

                            if (object.has("work")) {

                                JSONArray jsonArray = object.getJSONArray("work");

                                if (jsonArray.length() > 0) {

                                    for (int x = 0; x < jsonArray.length(); x++) {

                                        if (x == jsonArray.length() - 1) {

                                            JSONObject jsonWorkedObj = jsonArray.getJSONObject(x);

                                            if (jsonWorkedObj.has("employer")) {

                                                JSONObject jsonWorkingObj = jsonWorkedObj.getJSONObject("employer");

                                                if (jsonWorkingObj.has("name")) {

                                                    edtCompanyName.setText(jsonWorkingObj.getString("name"));

                                                }
                                            }
                                        }
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,location,work");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void setOnClick() {
        findViewById(R.id.img_select_profile).setOnClickListener(this);
        findViewById(R.id.img_location).setOnClickListener(this);
        txtLocation.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        txtLocation = (TextView) findViewById(R.id.txt_location);
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
        txtErrorLocation = (TextView) findViewById(R.id.txt_error_location);
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

                if (txtErrorLocation.getVisibility() == View.VISIBLE) {
                    txtErrorLocation.setVisibility(View.GONE);
                }

                txtLocation.setText(locationName);
                txtLocation.setEnabled(true);
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

                    if (txtErrorProfile.getVisibility() == View.VISIBLE) {
                        txtErrorProfile.setVisibility(View.GONE);
                    }

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

            ((ImageView) findViewById(R.id.img_location)).setImageDrawable(ContextCompat.getDrawable(RegistrationDetailsActivity.this,
                    R.drawable.location_icon));

            if (txtErrorLocation.getVisibility() == View.VISIBLE) {
                txtErrorLocation.setVisibility(View.GONE);
            }

            txtLocation.setText(locationName);
            txtLocation.setEnabled(true);
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
            case R.id.txt_location:
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

            case R.id.btn_next:


                boolean isRequiredFieldsFilled = AppUtils.isRequiredFieldsFilled(new TextInputLayout[]{txtInputFirstName, txtInputLastName,
                                txtInputCompanyName, txtInputPhoneNo, txtInputAboutYou},
                        getResources().getStringArray(R.array.registration_details_errors));

                if (txtLocation.getText().toString().equals(getResources().getString(R.string.location_service_required))) {
                    isRequiredFieldsFilled = false;
                    txtErrorLocation.setVisibility(View.VISIBLE);
                }

                if (imagePath == null) {
                    isRequiredFieldsFilled = false;
                    txtErrorProfile.setVisibility(View.VISIBLE);
                }


                if (isRequiredFieldsFilled) {
                    Toast.makeText(RegistrationDetailsActivity.this, "success", Toast.LENGTH_SHORT).show();
                } else {

                }

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
