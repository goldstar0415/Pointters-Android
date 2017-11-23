package com.pointters.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;
import com.pointters.adapter.ExploreServiceAdapter;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CustomTabLayout;

/**
 * Created by prashantkumar on 25/9/17.
 */

public class SellFragment extends Fragment {
    private ViewPager sellViewPager;
    private SharedPreferences sharedPreferences;
    private View view;
    private SellOrdersFragment sellOrdersFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_sell, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        sellViewPager = (ViewPager) view.findViewById(R.id.view_pager_my_orders);
        setupViewPager(sellViewPager);

        if (sharedPreferences.getString(ConstantUtils.SELECTED_TAB, "").equals(getResources().getString(R.string.orders)))
            sellViewPager.setCurrentItem(0);
        else if (sharedPreferences.getString(ConstantUtils.SELECTED_TAB, "").equals(getResources().getString(R.string.offers)))
            sellViewPager.setCurrentItem(1);
        else if (sharedPreferences.getString(ConstantUtils.SELECTED_TAB, "").equals(getResources().getString(R.string.jobs)))
            sellViewPager.setCurrentItem(2);
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ExploreServiceAdapter exploreServiceAdapter = new ExploreServiceAdapter(getChildFragmentManager());
        sellOrdersFragment=new SellOrdersFragment();

        OffersFragment OffersFragment = new OffersFragment();



        exploreServiceAdapter.addFrag(sellOrdersFragment, getResources().getString(R.string.orders));
        exploreServiceAdapter.addFrag(OffersFragment, getResources().getString(R.string.offers));
        exploreServiceAdapter.addFrag(new BlankFragment(), getResources().getString(R.string.jobs));
        viewPager.setAdapter(exploreServiceAdapter);

        CustomTabLayout tabLayout = (CustomTabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(sellOrdersFragment!=null)
            sellOrdersFragment.setUserVisibleHint(isVisibleToUser);

    }
}
