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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.pointters.R;
import com.pointters.fragment.AddServiceFragment;
import com.pointters.fragment.BlankFragment;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CustomTabLayout;
import com.pointters.utils.NonSwipeableViewPager;

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
    private BlankFragment postUpdateFragment;
    private AddServiceFragment addServiceFragment;
    private int mCurCheckPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_services);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        tabLayout = (CustomTabLayout) findViewById(R.id.tab_layout);
        imgClose = (ImageView) findViewById(R.id.img_close);
        viewPager = (NonSwipeableViewPager) findViewById(R.id.view_pager);


        tabLayout.addTab(tabLayout.newTab().setText("Post Update"));
        tabLayout.addTab(tabLayout.newTab().setText("Add Service"));

        tabLayout.addOnTabSelectedListener(this);

      //  setupViewPager(viewPager);



        imgClose.setOnClickListener(this);
        if (!sharedPreferences.getString(ConstantUtils.LAST_SELECTED_TAB, "").isEmpty() && sharedPreferences.getString(ConstantUtils.LAST_SELECTED_TAB, "").equals("0"))
        {
            tabLayout.getTabAt(0).select();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new BlankFragment()).commit();
        }else         if (!sharedPreferences.getString(ConstantUtils.LAST_SELECTED_TAB, "").isEmpty() && sharedPreferences.getString(ConstantUtils.LAST_SELECTED_TAB, "").equals("1"))
        {
            tabLayout.getTabAt(1).select();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new AddServiceFragment()).commit();
        }
        else {
            tabLayout.getTabAt(0).select();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new BlankFragment()).commit();
        }
           // viewPager.setCurrentItem(Integer.parseInt(sharedPreferences.getString(ConstantUtils.LAST_SELECTED_TAB, "")));



    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onClick(View view) {

        switch ((view.getId())) {


            case R.id.img_close:

                onBackPressed();

                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment =getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if(fragment!=null) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment =getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if(fragment!=null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {

            case 0:
                editor.putString(ConstantUtils.LAST_SELECTED_TAB, "0").apply();

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new BlankFragment()).commit();
                break;
            case 1:
                editor.putString(ConstantUtils.LAST_SELECTED_TAB, "1").apply();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new AddServiceFragment()).commit();
                break;




        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
