package com.pointters.utils;

import android.support.v4.view.ViewPager;
import android.view.View;

import me.crosswall.lib.coverflow.core.Utils;

/**
 * Created by prashantkumar on 5/10/17.
 */

public class ZoomOutPageTransformer implements ViewPager.PageTransformer {


    public static final String TAG = "CoverTransformer";

    public static final float SCALE_MIN = 0.3f;
    public static final float SCALE_MAX = 1f;
    public static final float MARGIN_MIN = 0f;
    public static final float MARGIN_MAX = 50f;
    public float scale  = 0f;

    private float pagerMargin = 0f;
    private float spaceValue = 0f;
    private float rotationY    = 0f;

    public ZoomOutPageTransformer(float scale, float pagerMargin, float spaceValue, float rotationY) {
        this.scale = scale;
        this.pagerMargin = pagerMargin;
        this.spaceValue = spaceValue;
        this.rotationY = rotationY;
    }

    public void transformPage(View page, float position) {


        if(rotationY!=0){
            float realRotationY = Math.min(rotationY,Math.abs(position * rotationY));
            page.setRotationY(position < 0f ? realRotationY : - realRotationY);
        }

        if (scale != 0f) {
            float realScale = Utils.getFloat(1 - Math.abs(position * scale),SCALE_MIN,SCALE_MAX);
            page.setScaleX(realScale);
            page.setScaleY(realScale);
        }

        if (pagerMargin != 0) {

            float realPagerMargin = position * (pagerMargin);

            if (spaceValue != 0) {
                float realSpaceValue = Utils.getFloat(Math.abs(position * spaceValue),MARGIN_MIN,MARGIN_MAX);
                realPagerMargin += (position > 0) ? realSpaceValue : - realSpaceValue;
            }

            page.setTranslationX(realPagerMargin);
        }


    }
}