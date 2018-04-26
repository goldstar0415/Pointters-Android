package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 12/26/17.
 */

public class LinkServiceModel {

    private String _id;
    private String createdAt;
    private String description;
    private FulfillmentMethod fulfillmentMethod;
    private List<LocationModel> location;
    private Object media;
    private List<Prices> prices;
    private String updatedAt;
    private String userId;


    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FulfillmentMethod getFulfillmentMethod() {
        return fulfillmentMethod;
    }

    public void setFulfillmentMethod(FulfillmentMethod fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
    }

    public List<LocationModel> getLocation() {
        return location;
    }

    public void setLocation(List<LocationModel> location) {
        this.location = location;
    }

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

    public void setMedia(Object media) {
        this.media = media;
    }

    public List<Prices> getPrices() {
        return prices;
    }

    public void setPrices(List<Prices> prices) {
        this.prices = prices;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
