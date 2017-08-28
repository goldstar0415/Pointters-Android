package com.pointters.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;
import com.pointters.adapter.ExploreServiceAdapter;
import com.pointters.utils.CustomTabLayout;

/**
 * Created by Vishal Sharma on 29-Jul-17.
 */

public class ExploreServiceAfterLoginFragment extends Fragment {

    private View view;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragement_explore_service_after_login, container, false);

        initViews();

        setupViewPager(viewPager);

        return view;

    }

    private void initViews() {

        viewPager = (ViewPager) view.findViewById(R.id.view_pager_services);

    }

    private void setupViewPager(ViewPager viewPager) {
        ExploreServiceAdapter exploreServiceAdapter = new ExploreServiceAdapter(getChildFragmentManager());

        ServiceFragment serviceFragment = new ServiceFragment();

        ServiceFragment liveOffersFragment = new ServiceFragment();

        ServiceFragment jobsFragment = new ServiceFragment();

        exploreServiceAdapter.addFrag(serviceFragment, getResources().getString(R.string.services));
        exploreServiceAdapter.addFrag(liveOffersFragment, getResources().getString(R.string.live_offers));
        exploreServiceAdapter.addFrag(jobsFragment, getResources().getString(R.string.jobs));
        viewPager.setAdapter(exploreServiceAdapter);

        CustomTabLayout tabLayout = (CustomTabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }
}
