package com.pointters.model.response;

/**
 * Created by jkc on 3/14/18.
 */

public class SellCounts {
    private Integer orders = 0;
    private Integer offers = 0;
    private Integer jobs = 0;
    public SellCounts(){
        orders = 0;
        offers = 0;
        jobs = 0;
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
    public Integer getJobs() {
        return jobs;
    }

    public void setJobs(Integer jobs) {
        this.jobs = jobs;
    }
}
