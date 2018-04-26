package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkc on 3/8/18.
 */

public class PostData {

    private String id;
    private String type;
    private String message;
    private String createdAt;
    private String updatedAt;
    private Integer countLikes;
    private Integer countComments;
    private Integer countShares;
    private ArrayList<TagSourceModel> tags;
    private Object media;


    public Media getMedia() {

        String strFileName = "";
        String strMediaType = "";

        if (media instanceof LinkedTreeMap) {
            strFileName = (String)((LinkedTreeMap) media).get("fileName");
            strMediaType = (String)((LinkedTreeMap) media).get("mediaType");
        }
        else if (media instanceof ArrayList) {
            if (((ArrayList) media).size() > 0) {
                strFileName = (String)((LinkedTreeMap) ((ArrayList) media).get(0)).get("fileName");
                strMediaType = (String)((LinkedTreeMap) ((ArrayList) media).get(0)).get("mediaType");
            }
        }

        return new Media(strFileName, strMediaType);
    }

    public void setMedia(Media media) {
        this.media = media;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCountLikes() {
        return countLikes;
    }

    public void setCountLikes(Integer countLikes) {
        this.countLikes = countLikes;
    }

    public ArrayList<TagSourceModel> getTags() {
        return tags;
    }

    public void setTags(ArrayList<TagSourceModel> tags) {
        this.tags = tags;
    }

    public Integer getCountComments() {
        return countComments;
    }

    public void setCountComments(Integer countComments) {
        this.countComments = countComments;
    }

    public Integer getCountShares() {
        return countShares;
    }

    public void setCountShares(Integer countShares) {
        this.countShares = countShares;
    }
}
