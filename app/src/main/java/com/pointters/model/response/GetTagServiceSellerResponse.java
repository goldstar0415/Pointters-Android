package com.pointters.model.response;

import com.pointters.model.FollowingModel;
import com.pointters.model.TagServiceTotalModel;

import java.util.List;

/**
 * Created by mac on 12/8/17.
 */

public class GetTagServiceSellerResponse {

    private TagServiceTotalModel hits;
    private int took;
    private boolean timed_out;


    public TagServiceTotalModel getUpHits() {
        return hits;
    }

    public void setUpHits(TagServiceTotalModel hits) {
        this.hits = hits;
    }

    public int getTook() {
        return took;
    }

    public void setTook(int took) {
        this.took = took;
    }

    public boolean getTimedOut() {
        return timed_out;
    }

    public void setTimedOut(boolean timed_out) {
        this.timed_out = timed_out;
    }
}
