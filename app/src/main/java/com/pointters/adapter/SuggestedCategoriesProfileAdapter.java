package com.pointters.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pointters.fragment.SuggestedFragment;
import com.pointters.fragment.SuggestedProfileFragment;

/**
 * Created by vikas on 8/16/2017.
 */




public class SuggestedCategoriesProfileAdapter extends FragmentStatePagerAdapter {


    public SuggestedCategoriesProfileAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new SuggestedProfileFragment();

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}