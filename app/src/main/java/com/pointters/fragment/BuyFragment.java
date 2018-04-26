package com.pointters.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.adapter.ExploreServiceAdapter;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CustomTabLayout;

/**
 * Created by prashantkumar on 25/9/17.
 */

public class BuyFragment extends Fragment {
    private ViewPager buyViewPager;
    private SharedPreferences sharedPreferences;
    private View view;
    private CustomTabLayout tabLayoutBuy;
    private BuyOrdersFragment buyOrdersFragment;
    private CustomOffersFragment customOffersFragment;
    private LiveOfferRequestFragment liveOfferRequestFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buy, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);

        buyViewPager = (ViewPager) view.findViewById(R.id.view_pager_my_orders);
        tabLayoutBuy = (CustomTabLayout) view.findViewById(R.id.tab_layout_buy);
        setupViewPager(buyViewPager);

        RelativeLayout tabOne = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) tabOne.findViewById(R.id.txt_tab);
        tabOne.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setText(getResources().getString(R.string.orders_caps));
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Regular.ttf");

        tv.setTypeface(custom_font);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.text_size_small));

        tabLayoutBuy.getTabAt(0).setCustomView(tabOne);
        if (sharedPreferences.getString(ConstantUtils.SELECTED_TAB, "").equals(getResources().getString(R.string.orders)))
            buyViewPager.setCurrentItem(0);
        else if (sharedPreferences.getString(ConstantUtils.SELECTED_TAB, "").equals(getResources().getString(R.string.custom_offer)))
            buyViewPager.setCurrentItem(1);
        else if (sharedPreferences.getString(ConstantUtils.SELECTED_TAB, "").equals(getResources().getString(R.string.live_offer)))
            buyViewPager.setCurrentItem(2);


        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ExploreServiceAdapter exploreServiceAdapter = new ExploreServiceAdapter(getChildFragmentManager());

        buyOrdersFragment = new BuyOrdersFragment();
        customOffersFragment = new CustomOffersFragment();
        liveOfferRequestFragment = new LiveOfferRequestFragment();

        exploreServiceAdapter.addFrag(buyOrdersFragment, "");
        exploreServiceAdapter.addFrag(customOffersFragment, getResources().getString(R.string.custom_offer));
        exploreServiceAdapter.addFrag(liveOfferRequestFragment, getResources().getString(R.string.live_offer));
        viewPager.setAdapter(exploreServiceAdapter);
        CustomTabLayout tabLayout = (CustomTabLayout) view.findViewById(R.id.tab_layout_buy);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (buyOrdersFragment != null)
            buyOrdersFragment.setUserVisibleHint(isVisibleToUser);
    }

}
