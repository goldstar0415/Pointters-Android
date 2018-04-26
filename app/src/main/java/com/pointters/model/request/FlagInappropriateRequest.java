package com.pointters.model.request;

public class FlagInappropriateRequest {
    private String comment;

    public FlagInappropriateRequest(String comment) {
        this.comment = comment;
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
