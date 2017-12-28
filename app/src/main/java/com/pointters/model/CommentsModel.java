package com.pointters.model;

/**
 * Created by vikas on 8/25/2017.
 */

public class CommentsModel {
    String name,comment,dateTime;

    public CommentsModel(String name, String comment, String dateTime) {
        this.name = name;
        this.comment = comment;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
