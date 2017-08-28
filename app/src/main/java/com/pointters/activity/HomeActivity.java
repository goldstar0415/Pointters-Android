package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.adapter.HomeViewPagerAdapter;
import com.pointters.model.BottomTabSeletedModel;
import com.pointters.utils.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.pointters.R.id.view;

/**
 * Created by Vishal Sharma on 28-Jul-17.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener , ViewPager.OnPageChangeListener{

    private ImageView imgFlag;
    private ImageView imgService;
    private ImageView imgCamera;
    private ImageView imgChat;
    private ImageView imgProfile;
    private RelativeLayout imgFlagRelative;
    private RelativeLayout imgServiceRelative;
    private RelativeLayout imgCameraRelative;
    private RelativeLayout imgChatRelative;
    private RelativeLayout imgProfileRelative;
    private TextView txtCamera,txtFlag,txtExplore,txtChat,txtMe;
    private TextView searchBar;
    private List<BottomTabSeletedModel> bottomTabSeletedModels = new ArrayList<>(5);
    private NonSwipeableViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpToolBar();

        initViews();

        setUpViewPager();

        setOnClick();
        bottomTabSeletedModels.add(0,new BottomTabSeletedModel(imgFlagRelative,txtFlag));
        bottomTabSeletedModels.add(1,new BottomTabSeletedModel(imgServiceRelative,txtExplore));
        bottomTabSeletedModels.add(2,new BottomTabSeletedModel(imgCameraRelative,txtCamera));
        bottomTabSeletedModels.add(3,new BottomTabSeletedModel(imgChatRelative,txtChat));
        bottomTabSeletedModels.add(4,new BottomTabSeletedModel(imgProfileRelative,txtMe));

        //updateSelectedPage(1);
    }

    private void setUpViewPager() {
        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(homeViewPagerAdapter);

        viewPager.setCurrentItem(1);
        imgCamera.setSelected(true);
        txtCamera.setTextColor(getResources().getColor(R.color.color_blue));
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
        txtExplore=(TextView)findViewById(R.id.txt_service);
        txtChat=(TextView)findViewById(R.id.txt_chat);
        txtMe=(TextView)findViewById(R.id.txt_me);
        txtFlag=(TextView)findViewById(R.id.txt_flag);


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

                viewPager.setCurrentItem(0);
//updateSelectedPage(0);
                //startActivity(new Intent(HomeActivity.this, NotificationOptionsActivity.class));

                break;

            case R.id.img_service_relative:
               // updateSelectedPage(1);

                //searchBar.setOnClickListener(this);

                viewPager.setCurrentItem(1);
                break;

            case R.id.img_camera_relative:
            //    updateSelectedPage(2);

                //   txtCamera.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.color_blue));
                startActivity(new Intent(this, AddServiceActivity.class));

                break;
            case R.id.img_chat_relative:
               // updateSelectedPage(3);

//                searchBar.setOnClickListener(null);
                viewPager.setCurrentItem(3);
                break;
/*
            case R.id.txt_search_here_hint:


                startActivity(new Intent(this, SearchServiceActivity.class));

                break;*/

            case R.id.img_user_relative:
               // updateSelectedPage(4);
                viewPager.setCurrentItem(4);
                //startActivity(new Intent(this, MenuScreenActivity.class));

                break;
        }
    }

    private void updateSelectedPage(int position) {

        for (int i = 0; i < bottomTabSeletedModels.size(); i++) {

            if (position == i) {
                bottomTabSeletedModels.get(i).getRelativeLayout().setSelected(true);
                bottomTabSeletedModels.get(i).getTextView().setTextColor(getResources().getColor(R.color.color_blue));
            }
            else {
                bottomTabSeletedModels.get(i).getRelativeLayout().setSelected(false);
                bottomTabSeletedModels.get(i).getTextView().setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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
}
