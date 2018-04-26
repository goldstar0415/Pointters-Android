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
 * Created by Vishal Sharma on 29-Jul-17.
 */

public class ExploreServiceAfterLoginFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View view;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        view = inflater.inflate(R.layout.fragement_explore_service_after_login, container, false);

        initViews();

        setupViewPager(viewPager);
        if (!sharedPreferences.getString(ConstantUtils.SELECTED_TAB,"").isEmpty() && sharedPreferences.getString(ConstantUtils.SELECTED_TAB, "").equals(getResources().getString(R.string.get_live_offer)))
            viewPager.setCurrentItem(1);

        editor.putString(ConstantUtils.SELECTED_TAB, "").apply();

        return view;

    }

    private void initViews() {

        viewPager = (ViewPager) view.findViewById(R.id.view_pager_services);

    }

    private void setupViewPager(ViewPager viewPager) {
        ExploreServiceAdapter exploreServiceAdapter = new ExploreServiceAdapter(getChildFragmentManager());
        //JobsFragment jobsFragment = new JobsFragment();

        exploreServiceAdapter.addFrag(new ServiceFragment(), getResources().getString(R.string.services));
        exploreServiceAdapter.addFrag(new LiveOffersFragment(), getResources().getString(R.string.live_offers));
        exploreServiceAdapter.addFrag(new JobsFragment(), getResources().getString(R.string.jobs));
        viewPager.setAdapter(exploreServiceAdapter);

        CustomTabLayout tabLayout = (CustomTabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
