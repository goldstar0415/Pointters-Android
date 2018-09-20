package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.adapter.IntroViewPagerAdapter;
import com.pointters.model.request.UserEmailLoginRequest;
import com.pointters.model.response.GetUserSettingsResponse;
import com.pointters.model.response.UserEmailLoginResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.service.GetUserDataService;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.ConstantUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager viewPagerIntro;
    private TimerTask timerTask;
    private Timer timer;
    private SharedPreferences sharedPreferences;
    private TextView introText;
    int[] titleArray = {R.string.intro_1, R.string.intro_2, R.string.intro_3, R.string.intro_4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        AndroidUtils.getHashKey(this);
        introText = (TextView)findViewById(R.id.txt_intro);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(ConstantUtils.PREF_IS_LOGIN, false)) {

            if(sharedPreferences.getBoolean(ConstantUtils.IS_ON_REGISTRATION_SCREEN,false)) {
                startActivity(new Intent(this, RegistrationDetailsActivity.class));
                finish();

            } else {
                startActivity(new Intent(IntroActivity.this, HomeActivity.class));
                finish();
            }

        }

        setUpViewPager();

        setOnClick();

        AndroidUtils.getHashKey(this);

    }



    private void setOnClick() {

        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        findViewById(R.id.btn_email_login).setOnClickListener(this);
        findViewById(R.id.txt_skip).setOnClickListener(this);

    }


    private void setUpViewPager() {

        viewPagerIntro = (ViewPager) findViewById(R.id.view_pager_intro);
        int[] imgArray = {R.drawable.splash1, R.drawable.splash2, R.drawable.splash3, R.drawable.splash4};

        IntroViewPagerAdapter introViewPagerAdapter = new IntroViewPagerAdapter(IntroActivity.this, imgArray);
        viewPagerIntro.setAdapter(introViewPagerAdapter);
        //CircleIndicator indicatorViewPager = (CircleIndicator) findViewById(R.id.indicator_view_pager);
        CirclePageIndicator indicatorViewPager = (CirclePageIndicator) findViewById(R.id.indicator_view_pager);
        indicatorViewPager.setViewPager(viewPagerIntro);
        viewPagerIntro.addOnPageChangeListener(this);
    }

    @Override
    protected void onResume() {
     //   enableAutoScrollViewPager();

        super.onResume();
    }

    @Override
    protected void onStop() {

        if (timer != null) {
            timer.purge();
            timer.cancel();
        }

        if (timerTask != null) {
            timerTask.cancel();
        }

        super.onStop();
    }

    private void enableAutoScrollViewPager() {

        final Handler handler = new Handler();

        timerTask = new TimerTask() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int currentPage = viewPagerIntro.getCurrentItem();
                        viewPagerIntro.setCurrentItem(currentPage == 2 ? 0 : ++currentPage);
                    }
                });

            }
        };

        timer = new Timer();
        timer.schedule(timerTask, 2000, 2000);


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_sign_up:

                //starting registration activity
                startActivity(new Intent(this, RegistrationActivity.class));
                break;

            case R.id.btn_email_login:
                //starting login activity
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                break;

            case R.id.txt_skip:
                getSharedPreferences(ConstantUtils.APP_PREF, MODE_PRIVATE).edit()
                        .putBoolean(ConstantUtils.PREF_IS_LOGIN, false).apply();

                    startActivity(new Intent(this, HomeActivity.class));

                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        introText.setText(titleArray[position]);
    }

    @Override
    public void onPageSelected(int position) {
//        introText.setText(titleArray[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
