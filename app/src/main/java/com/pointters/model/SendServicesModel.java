package com.pointters.model;

/**
 * Created by mac on 12/22/17.
 */

public class SendServicesModel {

    private int numOrders;
    private float avgRating;
    private int pointValue;
    private ServicesWoArray service;


    public ServicesWoArray getService() {
        return service;
    }

    public void setService(ServicesWoArray service) {
        this.service = service;
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

    public int getPointValue() {
        return pointValue;
    }

    public void setPointValue(int pointValue) {
        this.pointValue = pointValue;
    }
}
