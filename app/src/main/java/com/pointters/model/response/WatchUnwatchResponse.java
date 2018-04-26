package com.pointters.model.response;

/**
 * Created by mac on 1/30/18.
 */

public class WatchUnwatchResponse {

    private Boolean watched;


    public WatchUnwatchResponse(Boolean watched) {
        this.watched = watched;
    }

    public Boolean getWatched() {
        return watched;
    }

    public void setWatched(Boolean watched) {
        this.watched = watched;
    }
}
