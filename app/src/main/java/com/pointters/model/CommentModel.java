package com.pointters.model;

public class CommentModel {
    private String comment;
    private String updatedAt;
    private UserProfileModel user;

    public UserProfileModel getUser() {
        return user;
    }

    public void setUser(UserProfileModel user) {
        this.user = user;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
