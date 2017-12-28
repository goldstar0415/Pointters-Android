package com.pointters.model;

import java.util.List;

/**
 * Created by prashant on 21-11-2017.
 */

public class RequestModel {

    private String id;
    private String description;
    private String createdAt;
    private Integer numOffers;
    private Integer numNewOffers;
    private Integer low;
    private Integer high;
    private Integer expiresIn;
    private List<Media> media;


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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getNumOffers() {
        return numOffers;
    }

    public void setNumOffers(Integer numOffers) {
        this.numOffers = numOffers;
    }

    public Integer getNumNewOffers() {
        return numNewOffers;
    }

    public void setNumNewOffers(Integer numNewOffers) {
        this.numNewOffers = numNewOffers;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expires) {
        this.expiresIn = expires;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }
}
