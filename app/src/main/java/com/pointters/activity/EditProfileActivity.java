package com.pointters.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.adapter.EditProfileImageViewPagerAdapter;
import com.pointters.utils.AppUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 19-Jul-17.
 */

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textIndicator;

    private ViewPager viewpagerImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();

        //set toolbar
        AppUtils.setToolBarWithBothIcon(EditProfileActivity.this, getResources().getString(R.string.edit_profile),
                R.drawable.back_icon_grey, 0);

        setUpViewPager();

        setOnClick();

    }

    private void setUpViewPager() {

        viewpagerImages = (ViewPager) findViewById(R.id.view_pager_images);
        final int[] imgArray = {R.drawable.slider_img, R.drawable.slider_img, R.drawable.slider_img, R.drawable.slider_img};

        EditProfileImageViewPagerAdapter editProfileImageViewPagerAdapter = new EditProfileImageViewPagerAdapter(EditProfileActivity.this, imgArray);
        viewpagerImages.setAdapter(editProfileImageViewPagerAdapter);
        viewpagerImages.setPageMargin((int) getResources().getDimension(R.dimen._5sdp));

        int positionTxt = viewpagerImages.getCurrentItem() + 1;
        textIndicator.setText("" + positionTxt + " of " + imgArray.length);
        viewpagerImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                int positionTxt = position + 1;
                textIndicator.setText(positionTxt + " of " + imgArray.length);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void initViews() {

        textIndicator = (TextView) findViewById(R.id.textImageIndicator);

    }

    private void setOnClick() {


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.toolbar_lft_img:

                onBackPressed();

                break;
        }

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

}
