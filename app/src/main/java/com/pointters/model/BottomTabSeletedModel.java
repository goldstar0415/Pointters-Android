package com.pointters.model;

import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by prashantkumar on 18/8/17.
 */

public class BottomTabSeletedModel {
    private LinearLayout relativeLayout;
    private TextView textView;

    public BottomTabSeletedModel(LinearLayout relativeLayout, TextView textView) {
        this.relativeLayout = relativeLayout;
        this.textView = textView;
    }

    public LinearLayout getRelativeLayout() {
        return relativeLayout;
    }

    public void setRelativeLayout(LinearLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
