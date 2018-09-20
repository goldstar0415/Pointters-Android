package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.adapter.EditProfileImageViewPagerAdapter;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.Media;
import com.pointters.model.ParcelModel;
import com.pointters.utils.ConstantUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by mac on 1/7/18.
 */

public class PhotoSlideViewerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnBack;
    private EditProfileImageViewPagerAdapter editProfileImageViewPagerAdapter;

    private List<Media> mediaList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_slide_viewer);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        Gson gson = new Gson();
        String media = getIntent().getStringExtra(ConstantUtils.MEDIA);
        Media mediaModel = gson.fromJson(media, Media.class);
        mediaList.add(mediaModel);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        setUpSuggestedCategoryViewPager();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setUpSuggestedCategoryViewPager() {
        ViewPager viewPagerSuggestedServices = (ViewPager) findViewById(R.id.view_pager_suggested_services);
        editProfileImageViewPagerAdapter = new EditProfileImageViewPagerAdapter(this, mediaList);
        viewPagerSuggestedServices.setAdapter(editProfileImageViewPagerAdapter);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator_view_pager_suggested_services);
        circlePageIndicator.setViewPager(viewPagerSuggestedServices);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
