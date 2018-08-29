package com.pointters.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;

/**
 * Created by vishalsharma on 1/8/17.
 */

@SuppressLint("ValidFragment")
public class SuggestedFragment extends Fragment {
    private View view;
    private LinkedTreeMap banner;

    private ImageView bannerIconView;
    private TextView bannerTitle;
    private TextView bannerSubTitle;
    private ImageView bannerImageView;

    @SuppressLint("ValidFragment")
    public SuggestedFragment(LinkedTreeMap banner){
        this.banner = banner;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_suggested_category, container, false);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        bannerIconView = (ImageView) view.findViewById(R.id.img_icon);
        bannerImageView = (ImageView) view.findViewById(R.id.img_background);
        bannerTitle = (TextView) view.findViewById(R.id.txt_title);
        bannerSubTitle = (TextView) view.findViewById(R.id.txt_subtitle);

        bannerTitle.setText(banner.get("title").toString());
        bannerSubTitle.setText(banner.get("subTitle").toString());
        ImageLoader.getInstance().displayImage(banner.get("icon").toString(), bannerIconView, options);
        ImageLoader.getInstance().displayImage(banner.get("image").toString(), bannerImageView, options);

        return view;
    }
}
