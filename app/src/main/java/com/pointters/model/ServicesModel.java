package com.pointters.model;

/**
 * Created by prashantkumar on 17/11/17.
 */

public class ServicesModel {
    private ServicesWoArray service;
    private Integer pointValue;
    private Integer numOrders;
    private Integer avgRating;

    public ServicesWoArray getService() {
        return service;
    }

    public void setService(ServicesWoArray service) {
        this.service = service;
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
