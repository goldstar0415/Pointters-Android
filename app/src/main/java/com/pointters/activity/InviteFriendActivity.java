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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.pointters.R;
import com.pointters.fragment.AddServiceFragment;
import com.pointters.fragment.PostUpdateFragment;
import com.pointters.fragment.QrcodeFragment;
import com.pointters.fragment.SuggestedInviteFriendsFragment;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CustomTabLayout;
import com.pointters.utils.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class InviteFriendActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private CustomTabLayout tabLayout;
    private ImageView imgClose;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private NonSwipeableViewPager viewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_friend);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        tabLayout = (CustomTabLayout) findViewById(R.id.tab_layout);
        imgClose = (ImageView) findViewById(R.id.toolbar_lft_img);
        viewPager = (NonSwipeableViewPager) findViewById(R.id.view_pager);

        tabLayout.addOnTabSelectedListener(this);
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
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {
        switch ((view.getId())) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        Fragment fragment =getSupportFragmentManager().findFragmentById(R.id.frame_container);
//        if (fragment != null) {
//            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
//        if (fragment != null) {
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                editor.putString(ConstantUtils.LAST_SELECTED_TAB, "0").apply();
                break;
            case 1:
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

    private void setupViewPager(ViewPager viewPager) {
        InviteViewPager exploreServiceAdapter = new InviteViewPager(getSupportFragmentManager());
        //JobsFragment jobsFragment = new JobsFragment();

        exploreServiceAdapter.addFrag(new SuggestedInviteFriendsFragment(), getResources().getString(R.string.suggested));
        exploreServiceAdapter.addFrag(new QrcodeFragment(), getResources().getString(R.string.qr_code));
        viewPager.setAdapter(exploreServiceAdapter);

        tabLayout = (CustomTabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
    }

    public class InviteViewPager extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private String category;

        public InviteViewPager(FragmentManager fm) {
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
