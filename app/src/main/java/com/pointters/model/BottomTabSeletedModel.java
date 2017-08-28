package com.pointters.model;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by prashantkumar on 18/8/17.
 */

public class BottomTabSeletedModel {
    private RelativeLayout  relativeLayout;
    private TextView textView;

    public BottomTabSeletedModel(RelativeLayout relativeLayout, TextView textView) {
        this.relativeLayout = relativeLayout;
        this.textView = textView;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    public void setRelativeLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
