package com.pointters.model.response;

/**
 * Created by mac on 1/30/18.
 */

public class LikeUnlikeResponse {

    private Boolean liked;


    public LikeUnlikeResponse(Boolean liked) {
        this.liked = liked;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }
}
