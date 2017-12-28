package com.pointters.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pointters.R;
import com.pointters.utils.ConstantUtils;

/**
 * Created by Anil Jha on 29-Jul-17.
 */

public class ImageFragment extends Fragment implements View.OnClickListener {

    private RoundedImageView roundedImageView;
    private RelativeLayout layoutTagServiceSeller;
    private ImageView deleteButton;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crosswall_image, container, false);

        roundedImageView = (RoundedImageView) view.findViewById(R.id.crosswall_images);
        layoutTagServiceSeller = (RelativeLayout) view.findViewById(R.id.layout_tag_service);
        deleteButton = (ImageView) view.findViewById(R.id.delete_btn);
        deleteButton.setOnClickListener(this);
        layoutTagServiceSeller.setOnClickListener(this);

        if (getArguments().getString(ConstantUtils.SOURCE).equals("POSTUPDATE")) {
            layoutTagServiceSeller.setVisibility(View.VISIBLE);
            if (getArguments().getString("lastPosition").equals("yes")) {
                layoutTagServiceSeller.setVisibility(View.INVISIBLE);

            } else if (getArguments().getString("lastPosition").equals("no")) {
                 layoutTagServiceSeller.setVisibility(View.VISIBLE);
            }
        }
        else if (getArguments().getString(ConstantUtils.SOURCE).equals("ADDSERVICE")) {
            layoutTagServiceSeller.setVisibility(View.INVISIBLE);
        }


        if (getArguments().getString("lastPosition").equals("yes")) {
            deleteButton.setVisibility(View.GONE);
            roundedImageView.setOnClickListener(this);

        } else if (getArguments().getString("lastPosition").equals("no")) {
            deleteButton.setVisibility(View.VISIBLE);
            roundedImageView.setOnClickListener(null);
        }


        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_grey)
                .showImageForEmptyUri(R.drawable.upload_background_img_placeholder)
                .showImageOnFail(R.drawable.placeholder_grey)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        ImageLoader.getInstance().displayImage(getArguments().getString(ConstantUtils.INTENT_BUNDLE_IMAGE_VIDEO), roundedImageView, options);


        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_btn:
                 intent = new Intent("delete");
                intent.putExtra(ConstantUtils.POSITION, getArguments().getInt(ConstantUtils.POSITION));
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                break;
            case R.id.crosswall_images:
                intent = new Intent("pick");
                intent.putExtra(ConstantUtils.POSITION, getArguments().getInt(ConstantUtils.POSITION));
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                break;
            case R.id.layout_tag_service:
                intent = new Intent("openActivity");
                intent.putExtra(ConstantUtils.POSITION, getArguments().getInt(ConstantUtils.POSITION));
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                break;
        }

    }
}
