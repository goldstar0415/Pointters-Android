package com.pointters.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.activity.AddPriceOptionActivity;
import com.pointters.activity.ChooseCategoryActivity;
import com.pointters.activity.TagServiceActivity;
import com.pointters.adapter.AddFragmentsInCrosswallPagerAdapter;
import com.pointters.adapter.DeliveryMethodsRecyclerViewAdapter;
import com.pointters.adapter.PricingRvAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.CategoryModel;
import com.pointters.model.DeliveryMethod;
import com.pointters.model.FulfillmentMethodForCustom1;
import com.pointters.model.Media;
import com.pointters.model.Prices;
import com.pointters.model.request.AddServiceRequest;
import com.pointters.model.request.LocationRequestModel;
import com.pointters.model.request.LongitudeLatitude;
import com.pointters.model.response.GetCategoryResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CustomTabLayout;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.crosswall.lib.coverflow.CoverFlow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by prashantkumar on 18/9/17.
 */

public class AddServiceFragment extends Fragment implements View.OnClickListener, OnRecyclerViewItemClickListener, TabLayout.OnTabSelectedListener, OnApiFailDueToSessionListener, TextWatcher {

    public static final String APP_DIR = "VideoCompressor";
    public static final String COMPRESSED_VIDEOS_DIR = "/Compressed Videos/";
    public static final String TEMP_DIR = "/Temp/";

    private final int REQUEST_ADD_PRICE = 0;
    private final int REQUEST_EDIT_PRICE = 1;
    private final int REQUEST_CHOOSE_CATEGORY = 2;

    private View view;
    private Geocoder geocoder;
    private List<Address> addresses;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CustomTabLayout tabLayout;
    private RecyclerView recyclerViewDelivery, recyclerViewPricing;
    private ArrayList<DeliveryMethod> deliveryMethods;
    private DeliveryMethodsRecyclerViewAdapter deliveryMethodsRecyclerViewAdapter;
    private ArrayList<Prices> pricesList = new ArrayList<>();
    private PricingRvAdapter pricingRvAdapter;
    private ArrayList<Media> files = new ArrayList<>();
    private AddFragmentsInCrosswallPagerAdapter addFragmentsInCrosswallPagerAdapter;
    private RelativeLayout layoutCamera, layoutCrossWall, layoutChooseFromGallery, layoutChooseCategory;
    private EditText editServiceDesc;
    private String serviceDesc = "";
    private TextView addPriceBtn, txtTimer, txtChooseCategory, btnAdd;
    private CameraView cameraPreview;
    private Boolean IS_RECORDING_START = false;
    private CountDownTimer countDownTimer;
    private Boolean isCategory = false;
    private int groupPosition = -1, childPosition = -1;
    private ImageView takeImage;
    private ViewPager containerViewPager;
    private Runnable updateRecordingThread;
    private KProgressHUD loader;
    private String OBJECT_KEY, filePath, fileUrl, uncompressedfilePath;
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private BroadcastReceiver message;
    private Prices prices;
    private CategoryModel categoryModel;
    private FulfillmentMethodForCustom1 fulfillmentMethod;
    private Media media;
    private ArrayList<String> listDataHeader = new ArrayList<String>();
    private HashMap<String, List<CategoryModel>> listDataChild = new HashMap<String, List<CategoryModel>>();
    private GetCategoryResponse getCategoryResponse;
    private ArrayList<CategoryModel> childList;
    private String uncompressedFilePath;
    private FFmpeg ffmpeg;

    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;
    private int mServiceRadius = 15;
    private String currencyCode = "USD";
    private String currencySymbol = "$";


    public static void try2CreateCompressDir() {
        File f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + COMPRESSED_VIDEOS_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + TEMP_DIR);
        f.mkdirs();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_service, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (!sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "").equals("")) {
            mUserLat = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "0"));
        }
        if (!sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "").equals("")) {
            mUserLng = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "0"));
        }

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        initViews();
        setOnClickListners();
        prepareListData();

        tabLayout.addOnTabSelectedListener(this);

        loader = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        //Set Up Delivery method Recycler View
        deliveryMethods = new ArrayList<>();
        deliveryMethods.add(new DeliveryMethod("Online", false));
        deliveryMethods.add(new DeliveryMethod("Shipment", false));
        deliveryMethods.add(new DeliveryMethod("Local- anywhere in the city you service", true));
        deliveryMethods.add(new DeliveryMethod("Local- at your store locations", false));

        deliveryMethodsRecyclerViewAdapter = new DeliveryMethodsRecyclerViewAdapter(getActivity(), deliveryMethods, new OnRecyclerViewButtonClickListener(){
            @Override
            public void onButtonClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.img_checkbox:
                        setDeliveryMethod(position);
                        break;
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManagerDelivery = new LinearLayoutManager(getApplicationContext());
        recyclerViewDelivery.setLayoutManager(mLayoutManagerDelivery);
        recyclerViewDelivery.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDelivery.setAdapter(deliveryMethodsRecyclerViewAdapter);

        //Set Up Add Price Recycler View
        pricingRvAdapter = new PricingRvAdapter(getActivity(), pricesList, this);
        recyclerViewPricing.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPricing.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPricing.setAdapter(pricingRvAdapter);

        //Set Up Crosswall
        addFragmentsInCrosswallPagerAdapter = new AddFragmentsInCrosswallPagerAdapter(getChildFragmentManager(), files, getActivity(), "ADDSERVICE");
        containerViewPager.setAdapter(addFragmentsInCrosswallPagerAdapter);
        containerViewPager.setClipChildren(false);
        containerViewPager.setOffscreenPageLimit(addFragmentsInCrosswallPagerAdapter.getCount());

        if (files != null && files.size() > 0) {
            layoutChooseFromGallery.setVisibility(View.GONE);
            layoutCrossWall.setVisibility(View.VISIBLE);
        } else {
            layoutChooseFromGallery.setVisibility(View.VISIBLE);
            layoutCrossWall.setVisibility(View.GONE);
        }

        new CoverFlow.Builder()
                .with(containerViewPager)
                .pagerMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin))
                .scale(0.3f)
                .spaceSize(0f)
                .rotationY(0f)
                .build();

        //Set Up Aws S3 Bucket
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(ConstantUtils.AWS_ACCESS_KEY, ConstantUtils.AWS_SECRATE_KEY);
        s3 = new AmazonS3Client(basicAWSCredentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
        message = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                switch (intent.getAction()) {
                    case "delete":

                        if (files.size() > 0) {
                            files.remove(intent.getIntExtra(ConstantUtils.POSITION, 0));

                            allowAddService();
                            containerViewPager.setAdapter(null);
                            containerViewPager.setAdapter(addFragmentsInCrosswallPagerAdapter);
                            containerViewPager.setCurrentItem(addFragmentsInCrosswallPagerAdapter.getCount());
                            containerViewPager.setClipChildren(false);
                            containerViewPager.setOffscreenPageLimit(addFragmentsInCrosswallPagerAdapter.getCount());

                            new CoverFlow.Builder()
                                    .with(containerViewPager)
                                    .pagerMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin))
                                    .scale(0.3f)
                                    .spaceSize(0f)
                                    .rotationY(0f)
                                    .build();
                            if (files != null && files.size() == 1) {
                                layoutChooseFromGallery.setVisibility(View.VISIBLE);
                                layoutCrossWall.setVisibility(View.GONE);

                            } else {
                                layoutChooseFromGallery.setVisibility(View.GONE);
                                layoutCrossWall.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case "pick":
                        showDiag();
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("pick");
        intentFilter.addAction("delete");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(message, intentFilter);

        updateRecordingThread = new Runnable() {
            @Override
            public void run() {
                IS_RECORDING_START = false;
                takeImage.setImageResource(R.drawable.capture_button);
                cameraPreview.stopRecordingVideo();
            }

        };

        return view;
    }

    private void initViews() {
        //Tab Layout
        tabLayout = (CustomTabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("UPLOAD"));
        tabLayout.addTab(tabLayout.newTab().setText("SNAP PICTURE"));
        tabLayout.addTab(tabLayout.newTab().setText("TAKE VIDEO"));

        layoutChooseCategory = (RelativeLayout) view.findViewById(R.id.layout_choose_category);
        txtChooseCategory = (TextView) view.findViewById(R.id.txt_category);

        //Camera Layout
        layoutCamera = (RelativeLayout) view.findViewById(R.id.layout_camera);
        takeImage = (ImageView) view.findViewById(R.id.take_image);
        cameraPreview = (CameraView) view.findViewById(R.id.camera_preview);
        txtTimer = (TextView) view.findViewById(R.id.txt_timer);

        //Crosswall Layout
        layoutCrossWall = (RelativeLayout) view.findViewById(R.id.layout_crosswall);
        containerViewPager = (ViewPager) view.findViewById(R.id.container_viewpager);

        //Choose From Gallery Layout
        layoutChooseFromGallery = (RelativeLayout) view.findViewById(R.id.layout_choose_gallery);

        //Recycler views
        recyclerViewDelivery = (RecyclerView) view.findViewById(R.id.recycler_delivery_method);
        recyclerViewPricing = (RecyclerView) view.findViewById(R.id.recycler_view_pricing);

        editServiceDesc = (EditText) view.findViewById(R.id.edittext_service_description);
        editServiceDesc.addTextChangedListener(this);

        addPriceBtn = (TextView) view.findViewById(R.id.btn_add_price_button);
        btnAdd = (TextView) view.findViewById(R.id.btn_add);
    }

    private void setOnClickListners() {
        view.findViewById(R.id.img_choose_bg_images).setOnClickListener(this);
        takeImage.setOnClickListener(this);
        layoutChooseCategory.setOnClickListener(this);
        addPriceBtn.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }

    private void setDeliveryMethod(int position) {
        for (int i = 0; i < deliveryMethods.size(); i++) {
            if (i == position) {
                deliveryMethods.get(i).setSelected(true);
            } else {
                deliveryMethods.get(i).setSelected(false);
            }
        }

        if (position == 2) {
            View view = recyclerViewDelivery.getChildAt(position);
            EditText editMiles = (EditText) view.findViewById(R.id.edit_miles);
            String strMiles = editMiles.getText().toString();
            if (!strMiles.isEmpty() && Integer.parseInt(strMiles) > 0) {
                mServiceRadius = Integer.parseInt(strMiles);
            } else {
                mServiceRadius = 0;
            }
        } else {
            mServiceRadius = 0;
        }

        deliveryMethodsRecyclerViewAdapter.notifyDataSetChanged();
        allowAddService();
    }

    private void allowAddService() {
        if (!serviceDesc.equals("") && files.size() > 1 && pricesList.size() > 0 && isCategory) {
            if (deliveryMethods.get(2).isSelected()) {
                if (mServiceRadius > 0) {
                    btnAdd.setSelected(true);
                    btnAdd.setEnabled(true);
                } else {
                    btnAdd.setSelected(false);
                    btnAdd.setEnabled(false);
                }
            } else {
                btnAdd.setSelected(true);
                btnAdd.setEnabled(true);
            }
        } else {
            btnAdd.setSelected(false);
            btnAdd.setEnabled(false);
        }
    }

    private void uploadFilesToAws(final String mediaType) {
        transferUtility = new TransferUtility(s3, getApplicationContext());
        TransferObserver observer = transferUtility.upload(ConstantUtils.MY_BUCKET, OBJECT_KEY, new File(filePath), CannedAccessControlList.PublicRead);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    loader.dismiss();
                    if (!editServiceDesc.getText().toString().isEmpty() && pricesList.size() > 0 && !txtChooseCategory.getText().toString().equals(getResources().getString(R.string.choose_category))) {
                        btnAdd.setSelected(true);
                    } else {
                        btnAdd.setSelected(false);
                    }

                    if (files.size() == 0) {
                        media = new Media("", getResources().getString(R.string.image));
                        files.add(media);
                    }

                    if (mediaType.equals(getResources().getString(R.string.image))) {
                        media = new Media(fileUrl, getResources().getString(R.string.image));
                        files.add((files.size() - 1), media);
                    } else if (mediaType.equals(getResources().getString(R.string.video))) {
                        media = new Media(fileUrl, getResources().getString(R.string.video));
                        files.add((files.size() - 1), media);
                    }
                    containerViewPager.setAdapter(null);
                    // containerViewPager.setCurrentItem(files.size() - 1);

                    containerViewPager.setAdapter(addFragmentsInCrosswallPagerAdapter);
                    if (files.size() > 1)
                        containerViewPager.setCurrentItem(files.size() - 2);
                    containerViewPager.setClipChildren(false);
                    containerViewPager.setOffscreenPageLimit(addFragmentsInCrosswallPagerAdapter.getCount());
                    //  containerViewPager.setPageTransformer(true, new ZoomOutPageTransformer(0.3f,getResources().getDimensionPixelSize(R.dimen._minus40sdp),0f,0f));

                    new CoverFlow.Builder()
                            .with(containerViewPager)
                            .pagerMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin))
                            .scale(0.3f)
                            .spaceSize(0f)
                            .rotationY(0f)
                            .build();
                    if (files != null && files.size() > 0) {
                        layoutChooseFromGallery.setVisibility(View.GONE);
                        layoutCrossWall.setVisibility(View.VISIBLE);
                    } else {
                        layoutChooseFromGallery.setVisibility(View.VISIBLE);
                        layoutCrossWall.setVisibility(View.GONE);
                    }

                    if (tabLayout != null) {
                        tabLayout.getTabAt(0).select();
                    }
                    allowAddService();
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
    public void onClick(View v) {
        switch ((v.getId())) {
            case R.id.btn_add:
                loader.show();
                callAddServiceApi();
                break;

            case R.id.layout_choose_category:
                Intent intent = new Intent(getActivity(), ChooseCategoryActivity.class);
                intent.putStringArrayListExtra(ConstantUtils.CATEGORY_LIST_HEADRES, listDataHeader);
                intent.putExtra(ConstantUtils.CATEGORY_LIST_Child, listDataChild);
                intent.putExtra(ConstantUtils.CATEGORY_MODEL, categoryModel);
                intent.putExtra(ConstantUtils.GROUP_POSITION, groupPosition);
                intent.putExtra(ConstantUtils.CHILD_POSITION, childPosition);
                startActivityForResult(intent, REQUEST_CHOOSE_CATEGORY);
                break;

            case R.id.btn_add_price_button:
                startActivityForResult(new Intent(getActivity(), AddPriceOptionActivity.class), REQUEST_ADD_PRICE);
                break;

            case R.id.take_image:
                if (tabLayout.getSelectedTabPosition() == 1) {
                    // take snap and add in crosswall
                    capturePhoto();
                }
                else if (tabLayout.getSelectedTabPosition() == 2) {
                    if (IS_RECORDING_START) {
                        if (countDownTimer != null)
                            countDownTimer.cancel();
                        takeImage.setImageResource(R.drawable.capture_button);
                        IS_RECORDING_START = false;
                        cameraPreview.stopRecordingVideo();
                        // cameraPreview.removeCallbacks(updateRecordingThread);
                    }
                    else {
                        takeImage.setImageResource(R.drawable.video_capture_stop_button);
                        cameraPreview.startRecordingVideo();
                        IS_RECORDING_START = true;
                        takeImage.setVisibility(View.GONE);
                        CountDownTimer countDownTimerOneSec = new CountDownTimer(1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {}

                            @Override
                            public void onFinish() {
                                takeImage.setVisibility(View.VISIBLE);
                            }
                        }.start();

                        countDownTimer = new CountDownTimer(10000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtTimer.setText("" + millisUntilFinished / 1000 + " Secs");
                            }

                            @Override
                            public void onFinish() {
                                IS_RECORDING_START = false;
                                takeImage.setImageResource(R.drawable.capture_button);
                                cameraPreview.stopRecordingVideo();
                            }
                        }.start();
                        //   cameraPreview.postDelayed(updateRecordingThread, 10000);
                        captureVideo();
                    }
                }
                break;

            case R.id.img_choose_bg_images:
                showDiag();
                break;
        }
    }

    private void prepareListData() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetCategoryResponse> callGetCategoryApi = apiService.getCategories();
        callGetCategoryApi.enqueue(new Callback<GetCategoryResponse>() {
            @Override
            public void onResponse(Call<GetCategoryResponse> call, Response<GetCategoryResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    getCategoryResponse = response.body();
                    for (int i = 0; i < getCategoryResponse.getCategories().size(); i++) {
                        listDataHeader.add(getCategoryResponse.getCategories().get(i).getName());
                        childList = new ArrayList<CategoryModel>();
                        if (getCategoryResponse.getCategories().get(i).getSubCategories().size() > 0) {
                            for (int j = 0; j < getCategoryResponse.getCategories().get(i).getSubCategories().size(); j++) {
                                CategoryModel categoryModel = new CategoryModel(getCategoryResponse.getCategories().get(i).getSubCategories().get(j).get_id(), getCategoryResponse.getCategories().get(i).getSubCategories().get(j).getName());
                                childList.add(categoryModel);
                            }
                        }
                        listDataChild.put(listDataHeader.get(i), childList);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetCategoryResponse> call, Throwable t) {}
        });
    }

    // Capturing Images and Video taken from camera
    private void callAddServiceApi() {
        if (files.size() > 0) {
            files.remove(files.size() - 1);
        }

        //SetUp fulFillment method
        fulfillmentMethod = new FulfillmentMethodForCustom1(deliveryMethods.get(0).isSelected(), deliveryMethods.get(1).isSelected(), deliveryMethods.get(2).isSelected(), deliveryMethods.get(3).isSelected(), mServiceRadius, "mile");
        AddServiceRequest addServiceRequest = new AddServiceRequest(categoryModel, serviceDesc, fulfillmentMethod, files, pricesList);

        if (deliveryMethods.get(2).isSelected()) {
            try {
                List<Double> coordinates = new ArrayList<>();
                coordinates.add(0, mUserLng);
                coordinates.add(1, mUserLat);
                LongitudeLatitude longitudeLatitude = new LongitudeLatitude(coordinates, "Point");

                addresses = geocoder.getFromLocation(mUserLat, mUserLng, 1);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();

                LocationRequestModel location = new LocationRequestModel(city, country, longitudeLatitude, postalCode, state, state);
                ArrayList<LocationRequestModel> locations = new ArrayList<>();
                locations.add(location);
                addServiceRequest.setLocation(locations);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> addServiceRequestCall = apiService.postService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), addServiceRequest);
        addServiceRequestCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200) {
                    Toast.makeText(getActivity(), "Service added successfully!", Toast.LENGTH_SHORT).show();

                    serviceDesc = "";
                    editServiceDesc.setText("");

                    isCategory = false;
                    categoryModel = null;
                    groupPosition = -1;
                    childPosition = -1;
                    txtChooseCategory.setText(getResources().getString(R.string.choose_category));

                    setDeliveryMethod(2);

                    pricesList.clear();
                    pricingRvAdapter.notifyDataSetChanged();

                    files.clear();
                    containerViewPager.setAdapter(null);

                    layoutCrossWall.setVisibility(View.GONE);
                    layoutChooseFromGallery.setVisibility(View.VISIBLE);

                    allowAddService();
                } else {
                    Toast.makeText(getActivity(), "Adding service failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(getActivity(), "Adding service failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void capturePhoto() {
        loader.show();
        cameraPreview.captureImage();

        // Here is callback of snap taken
        cameraPreview.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);

                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                new fileFromBitmap(bitmap, getActivity()).execute();
            }
        });
    }

    void captureVideo() {
        // Here is callback of video taken
        cameraPreview.setCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                filePath = video.getAbsolutePath();
                OBJECT_KEY = generateFileName();
                fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
                if (cameraPreview != null) {
                    cameraPreview.stop();
                }
                loader.show();

                uploadFilesToAws(getResources().getString(R.string.video));
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        prices = new Prices(pricesList.get(position).getDescription(), pricesList.get(position).getPrice(), pricesList.get(position).getTime(), pricesList.get(position).getTimeUnitOfMeasure(), currencySymbol, currencyCode);
        Intent intent = new Intent(getActivity(), AddPriceOptionActivity.class);
        intent.putExtra(ConstantUtils.PRICE, prices);
        intent.putExtra(ConstantUtils.POSITION, String.valueOf(position));
        startActivityForResult(intent, REQUEST_EDIT_PRICE);
    }

    private void sendFile(File file) {
        saveBitmapToFile(file);

        filePath = file.getAbsolutePath();
        OBJECT_KEY = generateFileName();
        fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
        if (cameraPreview != null) {
            cameraPreview.stop();
        }

        uploadFilesToAws(getResources().getString(R.string.image));
    }

    //Dialog Box
    private void showDiag() {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog, null);

        final Dialog dialog = new Dialog(getActivity(), R.style.MyAlertDialogStyle);
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

                new ImagePicker.Builder(getActivity())
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

                new VideoPicker.Builder(getActivity())
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
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_PRICE:
                    prices = (Prices) data.getSerializableExtra(ConstantUtils.PRICE);
                    pricesList.add(prices);
                    pricingRvAdapter.notifyDataSetChanged();
                    allowAddService();
                    break;

                case REQUEST_EDIT_PRICE:
                    if (data.getSerializableExtra(ConstantUtils.DELETE).equals("yes")) {
                        pricesList.remove(Integer.parseInt(data.getStringExtra(ConstantUtils.POSITION)));
                        pricingRvAdapter.notifyDataSetChanged();

                    } else {
                        prices = (Prices) data.getSerializableExtra(ConstantUtils.PRICE);
                        pricesList.get(Integer.parseInt(data.getStringExtra(ConstantUtils.POSITION))).setDescription(prices.getDescription());
                        pricesList.get(Integer.parseInt(data.getStringExtra(ConstantUtils.POSITION))).setTimeUnitOfMeasure(prices.getTimeUnitOfMeasure());
                        pricesList.get(Integer.parseInt(data.getStringExtra(ConstantUtils.POSITION))).setTime(prices.getTime());
                        pricesList.get(Integer.parseInt(data.getStringExtra(ConstantUtils.POSITION))).setPrice(prices.getPrice());
                        pricingRvAdapter.notifyDataSetChanged();
                    }
                    allowAddService();
                    break;

                case REQUEST_CHOOSE_CATEGORY:
                    isCategory = true;
                    categoryModel = (CategoryModel) data.getSerializableExtra(ConstantUtils.CATEGORY_MODEL);
                    groupPosition = data.getIntExtra(ConstantUtils.GROUP_POSITION, -1);
                    childPosition = data.getIntExtra(ConstantUtils.CHILD_POSITION, -1);
                    txtChooseCategory.setText(categoryModel.getName().toString().toUpperCase());
                    allowAddService();
                    break;

                case ImagePicker.IMAGE_PICKER_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);

                        if (mPaths != null) {
                            loader.show();
                            filePath = mPaths.get(0);
                            saveBitmapToFile(new File(filePath));
                            OBJECT_KEY = generateFileName();
                            fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
                            uploadFilesToAws(getResources().getString(R.string.image));
                        }
                    }
                    break;

                case VideoPicker.VIDEO_PICKER_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        uncompressedFilePath = (String) data.getSerializableExtra(VideoPicker.EXTRA_VIDEO_PATH);

                        if (uncompressedFilePath != null) {
                            MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(uncompressedFilePath));
                            int duration = mp.getDuration();
                            mp.release();

                            if ((duration / 1000) > 10) {
                                AndroidUtils.showToast(getActivity(), "Duration of video can not be exceed by 10 sec");
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
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                if (files != null && files.size() > 0) {
                    layoutChooseFromGallery.setVisibility(View.GONE);
                    layoutCrossWall.setVisibility(View.VISIBLE);
                } else {
                    layoutChooseFromGallery.setVisibility(View.VISIBLE);
                    layoutCrossWall.setVisibility(View.GONE);
                }
                layoutCamera.setVisibility(View.GONE);

                if (IS_RECORDING_START) {
                    countDownTimer.cancel();
                    cameraPreview.stop();
                    cameraPreview.stopRecordingVideoWithRecording();
                    IS_RECORDING_START = false;
                }
                break;

            case 1:
                layoutCrossWall.setVisibility(View.GONE);
                layoutChooseFromGallery.setVisibility(View.GONE);
                layoutCamera.setVisibility(View.VISIBLE);
                txtTimer.setVisibility(View.GONE);
                takeImage.setImageResource(R.drawable.capture_button);

                if (IS_RECORDING_START) {
                    countDownTimer.cancel();
                    cameraPreview.stopRecordingVideoWithRecording();
                    IS_RECORDING_START = false;
                }

                tabLayout.getTabAt(1).select();
                if (cameraPreview != null && !cameraPreview.isStarted()) {
                    cameraPreview.start();
                }
                break;

            case 2:
                txtTimer.setText(10 + " Secs");
                layoutChooseFromGallery.setVisibility(View.GONE);
                layoutCamera.setVisibility(View.VISIBLE);
                txtTimer.setVisibility(View.VISIBLE);
                takeImage.setImageResource(R.drawable.capture_button);
                layoutCrossWall.setVisibility(View.GONE);

                if (cameraPreview != null && !cameraPreview.isStarted()) {
                    cameraPreview.start();
                }
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    private String generateFileName() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
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
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            }

            return file;

        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraPreview != null)
            cameraPreview.start();

        allowAddService();
    }

    @Override
    public void onPause() {
        if (cameraPreview != null) {
            //  cameraPreview.stop();
            if (IS_RECORDING_START) {
                if (countDownTimer != null)
                    countDownTimer.cancel();
                cameraPreview.stopRecordingVideoWithRecording();
                takeImage.setImageResource(R.drawable.capture_button);
                IS_RECORDING_START = false;
                txtTimer.setText("10 Secs");
            }
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraPreview != null) {
            cameraPreview.stop();
            if (IS_RECORDING_START) {
                if (countDownTimer != null)
                    countDownTimer.cancel();
                cameraPreview.stopRecordingVideoWithRecording();
            }
        }

        if (message != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(message);
    }

    @Override
    public void onApiFail(String apiSource) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        serviceDesc = String.valueOf(s);
        allowAddService();
    }

    public void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                ffmpeg = FFmpeg.getInstance(getActivity());
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

    //FFmpeg
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
    public void afterTextChanged(Editable s) {}

    @Override
    public void onStop() {
        super.onStop();
    }

    //Capture Image
    public class fileFromBitmap extends AsyncTask<Void, Integer, String> {
        Context context;
        Bitmap bitmap;
        File file;

        public fileFromBitmap(Bitmap bitmap, Context context) {
            this.bitmap = bitmap;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            file = new File(getActivity().getExternalFilesDir(null), "background_" + System.currentTimeMillis() + "_image.jpeg");

            try {
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            sendFile(file);
        }
    }
}
