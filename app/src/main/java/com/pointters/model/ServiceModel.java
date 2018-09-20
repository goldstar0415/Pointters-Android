package com.pointters.model;

/**
 * Created by prashantkumar on 11/11/17.
 */

public class ServiceModel {

    private LikedWatchingServiceModel service;
    private UserServiceModel user;
    private int numOrders;
    private float avgRating;
    private int ratingCount;
    private int pointValue;
    private boolean promoted;


    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }

    public LikedWatchingServiceModel getService() {
        return service;
    }

    public void setService(LikedWatchingServiceModel service) {
        this.service = service;
    }

    public UserServiceModel getUser() {
        return user;
    }

    public void setUser(UserServiceModel user) {
        this.user = user;
    }

    public int getNumOrders() {
        return numOrders;
    }

    public void setNumOrders(int numOrders) {
        this.numOrders = numOrders;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getPointValue() {
        return pointValue;
    }

    public void setPointValue(int pointValue) {
        this.pointValue = pointValue;
    }
}
