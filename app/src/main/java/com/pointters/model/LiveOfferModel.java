package com.pointters.model;

import java.util.List;

/**
 * Created by jkc on 3/13/18.
 */

public class LiveOfferModel {
    private String id;
    private String description;
    private List<LocationModel> location;
    private Media media;
    private Prices prices;
    private UserModel seller;

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

    public List<LocationModel> getLocation() {
        return location;
    }

    public void setLocation(List<LocationModel> location) {
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

    public UserModel getSeller() {
        return seller;
    }

    public void setSeller(UserModel seller) {
        this.seller = seller;
    }
}
