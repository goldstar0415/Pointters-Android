package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pointters.R;
import com.pointters.adapter.IntroViewPagerAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        setUpViewPager();

        setOnClick();

    }

    private void setOnClick() {

        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.txt_skip).setOnClickListener(this);

    }


    private void setUpViewPager() {

        ViewPager viewPagerIntro = (ViewPager) findViewById(R.id.view_pager_intro);
        int[] imgArray = {R.drawable.slider_img_b, R.drawable.slider_img_b, R.drawable.slider_img_b};

        IntroViewPagerAdapter introViewPagerAdapter = new IntroViewPagerAdapter(IntroActivity.this, imgArray);
        viewPagerIntro.setAdapter(introViewPagerAdapter);
        viewPagerIntro.setPageMargin((int) getResources().getDimension(R.dimen._5sdp));

        CirclePageIndicator indicatorViewPager = (CirclePageIndicator) findViewById(R.id.indicator_view_pager);
        indicatorViewPager.setViewPager(viewPagerIntro);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_sign_up:

                //starting registration activity
                startActivity(new Intent(this, RegistrationActivity.class));
                break;

            case R.id.btn_login:
                //starting login activity
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                break;

            case R.id.txt_skip:

                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
