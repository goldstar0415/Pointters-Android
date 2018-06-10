package com.pointters.listener;

import com.pointters.model.Prices;

/**
 * Created by prashantkumar on 6/10/17.
 */

public interface RecyclerViewItemClickWithSource {
    void onItemClick(int position, Prices source, int type);
}
