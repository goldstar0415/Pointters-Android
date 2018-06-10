package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.pointters.R;
import com.pointters.adapter.ExploreServiceAdapter;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.NonSwipeableViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

/**
 * Created by prashantkumar on 21/8/17.
 */

public class MeFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView toolbarLeftIamge;
    private ImageButton searchButton;
    private NonSwipeableViewPager buySellViewPager;
    private SharedPreferences sharedPreferences;
    private String json;
    private DisplayImageOptions options;
    private BuyFragment buyFragment;
    private SellFragment sellFragment;
    private MaterialSpinner spinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_order, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);

        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null)
            json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");

        toolbarLeftIamge = (ImageView) view.findViewById(R.id.toolbar_lft_img);
        searchButton = (ImageButton) view.findViewById(R.id.search_button);
        buySellViewPager = (NonSwipeableViewPager) view.findViewById(R.id.viewpager_buy_sell);
        spinner = (MaterialSpinner) view.findViewById(R.id.spinner_order);
        searchButton.setOnClickListener(this);
        toolbarLeftIamge.setOnClickListener(this);
        spinner.setItems("BUY", "SELL");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                buySellViewPager.setCurrentItem(position);
            }
        });
        if (sharedPreferences.getString(ConstantUtils.SOURCE, "") != null) {
            String tabString = sharedPreferences.getString(ConstantUtils.SOURCE, "");
            if (tabString.equals(getResources().getString(R.string.sell))){
                spinner.setSelectedIndex(1);
            }else{
                spinner.setSelectedIndex(0);
            }
        }
        setupViewPager(buySellViewPager);

        RelativeLayout tabOne = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) tabOne.findViewById(R.id.txt_tab);
        tabOne.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setText("Buy");
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Medium.ttf");
        tv.setTypeface(custom_font);
//        loadProfilePic();
        if (sharedPreferences.getString(ConstantUtils.SOURCE, "").equals(getResources().getString(R.string.buy)))
            buySellViewPager.setCurrentItem(0);
        else if (sharedPreferences.getString(ConstantUtils.SOURCE, "").equals(getResources().getString(R.string.sell)))
            buySellViewPager.setCurrentItem(1);

        return view;
    }

    void loadProfilePic() {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("profilePic")) {
                if (jsonObject.get("profilePic") != null && !jsonObject.get("profilePic").toString().isEmpty()) {
                    options = new DisplayImageOptions.Builder()
                            .showImageOnLoading(R.drawable.user_avatar_placeholder)
                            .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                            .showImageOnFail(R.drawable.user_avatar_placeholder)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .considerExifParams(true)
                            .build();

                    String profileUrl;

                    if (jsonObject.get("profilePic").toString().startsWith("https://s3.amazonaws.com")) {
                        profileUrl = jsonObject.get("profilePic").toString();
                    } else {
//                        profileUrl = "https://s3.amazonaws.com" + jsonObject.get("profilePic").toString();
                        profileUrl = jsonObject.get("profilePic").toString();
                    }
//                    ImageLoader.getInstance().displayImage(profileUrl, toolbarRightIamge, options);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ExploreServiceAdapter exploreServiceAdapter = new ExploreServiceAdapter(getChildFragmentManager());

         buyFragment = new BuyFragment();

         sellFragment = new SellFragment();


        exploreServiceAdapter.addFrag(buyFragment, "");
        exploreServiceAdapter.addFrag(sellFragment, getResources().getString(R.string.sell));

        viewPager.setAdapter(exploreServiceAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:
                Fragment fragment = new MenuScreenFragment();
                getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment)
                        .commitAllowingStateLoss();
//                startActivityForResult(new Intent(getActivity(), MenuScreenFragment.class), 2);
                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                json = data.getStringExtra("json");
//                loadProfilePic();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(buyFragment!=null)
            buyFragment.setUserVisibleHint(isVisibleToUser);
        if(sellFragment!=null)
            sellFragment.setUserVisibleHint(isVisibleToUser);
    }
}
