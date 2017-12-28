package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by mac on 12/5/17.
 */

public class JobRequestModel {

    private Object media;
    private String description;
    private String createdAt;
    private String requestId;


    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) { this.requestId = requestId; }

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

    public void setMedia(Object media) { this.media = media; }
}
