package com.pointters.model;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 17/11/17.
 */

public class ServicesExploreModel {
    private String id;
    private String description;
    private ArrayList<LocationModel> location;
    private Media media;
    private Prices prices;
    private SellerModel seller;
    private boolean promoted = false;
    private Integer pointValue;
    private Integer numOrders;
    private Float avgRating;



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

    public Float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Float avgRating) {
        this.avgRating = avgRating;
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

    public ArrayList<LocationModel> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<LocationModel> location) {
        this.location = location;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public SellerModel getSeller() {
        return seller;
    }

    public void setSeller(SellerModel seller) {
        this.seller = seller;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }
}
