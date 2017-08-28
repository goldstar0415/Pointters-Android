package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pointters.R;
import com.pointters.adapter.SuggestedCategoriesProfileAdapter;
import com.pointters.utils.AppUtils;
import com.viewpagerindicator.CirclePageIndicator;

import at.blogc.android.views.ExpandableTextView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ServiceDetailActivity extends AppCompatActivity implements View.OnClickListener  {
    private RelativeLayout moveToProfileScreen;

    ExpandableTextView expandableTextView;
    TextView buttonToggle;
    private GoogleMap mMap;
private Button buyForButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        setUpSuggestedCategoryViewPager();

        AppUtils.setToolBarWithBothIcon(ServiceDetailActivity.this, getResources().getString(R.string.service_detail),
                R.drawable.back_icon_grey, R.drawable.more_icon_horizontal);

        moveToProfileScreen=(RelativeLayout)findViewById(R.id.move_to_profile);
        buyForButton=(Button)findViewById(R.id.buy_for_button);
        buyForButton.setOnClickListener(this);
        moveToProfileScreen.setOnClickListener(this);

        expandableTextView = (ExpandableTextView) this.findViewById(R.id.expandableTextView);
        buttonToggle = (TextView) this.findViewById(R.id.button_toggle);


        //============Map========================================

        MapFragment mapFragment =(MapFragment) getFragmentManager().findFragmentById(R.id.mapId);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                // Add a marker in Agicent and move the camera
                LatLng TutorialsPoint = new LatLng(28.6125, 77.3773);
                mMap.addMarker(new MarkerOptions().position(TutorialsPoint));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
            }
        });

        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        expandableTextView.setAnimationDuration(750L);

        // set interpolators for both expanding and collapsing animations
        expandableTextView.setInterpolator(new OvershootInterpolator());

        // or set them separately
        expandableTextView.setExpandInterpolator(new OvershootInterpolator());
        expandableTextView.setCollapseInterpolator(new OvershootInterpolator());

        // but, you can also do the checks yourself
        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (expandableTextView.isExpanded())
                {
                    expandableTextView.collapse();
                    buttonToggle.setText("more");
                }
                else
                {
                    expandableTextView.expand();
                    buttonToggle.setText("less");
                }
            }
        });
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
            case R.id.move_to_profile:
                startActivity(new Intent(ServiceDetailActivity.this,ProfileScreenActivity.class));
                break;
            case R.id.buy_for_button:
                startActivity(new Intent(ServiceDetailActivity.this,CheckoutActivity.class));
                break;
        }
    }
}