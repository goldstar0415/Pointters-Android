package com.pointters.utils;

import android.content.Context;

import com.pointters.model.PostData;

/**
 * Created by jkc on 3/8/18.
 */

public class SpannableDataImpl implements SpannableData {

    private final float marginSize;
    private PostData postData;
    private Context context;
    public SpannableDataImpl(Context context, PostData data){
        this.context = context;
        this.postData = data;
        this.marginSize = 20.0f;
    }

    @Override
    public float autoSpan() {
        return 0;
    }

    @Override
    public PostData getDta() {
        return postData;
    }

    @Override
    public void setData(PostData data) {
        this.postData = data;
    }
}
