package com.pointters.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.AddFragmentsInCrosswallPagerAdapter;
import com.pointters.adapter.DeliveryMethodsRecyclerViewAdapter;
import com.pointters.adapter.MediaAdapter;
import com.pointters.adapter.PricingRvAdapter;
import com.pointters.adapter.TransactionCarouselAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.AddressModel;
import com.pointters.model.CategoryModel;
import com.pointters.model.DeliveryMethod;
import com.pointters.model.FulfillmentMethod;
import com.pointters.model.FulfillmentMethodForCustom1;
import com.pointters.model.Media;
import com.pointters.model.ParcelModel;
import com.pointters.model.Prices;
import com.pointters.model.ServiceInfoModel;
import com.pointters.model.request.AddServiceRequest;
import com.pointters.model.request.LocationRequestModel;
import com.pointters.model.request.LongitudeLatitude;
import com.pointters.model.response.GetCategoryResponse;
import com.pointters.model.response.GetServiceByIdResponse;
import com.pointters.model.response.GetServiceInfoResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.CommonUtils;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CustomTabLayout;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.crosswall.lib.coverflow.CoverFlow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by mac on 2/1/18.
 */

public class EditServiceDetailActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener, OnApiFailDueToSessionListener, DiscreteScrollView.OnItemChangedListener {

    public static final String APP_DIR = "VideoCompressor";
    public static final String COMPRESSED_VIDEOS_DIR = "/Compressed Videos/";
    public static final String TEMP_DIR = "/Temp/";
    private final int CAMERA_IMAGE_REQUEST = 30;
    private final int CAMERA_VIDEO_REQUEST = 40;
    private final int PERMISSION_REQUEST_CAMERA = 16;
    private final int REQUEST_SHIPPING_DETAIL = 3;

    private final int REQUEST_ADD_PRICE = 0;
    private final int REQUEST_EDIT_PRICE = 1;

    private Geocoder geocoder;
    private List<Address> addresses;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CustomTabLayout tabLayout;
    private RecyclerView recyclerMedia;
    private RecyclerView recyclerViewPricing;
    private ArrayList<DeliveryMethod> deliveryMethods;
    private DeliveryMethodsRecyclerViewAdapter deliveryMethodsRecyclerViewAdapter;
    private ArrayList<Prices> pricesList = new ArrayList<>();
    private PricingRvAdapter pricingRvAdapter;
    private ArrayList<Media> files = new ArrayList<>();

    private EditText editServiceDesc;
    private String serviceDesc = "";
//    private TextView addPriceBtn, txtTimer, txtChooseCategory, btnEdit;
    private Boolean IS_RECORDING_START = false;
    private Boolean isCategory = false;
    private KProgressHUD loader;
    private String OBJECT_KEY, filePath, fileUrl, uncompressedfilePath;
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private BroadcastReceiver message;
    private Prices prices;
    private ParcelModel parcelModel;
    private AddressModel addressModel;
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
    private int mServiceRadius = 25;
    private String currencyCode = "USD";
    private String currencySymbol = "$";

    private EditText edtServiceDescription;
    private ImageButton btnGallery, btnCamera, btnVideo;

    private ImageButton check1, check2, check3, check4;
    private RelativeLayout layoutShipping, layoutRadius;
    private Button btnRadius;

    Button btnAddPriceOption, btnAddService;
    public String serviceId = "";
    public String categoryId = "";
    private MediaAdapter adapter;
    String cameraImageFilePath = "";
    String cameraVideoFilePath = "";

    public static void try2CreateCompressDir() {
        File f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + COMPRESSED_VIDEOS_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + TEMP_DIR);
        f.mkdirs();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_service_details);
        Intent intent = getIntent();
        if (intent.getStringExtra("serviceId") == null) {
            categoryId = intent.getStringExtra("categoryId");
            AppUtils.setToolBarWithBothIcon(EditServiceDetailActivity.this, "Add Service Detail", R.drawable.back_icon, 0);
        }else{
            serviceId = intent.getStringExtra("serviceId");
            AppUtils.setToolBarWithBothIcon(EditServiceDetailActivity.this, "Edit Service", R.drawable.back_icon, 0);
            if (!serviceId.equals("")){
//                loader.show();
//                callGetServiceInfoApi(serviceId);
            }
        }
        if (intent.getStringExtra("categoryId") != null) {
            categoryId = intent.getStringExtra("categoryId");
            String categoryName = intent.getStringExtra("category");
            categoryModel = new CategoryModel(categoryId, categoryName);
        }
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        fulfillmentMethod = new FulfillmentMethodForCustom1();
        if (!sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "").equals("")) {
            mUserLat = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "0"));
        }
        if (!sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "").equals("")) {
            mUserLng = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "0"));
        }
        loader = KProgressHUD.create(EditServiceDetailActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);


        initViews();
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

                            allowUpdateService();
                            if (files != null && files.size() == 1) {
//                                layoutChooseFromGallery.setVisibility(View.VISIBLE);
//                                layoutCrossWall.setVisibility(View.GONE);

                            } else {
//                                layoutChooseFromGallery.setVisibility(View.GONE);
//                                layoutCrossWall.setVisibility(View.VISIBLE);
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
        LocalBroadcastManager.getInstance(EditServiceDetailActivity.this).registerReceiver(message, intentFilter);

    }

    private void initViews() {


        btnAddPriceOption = (Button) findViewById(R.id.btn_add_price_option);
        btnAddPriceOption.setOnClickListener(this);
        edtServiceDescription = (EditText) findViewById(R.id.edt_service_description);
        btnGallery = (ImageButton) findViewById(R.id.btn_gallery);
        btnCamera = (ImageButton) findViewById(R.id.btn_camera);
        btnVideo = (ImageButton) findViewById(R.id.btn_video);
        btnAddService = (Button) findViewById(R.id.btn_add_service);
        btnAddService.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnVideo.setOnClickListener(this);

        check1 = (ImageButton) findViewById(R.id.check_button1);
        check1.setOnClickListener(mRadioClickListener);
        check2 = (ImageButton) findViewById(R.id.check_button2);
        check2.setOnClickListener(mRadioClickListener);
        check3 = (ImageButton) findViewById(R.id.check_button3);
        check3.setOnClickListener(mRadioClickListener);
        check4 = (ImageButton) findViewById(R.id.check_button4);
        check4.setOnClickListener(mRadioClickListener);

        layoutShipping = (RelativeLayout) findViewById(R.id.layout_shipping);
        layoutShipping.setOnClickListener(this);
        layoutRadius = (RelativeLayout) findViewById(R.id.layout_radius);
        layoutRadius.setOnClickListener(this);

        btnRadius = (Button) findViewById(R.id.button_miles);
        btnRadius.setText(String.format("%d miles", fulfillmentMethod.getLocalServiceRadius()));
        btnRadius.setOnClickListener(this);
        layoutRadius.setVisibility(View.GONE);
        layoutShipping.setVisibility(View.GONE);
        recyclerViewPricing = (RecyclerView) findViewById(R.id.recycler_view_pricing);
        recyclerMedia = (RecyclerView) findViewById(R.id.recycler_media);

        recyclerMedia.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new MediaAdapter(getBaseContext(), files);
        recyclerMedia.setAdapter(adapter);
        adapter.setListener(new OnRecyclerViewButtonClickListener() {
            @Override
            public void onButtonClick(View v, int position) {
                files.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
//        recyclerMedia.setOrientation(DSVOrientation.HORIZONTAL);
//        recyclerMedia.setItemTransitionTimeMillis(150);
//        recyclerMedia.addOnItemChangedListener(this);
//        recyclerMedia.setItemTransformer(new ScaleTransformer.Builder().setMinScale(1).build());
        initRecycleViews();
        edtServiceDescription.requestFocus();
        edtServiceDescription.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (edtServiceDescription.getText().toString().equals("")) {
                    InputMethodManager keyboard = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        },200);

    }

    View.OnClickListener mRadioClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check_button1:
                    setDeliveryMethod(0);
                    break;
                case R.id.check_button2:
                    setDeliveryMethod(1);
                    break;
                case R.id.check_button3:
                    setDeliveryMethod(2);
                    break;
                case R.id.check_button4:
                    setDeliveryMethod(3);
                    break;
                default:
                    break;
            }
        }
    };



    private void showDiag() {
        final View dialogView = View.inflate(EditServiceDetailActivity.this, R.layout.dialog, null);

        final Dialog dialog = new Dialog(EditServiceDetailActivity.this, R.style.MyAlertDialogStyle);
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

                new ImagePicker.Builder(EditServiceDetailActivity.this)
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

                new VideoPicker.Builder(EditServiceDetailActivity.this)
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
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

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
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_add_service:
                CallAddServiceApi();
                break;
            case R.id.btn_add_price_option:
                Intent intent = new Intent(EditServiceDetailActivity.this, AddPriceOptionActivity.class);
                startActivityForResult(intent, REQUEST_ADD_PRICE);
                break;
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;

            case R.id.btn_gallery:
                showDiag(false);
                break;

            case R.id.btn_camera:
                if (checkCameraPermission()) {
                    Intent pictureIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    if(pictureIntent.resolveActivity(getPackageManager()) != null){
                        //Create a file to store the image
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(EditServiceDetailActivity.this,"com.pointters.androiddevelopment.provider", photoFile);
                            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(pictureIntent, CAMERA_IMAGE_REQUEST);
                        }
                    }
                }
                break;

            case R.id.btn_video:
                if (checkCameraPermission()) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                    startActivityForResult(cameraIntent, CAMERA_VIDEO_REQUEST);
                }
                break;

            case R.id.layout_radius:
                showRadiusDialog();
                break;

            case R.id.button_miles:
                showRadiusDialog();
                break;

            case R.id.layout_shipping:
                Intent intent1 = new Intent(EditServiceDetailActivity.this, AddShippingDetailActivity.class);
                Gson gson = new Gson();
                intent1.putExtra(ConstantUtils.ADD_ADDRESS, gson.toJson(fulfillmentMethod.getAddress()));
                intent1.putExtra(ConstantUtils.ADD_PARCEL, gson.toJson(fulfillmentMethod.getParcel()));
                startActivityForResult(intent1, REQUEST_SHIPPING_DETAIL);
                break;

        }

    }

    public void CallAddServiceApi(){
//        if (files.size() > 0) {
//            files.remove(files.size() - 1);
//        }
        serviceDesc = edtServiceDescription.getText().toString();
        //SetUp fulFillment method
//        fulfillmentMethod = new FulfillmentMethodForCustom1(deliveryMethods.get(0).isSelected(), deliveryMethods.get(1).isSelected(), deliveryMethods.get(2).isSelected(), deliveryMethods.get(3).isSelected(), mServiceRadius, "mile");
        AddServiceRequest addServiceRequest = new AddServiceRequest(categoryModel, serviceDesc, fulfillmentMethod, files, pricesList);

        loader.show();
        if (fulfillmentMethod.getShipment()) {
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
                fulfillmentMethod.setAddress(addressModel);
                fulfillmentMethod.setParcel(parcelModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> updateServiceRequestCall = apiService.postService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), addServiceRequest);
        updateServiceRequestCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200) {
                    Toast.makeText(EditServiceDetailActivity.this, "Service Added successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra(ConstantUtils.EDIT_SERVICE_DELETE, "no");
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Log.e("Error: ", response.errorBody().toString());
                    Toast.makeText(EditServiceDetailActivity.this, "adding service failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(EditServiceDetailActivity.this, "adding service failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void showRadiusDialog(){
        int selectedIndex = fulfillmentMethod.getLocalServiceRadius() / 5 - 1;
        new MaterialDialog.Builder(this)
                .title("Choose Radius")
                .items(R.array.radius)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        String radius = text.toString();
                        mServiceRadius = Integer.valueOf(radius.replace(" miles", ""));
                        btnRadius.setText(text);
                        fulfillmentMethod.setLocalServiceRadius(Integer.parseInt(((String)text).replace(" miles", "")));
                        fulfillmentMethod.setLocalServiceRadiusUom("mile");
                        return true;
                    }
                })
                .positiveText("OK")
                .negativeText("CANCEL")
                .show();

    }

    private void showDiag(final Boolean isCamera) {
        final View dialogView = View.inflate(EditServiceDetailActivity.this, R.layout.dialog, null);

        final Dialog dialog = new Dialog(EditServiceDetailActivity.this, R.style.MyAlertDialogStyle);
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

                if (isCamera) {
                    Intent pictureIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    if(pictureIntent.resolveActivity(getPackageManager()) != null){
                        //Create a file to store the image
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(EditServiceDetailActivity.this,"com.pointters.androiddevelopment.provider", photoFile);
                            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(pictureIntent, CAMERA_IMAGE_REQUEST);
                        }
                    }
//                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST);
                } else {
                    new ImagePicker.Builder(EditServiceDetailActivity.this)
                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                            .mode(ImagePicker.Mode.GALLERY)
                            .directory(ImagePicker.Directory.DEFAULT)
                            .extension(ImagePicker.Extension.JPG)
                            .allowMultipleImages(false)
                            .enableDebuggingMode(true)
                            .build();
                }
            }
        });

        linearVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                viewDialog.setVisibility(View.INVISIBLE);

                if (isCamera) {
                    Intent videoIntent = new Intent(
                            MediaStore.ACTION_VIDEO_CAPTURE);
                    if(videoIntent.resolveActivity(getPackageManager()) != null){
                        //Create a file to store the image
                        File videoFile = null;
                        try {
                            videoFile = createVideoFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        if (videoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(EditServiceDetailActivity.this,"com.pointters.androiddevelopment.provider", videoFile);
                            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                            startActivityForResult(videoIntent, CAMERA_VIDEO_REQUEST);
                        }
                    }
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                    startActivityForResult(cameraIntent, CAMERA_VIDEO_REQUEST);
                } else {
                    new VideoPicker.Builder(EditServiceDetailActivity.this)
                            .mode(VideoPicker.Mode.GALLERY)
                            .directory(VideoPicker.Directory.DEFAULT)
                            .extension(VideoPicker.Extension._MP4)
                            .build();
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkCameraPermission()) {
                    showDiag(true);
                }
            }
        }
    }

    private boolean checkCameraPermission() {
        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( EditServiceDetailActivity.this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditServiceDetailActivity.this, new String[] {Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            return false;

        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_IMAGE_REQUEST:
                    if (resultCode == RESULT_OK) {
                        if (!cameraImageFilePath.equals("")) {
                            loader.show();
                            filePath = cameraImageFilePath;
                            saveBitmapToFile(new File(filePath));
                            OBJECT_KEY = generateFileName();
                            fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
                            uploadFilesToAws(getResources().getString(R.string.image));
                        }
                    }
                    break;

                case CAMERA_VIDEO_REQUEST:
                    if (resultCode == RESULT_OK) {
                        Uri video;
                        if (data.getData() != null)
                            video = data.getData();
                        else if (data.getDataString() != null) {
                            String path = data.getDataString();
                            video = Uri.parse(path);
                        } else {
                            return;
                        }

                        loader.show();
                        uncompressedFilePath = CommonUtils.getVideoFilePathFromURI(EditServiceDetailActivity.this, video);
                        loadFFMpegBinary();
                        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compressed.mp4";
                        String[] command = {"-y", "-i", uncompressedFilePath, "-s", "640x480", "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
                        execFFmpegBinary(command);                    }
                    break;
                case REQUEST_SHIPPING_DETAIL:
                    Gson gson = new Gson();
                    String strAddress = data.getStringExtra(ConstantUtils.ADD_ADDRESS);
                    addressModel = gson.fromJson(strAddress, AddressModel.class);
                    String strParcel = data.getStringExtra(ConstantUtils.ADD_PARCEL);
                    parcelModel = gson.fromJson(strParcel, ParcelModel.class);
                    fulfillmentMethod.setAddress(addressModel);
                    fulfillmentMethod.setParcel(parcelModel);
                    allowUpdateService();
                    break;
                case REQUEST_ADD_PRICE:
                    prices = (Prices) data.getSerializableExtra(ConstantUtils.PRICE);
                    pricesList.add(prices);
                    pricingRvAdapter.setPricesList(pricesList);
                    pricingRvAdapter.notifyItemInserted(pricesList.size() - 1);
                    allowUpdateService();
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
                    allowUpdateService();
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
                            MediaPlayer mp = MediaPlayer.create(EditServiceDetailActivity.this, Uri.parse(uncompressedFilePath));
                            int duration = mp.getDuration();
                            mp.release();

                            if ((duration / 1000) > 10) {
                                AndroidUtils.showToast(EditServiceDetailActivity.this, "Duration of video can not be exceed by 10 sec");
                            } else {
                                loader.show();
                                loadFFMpegBinary();
                                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compressed.mp4";
                                String[] command = {"-y", "-i", uncompressedFilePath, "-s", "640x480", "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
                                execFFmpegBinary(command);
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void allowUpdateService() {
        btnAddService.setEnabled(true);
//        if (!serviceDesc.equals("") && files.size() > 0 && pricesList.size() > 0 && isCategory) {
//            if (fulfillmentMethod.getOnline() || fulfillmentMethod.getStore()) {
//                btnAddService.setEnabled(true);
//            } else if (fulfillmentMethod.getShipment()) {
//                if (fulfillmentMethod.getParcel() != null) {
//                    btnAddService.setEnabled(true);
//                }
//            } else if (fulfillmentMethod.getLocal()) {
//                if (fulfillmentMethod.getLocalServiceRadius() > 0) {
//                    btnAddService.setEnabled(true);
//                }
//            }
//        }
    }

    private void uploadFilesToAws(final String mediaType) {
        transferUtility = new TransferUtility(s3, getBaseContext());
        TransferObserver observer = transferUtility.upload(ConstantUtils.MY_BUCKET, OBJECT_KEY, new File(filePath), CannedAccessControlList.PublicRead);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    loader.dismiss();
                      if (mediaType.equals(getResources().getString(R.string.image))) {
                        media = new Media(fileUrl, getResources().getString(R.string.image));
                        files.add(media);
                    } else if (mediaType.equals(getResources().getString(R.string.video))) {
                        media = new Media(fileUrl, getResources().getString(R.string.video));
                        files.add(media);
                    }
                    adapter.setData(files);
//                    infiniteScrollAdapter = InfiniteScrollAdapter.wrap(adapter);
//                    infiniteScrollAdapter.notifyDataSetChanged();
//                    recyclerMedia.scrollToPosition(files.size() - 1);
//
                    allowUpdateService();
                } else if (state.equals(TransferState.FAILED)) {
                    AndroidUtils.showToast(EditServiceDetailActivity.this, "Uploading failed please try again");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.e("progress: ", String.valueOf(bytesCurrent));
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error: ", String.valueOf(ex.getLocalizedMessage()));
                loader.dismiss();
            }
        });
    }
    public void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                ffmpeg = FFmpeg.getInstance(EditServiceDetailActivity.this);
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
                ExifInterface ei = new ExifInterface(file.getAbsolutePath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(resizedBitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(resizedBitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(resizedBitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = resizedBitmap;
                }

                // override resized bitmap image
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            }

            return file;

        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
            return null;
        }
    }
    public File saveBitmapToFile(Bitmap bitmap) {
        File file = new File("pointters_capture_image"+generateFileName());
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 6;

            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            if (originalWidth > 160) {

                int reqWidth = 640;
                int reqHeight = (reqWidth * originalHeight) / originalWidth;

                // decode full image pre-resized
                options = new BitmapFactory.Options();

                // calc rought re-size (this is no exact resize)
                options.inSampleSize = Math.max(originalWidth / reqWidth, originalHeight / reqHeight);

                // calc exact destination size
                Matrix m = new Matrix();
                RectF inRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
                RectF outRect = new RectF(0, 0, reqWidth, reqHeight);
                m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
                float[] values = new float[9];
                m.getValues(values);

                // resize bitmap
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * values[0]), (int) (bitmap.getHeight() * values[4]), true);

                // override resized bitmap image
                FileOutputStream out = new FileOutputStream(file);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            }

            return file;

        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
            return null;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        cameraImageFilePath = image.getAbsolutePath();
        return image;
    }

    private File createVideoFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "VIDEO_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        cameraVideoFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onResume() {
        super.onResume();
        allowUpdateService();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (message != null)
            LocalBroadcastManager.getInstance(EditServiceDetailActivity.this).unregisterReceiver(message);
    }

    @Override
    public void onApiFail(String apiSource) {
        Log.e("failed upload;", apiSource);
        if (loader.isShowing()) {
            loader.dismiss();
        }
    }

    @Override
    public void onItemClick(int position) {
        prices = pricesList.get(position);
        Intent intent = new Intent(EditServiceDetailActivity.this, AddPriceOptionActivity.class);
        intent.putExtra(ConstantUtils.PRICE, prices);
        intent.putExtra(ConstantUtils.POSITION, String.valueOf(position));
        startActivityForResult(intent, REQUEST_EDIT_PRICE);
    }

    private void setServiceInfo(ServiceInfoModel service) {
        if (service.getCategory() != null && service.getCategory().getName() != null && !service.getCategory().getName().isEmpty()) {
            isCategory = true;
            categoryModel = service.getCategory();
//            txtChooseCategory.setText(service.getCategory().getName().toUpperCase());
        }

        if (service.getDescription() != null && !service.getDescription().isEmpty()) {
            serviceDesc = service.getDescription();
            editServiceDesc.setText(serviceDesc);
        }

        if (service.getMedia() != null && service.getMedia().size() > 0) {
            files.clear();
            files.addAll(service.getMedia());
            media = new Media("", getResources().getString(R.string.image));
            files.add(media);
        }

        if (service.getPrices() != null && service.getPrices().size() > 0) {
            pricesList.addAll(service.getPrices());
        }

        if (service.getFulfillmentMethod() != null) {
            if (service.getFulfillmentMethod().getOnline() != null && !service.getFulfillmentMethod().getOnline().isEmpty() && service.getFulfillmentMethod().getOnline().equals("true")) {
                setDeliveryMethod(0);
            } else if (service.getFulfillmentMethod().getShipment() != null && !service.getFulfillmentMethod().getShipment().isEmpty() && service.getFulfillmentMethod().getShipment().equals("true")) {
                setDeliveryMethod(1);
            } else if (service.getFulfillmentMethod().getLocal() != null && !service.getFulfillmentMethod().getLocal().isEmpty() && service.getFulfillmentMethod().getLocal().equals("true")) {
                setDeliveryMethod(2);
            } else if (service.getFulfillmentMethod().getStore() != null && !service.getFulfillmentMethod().getStore().isEmpty() && service.getFulfillmentMethod().getStore().equals("true")) {
                setDeliveryMethod(3);
            } else {
                setDeliveryMethod(2);
            }
        }

    }

    private void setDeliveryMethod(int index) {

        fulfillmentMethod.setLocal(false);
        fulfillmentMethod.setOnline(false);
        fulfillmentMethod.setShipment(false);
        fulfillmentMethod.setStore(false);
        fulfillmentMethod.setLocalServiceRadius(mServiceRadius);
        fulfillmentMethod.setLocalServiceRadiusUom("miles");
        check1.setSelected(false);
        check2.setSelected(false);
        check3.setSelected(false);
        check4.setSelected(false);
        layoutRadius.setVisibility(View.GONE);
        layoutShipping.setVisibility(View.GONE);
        switch (index) {
            case 0:
                check1.setSelected(true);
                fulfillmentMethod.setOnline(true);
                allowUpdateService();
                break;
            case 1:
                check2.setSelected(true);
                fulfillmentMethod.setShipment(true);
                layoutShipping.setVisibility(View.VISIBLE);
                allowUpdateService();
                break;
            case 2:
                check3.setSelected(true);
                fulfillmentMethod.setLocal(true);
                layoutRadius.setVisibility(View.VISIBLE);
                allowUpdateService();
                break;
            case 3:
                check4.setSelected(true);
                fulfillmentMethod.setStore(true);
                allowUpdateService();
                break;
            default:
                break;
        }

        allowUpdateService();
    }

    private void initRecycleViews() {
        //Set Up Add Price Recycler View
        pricingRvAdapter = new PricingRvAdapter(EditServiceDetailActivity.this, pricesList, this);
        recyclerViewPricing.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPricing.setLayoutManager(new LinearLayoutManager(EditServiceDetailActivity.this));
        recyclerViewPricing.setAdapter(pricingRvAdapter);

        if (files != null && files.size() > 0) {
//            layoutChooseFromGallery.setVisibility(View.GONE);
//            layoutCrossWall.setVisibility(View.VISIBLE);
        } else {
//            layoutChooseFromGallery.setVisibility(View.VISIBLE);
//            layoutCrossWall.setVisibility(View.GONE);
        }


    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {

    }
    /*
    private void setOnClickListners() {
        findViewById(R.id.img_choose_bg_images).setOnClickListener(this);
        takeImage.setOnClickListener(this);
        layoutChooseCategory.setOnClickListener(this);
        addPriceBtn.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        tabLayout.addOnTabSelectedListener(this);
    }

    private void setServiceInfo(ServiceInfoModel service) {
        if (service.getCategory() != null && service.getCategory().getName() != null && !service.getCategory().getName().isEmpty()) {
            isCategory = true;
            categoryModel = service.getCategory();
            txtChooseCategory.setText(service.getCategory().getName().toUpperCase());
        }

        if (service.getDescription() != null && !service.getDescription().isEmpty()) {
            serviceDesc = service.getDescription();
            editServiceDesc.setText(serviceDesc);
        }

        if (service.getMedia() != null && service.getMedia().size() > 0) {
            files.clear();
            files.addAll(service.getMedia());

            media = new Media("", getResources().getString(R.string.image));
            files.add(media);

            containerViewPager.setAdapter(null);
            containerViewPager.setAdapter(addFragmentsInCrosswallPagerAdapter);
        }

        if (service.getPrices() != null && service.getPrices().size() > 0) {
            pricesList.addAll(service.getPrices());
        }

        if (service.getFulfillmentMethod() != null) {
            if (service.getFulfillmentMethod().getOnline() != null && !service.getFulfillmentMethod().getOnline().isEmpty() && service.getFulfillmentMethod().getOnline().equals("true")) {
                setDeliveryMethod(0);
            } else if (service.getFulfillmentMethod().getShipment() != null && !service.getFulfillmentMethod().getShipment().isEmpty() && service.getFulfillmentMethod().getShipment().equals("true")) {
                setDeliveryMethod(1);
            } else if (service.getFulfillmentMethod().getLocal() != null && !service.getFulfillmentMethod().getLocal().isEmpty() && service.getFulfillmentMethod().getLocal().equals("true")) {
                setDeliveryMethod(2);
            } else if (service.getFulfillmentMethod().getStore() != null && !service.getFulfillmentMethod().getStore().isEmpty() && service.getFulfillmentMethod().getStore().equals("true")) {
                setDeliveryMethod(3);
            } else {
                setDeliveryMethod(2);
            }
        }

        initRecycleViews();
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
        allowUpdateService();
    }

    private void allowUpdateService() {
        if (!serviceDesc.equals("") && files.size() > 1 && pricesList.size() > 0 && isCategory) {
            if (deliveryMethods.get(2).isSelected()) {
                if (mServiceRadius > 0) {
                    btnEdit.setSelected(true);
                    btnEdit.setEnabled(true);
                } else {
                    btnEdit.setSelected(false);
                    btnEdit.setEnabled(false);
                }
            } else {
                btnEdit.setSelected(true);
                btnEdit.setEnabled(true);
            }
        } else {
            btnEdit.setSelected(false);
            btnEdit.setEnabled(false);
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
                        btnEdit.setSelected(true);
                    } else {
                        btnEdit.setSelected(false);
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
                    allowUpdateService();
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
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;
            case R.id.toolbar_right_img:
                loader.show();
                callDeleteServiceApi(serviceId);
                break;
            case R.id.btn_edit:
                loader.show();
                callUpdateServiceApi(serviceId);
                break;

            case R.id.layout_choose_category:
                Intent intent = new Intent(EditServiceDetailActivity.this, ChooseCategoryActivity.class);
                intent.putStringArrayListExtra(ConstantUtils.CATEGORY_LIST_HEADRES, listDataHeader);
                intent.putExtra(ConstantUtils.CATEGORY_LIST_Child, listDataChild);
                intent.putExtra(ConstantUtils.CATEGORY_MODEL, categoryModel);
                intent.putExtra(ConstantUtils.GROUP_POSITION, groupPosition);
                intent.putExtra(ConstantUtils.CHILD_POSITION, childPosition);
                startActivityForResult(intent, REQUEST_CHOOSE_CATEGORY);
                break;

            case R.id.btn_add_price_button:
                startActivityForResult(new Intent(EditServiceDetailActivity.this, AddPriceOptionActivity.class), REQUEST_ADD_PRICE);
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

    private void callGetServiceInfoApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetServiceInfoResponse> getServiceRequestCall = apiService.getServiceInfo(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        getServiceRequestCall.enqueue(new Callback<GetServiceInfoResponse>() {
            @Override
            public void onResponse(Call<GetServiceInfoResponse> call, Response<GetServiceInfoResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getService() != null) {
                        setServiceInfo(response.body().getService());
                    } else {
                        Toast.makeText(EditServiceDetailActivity.this, "No service info", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(EditServiceDetailActivity.this, "callServiceInfoApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(EditServiceDetailActivity.this);
                }
            }

            @Override
            public void onFailure(Call<GetServiceInfoResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(EditServiceDetailActivity.this, "No service info", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void callUpdateServiceApi(String id) {
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
        Call<Object> updateServiceRequestCall = apiService.updateService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), addServiceRequest, id);
        updateServiceRequestCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200) {
                    Toast.makeText(EditServiceDetailActivity.this, "Service updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra(ConstantUtils.EDIT_SERVICE_DELETE, "no");
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(EditServiceDetailActivity.this, "Updating service failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(EditServiceDetailActivity.this, "Updating service failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callDeleteServiceApi(String id) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> delServiceRequestCall = apiService.deleteService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        delServiceRequestCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200) {
                    Toast.makeText(EditServiceDetailActivity.this, "Service deleted successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra(ConstantUtils.EDIT_SERVICE_DELETE, "yes");
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(EditServiceDetailActivity.this, "Deleting service failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(EditServiceDetailActivity.this, "Deleting service failed!", Toast.LENGTH_SHORT).show();
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
                new fileFromBitmap(bitmap, EditServiceDetailActivity.this).execute();
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
        Intent intent = new Intent(EditServiceDetailActivity.this, AddPriceOptionActivity.class);
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
        final View dialogView = View.inflate(EditServiceDetailActivity.this, R.layout.dialog, null);

        final Dialog dialog = new Dialog(EditServiceDetailActivity.this, R.style.MyAlertDialogStyle);
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

                new ImagePicker.Builder(EditServiceDetailActivity.this)
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

                new VideoPicker.Builder(EditServiceDetailActivity.this)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_PRICE:
                    prices = (Prices) data.getSerializableExtra(ConstantUtils.PRICE);
                    pricesList.add(prices);
                    pricingRvAdapter.notifyDataSetChanged();
                    allowUpdateService();
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
                    allowUpdateService();
                    break;

                case REQUEST_CHOOSE_CATEGORY:
                    isCategory = true;
                    categoryModel = (CategoryModel) data.getSerializableExtra(ConstantUtils.CATEGORY_MODEL);
                    groupPosition = data.getIntExtra(ConstantUtils.GROUP_POSITION, -1);
                    childPosition = data.getIntExtra(ConstantUtils.CHILD_POSITION, -1);
                    txtChooseCategory.setText(categoryModel.getName().toString().toUpperCase());
                    allowUpdateService();
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
                            MediaPlayer mp = MediaPlayer.create(EditServiceDetailActivity.this, Uri.parse(uncompressedFilePath));
                            int duration = mp.getDuration();
                            mp.release();

                            if ((duration / 1000) > 10) {
                                AndroidUtils.showToast(EditServiceDetailActivity.this, "Duration of video can not be exceed by 10 sec");
                            } else {
                                loader.show();
                                loadFFMpegBinary();
                                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compressed.mp4";
                                String[] command = {"-y", "-i", uncompressedFilePath, "-s", "640x480", "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
                                execFFmpegBinary(command);
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

        allowUpdateService();
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
            LocalBroadcastManager.getInstance(EditServiceDetailActivity.this).unregisterReceiver(message);
    }

    @Override
    public void onApiFail(String apiSource) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        serviceDesc = String.valueOf(s);
        allowUpdateService();
    }

    public void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                ffmpeg = FFmpeg.getInstance(EditServiceDetailActivity.this);
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
            file = new File(getExternalFilesDir(null), "background_" + System.currentTimeMillis() + "_image.jpeg");

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
    */
}

