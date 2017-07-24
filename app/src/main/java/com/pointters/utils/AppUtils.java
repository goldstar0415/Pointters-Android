package com.pointters.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
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

    public static void applyFontsToTextInputLayout(Context context, TextInputLayout[] textInputLayouts) {

        for (TextInputLayout textInputLayout : textInputLayouts) {
            textInputLayout.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf"));
        }

    }

    public static boolean isRequiredFieldsFilled(TextInputLayout[] textInputLayouts, String[] errorMessages) {
        boolean isRequiredFieldsFilled = true;

        for (int i = 0; i < textInputLayouts.length; i++) {
            if (textInputLayouts[i].getEditText().getText().toString().trim().isEmpty()) {
                textInputLayouts[i].setError(errorMessages[i]);
                isRequiredFieldsFilled = false;
            }
        }

        return isRequiredFieldsFilled;

    }

}
