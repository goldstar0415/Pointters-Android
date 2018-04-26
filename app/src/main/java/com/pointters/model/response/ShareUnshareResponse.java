package com.pointters.model.response;

/**
 * Created by mac on 1/30/18.
 */

public class ShareUnshareResponse {

    private Boolean shared;


    public ShareUnshareResponse(Boolean shared) {
        this.shared = shared;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }
}
