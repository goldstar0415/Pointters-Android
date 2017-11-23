package com.pointters.model;

/**
 * Created by prashant on 21-11-2017.
 */

public class RequestModel {
    private String id;
    private String createdAt;
    private Integer numOffers;
    private Integer numNewOffers;
    private Integer low;
    private Integer high;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
