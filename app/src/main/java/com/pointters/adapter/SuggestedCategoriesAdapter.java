package com.pointters.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.internal.LinkedTreeMap;
import com.pointters.fragment.SuggestedFragment;

import java.util.ArrayList;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class SuggestedCategoriesAdapter extends FragmentStatePagerAdapter {


    private ArrayList<LinkedTreeMap> banners;
    public SuggestedCategoriesAdapter(FragmentManager fm, ArrayList<LinkedTreeMap> banners) {
        super(fm);
        this.banners = banners;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new SuggestedFragment(banners.get(position));

        return fragment;
    }

    @Override
    public int getCount() {
        return banners.size();
    }
}
