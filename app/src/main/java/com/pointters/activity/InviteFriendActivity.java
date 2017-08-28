package com.pointters.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pointters.R;
import com.pointters.adapter.ExploreServiceAdapter;
import com.pointters.fragment.HomeQRCodeFragment;
import com.pointters.fragment.InviteFriendSuggestedFragment;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CustomTabLayout;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class InviteFriendActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager inviteFriendViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        AppUtils.setToolBarWithBothIconWithShadow(InviteFriendActivity.this, getResources().getString(R.string.invite_friend), R.drawable.back_icon_grey, R.drawable.invite_button);
        inviteFriendViewPager = (ViewPager) findViewById(R.id.view_pager_invite_friend);
        setupViewPager(inviteFriendViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ExploreServiceAdapter exploreServiceAdapter = new ExploreServiceAdapter(getSupportFragmentManager());

        InviteFriendSuggestedFragment suggestedFragment = new InviteFriendSuggestedFragment();

        HomeQRCodeFragment homeQRCodeFragment = new HomeQRCodeFragment();


        exploreServiceAdapter.addFrag(suggestedFragment, getResources().getString(R.string.suggested));
        exploreServiceAdapter.addFrag(homeQRCodeFragment, getResources().getString(R.string.qr_code));

        viewPager.setAdapter(exploreServiceAdapter);

        CustomTabLayout tabLayout = (CustomTabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:

                onBackPressed();
                break;
            case R.id.toolbar_right_img:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
