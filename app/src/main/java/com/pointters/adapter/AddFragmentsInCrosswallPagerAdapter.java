package com.pointters.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pointters.R;
import com.pointters.fragment.ImageFragment;
import com.pointters.fragment.VideoFragment;
import com.pointters.model.Media;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;

import javax.xml.transform.Source;

/**
 * Created by aniljha on 21/9/17.
 */

public class AddFragmentsInCrosswallPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Media> mediaArrayList;
    private Context context;
    private String source;

    public AddFragmentsInCrosswallPagerAdapter(FragmentManager fm, ArrayList<Media> mediaArrayList, Context context, String source) {
        super(fm);
        this.mediaArrayList = mediaArrayList;
        this.context = context;
        this.source = source;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        Bundle bundle;

        if (mediaArrayList.get(position).getMediaType().equals(context.getResources().getString(R.string.image))) {

            fragment = new ImageFragment();
            bundle = new Bundle();
            if (position == mediaArrayList.size() - 1)
                bundle.putString("lastPosition","yes");
            else
                bundle.putString("lastPosition","no");

            if(source.equals("POSTUPDATE"))
            bundle.putString(ConstantUtils.SOURCE,"POSTUPDATE");
            else if(source.equals("ADDSERVICE"))
                bundle.putString(ConstantUtils.SOURCE,"ADDSERVICE");

            bundle.putInt(ConstantUtils.POSITION, position);
            bundle.putString(ConstantUtils.INTENT_BUNDLE_IMAGE_VIDEO, mediaArrayList.get(position).getFileName());
            fragment.setArguments(bundle);

        } else if (mediaArrayList.get(position).getMediaType().equals(context.getResources().getString(R.string.video))) {

            fragment = new VideoFragment();
            bundle = new Bundle();
            if (mediaArrayList.get(position).getMediaType().equals(context.getResources().getString(R.string.image))) {
                bundle.putString("showImage","yes");
            }else   bundle.putString("showImage","no");

            if (position == mediaArrayList.size() - 1)
                bundle.putString("lastPosition","yes");
            else
                bundle.putString("lastPosition","no");

            if(source.equals("POSTUPDATE"))
                bundle.putString(ConstantUtils.SOURCE,"POSTUPDATE");
            else if(source.equals("ADDSERVICE"))
                bundle.putString(ConstantUtils.SOURCE,"ADDSERVICE");

            bundle.putInt(ConstantUtils.POSITION, position);
            bundle.putString(ConstantUtils.INTENT_BUNDLE_IMAGE_VIDEO, mediaArrayList.get(position).getFileName());
            fragment.setArguments(bundle);

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mediaArrayList.size();
    }

}
