package com.pointters.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pointters.fragment.CameraFragment;
import com.pointters.fragment.ChatFragment;
import com.pointters.fragment.CurrentUpdatesFragment;
import com.pointters.fragment.ExploreServiceAfterLoginFragment;
import com.pointters.fragment.ExploreServiceFragment;
import com.pointters.fragment.FlagFragment;
import com.pointters.fragment.MyOrderFragment;

/**
 * Created by Vishal Sharma on 29-Jul-17.
 */

public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {

    public HomeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {

            case 0:
                fragment = new CurrentUpdatesFragment();
                break;
            case 1:
                fragment = new ExploreServiceFragment();
                break;
            case 2:
                fragment = new CameraFragment();
                break;
            case 3:
                fragment = new ChatFragment();
                break;
            case 4:
                fragment = new MyOrderFragment();
                break;

        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }
}


