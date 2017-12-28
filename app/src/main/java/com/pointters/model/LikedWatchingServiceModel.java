package com.pointters.model;

import com.pointters.model.request.LocationRequestModel;

import java.util.List;

/**
 * Created by prashantkumar on 17/11/17.
 */

public class LikedWatchingServiceModel {
    private String id;
    private String description;
    private Media media;
    private List<LocationRequestModel> location;
    private Prices prices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public List<LocationRequestModel> getLocation() {
        return location;
    }

    public void setLocation(List<LocationRequestModel> location) {
        this.location = location;
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }
}
