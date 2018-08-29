package com.pointters.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pointters.R;

public class LikeTabLayout extends LinearLayout {
    public boolean isSelected = false;
    TextView titleView;
    ImageView imageView;
    View underlineview;
    String title;
    int image;


    public LikeTabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.like_tab, this, true);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.tab_style, 0, 0);

        try {
            title = a.getString(R.styleable.tab_style_title);
            image = a.getResourceId(R.styleable.tab_style_image, R.drawable.heart_on);
            isSelected = a.getBoolean(R.styleable.tab_style_selected, false);
        } finally {
            a.recycle();
        }

        // Throw an exception if required attributes are not set
        if (title == null) {
            throw new RuntimeException("No title provided");
        }
        if (image < 0) {
            throw new RuntimeException("No image provided");
        }

        init(title, image, isSelected);
    }

    // Setup views
    private void init(String title, int image, boolean isSelected) {
        titleView = (TextView) findViewById(R.id.title);
        imageView = (ImageView) findViewById(R.id.img_tab);
        underlineview = findViewById(R.id.underline);

        titleView.setText(title);
        imageView.setImageResource(image);
        setSelect(isSelected);
    }

    public void setTitle(String t) {
        this.title = t;
        titleView.setText(t);
    }

    public void setSelect(boolean b){
        isSelected = b;
        if (b){
            titleView.setAlpha(1.0f);
            titleView.setTextColor(ContextCompat.getColor(getContext(), R.color.card_title_text_color));
            underlineview.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }else{
            titleView.setAlpha(0.7f);
            titleView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_dark));
            underlineview.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        }
    }

}
