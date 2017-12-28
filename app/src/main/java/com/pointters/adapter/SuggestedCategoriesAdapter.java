package com.pointters.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pointters.fragment.SuggestedFragment;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class SuggestedCategoriesAdapter extends FragmentStatePagerAdapter {


    public SuggestedCategoriesAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new SuggestedFragment();

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
