package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pointters.R;
import com.pointters.adapter.SuggestedCategoriesProfileAdapter;
import com.pointters.utils.AppUtils;
import com.viewpagerindicator.CirclePageIndicator;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by prashantkumar on 16/8/17.
 */

public class ProfileScreenActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        setUpSuggestedCategoryViewPager();

        AppUtils.setToolBarWithBothIcon(ProfileScreenActivity.this, getResources().getString(R.string.profile),
                R.drawable.back_icon_grey, R.drawable.more_icon_horizontal);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void setUpSuggestedCategoryViewPager() {

        ViewPager viewPagerSuggestedServices = (ViewPager)findViewById(R.id.view_pager_suggested_services);
        SuggestedCategoriesProfileAdapter suggestedCategoriesProfileAdapter = new SuggestedCategoriesProfileAdapter(getSupportFragmentManager());
        viewPagerSuggestedServices.setAdapter(suggestedCategoriesProfileAdapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)findViewById(R.id.indicator_view_pager_suggested_services);
        circlePageIndicator.setViewPager(viewPagerSuggestedServices);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:

                onBackPressed();
                break;

            case R.id.toolbar_right_img:
                break;

        }
    }
}
