package com.pointters.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
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
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
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
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.LinkConsumableTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.model.Media;
import com.pointters.model.request.LocationRequestModel;
import com.pointters.model.request.LongitudeLatitude;
import com.pointters.model.request.UserPutRequest;
import com.pointters.model.request.UserRegisterPutRequest;
import com.pointters.model.response.ResponsePutUser;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConnectivityController;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.MyTextWatcher;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.pointters.R.id.ifRoom;
import static com.pointters.R.id.image;
import static com.pointters.R.id.img_profile;

/**
 * Created by Vishal Sharma on 19-Jul-17.
 */

public class RegistrationDetailsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, OnEditTextChangeListener, OnApiFailDueToSessionListener {
    public static final String APP_DIR = "profilepic";
    public static final String COMPRESSED_VIDEOS_DIR = "/Compressed Videos/";
    public static final String TEMP_DIR = "/Temp/";

    private final int REQUEST_CHECK_SETTINGS = 1000;
    private final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 2000;
    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 2001;
    AmazonS3 s3;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private EditText edtInputLocation;
    private String imagePath;
    private RoundedImageView imgProfile;
    private ProfilePictureView fbimgProfile;
    private TextInputLayout txtInputFirstNamme;
    private TextInputLayout txtInputLastName;
    private TextInputLayout txtInputLocation;
    private SharedPreferences.Editor editor;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtLocation;
    private CardView btnNext;
    private TextView txtErrorProfile;
    private SpotsDialog spotsDialog;
    private boolean isRequiredFieldsFilled;
    private DisplayImageOptions options;
    private String profilePicUrl = "";
    private TransferUtility transferUtility;
    private SharedPreferences sharedPreferences;
    /* private String MY_BUCKET ="pointterstestbucket";*/
    private String MY_BUCKET = "pointters_dev/dev", OBJECT_KEY;
    private String imageUrl = "https://s3.amazonaws.com/pointters_dev/dev/";
    /* private String imageUrl = "https://s3.amazonaws.com/pointterstestbucket/";*/
    private Location location = null;
    private UserRegisterPutRequest userPutRequest;
    private ArrayList<Media> bgFiles = new ArrayList<>();
    private Boolean isImageUploaded = false;
    private String userDetails;
    private LinkConsumableTextView txtTermsConditions;
    public static void try2CreateCompressDir() {
        File f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + COMPRESSED_VIDEOS_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + TEMP_DIR);
        f.mkdirs();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        initViews();


        makeSpannableText();
        editor.putBoolean(ConstantUtils.IS_ON_REGISTRATION_SCREEN, true).apply();

        //set toolbar
        AppUtils.setToolBarWithBothIcon(RegistrationDetailsActivity.this, getResources().getString(R.string.tell_us_few_things),
                R.drawable.back_icon, 0);

        turnOnLocation();

        setOnClick();

        //calligraphy library not applying fonts to text input layout hence done programmatically
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{txtInputFirstNamme, txtInputLastName, txtInputLocation});

        requestStorage();


        OBJECT_KEY = generateFileName();

        setPrefilledDataToInputFields();
        setEditTextListener();


        userDetails = sharedPreferences.getString(ConstantUtils.USER_DATA, "");

        getUserData();
    }
    private void makeSpannableText() {
        final Link link = new Link("Terms of Service");
        link.setUnderlined(false);
        final Link link1 = new Link("Privacy Policy");
        link1.setUnderlined(false);
        link.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String s) {
                Intent intent = new Intent(RegistrationDetailsActivity.this, TermsActivity.class);
                intent.putExtra("type", "terms");
                startActivity(intent);
                Toast.makeText(RegistrationDetailsActivity.this, "Terms of Service", Toast.LENGTH_SHORT).show();
            }
        });

        link1.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String s) {
                Intent intent = new Intent(RegistrationDetailsActivity.this, TermsActivity.class);
                intent.putExtra("type", "privacy");
                startActivity(intent);
                Toast.makeText(RegistrationDetailsActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            }
        });

        LinkBuilder.Companion.on(txtTermsConditions).addLink(link1).addLink(link).build();

    }


    private void getUserData() {

        try {
            JSONObject jsonObject = new JSONObject(userDetails);

            if (jsonObject.has("firstName")) {
                if (jsonObject.get("firstName") != null && !jsonObject.get("firstName").toString().isEmpty())
                    edtFirstName.setText(jsonObject.get("firstName").toString());

            }

            if (jsonObject.has("lastName")) {
                if (jsonObject.get("lastName") != null && !jsonObject.get("lastName").toString().isEmpty())
                    edtLastName.setText(jsonObject.get("lastName").toString());

            }
//
//            if (jsonObject.has("companyName")) {
//                if (jsonObject.get("companyName") != null && !jsonObject.get("companyName").toString().isEmpty())
//                    edtCompanyName.setText(jsonObject.get("companyName").toString());
//
//            }
//
//            if (jsonObject.has("description")) {
//                if (jsonObject.get("description") != null && !jsonObject.get("description").toString().isEmpty())
//                    edtAboutYou.setText(jsonObject.get("description").toString());
//
//            }
//
//
//            if (jsonObject.has("phone")) {
//                if (jsonObject.get("phone") != null && !jsonObject.get("phone").toString().isEmpty())
//                    edtPhoneNo.setText(jsonObject.get("phone").toString());
//
//            }
//
            if (jsonObject.has("location")) {
                JSONArray jsonArray = new JSONArray(jsonObject.get("location").toString());
                if(jsonArray.length()>0) {
                    JSONObject jsonObjectLocation = jsonArray.getJSONObject(0);
                    if (jsonObjectLocation.has("city") && jsonObjectLocation.has("country") && jsonObjectLocation.has("state")) {
                        if (jsonObjectLocation.get("city") != null && jsonObjectLocation.get("country") != null && jsonObjectLocation.get("state") != null) {
                            String cityName = jsonObjectLocation.get("city").toString();
                            String stateName = jsonObjectLocation.get("state").toString();
                            String countryName = jsonObjectLocation.get("country").toString();
                            String postalCode = jsonObjectLocation.get("postalCode").toString();
                            edtInputLocation.setText(cityName + ", " + stateName + " " + countryName);
                        }
                    }
                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (AccessToken.getCurrentAccessToken() != null) {
            if (!AccessToken.getCurrentAccessToken().getToken().equals("")){
                final AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(fbAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response != null){
                            try {
                                JSONObject data = response.getJSONObject();
                                if (data.has("picture")){
                                    profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    fbimgProfile.setProfileId(data.getString("id"));
                                    String fbimageurl = "https://graph.facebook.com/"+data.getString("id")+"/picture?type=small";
                                    options = new DisplayImageOptions.Builder()
                                            .showImageOnLoading(R.drawable.user_avatar_placeholder)
                                            .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                                            .showImageOnFail(R.drawable.user_avatar_placeholder)
                                            .cacheInMemory(true)
                                            .cacheOnDisk(true)
                                            .considerExifParams(true)
                                            .build();

                                    ImageLoader.getInstance().displayImage(profilePicUrl, imgProfile, options);

                                }
                                if (data.has("first_name")){
                                    String firstname = data.getString("first_name");
                                    edtFirstName.setText(firstname);
                                }
                                if (data.has("last_name")){
                                    String lastname = data.getString("last_name");
                                    edtLastName.setText(lastname);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("fields", "id,name,first_name,last_name,picture,email");
                request.setParameters(bundle);
                request.executeAsync();

            }
        }


    }

    private void setPrefilledDataToInputFields() {

        if (sharedPreferences.getString(ConstantUtils.FIRST_NAME, "") != null && !sharedPreferences.getString(ConstantUtils.FIRST_NAME, "").isEmpty())
            edtFirstName.setText(sharedPreferences.getString(ConstantUtils.FIRST_NAME, ""));
        if (sharedPreferences.getString(ConstantUtils.LAST_NAME, "") != null && !sharedPreferences.getString(ConstantUtils.LAST_NAME, "").isEmpty())
            edtLastName.setText(sharedPreferences.getString(ConstantUtils.LAST_NAME, ""));
        if (sharedPreferences.getString(ConstantUtils.LOCATION, "") != null && !sharedPreferences.getString(ConstantUtils.LOCATION, "").isEmpty())
            edtLocation.setText(sharedPreferences.getString(ConstantUtils.ABOUT_YOU, ""));

    }

    private String generateFileName() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

        return simpleDateFormat.format(calendar.getTime());
    }

    private void setOnClick() {
//        findViewById(R.id.img_select_profile).setOnClickListener(this);
        findViewById(R.id.img_profile).setOnClickListener(this);

//        findViewById(R.id.img_location).setOnClickListener(this);
        edtInputLocation.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void initViews() {

        edtInputLocation = (EditText) findViewById(R.id.edt_location);
        imgProfile = (RoundedImageView) findViewById(img_profile);
        txtInputFirstNamme = (TextInputLayout) findViewById(R.id.text_input_first_name);
        txtInputLastName = (TextInputLayout) findViewById(R.id.text_input_last_name);
        txtInputLocation = (TextInputLayout) findViewById(R.id.text_input_location);
        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        edtLastName = (EditText) findViewById(R.id.edt_last_name);
        edtLocation = (EditText) findViewById(R.id.edt_location);
        btnNext = (CardView) findViewById(R.id.btn_sign_up);
        txtErrorProfile = (TextView) findViewById(R.id.txt_error_profile);
        txtTermsConditions = (LinkConsumableTextView)findViewById(R.id.txt_privacy_conditions);
        fbimgProfile = (ProfilePictureView) findViewById(R.id.fb_img_profile);


    }

    private void setEditTextListener() {

        //Custom Edit text change listener with returning id of edit text
        edtFirstName.addTextChangedListener(new MyTextWatcher(edtFirstName, this));
        edtLastName.addTextChangedListener(new MyTextWatcher(edtLastName, this));
        edtInputLocation.addTextChangedListener(new MyTextWatcher(edtInputLocation, this));


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
                        // LocationRequestModel settings are not satisfied. But could be fixed by showing the user
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
                        // LocationRequestModel settings are not satisfied. However, we have no way to fix the
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

    private boolean requestStorage(){
        if (ContextCompat.checkSelfPermission(RegistrationDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }else{
            return true;
        }
        return false;
    }
    private void getCurrentLocation() {


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

//                ((ImageView) findViewById(R.id.img_location)).setImageDrawable(ContextCompat.getDrawable(RegistrationDetailsActivity.this,
//                        R.drawable.location_icon));
//

                edtInputLocation.setText(locationName);
                edtInputLocation.setEnabled(true);
                edtInputLocation.setTextColor(ContextCompat.getColor(RegistrationDetailsActivity.this, R.color.color_black_info));
                imgProfile.buildDrawingCache(true);

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
                        saveBitmapToFile(new File(imagePath));
                        Bitmap photo = BitmapFactory.decodeFile(imagePath);
                        imgProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgProfile.setImageBitmap(photo);
                        ImgUploadOnAws();

                    }
                }
                break;


        }
    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            inputStream = null;

            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            if (originalWidth > 160) {

                int reqWidth = 640;
                int reqHeight = (reqWidth * originalHeight) / originalWidth;

                // decode full image pre-resized
                inputStream = new FileInputStream(file);
                options = new BitmapFactory.Options();

                // calc rought re-size (this is no exact resize)
                options.inSampleSize = Math.max(originalWidth / reqWidth, originalHeight / reqHeight);
                // decode full image
                Bitmap roughBitmap = BitmapFactory.decodeStream(inputStream, null, options);

                // calc exact destination size
                Matrix m = new Matrix();
                RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
                RectF outRect = new RectF(0, 0, reqWidth, reqHeight);
                m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
                float[] values = new float[9];
                m.getValues(values);

                // resize bitmap
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);

                // override resized bitmap image
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);

            }

            return file;

        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
            return null;
        }

    }

    public File saveBitmapToFile(Bitmap bitmap, File file) {
        try {


                // resize bitmap
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()), (int) (bitmap.getHeight()), true);

                // override resized bitmap image
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);


            return file;

        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
            return null;
        }

    }

    private void ImgUploadOnAws() {

     /*   CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(getApplicationContext(), "us-west-2:9d6b32ea-1d34-4d9b-8813-5dd7d28d8795", Regions.US_WEST_2);
        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.US_WEST_2));
        transferUtility = new TransferUtility(s3, getApplicationContext());

            TransferObserver observer = transferUtility.upload(MY_BUCKET,OBJECT_KEY,new File(imagePath));*/


        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(ConstantUtils.AWS_ACCESS_KEY, ConstantUtils.AWS_SECRATE_KEY);
        s3 = new AmazonS3Client(basicAWSCredentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));

        transferUtility = new TransferUtility(s3, getApplicationContext());
        TransferObserver observer = transferUtility.upload(MY_BUCKET, OBJECT_KEY, new File(imagePath), CannedAccessControlList.PublicRead);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    isImageUploaded = true;

                } else if (state.equals(TransferState.FAILED)) {
                    // AndroidUtils.showToast(RegistrationDetailsActivity.this, "Uploading failed please try again");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                ex.getMessage();

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getCurrentLocation();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //Show permission explanation dialog...
                        } else {
                            Toast.makeText(RegistrationDetailsActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                            //RegistrationDetailsActivity.this.finish();
                        }
                    }
                }

            }
            break;
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:{
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //Show permission explanation dialog...
                        } else {
                            Toast.makeText(RegistrationDetailsActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                        }
                    }
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

    @Override
    public void onLocationChanged(Location location) {

        String locationName = AndroidUtils.getLocationName(RegistrationDetailsActivity.this, location);
        if (!locationName.isEmpty()) {

//            ((ImageView) findViewById(R.id.img_location)).setImageDrawable(ContextCompat.getDrawable(RegistrationDetailsActivity.this,
//                    R.drawable.location_icon));
//
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
//
//            case R.id.img_location:
//            case R.id.edt_text_location:
//                if (edtInputLocation.getText().toString().isEmpty())
//                    turnOnLocation();
//                break;
//
            case R.id.toolbar_lft_img:

                onBackPressed();

                break;

//            case R.id.img_select_profile:
//                isRequiredFieldsFilled = false;
//                txtErrorProfile.setVisibility(View.GONE);
//                new ImagePicker.Builder(RegistrationDetailsActivity.this)
//                        .mode(ImagePicker.Mode.GALLERY)
//                        .directory(ImagePicker.Directory.DEFAULT)
//                        .extension(ImagePicker.Extension.JPG)
//                        .enableDebuggingMode(true)
//                        .build();
//                break;
//
            case img_profile:
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

            case R.id.btn_sign_up:
                //btnNext.setEnabled(false);

//                isRequiredFieldsFilled = AppUtils.isRequiredFieldsFilled(RegistrationDetailsActivity.this,new TextInputLayout[]{txtInputFirstName, txtInputLastName, txtInputLocation
//                                 /*, txtInputCompanyName, txtInputPhoneNo, txtInputAboutYou*/},
//                        getResources().getStringArray(R.array.registration_details_errors));

                isRequiredFieldsFilled = true;
                if (edtInputLocation.getText().toString().isEmpty()) {
                    isRequiredFieldsFilled = false;

                }

                if (imagePath == null && profilePicUrl.equals("")) {
                    isRequiredFieldsFilled = false;
                    txtErrorProfile.setVisibility(View.VISIBLE);
                }


                if (isRequiredFieldsFilled) {


                    if (ConnectivityController.isNetworkAvailable(RegistrationDetailsActivity.this)) {

                        callPutUserApi();
                    } else {
                        Toast.makeText(RegistrationDetailsActivity.this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    // AndroidUtils.showToast(RegistrationDetailsActivity.this, "Please fill");
                }

                break;
        }
    }

    private void callPutUserApi() {

       /* spotsDialog = new SpotsDialog(RegistrationDetailsActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);*/
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(0, location.getLongitude());
        coordinates.add(1, location.getLatitude());

        LongitudeLatitude longitudeLatitude = new LongitudeLatitude(coordinates, "Point");

        //  LongitudeLatitude longitudeLatitude = new LongitudeLatitude(location.getLongitude(), location.getLatitude());
        String cityName = AndroidUtils.getCityName(RegistrationDetailsActivity.this, location);
        String countryName = AndroidUtils.getCountryName(RegistrationDetailsActivity.this, location);
        String postalCode = AndroidUtils.getPostalCode(RegistrationDetailsActivity.this, location);
        String stateName = AndroidUtils.getState(RegistrationDetailsActivity.this, location);
        String longitude = String.valueOf(location.getLongitude());
        String Latitude = String.valueOf(location.getLatitude());
        if (postalCode.equals("")) {
            postalCode = "11111";
        }
        LocationRequestModel location = new LocationRequestModel(cityName, countryName, longitudeLatitude, postalCode, " ", stateName);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String completedRegistrationDate = df.format(new Date());

        String imgurl = "";
        if (imagePath != null){
            imgurl = imageUrl + OBJECT_KEY;
        }else if (!profilePicUrl.equals("")){
            imgurl = profilePicUrl;
        }
        if (imgurl.equals("")){
            return;
        }

        userPutRequest = new UserRegisterPutRequest(txtInputFirstNamme.getEditText().getText().toString().trim(), txtInputLastName.getEditText().getText().toString().trim(), location, imgurl, true, completedRegistrationDate);

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<ResponsePutUser> responsePutUserCall = apiService.putRegisterUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), userPutRequest);
        responsePutUserCall.enqueue(new Callback<ResponsePutUser>() {
            @Override
            public void onResponse(Call<ResponsePutUser> call, Response<ResponsePutUser> response) {
                           /* if (spotsDialog != null && spotsDialog.isShowing()) {
                                spotsDialog.cancel();
                            }*/

                if (response.code() == 200 && response.body().isSuccess()) {

                    editor.putBoolean(ConstantUtils.IS_ON_REGISTRATION_SCREEN, false).commit();
                    getUserDataApiCall();

                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(RegistrationDetailsActivity.this, "PutUser");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(RegistrationDetailsActivity.this);


                }
            }

            @Override
            public void onFailure(Call<ResponsePutUser> call, Throwable t) {
                Log.e("error", t.getLocalizedMessage());
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

        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

        if (taskList.get(0).numActivities > 2) {

            super.onBackPressed();

        } else {
            Intent intent = new Intent(RegistrationDetailsActivity.this, IntroActivity.class);
            editor.clear().commit();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

    }

    @Override
    public void onTextChange(String text, View view) {
        EditText editText = (EditText) view;


//        if (edtFirstName.hashCode() == editText.hashCode())
//            editor.putString(ConstantUtils.FIRST_NAME, text.trim());
//        if (edtLastName.hashCode() == editText.hashCode())
//            editor.putString(ConstantUtils.LAST_NAME, text.trim());
//        if (edtAboutYou.hashCode() == editText.hashCode())
//            editor.putString(ConstantUtils.ABOUT_YOU, text.trim());
//        if (edtPhoneNo.hashCode() == editText.hashCode())
//            editor.putString(ConstantUtils.PHONE_NUMBER, text.trim());
//        if (edtCompanyName.hashCode() == editText.hashCode())
//            editor.putString(ConstantUtils.COMPANY_NAME, text.trim());

        editor.commit();

        if (!text.trim().isEmpty()) {
            if(((TextInputLayout) editText.getParentForAccessibility())!=null) {
                ((TextInputLayout) editText.getParentForAccessibility()).setError(null);
                ((TextInputLayout) editText.getParentForAccessibility()).setErrorEnabled(false);
            }
        }
    }

    private void getUserDataApiCall() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);

        Call<Object> getUserInformation = apiService.getUserInformation(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        getUserInformation.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                if (response.code() == 200) {
                    try {

                        String json = new Gson().toJson(((LinkedTreeMap) response.body()).get("user"));
                        editor.putString(ConstantUtils.USER_DATA, json).commit();
                        JSONObject jsonObject = new JSONObject(json);


                        if (jsonObject.has("completedRegistration")) {
                            editor.putBoolean(ConstantUtils.IS_REGISTRATION_COMPLETED, (Boolean) jsonObject.get("completedRegistration")).commit();

                        }

                        Intent intent = new Intent(RegistrationDetailsActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {

                    // We will have to call login api as session is expired
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(RegistrationDetailsActivity.this, "callGetUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(RegistrationDetailsActivity.this);

                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("error", t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void onApiFail(String apiSource) {
        if (apiSource.equals("PutUser")) {

            callPutUserApi();
        } else if (apiSource.equals("callGetUserApi")) {

            getUserDataApiCall();
        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            persistImage(myBitmap, imagePath);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void persistImage(Bitmap bitmap, String name) {
        File filesDir = getBaseContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            ImgUploadOnAws();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
    }

}
