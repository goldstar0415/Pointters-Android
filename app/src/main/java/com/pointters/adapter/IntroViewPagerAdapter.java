package com.pointters.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pointters.R;

/**
 * Created by Vishal Sharma on 17-Jul-17.
 */

public class IntroViewPagerAdapter extends PagerAdapter {

    private Context context;
    private int[] imgArray;
    private LayoutInflater layoutInflater;

    public IntroViewPagerAdapter(Context context, int[] imgArray) {
        this.context = context;
        this.imgArray = imgArray;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = layoutInflater.inflate(R.layout.adapter_intro, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.img_intro);
        imageView.setImageDrawable(ContextCompat.getDrawable(context, imgArray[position]));

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return imgArray.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
