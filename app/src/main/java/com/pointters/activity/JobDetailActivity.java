package com.pointters.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.ExploreJobsModel;
import com.pointters.model.Media;
import com.pointters.model.response.GetServiceByIdResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mac on 12/25/17.
 */

public class JobDetailActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private final int REQUEST_CHECK_SETTINGS = 1000;
    private final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 2000;
    Calendar calendar = Calendar.getInstance();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private LinearLayout layoutCategory;
    private LinearLayout layoutLocation;
    private RecyclerView recyclerMedia;
    private TextView txtTitle, txtCategory, txtLocation;
    private EditText txtDesc;
    private Button btnScheduleTime, btnMinPrice, btnMaxPrice;
    private Button sendOfferButton, chatButton;

    private KProgressHUD loader;

    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String offer_id = "";
    private ExploreJobsModel offerInfo;
    private List<Double> service_pos = new ArrayList<>();

    private String dateString, timeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

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

        initUI();

        offer_id = getIntent().getStringExtra(ConstantUtils.SELECT_JOB_ID);
        if (!offer_id.equals("")) {
            loader.show();
            callGEtJobDetail(offer_id);
        } else {
            Toast.makeText(this, "Invalid offer!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initUI() {
        txtTitle = (TextView) findViewById(R.id.toolbar_title);
        txtTitle.setText(R.string.job_detail);

        findViewById(R.id.toolbar_right_img).setVisibility(View.GONE);
        findViewById(R.id.toolbar_lft_img).setOnClickListener(this);
        recyclerMedia = (RecyclerView) findViewById(R.id.recycler_media);
        txtCategory = (TextView) findViewById(R.id.txt_category_name);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        btnMinPrice = (Button) findViewById(R.id.btn_min_price);
        btnMaxPrice = (Button) findViewById(R.id.btn_max_price);
        btnScheduleTime = (Button) findViewById(R.id.btn_schedule);
        txtCategory = (TextView) findViewById(R.id.txt_category_name);
        txtDesc = (EditText) findViewById(R.id.edt_description);
        txtDesc.setFocusable(false);
        txtDesc.setFocusableInTouchMode(true);
        sendOfferButton = (Button) findViewById(R.id.btn_send_offer);
        chatButton = (Button) findViewById(R.id.btn_chat);
        layoutCategory = (LinearLayout)findViewById(R.id.category_view);
        layoutLocation = (LinearLayout)findViewById(R.id.location_view);

        layoutCategory.setOnClickListener(this);
        layoutLocation.setOnClickListener(this);
        btnScheduleTime.setOnClickListener(this);

    }

    @SuppressLint({"DefaultLocale", "SimpleDateFormat"})
    private void setJobDetail() {
        txtDesc.setText(offerInfo.getDescription());
        btnMinPrice.setText(String.format("$%.2f", Float.valueOf(offerInfo.getMinPrice())));
        btnMaxPrice.setText(String.format("$%.2f", Float.valueOf(offerInfo.getMaxPrice())));
        txtLocation.setText(offerInfo.getLocation().FullAddress());
        txtCategory.setText(offerInfo.getCategory().getName());
        ArrayList<Media> mediaArrayList = offerInfo.getMedia();
        ImageAdapter adapter = new ImageAdapter(getBaseContext(), mediaArrayList);
        recyclerMedia.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerMedia.setAdapter(adapter);
        adapter.setActivity(this);
        if (offerInfo.getOfferSent() != null) {
            sendOfferButton.setText("EDIT OFFER");
        }else{
            sendOfferButton.setText("SEND OFFER");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date date = sdf.parse(offerInfo.getScheduleDate());
            if (date != null) {
                sdf = new SimpleDateFormat("d MMM yyyy h:mm a");
                btnScheduleTime.setText(sdf.format(date));
            }else{

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void callGEtJobDetail(final String offerId) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<ExploreJobsModel> customOfferDetailsCall = apiService.getJobsDetail(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), offerId);
        customOfferDetailsCall.enqueue(new Callback<ExploreJobsModel>() {
            @Override
            public void onResponse(Call<ExploreJobsModel> call, Response<ExploreJobsModel> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    offerInfo = response.body();
                    if (offerInfo.getOfferSent() != null) {
                    } else {
                    }
                    setJobDetail();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(JobDetailActivity.this, "callCustomOfferDetailsApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(JobDetailActivity.this);
                }
                else if (response.code() == 404) {
                    Toast.makeText(JobDetailActivity.this, "Can't get the offer info!", Toast.LENGTH_SHORT).show();
//                    finish();
                }
            }

            @Override
            public void onFailure(Call<ExploreJobsModel> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(JobDetailActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
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
                    } else {
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(JobDetailActivity.this, "callServiceByIdApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(JobDetailActivity.this);
                }
                else if (response.code() == 404) {
                    Toast.makeText(JobDetailActivity.this, "No linked service info!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetServiceByIdResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(JobDetailActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;
            case R.id.btn_schedule:
                Calendar now = Calendar.getInstance();
                if (datePickerDialog == null) {
                    datePickerDialog = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                }else{
                    datePickerDialog.initialize(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                }
                datePickerDialog.setThemeDark(false);
                datePickerDialog.vibrate(true);
                datePickerDialog.dismissOnPause(true);
                datePickerDialog.showYearPickerFirst(true);
                datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
                datePickerDialog.setAccentColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
                datePickerDialog.show(getFragmentManager(), "Date");
                break;
            case R.id.edt_time:
                Calendar now1 = Calendar.getInstance();
                if (timePickerDialog == null) {
                    timePickerDialog = TimePickerDialog.newInstance(this, now1.get(Calendar.HOUR_OF_DAY), now1.get(Calendar.MINUTE), false);
                }else{
                    timePickerDialog.initialize(this, now1.get(Calendar.HOUR_OF_DAY), now1.get(Calendar.MINUTE), now1.get(Calendar.SECOND), false);
                }
                timePickerDialog.setThemeDark(false);
                timePickerDialog.vibrate(true);
                timePickerDialog.dismissOnPause(true);
                timePickerDialog.enableSeconds(true);
                timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
                timePickerDialog.setAccentColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
                timePickerDialog.show(getFragmentManager(), "Time");
                break;

            case R.id.location_view:
                if (offerInfo.getLocation() != null){
                    String directionUrl = "http://maps.apple.com/?saddr=" + String.valueOf(mUserLat) + "," + String.valueOf(mUserLng) + "&daddr=" + String.valueOf(offerInfo.getLocation().getGeoJson().getCoordinates().get(1)) + "," + String.valueOf(offerInfo.getLocation().getGeoJson().getCoordinates().get(0));
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(directionUrl));
                    startActivity(browserIntent);
                }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(JobDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //Show permission explanation dialog...
                        } else {
                            Toast.makeText(JobDetailActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                switch (resultCode) {
                    case RESULT_OK:
                        break;

                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm a");
        dateString = dateFormat.format(calendar.getTime());
        btnScheduleTime.setText(dateString);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        Calendar now1 = Calendar.getInstance();
        if (timePickerDialog == null) {
            timePickerDialog = TimePickerDialog.newInstance(this, now1.get(Calendar.HOUR_OF_DAY), now1.get(Calendar.MINUTE), false);
        }else{
            timePickerDialog.initialize(this, now1.get(Calendar.HOUR_OF_DAY), now1.get(Calendar.MINUTE), now1.get(Calendar.SECOND), false);
        }
        timePickerDialog.setThemeDark(false);
        timePickerDialog.vibrate(true);
        timePickerDialog.dismissOnPause(true);
        timePickerDialog.enableSeconds(true);
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
        timePickerDialog.setAccentColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        timePickerDialog.show(getFragmentManager(), "Time");

    }


    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private ArrayList<Media> medias = new ArrayList<>();
        private Context context;
        private JobDetailActivity activity;

        public ImageAdapter(Context context, ArrayList<Media> android_versions) {
            this.context = context;
            this.medias = android_versions;

        }

        public void setActivity(JobDetailActivity activity){
            this.activity = activity;
        }

        @Override
        public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fulfillment_details_list,
                    viewGroup, false);
            return new ImageAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageAdapter.ViewHolder viewHolder, int i) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.photo_placeholder)
                    .showImageForEmptyUri(R.drawable.photo_placeholder)
                    .showImageOnFail(R.drawable.photo_placeholder)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
            viewHolder.txtView.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(medias.get(i).getFileName(), viewHolder.img_rv, options);
            viewHolder.img_rv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, PhotoViewerActivity.class);
                    intent.putExtra(ConstantUtils.CHAT_TAP_PHOTO, medias.get(i).getFileName());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return medias.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img_rv;
            TextView txtView;
            public ViewHolder(View view) {
                super(view);
                img_rv = (ImageView) view.findViewById(R.id.mUserDpOfFulfillment);
                txtView = (TextView) view.findViewById(R.id.txt_detail);
            }
        }
    }
}
