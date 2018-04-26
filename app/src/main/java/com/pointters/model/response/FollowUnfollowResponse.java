package com.pointters.model.response;

/**
 * Created by prashantkumar on 29/9/17.
 */

public class FollowUnfollowResponse {

    private Boolean followed;


    public FollowUnfollowResponse(Boolean followed) {
        this.followed = followed;
    }

    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }
}
