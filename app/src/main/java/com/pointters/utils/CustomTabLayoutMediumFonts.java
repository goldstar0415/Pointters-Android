package com.pointters.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by prashantkumar on 25/9/17.
 */

public class CustomTabLayoutMediumFonts  extends TabLayout {
    private Typeface typeface;

    public CustomTabLayoutMediumFonts(Context context) {
        super(context);
    }

    public CustomTabLayoutMediumFonts(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayoutMediumFonts(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void addTab(TabLayout.Tab tab) {
        super.addTab(tab);
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Medium.ttf");
        ViewGroup mainView = (ViewGroup) getChildAt(0);
        ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());

        int tabChildCount = tabView.getChildCount();
        for (int i = 0; i < tabChildCount; i++) {
            View tabViewChild = tabView.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                ((TextView) tabViewChild).setTypeface(typeface, Typeface.NORMAL);
            }
        }
    }


    @Override
    public void setupWithViewPager(ViewPager viewPager) {
        super.setupWithViewPager(viewPager);
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Medium.ttf");
        if (typeface != null) {
            this.removeAllTabs();

            ViewGroup slidingTabStrip = (ViewGroup) getChildAt(0);

            PagerAdapter adapter = viewPager.getAdapter();

            for (int i = 0, count = adapter.getCount(); i < count; i++) {
                TabLayout.Tab tab = this.newTab();
                this.addTab(tab.setText(adapter.getPageTitle(i)));
                AppCompatTextView view = (AppCompatTextView) ((ViewGroup) slidingTabStrip.getChildAt(i)).getChildAt(1);
                view.setTypeface(typeface, Typeface.NORMAL);
            }
        }
    }
}

