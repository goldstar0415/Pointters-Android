package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;

public class LikesModel {
    private LinkedTreeMap like;
    private UserProfileModel user;
    private boolean followed;

    public UserProfileModel getUser() {
        return user;
    }

    public void setUser(UserProfileModel user) {
        this.user = user;
    }

    public String getLikeDate(){
        String createDate = "";
        createDate = getLike().get("createdAt").toString();
        return createDate;
    }

    public LinkedTreeMap getLike() {
        return like;
    }

    public void setLike(LinkedTreeMap like) {
        this.like = like;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }
}
