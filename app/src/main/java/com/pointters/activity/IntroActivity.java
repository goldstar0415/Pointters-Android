package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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

import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPagerIntro;
    private TimerTask timerTask;
    private Timer timer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        AndroidUtils.getHashKey(this);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);

        // For testing get request
        //callGetUserSettingsApi();

        if (sharedPreferences.getBoolean(ConstantUtils.PREF_IS_LOGIN, false)) {

            startActivity(new Intent(IntroActivity.this, HomeActivity.class));
            finish();

        }

        setUpViewPager();

        setOnClick();

        AndroidUtils.getHashKey(this);

    }


    private void callGetUserSettingsApi() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<GetUserSettingsResponse> response = apiService.getUserSettings("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU5ODI4N2EyMDUyYjA0MzNmMjc2MWVjZiIsImlhdCI6MTUwMjM0MzUyOCwiZXhwIjozMzAzODM0MzUyOH0.f3L2bF2-P1kgQPaIK9zo5BbywrIB2CuhXoOF1uqZUgY");

        response.enqueue(new Callback<GetUserSettingsResponse>() {
            @Override
            public void onResponse(Call<GetUserSettingsResponse> call, retrofit2.Response<GetUserSettingsResponse> rawResponse) {
                try {

                    //Getting response here....
                    if (rawResponse.code() == 200 && rawResponse.body() != null) {

                    } else if (rawResponse.code() == 401) {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetUserSettingsResponse> call, Throwable throwable) {

                Log.d("Splash", throwable.getMessage());

            }
        });

    }

    private void setOnClick() {

        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        findViewById(R.id.btn_email_login).setOnClickListener(this);
        findViewById(R.id.txt_skip).setOnClickListener(this);

    }


    private void setUpViewPager() {

        viewPagerIntro = (ViewPager) findViewById(R.id.view_pager_intro);
        int[] imgArray = {R.drawable.slider_img_b, R.drawable.slider_img_b, R.drawable.slider_img_b};

        IntroViewPagerAdapter introViewPagerAdapter = new IntroViewPagerAdapter(IntroActivity.this, imgArray);
        viewPagerIntro.setAdapter(introViewPagerAdapter);
        viewPagerIntro.setPageMargin((int) getResources().getDimension(R.dimen._5sdp));

        CirclePageIndicator indicatorViewPager = (CirclePageIndicator) findViewById(R.id.indicator_view_pager);
        indicatorViewPager.setViewPager(viewPagerIntro);

    }

    @Override
    protected void onResume() {
        enableAutoScrollViewPager();
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
}
