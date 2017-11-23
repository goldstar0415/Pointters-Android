package com.pointters.model.request;

import com.pointters.model.Media;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 12/10/17.
 */

public class PostUpadateRequest {
    private String message;
    private ArrayList<Media> media;
    private ArrayList<String> tags;

    public PostUpadateRequest(String message, ArrayList<Media> media, ArrayList<String> tags) {
        this.message = message;
        this.media = media;
        this.tags = tags;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Media> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<Media> media) {
        this.media = media;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
