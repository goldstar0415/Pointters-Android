package com.pointters.model;

import android.widget.LinearLayout;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by vikas on 8/25/2017.
 */

public class CommentsModel {
    private LinkedTreeMap comment;
    private UserProfileModel user;

    public UserProfileModel getUser() {
        return user;
    }

    public void setUser(UserProfileModel user) {
        this.user = user;
    }

    public String getUpdatedAt() {
        return getComment().get("updatedAt").toString();
    }


    public String getCommentText() {

        return getComment().get("comment").toString();
    }


    public LinkedTreeMap getComment() {
        return comment;
    }

    public void setComment(LinkedTreeMap comment) {
        this.comment = comment;
    }
}
