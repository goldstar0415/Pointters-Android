package com.pointters.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
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
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.location.LocationRequest;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.adapter.EditProfileImageViewPagerAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.model.Media;
import com.pointters.model.request.LocationRequestModel;
import com.pointters.model.request.LongitudeLatitude;
import com.pointters.model.request.UserPutRequest;
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
import net.alhazmy13.mediapicker.Video.VideoPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.pointters.utils.ConstantUtils.MY_BUCKET;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener, OnEditTextChangeListener {

    private AmazonS3 s3;
    private TextView textIndicator, txtChangeProfilePic;
    private RoundedImageView imgProfile;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String imagePath, cityName, countryName, stateName, postalCode, provinceName;
    private String OBJECT_KEY;
    private String imageUrl, backgroundMediaPath, filePath, fileUrl;
    private Button btnSave;
    private TextInputLayout firstNameTextInputLayout, lastNamTextInputLayout, companyTextInputLayout, aboutMeTextInputLayout, educationTextInputLayout, insuranceTextInputLayout, licencesTextInputLayout, awardsTextInputLayout, phoneTextInputLayout, locationTextInputLayout;
    private EditText firstNameEditText, lastNameEditText, companyEditText, aboutMeEditText, educationEditText, insuranceEditText, licencesEditText, awardsEditText, phoneEditText, locationEditText;
    private ViewPager viewpagerImages;
    private DisplayImageOptions options;
    private UserPutRequest userPutRequest;
    private TransferUtility transferUtility;
    private String json,uncompressedFilePath;
    private ImageView chooseBgImagesVideos;
    private boolean isRequiredFieldsFilled;
    private Boolean backgroundPicker = false;
    private ConstraintLayout layoutChooseBg;
    private ImageView imgCamera, imgDelete;
    private ArrayList<Media> bgFiles = new ArrayList<>();
    private EditProfileImageViewPagerAdapter editProfileImageViewPagerAdapter;
    private Double lat,lng;
    private FFmpeg ffmpeg;
    private KProgressHUD loader;

    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (!sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "").equals("")) {
            mUserLat = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "0"));
        }
        if (!sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "").equals("")) {
            mUserLng = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "0"));
        }

        loader = KProgressHUD.create(EditProfileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null)
            json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");


        //set toolbar
        AppUtils.setToolBarWithBothIcon(EditProfileActivity.this, getResources().getString(R.string.edit_profile), R.drawable.back_icon_grey, 0);
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{firstNameTextInputLayout, lastNamTextInputLayout, companyTextInputLayout, aboutMeTextInputLayout, educationTextInputLayout, insuranceTextInputLayout, licencesTextInputLayout, awardsTextInputLayout, phoneTextInputLayout
                , locationTextInputLayout});

        setOnClick();
        setUpViewPager();
        getUserDataApiCall();
        setEditTextListener();

        AWSCredentials basicAWSCredentials = new BasicAWSCredentials(ConstantUtils.AWS_ACCESS_KEY, ConstantUtils.AWS_SECRATE_KEY);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        s3 = new AmazonS3Client(basicAWSCredentials,clientConfig);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    private String generateFileName() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    private void setEditTextListener() {
        //Custom Edit text change listener with returning id of edit text
        firstNameEditText.addTextChangedListener(new MyTextWatcher(firstNameEditText, this));
        lastNameEditText.addTextChangedListener(new MyTextWatcher(lastNameEditText, this));
        locationEditText.addTextChangedListener(new MyTextWatcher(locationEditText, this));
    }

    private void getUserDataApiCall() {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("profilePic")) {
                if (jsonObject.get("profilePic") != null && !jsonObject.get("profilePic").toString().isEmpty()) {
                    imageUrl = jsonObject.get("profilePic").toString();

                    options = new DisplayImageOptions.Builder()
                            .showImageOnLoading(R.drawable.user_avatar_placeholder)
                            .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                            .showImageOnFail(R.drawable.user_avatar_placeholder)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .considerExifParams(true)
                            .build();

                    String profileUrl = "";
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
                    firstNameEditText.setText(jsonObject.get("firstName").toString());
            }

            if (jsonObject.has("lastName")) {
                if (jsonObject.get("lastName") != null && !jsonObject.get("lastName").toString().isEmpty())
                    lastNameEditText.setText(jsonObject.get("lastName").toString());
            }

            if (jsonObject.has("companyName")) {
                if (jsonObject.get("companyName") != null && !jsonObject.get("companyName").toString().isEmpty())
                    companyEditText.setText(jsonObject.get("companyName").toString());
            }

            if (jsonObject.has("description")) {
                if (jsonObject.get("description") != null && !jsonObject.get("description").toString().isEmpty())
                    aboutMeEditText.setText(jsonObject.get("description").toString());
            }

            if (jsonObject.has("education")) {
                if (jsonObject.get("education") != null && !jsonObject.get("education").toString().isEmpty())
                    educationEditText.setText(jsonObject.get("education").toString());
            }

            if (jsonObject.has("insurance")) {
                if (jsonObject.get("insurance") != null && !jsonObject.get("insurance").toString().isEmpty())
                    insuranceEditText.setText(jsonObject.get("insurance").toString());
            }

            if (jsonObject.has("license")) {
                if (jsonObject.get("license") != null && !jsonObject.get("license").toString().isEmpty())
                    licencesEditText.setText(jsonObject.get("license").toString());
            }

            if (jsonObject.has("awards")) {
                if (jsonObject.get("awards") != null && !jsonObject.get("awards").toString().isEmpty())
                    awardsEditText.setText(jsonObject.get("awards").toString());
            }

            if (jsonObject.has("phone")) {
                if (jsonObject.get("phone") != null && !jsonObject.get("phone").toString().isEmpty())
                    phoneEditText.setText(jsonObject.get("phone").toString());
            }

            if (jsonObject.has("profileBackgroundMedia")) {
                JSONArray jsonArray = new JSONArray(jsonObject.get("profileBackgroundMedia").toString());
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectBackgroundMedia = jsonArray.getJSONObject(i);

                    jsonObjectBackgroundMedia.get("mediaType");

                    Media media = new Media( jsonObjectBackgroundMedia.get("fileName").toString(), jsonObjectBackgroundMedia.get("mediaType").toString());
                    bgFiles.add(media);

                    if (bgFiles.size() == 4) {
                        imgCamera.setVisibility(View.INVISIBLE);
                    }
                }
                if (bgFiles.size() > 0) {
                    layoutChooseBg.setVisibility(View.GONE);
                    viewpagerImages.setVisibility(View.VISIBLE);
                    setUpViewPager();
                }
            }

            if (jsonObject.has("location")) {
                JSONObject jsonObjectLocation= (JSONObject)jsonObject.get("location");
                if (jsonObjectLocation.has("city") && jsonObjectLocation.has("country") && jsonObjectLocation.has("state")) {
                    if (jsonObjectLocation.get("city") != null && jsonObjectLocation.get("country") != null && jsonObjectLocation.get("state") != null && jsonObjectLocation.get("geoJson")!=null) {
                        cityName = jsonObjectLocation.get("city").toString();
                        provinceName = jsonObjectLocation.get("province").toString();
                        stateName = jsonObjectLocation.get("state").toString();
                        countryName = jsonObjectLocation.get("country").toString();
                        postalCode = jsonObjectLocation.get("postalCode").toString();
                        locationEditText.setText(postalCode + ", " + cityName + " " + stateName);

                        JSONObject jsonObject1 = (JSONObject) jsonObjectLocation.get("geoJson");
                        JSONArray jsonArrayCoordinates = new JSONArray(jsonObject1.get("coordinates").toString());
                        lat = Double.parseDouble(jsonArrayCoordinates.get(1).toString());
                        lng = Double.parseDouble(jsonArrayCoordinates.get(0).toString());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUpViewPager() {
        viewpagerImages = (ViewPager) findViewById(R.id.view_pager_images);
        editProfileImageViewPagerAdapter = new EditProfileImageViewPagerAdapter(EditProfileActivity.this, bgFiles);
        viewpagerImages.setAdapter(editProfileImageViewPagerAdapter);
        viewpagerImages.setPageMargin((int) getResources().getDimension(R.dimen._5sdp));

        int positionTxt = viewpagerImages.getCurrentItem() + 1;
        textIndicator.setText("" + positionTxt + " of " + bgFiles.size());
        viewpagerImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                int positionTxt = position + 1;
                textIndicator.setText(positionTxt + " of " + bgFiles.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void initViews() {
        textIndicator = (TextView) findViewById(R.id.textImageIndicator);
        imgCamera = (ImageView) findViewById(R.id.imageCamera);
        imgCamera.setVisibility(View.VISIBLE);
        imgDelete = (ImageView) findViewById(R.id.imageDelete);
        layoutChooseBg = (ConstraintLayout) findViewById(R.id.layout_choose_bg);
        chooseBgImagesVideos = (ImageView) findViewById(R.id.img_choose_bg_images);

        firstNameTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_first_name);
        lastNamTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_last_name);
        companyTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_company_name);
        aboutMeTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_about_me);
        educationTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_education);
        insuranceTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_insurance);
        licencesTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_license);
        awardsTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_awards);
        phoneTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_phone_number);
        locationTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_location);

        imgProfile = (RoundedImageView) findViewById(R.id.img_profile);
        txtChangeProfilePic = (TextView) findViewById(R.id.txt_change_profile);

        firstNameEditText = (EditText) findViewById(R.id.edt_first_name);
        lastNameEditText = (EditText) findViewById(R.id.edt_last_name);
        companyEditText = (EditText) findViewById(R.id.edt_company_name);
        aboutMeEditText = (EditText) findViewById(R.id.edt_about_me);
        educationEditText = (EditText) findViewById(R.id.edt_education);
        insuranceEditText = (EditText) findViewById(R.id.edt_insurance);
        licencesEditText = (EditText) findViewById(R.id.edt_license);
        awardsEditText = (EditText) findViewById(R.id.edt_awards);
        phoneEditText = (EditText) findViewById(R.id.edt_phone_number);
        locationEditText = (EditText) findViewById(R.id.edt_location);

        btnSave = (Button) findViewById(R.id.btn_save);
    }

    private void setOnClick() {
        imgDelete.setOnClickListener(this);
        imgCamera.setOnClickListener(this);
        txtChangeProfilePic.setOnClickListener(this);
        locationEditText.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        chooseBgImagesVideos.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageCamera:
                backgroundPicker = true;
                showDiag();
                break;
            case R.id.imageDelete:
                bgFiles.remove(viewpagerImages.getCurrentItem());
                setUpViewPager();
                if (bgFiles.isEmpty()) {
                    layoutChooseBg.setVisibility(View.VISIBLE);
                    viewpagerImages.setVisibility(View.GONE);
                }
                if (bgFiles.size() < 4)
                    imgCamera.setVisibility(View.VISIBLE);
                break;

            case R.id.toolbar_lft_img:
                onBackPressed();
                break;

            case R.id.txt_change_profile:
                backgroundPicker = false;
                new ImagePicker.Builder(EditProfileActivity.this)
                        .mode(ImagePicker.Mode.GALLERY)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .enableDebuggingMode(true)
                        .build();
                break;

            case R.id.img_choose_bg_images:
                backgroundPicker = true;
                showDiag();
                break;

            case R.id.edt_location:
                setUserLocation();
                break;

            case R.id.btn_save:
                if (ConnectivityController.isNetworkAvailable(EditProfileActivity.this)) {
                    isRequiredFieldsFilled = AppUtils.isRequiredFieldsFilled(EditProfileActivity.this,new TextInputLayout[]{firstNameTextInputLayout, lastNamTextInputLayout},
                            getResources().getStringArray(R.array.registration_details_errors));
                    if (isRequiredFieldsFilled) {
                        putUserApiCall();
                    }
                }
                break;

            default:
                break;
        }
    }

    private void setUserLocation() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(mUserLat, mUserLng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            lat = mUserLat;
            lng = mUserLng;
            cityName = addresses.get(0).getLocality();
            provinceName = addresses.get(0).getSubAdminArea();
            stateName = addresses.get(0).getAdminArea();
            countryName = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LocationRequestModel getUserLocation() {
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(0, lng);
        coordinates.add(1, lat);

        LongitudeLatitude longitudeLatitude = new LongitudeLatitude(coordinates, "Point");

        if (cityName == null) { cityName = ""; }
        if (provinceName == null) { provinceName = ""; }
        if (stateName == null) { stateName = ""; }
        if (countryName == null) { countryName = ""; }
        if (postalCode == null) { postalCode = ""; }

        return new LocationRequestModel(cityName, countryName, longitudeLatitude, postalCode, provinceName, stateName);
    }

    private void putUserApiCall() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String completedRegistrationDate = df.format(new Date());

        userPutRequest = new UserPutRequest();
        userPutRequest.setFirstName(firstNameEditText.getText().toString().trim());
        userPutRequest.setLastName(lastNameEditText.getText().toString().trim());
        userPutRequest.setDescription(aboutMeEditText.getText().toString().trim());
        userPutRequest.setCompanyName(companyEditText.getText().toString().trim());
        userPutRequest.setEducation(educationEditText.getText().toString().trim());
        userPutRequest.setInsurance(insuranceEditText.getText().toString().trim());
        userPutRequest.setLicense(licencesEditText.getText().toString().trim());
        userPutRequest.setAwards(awardsEditText.getText().toString().trim());
        userPutRequest.setPhone(phoneEditText.getText().toString());
        userPutRequest.setLocation(getUserLocation());
        userPutRequest.setProfilePic(imageUrl);
        userPutRequest.setProfileBackgroundMedia(bgFiles);
        userPutRequest.setCompletedRegistration(true);
        userPutRequest.setCompletedRegistrationDate(completedRegistrationDate);

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<ResponsePutUser> responsePutUserCall = apiService.putUser(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), userPutRequest);
        responsePutUserCall.enqueue(new Callback<ResponsePutUser>() {
            @Override
            public void onResponse(Call<ResponsePutUser> call, Response<ResponsePutUser> response) {
                if (response.code() == 200 && response.body().isSuccess()) {
                    Toast.makeText(EditProfileActivity.this, "Updated user profile successfully!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(EditProfileActivity.this, "PutUser");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(EditProfileActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ResponsePutUser> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Update Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ImgUploadOnAws() {
        transferUtility = new TransferUtility(s3, getApplicationContext());
        TransferObserver observer = transferUtility.upload(ConstantUtils.MY_BUCKET, OBJECT_KEY, new File(imagePath), CannedAccessControlList.PublicRead);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    //  AndroidUtils.showToast(EditProfileActivity.this, "Uploading finished");
                } else if (state.equals(TransferState.FAILED)) {
                    //AndroidUtils.showToast(RegistrationDetailsActivity.this, "Uploading failed please try again");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            @Override
            public void onError(int id, Exception ex) {}
        });
    }

    private void uploadFilesToAws(final String mediaType) {
        transferUtility = new TransferUtility(s3, getApplicationContext());
        TransferObserver observer = transferUtility.upload(ConstantUtils.MY_BUCKET, OBJECT_KEY, new File(filePath), CannedAccessControlList.PublicRead);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    loader.dismiss();
                    if (mediaType.equals(getResources().getString(R.string.image))) {
                        Media media=new Media(fileUrl,getResources().getString(R.string.image));
                        bgFiles.add(media);
                        layoutChooseBg.setVisibility(View.GONE);
                        viewpagerImages.setVisibility(View.VISIBLE);
                        setUpViewPager();
                    } else if (mediaType.equals(getResources().getString(R.string.video))) {
                        Media media=new Media(fileUrl,getResources().getString(R.string.video));
                        bgFiles.add(media);
                        layoutChooseBg.setVisibility(View.GONE);
                        viewpagerImages.setVisibility(View.VISIBLE);
                        setUpViewPager();
                    }

                    if (bgFiles.size() == 4) {
                        imgCamera.setVisibility(View.INVISIBLE);
                    }
                } else if (state.equals(TransferState.FAILED)) {
                    //AndroidUtils.showToast(RegistrationDetailsActivity.this, "Uploading failed please try again");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            @Override
            public void onError(int id, Exception ex) {}
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImagePicker.IMAGE_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
                    if (mPaths != null && mPaths.size() == 1) {
                        if (backgroundPicker) {
                            filePath = mPaths.get(0);
                            if (filePath != null) {
                                loader.show();
                                saveBitmapToFile(new File(filePath));
                                OBJECT_KEY = generateFileName();
                                fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
                                uploadFilesToAws(getResources().getString(R.string.image));
                            }
                        } else {
                            if (sharedPreferences.getString("keyName", "") != null && !sharedPreferences.getString("keyName", "").isEmpty()) { // DeleteAnObjectNonVersionedBucket(sharedPreferences.getString("keyName",""));
                                DeleteAnObjectNonVersionedBucket deleteAnObjectNonVersionedBucket = new DeleteAnObjectNonVersionedBucket();
                                deleteAnObjectNonVersionedBucket.execute(sharedPreferences.getString("keyName", ""));
                            }

                            imagePath = mPaths.get(0);
                            saveBitmapToFile(new File(imagePath));
                            Bitmap photo = BitmapFactory.decodeFile(imagePath);
                            imgProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imgProfile.setImageBitmap(photo);
                            OBJECT_KEY = generateFileName();
                            editor.putString("keyName", OBJECT_KEY).apply();
                            imageUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
                            ImgUploadOnAws();
                        }
                    }
                }
                break;

            case VideoPicker.VIDEO_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    uncompressedFilePath = (String) data.getSerializableExtra(VideoPicker.EXTRA_VIDEO_PATH);
                    if (uncompressedFilePath != null) {
                        MediaPlayer mp = MediaPlayer.create(EditProfileActivity.this, Uri.parse(uncompressedFilePath));
                        int duration = mp.getDuration();
                        mp.release();

                        if ((duration / 1000) > 10) {
                            AndroidUtils.showToast(EditProfileActivity.this, "Duration of video can not be exceed by 10 sec");
                        } else {
                            loader.show();
                            loadFFMpegBinary();
                            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compressed.mp4";
                            String[] command = {"-y", "-i", uncompressedFilePath, "-s", "640x480", "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
                            execFFmpegBinary(command);

                                /*OBJECT_KEY = generateFileName();
                                fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
                                uploadFilesToAws(getResources().getString(R.string.video));*/
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    //FFmpeg
    public void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                ffmpeg = FFmpeg.getInstance(EditProfileActivity.this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.d("ffmpeg4", "onFailure: ");
                    // showUnsupportedExceptionDialog();
                }

                @Override
                public void onSuccess() {
                    Log.d("ffmpeg4", "onSuccess: ");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            //  showUnsupportedExceptionDialog();
        } catch (Exception e) {
        }
    }


    public void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d("ffmpeg4", "finalfailed: " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Log.d("ffmpeg4", "finalSuccess: " + s);
                    //Perform action on success
                    OBJECT_KEY = generateFileName();
                    fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
                    uploadFilesToAws(getResources().getString(R.string.video));
                }

                @Override
                public void onFinish() {
                    Log.d("ffmpeg4", "finish: ");
                }

                @Override
                public void onProgress(String s) {
                }

                @Override
                public void onStart() {
                    Log.d("ffmpeg4", "start: ");
                }

            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.d("ffmpeg4", "eroro: " + e.getMessage());
        }
    }


    @Override
    public void onApiFail(String apiSource) {
        if (apiSource.equals("PutUser")) {
            getUserDataApiCall();
        }
    }

    @Override
    public void onTextChange(String text, View view) {
        EditText editText = (EditText) view;
        if (!text.trim().isEmpty()) {
            ((TextInputLayout) editText.getParentForAccessibility()).setError(null);
            ((TextInputLayout) editText.getParentForAccessibility()).setErrorEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
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
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            }

            return file;
        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
            return null;
        }
    }

    private void showDiag() {
        final View dialogView = View.inflate(EditProfileActivity.this, R.layout.dialog, null);
        final Dialog dialog = new Dialog(EditProfileActivity.this, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        final View viewDialog = dialogView.findViewById(R.id.dialog);
        RelativeLayout relativeLayoutFullPicker = (RelativeLayout) dialog.findViewById(R.id.layout_full_picker_title);
        RelativeLayout relativeLayoutDialog = (RelativeLayout) dialog.findViewById(R.id.layout_picker);
        LinearLayout linearImage = (LinearLayout) dialog.findViewById(R.id.linear_image);
        LinearLayout linearVideo = (LinearLayout) dialog.findViewById(R.id.linear_video);

        relativeLayoutFullPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogView, false, dialog);
                    return true;
                }
                return false;
            }
        });

        relativeLayoutDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do nothing
            }
        });

        linearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                viewDialog.setVisibility(View.INVISIBLE);

                new ImagePicker.Builder(EditProfileActivity.this)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .mode(ImagePicker.Mode.GALLERY)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .build();
            }
        });

        linearVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                viewDialog.setVisibility(View.INVISIBLE);

                new VideoPicker.Builder(EditProfileActivity.this)
                        .mode(VideoPicker.Mode.GALLERY)
                        .directory(VideoPicker.Directory.DEFAULT)
                        .extension(VideoPicker.Extension._MP4)
                        .build();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private void revealShow(View dialogView, boolean b, final Dialog dialog) {
        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();
        int endRadius = (int) Math.hypot(w, h);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int cy = height / 2;
        int cx = width / 2;

        if (b) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
                view.setVisibility(View.VISIBLE);
                revealAnimator.setDuration(700);
                revealAnimator.start();
            } else {
                view.setVisibility(View.VISIBLE);
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        dialog.dismiss();
                        view.setVisibility(View.INVISIBLE);
                    }
                });
                anim.setDuration(500);
                anim.start();
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    class DeleteAnObjectNonVersionedBucket extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                s3.deleteObject(MY_BUCKET, sharedPreferences.getString("keyName",""));
            } catch (AmazonServiceException ase) {
                System.out.println("Caught an AmazonServiceException.");
                System.out.println("Error Message:    " + ase.getMessage());
                System.out.println("HTTP Status Code: " + ase.getStatusCode());
                System.out.println("AWS Error Code:   " + ase.getErrorCode());
                System.out.println("Error Type:       " + ase.getErrorType());
                System.out.println("Request ID:       " + ase.getRequestId());
            } catch (AmazonClientException ace) {
                System.out.println("Caught an AmazonClientException.");
                System.out.println("Error Message: " + ace.getMessage());
            }
            return null;
        }
    }
}

