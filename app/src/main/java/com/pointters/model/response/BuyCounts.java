package com.pointters.model.response;

/**
 * Created by jkc on 3/14/18.
 */

public  class BuyCounts {
    private Integer orders = 0;
    private Integer offers = 0;
    private Integer request = 0;

    public BuyCounts(){
        orders = 0;
        offers = 0;
        request = 0;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public Integer getOffers() {
        return offers;
    }

    public void setOffers(Integer offers) {
        this.offers = offers;
    }

    public Integer getRequest() {
        return request;
    }

    public void setRequest(Integer request) {
        this.request = request;
    }
}
