package com.pointters.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraView;
import com.pointters.R;
import com.pointters.adapter.HomeViewPagerAdapter;
import com.pointters.model.BottomTabSeletedModel;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.GPSTracker;
import com.pointters.utils.NonSwipeableViewPager;
import com.pointters.utils.SocketManager;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 28-Jul-17.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private final int CALL_PHONE_REQUEST = 3;
    private final int READ_STORAGE_REQUEST = 4;
    private final int WRITE_STORAGE_REQUEST = 5;

    private ImageView imgFlag;
    private ImageView imgService;
    private ImageView imgCamera;
    private ImageView imgChat;
    private ImageView imgProfile;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout imgFlagRelative;
    private RelativeLayout imgServiceRelative;
    private RelativeLayout imgCameraRelative;
    private RelativeLayout imgChatRelative;
    private RelativeLayout imgProfileRelative, rootLayout;
    private TextView txtCamera, txtFlag, txtExplore, txtChat, txtMe;
    private TextView searchBar;
    private List<BottomTabSeletedModel> bottomTabSeletedModels = new ArrayList<>(5);
    private NonSwipeableViewPager viewPager;
    private CameraView mCamera;
    private GPSTracker mTrackGPS;
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setUpToolBar();

        initViews();
        enablePermissions();
        SocketManager.getInstance().init(sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));

        setUpViewPager();

        setOnClick();
        bottomTabSeletedModels.add(0, new BottomTabSeletedModel(imgFlagRelative, txtFlag));
        bottomTabSeletedModels.add(1, new BottomTabSeletedModel(imgServiceRelative, txtExplore));
        bottomTabSeletedModels.add(2, new BottomTabSeletedModel(imgCameraRelative, txtCamera));
        bottomTabSeletedModels.add(3, new BottomTabSeletedModel(imgChatRelative, txtChat));
        bottomTabSeletedModels.add(4, new BottomTabSeletedModel(imgProfileRelative, txtMe));

        if (getIntent() != null && getIntent().getStringExtra(ConstantUtils.SOURCE) != null) {
            if (getIntent().getStringExtra(ConstantUtils.SOURCE).equals(ConstantUtils.MENU_SCREEN))
                viewPager.setCurrentItem(4);

        }

        updateSelectedPage(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpViewPager() {
        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(homeViewPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setCurrentItem(1);
        // imgCamera.setSelected(true);
        //txtCamera.setTextColor(getResources().getColor(R.color.color_blue));
    }

    private void setOnClick() {
        imgFlagRelative.setOnClickListener(this);
        imgServiceRelative.setOnClickListener(this);
        imgCameraRelative.setOnClickListener(this);
        imgChatRelative.setOnClickListener(this);
        imgProfileRelative.setOnClickListener(this);
        // searchBar.setOnClickListener(this);
    }

    private void initViews() {
        //searchBar = (TextView) findViewById(R.id.txt_search_here_hint);
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        viewPager = (NonSwipeableViewPager) findViewById(R.id.view_pager);
        imgFlag = (ImageView) findViewById(R.id.img_flag);
        imgService = (ImageView) findViewById(R.id.img_service);
        imgCamera = (ImageView) findViewById(R.id.img_camera);
        imgChat = (ImageView) findViewById(R.id.img_chat);
        imgProfile = (ImageView) findViewById(R.id.img_user_profile);

        imgFlagRelative = (RelativeLayout) findViewById(R.id.img_flag_relative);
        imgServiceRelative = (RelativeLayout) findViewById(R.id.img_service_relative);
        imgCameraRelative = (RelativeLayout) findViewById(R.id.img_camera_relative);
        imgChatRelative = (RelativeLayout) findViewById(R.id.img_chat_relative);
        imgProfileRelative = (RelativeLayout) findViewById(R.id.img_user_relative);

        txtCamera = (TextView) findViewById(R.id.txt_camera);
        txtExplore = (TextView) findViewById(R.id.txt_service);
        txtChat = (TextView) findViewById(R.id.txt_chat);
        txtMe = (TextView) findViewById(R.id.txt_me);
        txtFlag = (TextView) findViewById(R.id.txt_flag);
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_flag_relative:
               // viewPager.setCurrentItem(0);
               // updateSelectedPage(0);

                break;

            case R.id.img_service_relative:
                updateSelectedPage(1);
                viewPager.setCurrentItem(1);
                break;

            case R.id.img_camera_relative:
                //   updateSelectedPage(2);
                //   txtCamera.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.color_blue));
                startActivity(new Intent(this, AddServiceActivity.class));

                break;
            case R.id.img_chat_relative:
                updateSelectedPage(3);
                viewPager.setCurrentItem(3);
                break;


            case R.id.img_user_relative:
                 updateSelectedPage(4);
                if (sharedPreferences.getBoolean(ConstantUtils.PREF_IS_LOGIN, false)) {
                    viewPager.setCurrentItem(4);
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    // finish();
                }


                break;
        }
    }

    private void updateSelectedPage(int position) {

        for (int i = 0; i < bottomTabSeletedModels.size(); i++) {

            if (position == i) {
                bottomTabSeletedModels.get(i).getRelativeLayout().setSelected(true);
                if(i!=4)
                bottomTabSeletedModels.get(i).getTextView().setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.color_blue));
            } else {
                bottomTabSeletedModels.get(i).getRelativeLayout().setSelected(false);
                bottomTabSeletedModels.get(i).getTextView().setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.bottom_bar_text_color));
            }

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updateSelectedPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        if (sharedPreferences.getBoolean(ConstantUtils.PREF_IS_LOGIN, true)) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            } else {
                this.doubleBackToExitPressedOnce = true;
                Snackbar snackbar = Snackbar.make(rootLayout, "Please click BACK again to exit", Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackground(ContextCompat.getDrawable(HomeActivity.this,R.color.colorAccent));
                snackbar.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GPSTracker.PERMISSION_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enablePermissions();
                }
                break;

            case READ_STORAGE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enablePermissions();
                }
                break;

            case WRITE_STORAGE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enablePermissions();
                }
                break;

            case CALL_PHONE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enablePermissions();
                }
                break;

            default:
                break;
        }
    }

    private boolean checkLocationPermission() {
        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, GPSTracker.PERMISSION_LOCATION);
            return false;

        }
        return true;
    }

    private boolean checkPhoneCallPermission() {
        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( this, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST);
            return false;

        } else {
            return true;
        }
    }

    private boolean checkExternalReadStoragePermission() {
        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_REQUEST);
            return false;

        } else {
            return true;
        }
    }

    public boolean checkExternalStoragePermission() {
        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_REQUEST);
            return false;

        } else {
            return true;
        }
    }

    private void enablePermissions() {
        if (!checkExternalReadStoragePermission()) {
            return;
        }
        if (!checkExternalStoragePermission()) {
            return;
        }
        if (!checkPhoneCallPermission()) {
            return;
        }
        if (checkLocationPermission()) {
            mTrackGPS = new GPSTracker(this);
            if (mTrackGPS.canGetLocation()) {
                String latitude = String.valueOf(mTrackGPS.getLatitude());
                String longitude = String.valueOf(mTrackGPS.getLongitude());

                editor.putString(ConstantUtils.USER_LATITUDE, latitude).apply();
                editor.putString(ConstantUtils.USER_LONGITUDE, longitude).apply();
            } else {
                mTrackGPS.showSettingsAlert();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().disconnectSocket();
    }
}
