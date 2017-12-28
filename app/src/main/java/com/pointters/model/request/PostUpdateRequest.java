package com.pointters.model.request;

import com.pointters.model.Media;
import com.pointters.model.Tags;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 12/10/17.
 */

public class PostUpdateRequest {

    private String message;
    private ArrayList<Media> media;
    private ArrayList<Tags> tags;


    public PostUpdateRequest(String message, ArrayList<Media> media, ArrayList<Tags> tags) {
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

    public ArrayList<Tags> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tags> tags) {
        this.tags = tags;
    }
}
