package com.pointters.model.response;

import com.pointters.model.SearchConversationsTotalModel;

/**
 * Created by mac on 12/22/17.
 */

public class SearchConversationsResponse {

    private SearchConversationsTotalModel hits;
    private int took;
    private boolean timed_out;


    public SearchConversationsTotalModel getUpHits() {
        return hits;
    }

    public void setUpHits(SearchConversationsTotalModel hits) {
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
