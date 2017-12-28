package com.pointters.model;

import com.pointters.model.request.LocationRequestModel;

import java.util.List;

/**
 * Created by prashantkumar on 8/11/17.
 */

public class Service {
    private String id;
    private String description;
    private List<Media> media;
    private List<LocationRequestModel> location;
    private List<Prices> prices;
    private boolean promoted;
    private Integer pointValue;
    private Integer numOrders;
    private Integer avgRating;

    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }

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

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public List<LocationRequestModel> getLocation() {
        return location;
    }

    public void setLocation(List<LocationRequestModel> location) {
        this.location = location;
    }

    public List<Prices> getPrices() {
        return prices;
    }

    public void setPrices(List<Prices> prices) {
        this.prices = prices;
    }

    public Integer getPointValue() {
        return pointValue;
    }

    public void setPointValue(Integer pointValue) {
        this.pointValue = pointValue;
    }

    public Integer getNumOrders() {
        return numOrders;
    }

    public void setNumOrders(Integer numOrders) {
        this.numOrders = numOrders;
    }

    public Integer getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Integer avgRating) {
        this.avgRating = avgRating;
    }
}
