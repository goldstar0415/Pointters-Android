package com.pointters.utils;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Vishal Sharma on 19-Jul-17.
 */

public class AppUtils {

    public static void setDefaultToolbarWithBackIcon(AppCompatActivity context, Toolbar toolbar) {
        context.setSupportActionBar(toolbar);

        //enable back icon
        if (context.getSupportActionBar() != null) {
            context.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            context.getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
    }

}