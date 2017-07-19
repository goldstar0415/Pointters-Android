package com.pointters.application;

import android.app.Application;

import com.pointters.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Vishal Sharma on 19-Jul-17.
 */

public class PointtersApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
