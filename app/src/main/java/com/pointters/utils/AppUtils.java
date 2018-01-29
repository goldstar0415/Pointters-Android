package com.pointters.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pointters.R;

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

    // Toolbar with both button with custom icon
    public static void setToolBarWithBothIcon(Context context, String toolBarTitle, int leftImageId, int rightImageId) {

        TextView txtToolbarTitle = (TextView) ((Activity) context).findViewById(R.id.toolbar_title);
        txtToolbarTitle.setText(toolBarTitle);
        ImageView imgLeftIcon = (ImageView) ((Activity) context).findViewById(R.id.toolbar_lft_img);
        ImageView imgRightIcon = (ImageView) ((Activity) context).findViewById(R.id.toolbar_right_img);

        txtToolbarTitle.setOnClickListener(((View.OnClickListener) context));

        if (leftImageId != 0) {
            imgLeftIcon.setVisibility(View.VISIBLE);
            Drawable leftImage = ContextCompat.getDrawable(context, leftImageId);
            imgLeftIcon.setImageDrawable(leftImage);
            imgLeftIcon.setOnClickListener(((View.OnClickListener) context));
        } else {
            imgLeftIcon.setVisibility(View.INVISIBLE);

        }

        if (rightImageId != 0) {
            imgRightIcon.setVisibility(View.VISIBLE);
            Drawable rightImage = ContextCompat.getDrawable(context, rightImageId);
            imgRightIcon.setImageDrawable(rightImage);
            imgRightIcon.setOnClickListener(((View.OnClickListener) context));

        } else {
            imgRightIcon.setVisibility(View.INVISIBLE);
        }


    }
    public static void setToolBarWithBothIconWithShadow(Context context, String toolBarTitle, int leftImageId, int rightImageId) {

        TextView txtToolbarTitle = (TextView) ((Activity) context).findViewById(R.id.toolbar_title);
        txtToolbarTitle.setText(toolBarTitle);
        ImageView imgLeftIcon = (ImageView) ((Activity) context).findViewById(R.id.toolbar_lft_img);
        ImageView imgRightIcon = (ImageView) ((Activity) context).findViewById(R.id.toolbar_right_img);

        txtToolbarTitle.setOnClickListener(((View.OnClickListener) context));

        if (leftImageId != 0) {
            imgLeftIcon.setVisibility(View.VISIBLE);
            Drawable leftImage = ContextCompat.getDrawable(context, leftImageId);
            imgLeftIcon.setImageDrawable(leftImage);
            imgLeftIcon.setOnClickListener(((View.OnClickListener) context));
        } else {
            imgLeftIcon.setVisibility(View.INVISIBLE);

        }

        if (rightImageId != 0) {
            imgRightIcon.setVisibility(View.VISIBLE);
            Drawable rightImage = ContextCompat.getDrawable(context, rightImageId);
            imgRightIcon.setImageDrawable(rightImage);
            imgRightIcon.setOnClickListener(((View.OnClickListener) context));

        } else {
            imgRightIcon.setVisibility(View.INVISIBLE);
        }


    }
    public static void setToolBarWithBothIconFragment(Context context, String toolBarTitle, int leftImageId, int rightImageId, Fragment callingFragment, View fragView) {        TextView txtToolbarTitle = (TextView) fragView.findViewById(R.id.toolbar_title);        txtToolbarTitle.setVisibility(View.VISIBLE);        txtToolbarTitle.setText(toolBarTitle);        ImageView imgLeftIcon = (ImageView) fragView.findViewById(R.id.toolbar_lft_img);        ImageView imgRightIcon = (ImageView) fragView.findViewById(R.id.toolbar_right_img);        txtToolbarTitle.setOnClickListener((View.OnClickListener) callingFragment);        if (leftImageId != 0) {            imgLeftIcon.setVisibility(View.VISIBLE);            Drawable leftImage = ContextCompat.getDrawable(context, leftImageId);            imgLeftIcon.setImageDrawable(leftImage);            imgLeftIcon.setOnClickListener(((View.OnClickListener) callingFragment));        } else {            imgLeftIcon.setVisibility(View.INVISIBLE);        }        if (rightImageId != 0) {            imgRightIcon.setVisibility(View.VISIBLE);            Drawable rightImage = ContextCompat.getDrawable(context, rightImageId);            imgRightIcon.setImageDrawable(rightImage);            imgRightIcon.setOnClickListener(((View.OnClickListener) callingFragment));        } else {            imgRightIcon.setVisibility(View.INVISIBLE);        }    }

    public static void applyFontsToTextInputLayout(Context context, TextInputLayout[] textInputLayouts) {

        for (TextInputLayout textInputLayout : textInputLayouts) {
            textInputLayout.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Medium.ttf"));
        }

    }

    public static boolean isRequiredFieldsFilled(Context context, TextInputLayout[] textInputLayouts, String[] errorMessages) {
        boolean isRequiredFieldsFilled = true;

        for (int i = 0; i < textInputLayouts.length; i++) {
            if (textInputLayouts[i].getEditText().getText().toString().trim().isEmpty()) {
                SpannableString s = new SpannableString(errorMessages[i]);
                Typeface font=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
                s.setSpan(new com.pointters.utils.TypefaceSpan(font), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textInputLayouts[i].setError(s);
                isRequiredFieldsFilled = false;

            } else if ((textInputLayouts[i].getEditText().getInputType() == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS || textInputLayouts[i].getEditText().getInputType() == 33) && !AndroidUtils.isValidEmailAddress(textInputLayouts[i].getEditText().getText().toString())) {
                SpannableString s = new SpannableString(errorMessages[i]);
                Typeface font=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
                s.setSpan(new com.pointters.utils.TypefaceSpan(font), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textInputLayouts[i].setError(s);
                isRequiredFieldsFilled = false;
            }
        }

        return isRequiredFieldsFilled;
    }

}
