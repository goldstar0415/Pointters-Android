package com.pointters.model.response;

import com.pointters.model.SearchSendServicesTotalModel;

/**
 * Created by mac on 12/22/17.
 */

public class SearchSendServicesResponse {

    private SearchSendServicesTotalModel hits;
    private int took;
    private boolean timed_out;


    public SearchSendServicesTotalModel getUpHits() {
        return hits;
    }

    public void setUpHits(SearchSendServicesTotalModel hits) {
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
