package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.pointters.R;
import com.pointters.fragment.AddServiceFragment;
import com.pointters.fragment.PostUpdateFragment;
import com.pointters.model.Pusher;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CustomTabLayout;
import com.pointters.utils.NonSwipeableViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class AddServiceActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private CustomTabLayout tabLayout;
    private ImageView imgClose;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private NonSwipeableViewPager viewPager;
    public Button postButton;
    AddServicePageAdapter exploreServiceAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_services);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        imgClose = (ImageView) findViewById(R.id.img_close);
        viewPager = (NonSwipeableViewPager) findViewById(R.id.view_pager);

//        tabLayout.addTab(tabLayout.newTab().setText("Post Update"));
//        tabLayout.addTab(tabLayout.newTab().setText("Add Service"));
        postButton = (Button) findViewById(R.id.btn_post);
        postButton.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        setupViewPager(viewPager);

        if (!sharedPreferences.getString(ConstantUtils.LAST_SELECTED_TAB, "").isEmpty() && sharedPreferences.getString(ConstantUtils.LAST_SELECTED_TAB, "").equals("0")) {
            tabLayout.getTabAt(0).select();
            viewPager.setCurrentItem(0);
        }
        else if (!sharedPreferences.getString(ConstantUtils.LAST_SELECTED_TAB, "").isEmpty() && sharedPreferences.getString(ConstantUtils.LAST_SELECTED_TAB, "").equals("1")) {
            tabLayout.getTabAt(1).select();
            viewPager.setCurrentItem(1);
        }
        else {
            tabLayout.getTabAt(0).select();
            viewPager.setCurrentItem(0);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {
        switch ((view.getId())) {
            case R.id.img_close:
                InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                hide.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

                onBackPressed();
                break;
            case R.id.btn_post:
                postUpdate();
                break;
            default:
                break;
        }
    }

    public void postUpdate(){
        EventBus.getDefault().post(new Pusher("post"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Fragment fragment =getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (fragment != null) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 998) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
        exploreServiceAdapter.getItem(viewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                postButton.setVisibility(View.VISIBLE);
                editor.putString(ConstantUtils.LAST_SELECTED_TAB, "0").apply();
                break;
            case 1:
                postButton.setVisibility(View.INVISIBLE);
                editor.putString(ConstantUtils.LAST_SELECTED_TAB, "1").apply();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void omEventMainThread(Pusher pusher){
        switch (pusher.getAction()) {
            case "enable":
                postButton.setEnabled(true);
                postButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
                break;
            case "disable":
                postButton.setEnabled(false);
                postButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
                break;
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        exploreServiceAdapter = new AddServicePageAdapter(getSupportFragmentManager());
        //JobsFragment jobsFragment = new JobsFragment();

        exploreServiceAdapter.addFrag(new PostUpdateFragment(), getResources().getString(R.string.post_update));
        exploreServiceAdapter.addFrag(new AddServiceFragment(), getResources().getString(R.string.add_service));
        viewPager.setAdapter(exploreServiceAdapter);

        tabLayout = (CustomTabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = exploreServiceAdapter.getItem(position);
                if (position == 0) {
                    if ((PostUpdateFragment)fragment != null) {
                        InputMethodManager keyboard = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }else{
                    if (getCurrentFocus() != null) {
                        InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        hide.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class AddServicePageAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private String category;

        public AddServicePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = mFragmentList.get(position);
            Bundle args = new Bundle();
            args.putString("category", String.valueOf(position));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
