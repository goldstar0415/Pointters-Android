package com.pointters.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pointters.R;
import com.pointters.activity.MenuScreenActivity;
import com.pointters.adapter.ExploreServiceAdapter;
import com.pointters.adapter.OffersAdapter;
import com.pointters.utils.CustomTabLayout;

/**
 * Created by prashantkumar on 21/8/17.
 */

public class MyOrderFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView toolbarLeftIamge;
    private RoundedImageView toolbarRightIamge;
    private ViewPager myOrdersViewPager;
    private TextView toolbarTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_order, container, false);
        toolbarLeftIamge = (ImageView) view.findViewById(R.id.toolbar_lft_img);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        toolbarRightIamge = (RoundedImageView) view.findViewById(R.id.toolbar_right_img);
        myOrdersViewPager = (ViewPager) view.findViewById(R.id.view_pager_my_orders);
        toolbarTitle.setText(getResources().getString(R.string.my_order));
        toolbarRightIamge.setOnClickListener(this);
        toolbarLeftIamge.setOnClickListener(this);
        setupViewPager(myOrdersViewPager);

        myOrdersViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    toolbarTitle.setText(getResources().getString(R.string.my_order));
                } else if (position == 1) {
                    toolbarTitle.setText(getResources().getString(R.string.offers));
                } else if (position == 2) {
                    toolbarTitle.setText(getResources().getString(R.string.jobs));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ExploreServiceAdapter exploreServiceAdapter = new ExploreServiceAdapter(getChildFragmentManager());

        OrdersFragment ordersFragment = new OrdersFragment();

        OffersFragment OffersFragment = new OffersFragment();

        JobsFragment jobsFragment = new JobsFragment();

        exploreServiceAdapter.addFrag(ordersFragment, getResources().getString(R.string.orders));
        exploreServiceAdapter.addFrag(OffersFragment, getResources().getString(R.string.offers));
        exploreServiceAdapter.addFrag(jobsFragment, getResources().getString(R.string.jobs));
        viewPager.setAdapter(exploreServiceAdapter);

        CustomTabLayout tabLayout = (CustomTabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:
            //    startActivity(new Intent(getActivity(), MenuScreenActivity.class));
                break;
            case R.id.toolbar_right_img:
                startActivity(new Intent(getActivity(), MenuScreenActivity.class));
                break;
        }

    }

}
