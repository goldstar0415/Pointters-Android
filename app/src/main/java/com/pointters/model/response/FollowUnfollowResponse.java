package com.pointters.model.response;

/**
 * Created by prashantkumar on 29/9/17.
 */

public class FollowUnfollowResponse {
    private  boolean following;

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public FollowUnfollowResponse(boolean following) {
        this.following = following;


    }
}
