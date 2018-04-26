package com.pointters.model;

/**
 * Created by prashantkumar on 17/11/17.
 */

public class ServicesModel {

    private ServicesWoArray service;
    private UserServiceModel user;
    private Integer pointValue;
    private Integer numOrders;
    private Float avgRating;


    public ServicesWoArray getService() {
        return service;
    }

    public void setService(ServicesWoArray service) {
        this.service = service;
    }

    public UserServiceModel getUser() {
        return user;
    }

    public void setUser(UserServiceModel user) {
        this.user = user;
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

    public Float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Float avgRating) {
        this.avgRating = avgRating;
    }
}
