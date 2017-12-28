package com.pointters.model;

/**
 * Created by prashantkumar on 9/11/17.
 */

public class FollowersAndFollowing {
    private FollowFollowing followTo;
    private FollowFollowing followFrom;

    public FollowFollowing getFollowTo() {
        return followTo;
    }

    public void setFollowTo(FollowFollowing followTo) {
        this.followTo = followTo;
    }

    public FollowFollowing getFollowFrom() {
        return followFrom;
    }

    public void setFollowFrom(FollowFollowing followFrom) {
        this.followFrom = followFrom;
    }
}
