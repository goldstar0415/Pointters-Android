package com.pointters.utils;

import com.pointters.model.PostData;

/**
 * Created by jkc on 3/8/18.
 */

public interface SpannableData extends AutoSpannable {
    PostData getDta();
    void setData(PostData data);
}
