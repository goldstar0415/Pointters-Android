package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;

public class ShareModel {
    private LinkedTreeMap share;
    private UserProfileModel user;

    public UserProfileModel getUser() {
        return user;
    }

    public void setUser(UserProfileModel user) {
        this.user = user;
    }

    public String getShareDate(){
        String createDate = "";
        createDate = getShare().get("createdAt").toString();
        return createDate;
    }

    public LinkedTreeMap getShare() {
        return share;
    }

    public void setShare(LinkedTreeMap share) {
        this.share = share;
    }
}
